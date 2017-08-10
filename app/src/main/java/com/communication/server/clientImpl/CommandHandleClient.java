package com.communication.server.clientImpl;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.communication.server.constant.Constant;
import com.communication.server.httpd.NanoHTTPd;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CommandHandleClient {
	
	private volatile static CommandHandleClient instance;
	public static String TAG = "CommandHandleClient";
	public static final CharsetDecoder decoder = (Charset.forName("UTF-8")).newDecoder();	
	public Gson mGson = new Gson();

	private static Context mContext;
	private NanoHTTPd na;
	
	private static Handler mHandler = new Handler();

	
	private CommandHandleClient() {
	}
	
	public static CommandHandleClient getInstance() {
		if (instance == null) {
			synchronized (CommandHandleClient.class) {
				if (instance == null) {
					instance = new CommandHandleClient();
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
			for(File f : childFile){
				RecursionDeleteFile(f);
				Log.d(TAG,"delete files");
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

	public boolean  clearLog(){
		final String dir = "NightVision";
		final String des = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dir;
		Log.i(TAG, "start clearLog del path: " + des);

		File file = new File(des);//the file to save the path
		RecursionDeleteFile(file);

		final String dir2 = "mtklog";
		final String des2 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dir2;
		Log.i(TAG, "start clearLog del path: " + des2);

		File file2 = new File(des2);//the file to save the path
		RecursionDeleteFile(file2);

		Log.i(TAG, "clear log success");

		return true;
	}
}
