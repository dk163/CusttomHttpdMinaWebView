package com.communication.server.impl;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.Log;
import android.widget.Toast;

import com.communication.server.constant.Constant;
import com.communication.server.data.DataBase;
import com.google.gson.Gson;
import com.kang.custom.activity.MainActivity;

public final class CommandManger {
	private volatile static CommandManger instance;
	
	public static String TAG = "CommandManger";
	public static final CharsetDecoder decoder = (Charset.forName("UTF-8")).newDecoder();	
	public Gson mGson = new Gson();
	private CommandManagerHander mHander;
	private Intent intent;

	private String mLeftStr = "";

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
			Log.e(TAG, "port is error");
			return;
		}
		
		String sp = new String(data);
		if ((sp == null) || (sp.length() <= 0)) {
			Log.e(TAG,"process sp is null return");
			return;
		}

		Log.d(TAG,"dvr sp:" + sp);

		handlerPackage(sp);
		
		return;
	}
	
	public void handlerPackage(String str) {
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
        		Log.d(TAG, "fullPackage:" + fullPackage);
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
	
	public void handlerJson(String str) {
		DataBase base = mGson.fromJson(str, DataBase.class);
		int id = base.getMsg_id();
		Log.d(TAG,"id:" + id);
		switch (id){
			case CommandResource.SYS_CMD_STARTHTTPD:
				CommandHandle.getInstance().startHttpd();
				break;
			case  CommandResource.SYS_CMD_STARTMTKLOG:
				intent = new Intent();//start mtklog, com.mediatek.mtklogger.ADB_CMD -e cmd_name start/stop --ei cmd_target 23
				intent.setAction(Constant.ACTION_MTKLOG);
				intent.putExtra("cmd_name", "start");
				intent.putExtra("cmd_target", 23);
				CommandHandle.getInstance().getContext().sendBroadcast(intent);
				Log.i(TAG, "sendBroadcast mtklog start");
				mHander.sendEmptyMessage(CommandResource.SYS_CMD_STARTMTKLOG);
				break;
			case CommandResource.SYS_CMD_STOPMTKLOG:
				intent = new Intent();//stop mtklog
				intent.setAction(Constant.ACTION_MTKLOG);
				intent.putExtra("cmd_name", "stop");
				intent.putExtra("cmd_target", 23);
				CommandHandle.getInstance().getContext().sendBroadcast(intent);
				Log.i(TAG, "sendBroadcast mtklog stop");
				mHander.sendEmptyMessage(CommandResource.SYS_CMD_STOPMTKLOG);
				break;
			case CommandResource.SYS_CMD_CLEARMTKLOG:
				intent = new Intent();//stop mtklog
				intent.setAction(Constant.ACTION_MTKLOG);
				intent.putExtra("cmd_name", "clear_all_logs");
				CommandHandle.getInstance().getContext().sendBroadcast(intent);
				Log.i(TAG, "sendBroadcast mtklog clear");
				mHander.sendEmptyMessage(CommandResource.SYS_CMD_CLEARMTKLOG);
				break;
			case CommandResource.SYS_CMD_CLEARLOG:
				if(CommandHandle.getInstance().clearLog()){
					mHander.sendEmptyMessage(CommandResource.SYS_CMD_CLEARLOG);
				}
				break;

			default:
					Log.i(TAG, "handelrJson switch default");
					break;
		}
	}
	
	public static byte[] hexStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
	
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
					Log.i(TAG,"SYS_CMD_STARTMTKLOG ");
					Toast.makeText(CommandHandle.getInstance().getContext(), "start mtklog success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_STOPMTKLOG:
					Log.i(TAG,"SYS_CMD_STOPMTKLOG ");
					Toast.makeText(CommandHandle.getInstance().getContext(), "stop mtklog success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_CLEARMTKLOG:
					Log.i(TAG,"SYS_CMD_CLEARMTKLOG ");
					Toast.makeText(CommandHandle.getInstance().getContext(), "clear mtklog success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_CLEARLOG:
					Log.i(TAG, "SYS_CMD_CLEARLOG");
					Toast.makeText(CommandHandle.getInstance().getContext(), "clear NightVision log", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
			}
		}
		
	}
}
