package com.communication.server.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.widget.Toast;

import com.communication.server.constant.Constant;
import com.communication.server.data.AppData;
import com.communication.server.data.DataBase;
import com.communication.server.data.PuhFile;
import com.communication.server.session.CSession;
import com.communication.server.session.ServerSessionManager;
import com.kang.custom.util.FileUtil;
import com.kang.custom.util.LogUtils;
import com.google.gson.Gson;
import com.kang.custom.util.AppInfo;

public final class CommandManger {
	private volatile static CommandManger instance;
	
	public static String TAG = "customLog";
	public static final CharsetDecoder decoder = (Charset.forName("UTF-8")).newDecoder();	
	public Gson mGson = new Gson();
	private CommandManagerHander mHander;
	private Intent mIntent;

	private String mLeftStr = "";
    private String json = "";

	private CommandManger() {
		mHander = new CommandManagerHander(CommandHandle.getInstance().getContext().getMainLooper());
	}
	
	public static CommandManger getInstance() {
		if (instance == null) {
			synchronized (CommandManger.class) {
				if (instance == null) {
					instance = new CommandManger();
				}
			}
		}
		return instance;
	}
	
	public void process(int port, byte[] data) {
		if (Constant.MINA_PORT != port) {
			LogUtils.e(TAG, "port is error");
			return;
		}
		
		String sp = new String(data);
		if ((sp == null) || (sp.length() <= 0)) {
			LogUtils.e(TAG,"process sp is null return");
			return;
		}

		LogUtils.d(TAG,"dvr sp:" + sp);

		handlerPackage(sp);
		
		return;
	}

	private void handlerPackage(String str) {
		int left = 0;
        int right = 0;
        int index = 0;
        
        mLeftStr += str;
        
        int length = mLeftStr.length();
        
        while (index < length) {
        	char ch = mLeftStr.charAt(index);
        	if ('{' == ch) {
        		left++;
        	}
        	else if ('}' == ch) {
        		right++;
        	}
        	
        	index++;
        	
        	// 完整包
        	if (0 != left && left == right) {
        		// 处理整包
        		String fullPackage = mLeftStr.substring(0, index);
        		LogUtils.d(TAG, "fullPackage:" + fullPackage);
        		handlerJson(fullPackage);
        		
        		// 剩余包
        		mLeftStr = mLeftStr.substring(index);
        		index = 0;
        		left = 0;
        		right = 0;
        		length = mLeftStr.length();
        	}
        	
        	
        }
	}

	private void handlerJson(String str) {
		DataBase base = mGson.fromJson(str, DataBase.class);
		CSession session  =  ServerSessionManager.getInstance().getSession(Constant.MINA_PORT);
		int id = base.getMsg_id();
		LogUtils.d(TAG,"id:" + id);
		switch (id){
			case CommandResource.SYS_CMD_STARTHTTPD:
				CommandHandle.getInstance().startHttpd();

                AppData appData = new AppData(1, AppInfo.getVersionCode(CommandHandle.getInstance().getContext()));
                json = (new Gson()).toJson(appData);
				session.write(IoBuffer.wrap((json).getBytes()));
				break;
			case  CommandResource.SYS_CMD_STARTMTKLOG:
                mIntent = new Intent();//start mtklog, com.mediatek.mtklogger.ADB_CMD -e cmd_name start/stop --ei cmd_target 23

                mIntent.setAction(Constant.ACTION_MTKLOG);
                mIntent.putExtra("cmd_name", "set_auto_start_1");
                mIntent.putExtra("cmd_target", 23);
                CommandHandle.getInstance().getContext().sendBroadcast(mIntent);

                mIntent.setAction(Constant.ACTION_MTKLOG);
                mIntent.putExtra("cmd_name", "start");
                mIntent.putExtra("cmd_target", 23);
				CommandHandle.getInstance().getContext().sendBroadcast(mIntent);

				LogUtils.i(TAG, "sendBroadcast mtklog start");
				mHander.sendEmptyMessage(CommandResource.SYS_CMD_STARTMTKLOG);

				session.write(IoBuffer.wrap((Constant.CMD_START_MTKLOG).getBytes()));
				break;
			case CommandResource.SYS_CMD_STOPMTKLOG:
                mIntent = new Intent();//stop mtklog

                mIntent.setAction(Constant.ACTION_MTKLOG);
                mIntent.putExtra("cmd_name", "set_auto_start_0");
                mIntent.putExtra("cmd_target", 23);
                CommandHandle.getInstance().getContext().sendBroadcast(mIntent);

                mIntent.setAction(Constant.ACTION_MTKLOG);
                mIntent.putExtra("cmd_name", "stop");
                mIntent.putExtra("cmd_target", 23);
				CommandHandle.getInstance().getContext().sendBroadcast(mIntent);
				LogUtils.i(TAG, "sendBroadcast mtklog stop");
				mHander.sendEmptyMessage(CommandResource.SYS_CMD_STOPMTKLOG);

                session.write(IoBuffer.wrap((Constant.CMD_STOP_MTKLOG).getBytes()));
				break;
			case CommandResource.SYS_CMD_CLEARMTKLOG:
                mIntent = new Intent();//stop mtklog
                mIntent.setAction(Constant.ACTION_MTKLOG);
                mIntent.putExtra("cmd_name", "clear_all_logs");
				CommandHandle.getInstance().getContext().sendBroadcast(mIntent);
				LogUtils.i(TAG, "sendBroadcast mtklog clear");
				mHander.sendEmptyMessage(CommandResource.SYS_CMD_CLEARMTKLOG);
				break;
			case CommandResource.SYS_CMD_CLEARLOG:
				LogUtils.i(TAG, "SYS_CMD_CLEARLOG");
				if(CommandHandle.getInstance().clearLog()){
					mHander.sendEmptyMessage(CommandResource.SYS_CMD_CLEARLOG);
                    session.write(IoBuffer.wrap((Constant.CMD_CLEAR_LOG).getBytes()));
				}else{
                    LogUtils.i(TAG, "");
                    session.write(IoBuffer.wrap((Constant.CMD_ERROR).getBytes()));
                }

				break;
			case CommandResource.SYS_CMD_PUSHFILE:
				LogUtils.i(TAG, "SYS_CMD_PUSHFILE");

                PuhFile pf = mGson.fromJson(str, PuhFile.class);
                CommandHandle.getInstance().pushFile(pf.getIp(), pf.getPath(), pf.getFileName());

                session.write(IoBuffer.wrap((Constant.CMD_PUSH_FILE).getBytes()));
				break;
            case CommandResource.SYS_CMD_ZIPMTKLOG:
                LogUtils.i(TAG, "SYS_CMD_ZIPMTKLOG");

                String src = (Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator).concat("mtklog");
                String des = (Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator).concat("mtklog.zip");

                File file = new File(des);
                if(file.exists()){
                    file.delete();
                    LogUtils.d("del mtklog.zip: " + des);
                }

                if(FileUtil.zipMultiFile(src, des, true)){
                    session.write(IoBuffer.wrap((Constant.CMD_ZIP_MTKLOG).getBytes()));
                }else{
                    session.write(IoBuffer.wrap((Constant.CMD_ERROR).getBytes()));
                }
                break;

			default:
					LogUtils.i(TAG, "handelrJson switch default");
					break;
		}
	}
	
//	public static byte[] hexStringToBytes(String s) {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
//        }
//        return data;
//    }
	
	private class CommandManagerHander extends Handler{
		public CommandManagerHander(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch(msg.what){
				case CommandResource.SYS_CMD_STARTMTKLOG:
					LogUtils.i(TAG,"handleMessage sys_cmd_startmtklog ");
					Toast.makeText(CommandHandle.getInstance().getContext(), "start mtklog success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_STOPMTKLOG:
					LogUtils.i(TAG,"handleMessage sys_cmd_stopmtklog ");
					Toast.makeText(CommandHandle.getInstance().getContext(), "stop mtklog success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_CLEARMTKLOG:
					LogUtils.i(TAG,"handleMessage sys_cmd_clearmtklog ");
					Toast.makeText(CommandHandle.getInstance().getContext(), "clear mtklog success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_CLEARLOG:
					LogUtils.i(TAG, "handleMessage sys_cmd_clearlog");
					Toast.makeText(CommandHandle.getInstance().getContext(), "clear log sucess", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_PUSHFILE:
					LogUtils.i(TAG, "handleMessage sys_cmd_pushfile");
					Toast.makeText(CommandHandle.getInstance().getContext(), "push file", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
			}
		}
		
	}
}
