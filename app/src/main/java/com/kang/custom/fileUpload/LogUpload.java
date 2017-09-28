package com.kang.custom.fileUpload;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;

import com.kang.custom.activity.MainActivity;
import com.kang.custom.util.LogUtils;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.ProgressCallback;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by kang on 2017/9/25.
 */

public class LogUpload {
    private ProgressDialog dialog =null;
    private static String url="";
    private static Context mContext;
    /**
     * SDK初始化也可以放到Application中,第三方https://www.bmob.cn/
     */
    //public static String APPID = "c3cfb6bdcfc0d484f5c47ff215424c83";
    public static String APPID = "736e32cf54d4993a87555df262dd19db";//kang

    private volatile static LogUpload instance;
    private String mImei = "1234567890";
    private String mProject = "default";

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setmImei(String mImei) {
        this.mImei = mImei;
    }

    public void setmProject(String mProject) {
        this.mProject = mProject;
    }

    public static LogUpload getInstance() {
        if (instance == null) {
            synchronized (LogUpload.class) {
                if (instance == null) {
                    instance = new LogUpload();
                }
            }
        }
        return instance;
    }

    private LogUpload() {
        if(null == mContext){
            LogUtils.e("mContext is null, return");
            return;
        }else{
            dialog = new ProgressDialog(mContext);
        }
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public void init(){
//        BmobConfig config =new BmobConfig.Builder(mActivity)
//		//设置appkey
//		.setApplicationId(APPID)
//		//请求超时时间（单位为秒）：默认15s
//		.setConnectTimeout(30)
//		//文件分片上传时每片的大小（单位字节），默认512*1024
//		.setUploadBlockSize(1024*1024)
//		//文件的过期时间(单位为秒)：默认24h
//		.setFileExpiration(86400)
//		.build();
//		Bmob.initialize(config);
        if(null == mContext){
            LogUtils.e("mContext is null, return");
            return;
        }else{
            Bmob.initialize(mContext, APPID);
        }
    }
    public void upload(final String path){
        if(null == dialog){
            dialog = new ProgressDialog(mContext);
        }
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp.jpg";
        if(TextUtils.isEmpty(path)){
            LogUtils.e("upload path is null");
            return;
        }
        LogUtils.i("path : "  + path);
        final BmobFile bmobFile = new BmobFile(new File(path));

        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("上传中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        bmobFile.uploadObservable(new ProgressCallback() {//上传文件操作
            @Override
            public void onProgress(Integer value, long total) {
                LogUtils.i("uploadMovoieFile-->onProgress:"+value);
                dialog.setProgress(value);
            }
        }).doOnNext(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                url = bmobFile.getUrl();
                LogUtils.i("上传成功："+url+","+bmobFile.getFilename());
                Message msg = new Message();
                msg.what = MainActivity.UPLOAD_FILE_RESULT;
                Bundle data = new Bundle();
                data.putInt("result", 1);//1 success
                msg.setData(data);
                MainActivity.getmHandler().sendMessage(msg);
            }
        }).concatMap(new Func1<Void, Observable<String>>() {//将bmobFile保存到movie表中
            @Override
            public Observable<String> call(Void aVoid) {
                return saveObservable(new CustomLog(mProject, mImei, bmobFile));
            }
        }).concatMap(new Func1<String, Observable<String>>() {//下载文件
            @Override
            public Observable<String> call(String s) {
                return bmobFile.downloadObservable(new ProgressCallback() {
                    @Override
                    public void onProgress(Integer value, long total) {
                        LogUtils.i("download-->onProgress:"+value+","+total);
                    }
                });
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                LogUtils.i("--onCompleted--");
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.i("--onError--:"+e.getMessage());
                dialog.dismiss();
            }

            @Override
            public void onNext(String s) {
                dialog.dismiss();
                dialog = null;
                LogUtils.i("download的文件地址："+s);
            }
        });
    }

    private Observable<String> saveObservable(BmobObject obj){
        return obj.saveObservable();
    }
}
