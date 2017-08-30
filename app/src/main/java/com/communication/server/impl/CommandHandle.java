package com.communication.server.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.communication.server.constant.Constant;
import com.communication.server.http.OkHttpClientManager;
import com.communication.server.httpd.NanoHTTPd;
import com.communication.server.util.LogUtils;
import com.communication.server.util.FileUtil;
import com.communication.server.util.ShellUtils;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;

public class CommandHandle {
	
	private volatile static CommandHandle instance;
	public static String TAG = "customLog";
	public static final CharsetDecoder decoder = (Charset.forName("UTF-8")).newDecoder();	
	public Gson mGson = new Gson();

	private static Context mContext;
	private NanoHTTPd na;


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

	public void  startHttpd(){
		try {
			if(na == null){
				na = new NanoHTTPd(Constant.HTTPD_PORT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		LogUtils.i(TAG, "start com.communication.server.httpd");
	}

	public boolean  clearLog(){
		final String dir = "NightVision";
		final String des = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dir;
		LogUtils.i(TAG, "start clearLog del path: " + des);

		File file = new File(des);//the file to save the path
		FileUtil.RecursionDeleteFile(file);

		final String dir2 = "mtklog";
		final String des2 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dir2;
		LogUtils.i(TAG, "start clearLog del path: " + des2);

		File file2 = new File(des2);//the file to save the path
		FileUtil.RecursionDeleteFile(file2);

		LogUtils.i(TAG, "clear log success");

		return true;
	}

	public boolean pushFile(final String IP, final String path, final String fileName){
		//http://192.168.42.129:8080/mnt/sdcard/customLog/
		String url = "http://"+IP.concat(":8080") +File.separator+path;
		final String des = Environment.getExternalStorageDirectory().getAbsolutePath();
		final String delName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
		LogUtils.i(TAG, "url: " + url);
		File file = new File(delName);
		if(file.exists()){
			file.delete();
			LogUtils.d("del last name: " + fileName);
		}

        String cp = FileUtil.RecursionFindFile(new File("/system/app/"), fileName.replace(".apk", ""));
        if(null == cp){
            cp = FileUtil.RecursionFindFile(new File("/system/priv-app/"), fileName.replace(".apk", ""));
            if(null == cp){
                LogUtils.e("push dir is not find");
                return false;
            }
        }

        final String cpCMD = ("cp -rf /mnt/sdcard/CustomLog.apk ").concat(cp);
        LogUtils.i("cp : " + cp + ", cpCMD: " + cpCMD);

		OkHttpClientManager.downloadAsyn(url, des, fileName, new OkHttpClientManager.ResultCallback<String>() {
			@Override
			public void onResponse(String response) {
				//文件下载成功，这里回调的reponse为文件的absolutePath
				LogUtils.i("downloadAsyn ok, path:  " + response);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.i("ShellUtils start");
				        String[] commands = new String[] {"mount -o rw,remount /system", cpCMD};
                        boolean ret =ShellUtils.checkRootPermission();//true is root
                        LogUtils.i("ShellUtils checkRootPermission ret1: " + ret);
                        if(ret){
                            ShellUtils.CommandResult result = ShellUtils.execCommand(commands, true);//do su,true
                            LogUtils.i("ShellUtils result: " + result.result);
                            LogUtils.i("ShellUtils result: " + result.errorMsg);
                            LogUtils.i("ShellUtils result: " + result.successMsg);
                        }
                    }
                }).start();
			}

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				LogUtils.e("downloadAsyn Exception:" + e.toString());
			}
		});

		return true;
	}
}
