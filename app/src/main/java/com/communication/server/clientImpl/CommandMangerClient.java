package com.communication.server.clientImpl;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.communication.server.constant.Constant;
import com.communication.server.data.DataBase;
import com.communication.server.impl.CommandResource;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public final class CommandMangerClient {
	private volatile static CommandMangerClient instance;

	public static String TAG = "CommandMangerClient";
	public static final CharsetDecoder decoder = (Charset.forName("UTF-8")).newDecoder();
	public Gson mGson = new Gson();
	private CommandManagerHander mHander;

	private String mLeftStr = "";

	private CommandMangerClient() {
		mHander = new CommandManagerHander(CommandHandleClient.getInstance().getContext().getMainLooper());
	}
	
	public static CommandMangerClient getInstance() {
		if (instance == null) {
			synchronized (CommandMangerClient.class) {
				if (instance == null) {
					instance = new CommandMangerClient();
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

	private void handlerJson(String str) {
		DataBase base = mGson.fromJson(str, DataBase.class);
		int id = base.getMsg_id();
		Log.d(TAG,"id:" + id);
		switch (id){
			case CommandResource.SYS_CMD_STARTHTTPD:
				Log.i(TAG, "SYS_CMD_STARTHTTPD receive");
				mHander.sendEmptyMessage(CommandResource.SYS_CMD_STARTHTTPD);
				break;
			case CommandResource.SYS_CMD_STARTMTKLOG:
				Log.i(TAG, "SYS_CMD_STARTMTKLOG receive");
				mHander.sendEmptyMessageDelayed(CommandResource.SYS_CMD_STARTMTKLOG, 4*1000);
				break;
			case CommandResource.SYS_CMD_STOPMTKLOG:
				Log.i(TAG, "SYS_CMD_STOPMTKLOG receive");
				mHander.sendEmptyMessageDelayed(CommandResource.SYS_CMD_STOPMTKLOG, 4*1000);
				break;
			case CommandResource.SYS_CMD_CLEARMTKLOG:
				Log.i(TAG, "SYS_CMD_CLEARMTKLOG receive");
				mHander.sendEmptyMessageDelayed(CommandResource.SYS_CMD_CLEARMTKLOG, 4*1000);
				break;
			case CommandResource.SYS_CMD_CLEARLOG:
				Log.i(TAG, "SYS_CMD_CLEARLOG receive");
				mHander.sendEmptyMessageDelayed(CommandResource.SYS_CMD_CLEARLOG, 5*1000);
				break;

			default:
					Log.i(TAG, "handelrJson switch default");
					break;
		}
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
				case CommandResource.SYS_CMD_STARTHTTPD:
					Log.i(TAG,"SYS_CMD_STARTHTTPD ");
					Toast.makeText(CommandHandleClient.getInstance().getContext(), "start httpd and connect success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_STARTMTKLOG:
					Log.i(TAG,"SYS_CMD_STARTMTKLOG ");
					Toast.makeText(CommandHandleClient.getInstance().getContext(), "start mtklog success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_STOPMTKLOG:
					Log.i(TAG,"SYS_CMD_STOPMTKLOG ");
					Toast.makeText(CommandHandleClient.getInstance().getContext(), "stop mtklog success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_CLEARMTKLOG:
					Log.i(TAG,"SYS_CMD_CLEARMTKLOG ");
					Toast.makeText(CommandHandleClient.getInstance().getContext(), "clear mtklog success", Toast.LENGTH_SHORT).show();
					break;
				case CommandResource.SYS_CMD_CLEARLOG:
					Log.i(TAG, "SYS_CMD_CLEARLOG");
					Toast.makeText(CommandHandleClient.getInstance().getContext(), "clear custom log", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
			}
		}
		
	}
}
