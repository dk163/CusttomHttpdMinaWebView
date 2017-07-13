package com.communication.server.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.communication.server.constant.Constant;
import com.communication.server.handler.ServerAcceptor;
import com.communication.server.httpd.NanoHTTPd;
import com.google.gson.Gson;

public class CommandHandle {
	
	private volatile static CommandHandle instance;
	public static String TAG = "CommandHandle";
	public static final CharsetDecoder decoder = (Charset.forName("UTF-8")).newDecoder();	
	public Gson mGson = new Gson();

	private static Context mContext;
	private NanoHTTPd na;
	
	private static Handler mHandler = new Handler();

	
	private CommandHandle() {
	}
	
	public static CommandHandle getInstance() {
		if (instance == null) {
			synchronized (CommandHandle.class) {
				if (instance == null) {
					instance = new CommandHandle();
				}
			}
		}
		return instance;
	}
	
	public static void setContext(Context context) {
		mContext = context;
	}
	
	public Context getContext() {
		return mContext;
	}

	
	/**
	 * convertTimeStamp
	 * @param path ,C:\\DCIM\\170607002\\200245BB.MP4
	 * @return
	 */
	private long convertTimeStamp( String path){
		long timeStamp = 0;
		String str = "20";
		
		String tmp [] = path.split("\\\\");

    	str = str +tmp[tmp.length-2].substring(0, tmp[tmp.length-2].length()-3);//year,month,day,20170607
    	str = str + tmp[tmp.length-1].substring(0, tmp[tmp.length-1].length()-6);//time,20:02:45
    	 	
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
		try {
			date = simpleDateFormat .parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        timeStamp = date.getTime()/1000L;
        
		return timeStamp;
	}
	/**
	 * 递归删除文件和文件夹
	 * @param file 要删除的根目录
	 */
	private void RecursionDeleteFile(File file){
	    if(file.isFile()){
	        file.delete();
	        return;
	    }
	    if(file.isDirectory()){
	        File[] childFile = file.listFiles();
	        if(childFile == null || childFile.length == 0){
	            file.delete();
	            return;
	        }
	        if(childFile.length == 5){
		        for(File f : childFile){
		            RecursionDeleteFile(f);
		            Log.d(TAG,"dele >5 files");
		        }
	        }
	    }
	}

	public void  startHttpd(){
		try {
			if(na == null){
				na = new NanoHTTPd(Constant.HTTPD_PORT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i(TAG, "start com.communication.server.httpd");
	}
}
