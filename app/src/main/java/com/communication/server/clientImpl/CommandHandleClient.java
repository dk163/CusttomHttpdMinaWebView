package com.communication.server.clientImpl;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.communication.server.constant.Constant;
import com.communication.server.http.OkHttpClientManager;
import com.communication.server.httpd.NanoHTTPd;
import com.google.gson.Gson;
import com.kang.custom.activity.MainActivity;
import com.kang.custom.fileUpload.LogUpload;
import com.kang.custom.util.LogUtils;
import com.kang.custom.util.TimeUtil;
import com.squareup.okhttp.Request;

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
	private final String ZIP_PREFIX = ".zip";
	private String project_prefix = "C41_";


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

	public void setmFileName(String fileName, String imei) {
        if(TextUtils.isEmpty(fileName)){
            LogUtils.e("fileName is null");
            return;
        }

        String [] versionSplit = fileName.split("_");
		this.project_prefix = versionSplit[0].concat("_");
        LogUpload.getInstance().setmImei(imei);
        LogUpload.getInstance().setmProject(fileName);
	}

	/**
	 * download CustomLog.zip from server
	 * @return
	 */
	public boolean downloadLogZIP() {
		final String dir = project_prefix + "CustomLog_" + TimeUtil.timeConvertToString() + ZIP_PREFIX;
		final String des = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		final String url = "http://192.168.43.1:8080/sdcard/CustomLog.zip";

		OkHttpClientManager.downloadAsyn(url, des, dir, new OkHttpClientManager.ResultCallback<String>() {
			@Override
			public void onError(Request request, Exception e) {
				LogUtils.e("download Log zip fail");
                Message msg = new Message();
                msg.what = MainActivity.DOWNLOAD_STATE;
                Bundle data = new Bundle();
                data.putString("path", "fail");
                msg.setData(data);
                MainActivity.getmHandler().sendMessage(msg);

				Toast.makeText(CommandHandleClient.getInstance().getContext(), "download Log failed", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onResponse(String response) {
				LogUtils.i("download Log zip success");
				Message msg = new Message();
				msg.what = MainActivity.DOWNLOAD_STATE;
				Bundle data = new Bundle();
				data.putString("path", des+dir);
				msg.setData(data);
				MainActivity.getmHandler().sendMessage(msg);

				Toast.makeText(CommandHandleClient.getInstance().getContext(), "download Log success", Toast.LENGTH_SHORT).show();
			}
		});

		return true;
	}

	/**
	 * download MtkLog.zip from server
	 * @return
	 */
	public boolean downloadMtkLogZIP() {
		final String dir = project_prefix + "MtkLog_" + TimeUtil.timeConvertToString() + ZIP_PREFIX;
		final String des = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		final String url = "http://192.168.43.1:8080/sdcard/MtkLog.zip";

		OkHttpClientManager.downloadAsyn(url, des, dir, new OkHttpClientManager.ResultCallback<String>() {
			@Override
			public void onError(Request request, Exception e) {
				LogUtils.e("download mtkLog zip fail");
                Message msg = new Message();
                msg.what = MainActivity.DOWNLOAD_STATE;
                Bundle data = new Bundle();
                data.putString("path", "fail");
                msg.setData(data);
                MainActivity.getmHandler().sendMessage(msg);

				Toast.makeText(CommandHandleClient.getInstance().getContext(), "download mtkLog failed", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onResponse(String response) {
				LogUtils.i("download mtkLog zip success");
				Message msg = new Message();
				msg.what = MainActivity.DOWNLOAD_STATE;
				Bundle data = new Bundle();
				data.putString("path", des+dir);
				msg.setData(data);
				MainActivity.getmHandler().sendMessage(msg);
				Toast.makeText(CommandHandleClient.getInstance().getContext(), "download mtkLog success", Toast.LENGTH_SHORT).show();
			}
		});

		return true;
	}
}
