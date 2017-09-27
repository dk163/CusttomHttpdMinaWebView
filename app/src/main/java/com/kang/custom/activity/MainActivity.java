package com.kang.custom.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.communication.server.constant.Constant;
import com.communication.server.handler.ClientConnector;
import com.communication.server.httpd.NanoHTTPd;
import com.kang.custom.application.MyApplication;
import com.kang.custom.fileUpload.LogUpload;
import com.kang.custom.util.LogUtils;
import com.kang.custom.service.MinaClient;
import com.kang.custom.util.AppInfo;
import com.kang.custom.util.PermissionUtil;
import com.kang.customhttpdmina.R;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "customLog";

    private Button startClient;
    private Button stopClient;
    private Button startMtkLog;
    private Button stopMtkLog;
    private Button getMtkLog;
    private Button clearCustomLog;
    private Button pushBtn;
    private Button getLogBtn;
    private Button startWebView;
    private Button uploadLog;
    private TextView state;

    private final int TOAST_START_HTTPD = 0;
    private final int TOAST_STOP_HTTPD = 1;
    public static final int TOAST_ERROR = 2;
    private final int TOAST_STOP_CLIENT = 3;
    private final int TOAST_START_HTTPD_CLIENT = 4;
    private final static int STOP_CLIENT = 5;
    private final int START_CLIENT_ALREADY = 6;
    public final static int APP_VERSION = 7;
    public final static int DOWNLOAD_STATE = 8;
    public final static int UPLOAD_FILE_RESULT = 9;

    private static NanoHTTPd na;
    private Context mContext;
    private static String tmp = "";

    private static MainHandler mHandler;
    private static boolean DebugMode =  false;//debug flag

    private String uploadPath;
    private long size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.setContext(this);
        PermissionUtil.verifyStoragePermissions(this);
        /**
         * upload log init
         */
        try {
            LogUpload.getInstance().init();
            //upload.upload();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mContext = this;
        mHandler =  new MainHandler(Looper.getMainLooper());

        //init views
        if(!DebugMode){
            initViews();
        }else{
            initViewsDebug();//init debug
        }

        initListeners();

        LogUtils.i("app name: " +AppInfo.getPackageName(this));
        LogUtils.i("app version: "+AppInfo.getVersionCode(this));

        if(na == null) {
            try {
                na = new NanoHTTPd(Constant.HTTPD_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(TOAST_START_HTTPD_CLIENT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "MainActivity onDestroy:");

        Intent mIntent = new Intent(mContext, MinaClient.class);
        stopService(mIntent);

        if(na != null) na.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startClient: {
                if (MinaClient.isClientInstance()) {
                    mHandler.sendEmptyMessage(START_CLIENT_ALREADY);
                    return;
                }

                EditText edConIp = (EditText) findViewById(R.id.edConIp);
                tmp = edConIp.getText().toString();
                if (!(tmp.isEmpty()) && ((tmp.length()) != 0)) {
                    Constant.setIP(tmp);//获取clent ip
                }

                Intent mIntent = new Intent(mContext, MinaClient.class);
                startService(mIntent);
            }
            break;
            case R.id.stopClient: {
                if (!MinaClient.isClientInstance()) {
                    mHandler.sendEmptyMessage(TOAST_ERROR);
                } else {
                    Intent mIntent = new Intent(mContext, MinaClient.class);
                    stopService(mIntent);
                    mHandler.sendEmptyMessageDelayed(TOAST_STOP_CLIENT, 0);
                }
            }
            break;
            case R.id.startMtkLog: {
                if (null == ClientConnector.getClientAcceptorHandler()) {
                    mHandler.sendEmptyMessageDelayed(TOAST_ERROR, 0);
                    return;
                }
                ClientConnector.getClientAcceptorHandler().sendEmptyMessage(Constant.MSG_START_MTKLOG);
                LogUtils.i(TAG, "start mtklog");
            }
            break;
            case R.id.stopMtkLog: {
                if (null == ClientConnector.getClientAcceptorHandler()) {
                    mHandler.sendEmptyMessageDelayed(TOAST_ERROR, 0);
                    return;
                }
                ClientConnector.getClientAcceptorHandler().sendEmptyMessage(Constant.MSG_STOP_MTKLOG);
                LogUtils.i(TAG, "stop mtklog");
            }
            break;
            case R.id.getMtkLog: {
                if (null == ClientConnector.getClientAcceptorHandler()) {
                    mHandler.sendEmptyMessageDelayed(TOAST_ERROR, 0);
                    return;
                }

                if (true) {
                    state = (TextView) findViewById(R.id.downLoadState);
                    state.setText("Downloading files......");
                }

                ClientConnector.getClientAcceptorHandler().sendEmptyMessage(Constant.MSG_ZIP_MTKLOG);
                LogUtils.i(TAG, "getMtkLog mtklog");
            }
            break;
            case R.id.clearCustomLog: {
                if (null == ClientConnector.getClientAcceptorHandler()) {
                    mHandler.sendEmptyMessageDelayed(TOAST_ERROR, 0);
                    return;
                }
                ClientConnector.getClientAcceptorHandler().sendEmptyMessage(Constant.MSG_CLEAR_LOG);
                LogUtils.i(TAG, "clear log");
            }
            break;
            case R.id.pushButton: {
//                if(null == ClientConnector.getClientAcceptorHandler()){
//                    mHandler.sendEmptyMessageDelayed(TOAST_ERROR, 0);
//                    return;
//                }
//                ClientConnector.getClientAcceptorHandler().sendEmptyMessage(Constant.MSG_PUSH_FILE);
//                LogUtils.i(TAG, "push file to server");
            }
            break;
            case R.id.getLogBtn: {
                if (null == ClientConnector.getClientAcceptorHandler()) {
                    mHandler.sendEmptyMessageDelayed(TOAST_ERROR, 0);
                    return;
                }

                if (true) {
                    state = (TextView) findViewById(R.id.downLoadState);
                    state.setText("Downloading files......");
                }

                ClientConnector.getClientAcceptorHandler().sendEmptyMessage(Constant.MSG_ZIP_LOG);
                LogUtils.i(TAG, "zip log");
            }
            break;
            case R.id.startWebView: {
                EditText edHttpdUrl = (EditText) findViewById(R.id.edHttpdUrl);//httpd server url
                tmp = edHttpdUrl.getText().toString();
                if (!(tmp.isEmpty()) && (tmp.length() != 0)) {
                    Constant.setHTTPIPPORT("http://" + tmp + ":8080");
                    LogUtils.i(TAG, "httpd server ip: " + ("http://" + tmp + ":8080"));
                }

                Intent intent = new Intent();//download file to /mnt/sdcard/MyFavorite
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(Constant.HTTPDIPPORT);
                intent.setData(content_url);
                startActivity(intent);
//                mIntent = new Intent(mContext, WebViewActivity.class);
//                startActivity(mIntent);
                LogUtils.i(TAG, "open webview");
            }
            break;
            case R.id.uploadLog: {
                LogUpload.getInstance().upload(uploadPath);
            }
            break;

            default:
                break;
        }
    }

    /**
     * init listener
     */
    private void initListeners() {
        if(DebugMode){
            startClient.setOnClickListener(this);
            stopClient.setOnClickListener(this);
        }

        startMtkLog.setOnClickListener(this);
        stopMtkLog.setOnClickListener(this);
        getMtkLog.setOnClickListener(this);
        clearCustomLog.setOnClickListener(this);
        pushBtn.setOnClickListener(this);
        getLogBtn.setOnClickListener(this);
        startWebView.setOnClickListener(this);
        uploadLog.setOnClickListener(this);
    }

    //user layout init
    private void initViews(){
        setContentView(R.layout.activity_main_user);

        Intent mIntent = new Intent(mContext, MinaClient.class);
        startService(mIntent);

        state = (TextView) findViewById(R.id.downLoadState);

        startMtkLog = (Button)findViewById(R.id.startMtkLog);
        stopMtkLog = (Button)findViewById(R.id.stopMtkLog);
        //get MtkLog
        getMtkLog = (Button) findViewById(R.id.getMtkLog);
        clearCustomLog = (Button)findViewById(R.id.clearCustomLog);
        //push file
        pushBtn = (Button) findViewById(R.id.pushButton);
        //get customLog
        getLogBtn = (Button) findViewById(R.id.getLogBtn);
        startWebView = (Button)findViewById(R.id.startWebView);
        //upload file
        uploadLog = (Button) findViewById(R.id.uploadLog);

    }

    //debug layout init
    private void initViewsDebug(){
        setContentView(R.layout.activity_main_debug);

        state = (TextView) findViewById(R.id.downLoadState);

        startMtkLog = (Button)findViewById(R.id.startMtkLog);
        stopMtkLog = (Button)findViewById(R.id.stopMtkLog);
        getMtkLog = (Button) findViewById(R.id.getMtkLog);
        clearCustomLog = (Button)findViewById(R.id.clearCustomLog);
        //push file
        pushBtn = (Button) findViewById(R.id.pushButton);
        getLogBtn = (Button) findViewById(R.id.getLogBtn);
        startWebView = (Button)findViewById(R.id.startWebView);
        //upload file
        uploadLog = (Button) findViewById(R.id.uploadLog);
        startClient = (Button)findViewById(R.id.startClient);
        stopClient = (Button)findViewById(R.id.stopClient);
    }

    public static MainHandler getmHandler() {
        return mHandler;
    }

    public static void stopClient(){
        mHandler.sendEmptyMessage(STOP_CLIENT);//broadcast stop mina client
    }

    /**
     * app version info
     * @param ver
     */
    private void setVesrion(String ver){
        TextView version = (TextView) findViewById(R.id.version);
        version.setText("Ver: "+ ver);
        LogUtils.i(TAG,"app version: " + ver);
    }

    /**
     * Prompt statement
     * @param info
     */
    private void setDownloadState(final String info){
        if(true){
            LogUtils.i("init debug mode");
        }
        if((info.equalsIgnoreCase("fail")) || (TextUtils.isEmpty(info))){
            state.setText("Download log zip failed");
        }else if(info.equalsIgnoreCase("disconnect")){
            state.setText("Connect device wifi,please");
        }else{
            File file = new File(info);
            size = -1;
            if(file.isFile() && file.exists()){
                size = (file.length()/1024);//kb
            }
            state.setText("Download file success, file size:"+size +"Kb.\n" +"File path: "+info+".\n"+"Note:/storage/emulated/0/是内置SD卡路径");
            LogUtils.i(TAG,"download file path:" + info);
            uploadPath = info;
        }
    }

    public class MainHandler extends Handler {

        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String info = "";

            super.handleMessage(msg);
            switch (msg.what) {
                case TOAST_START_HTTPD:
                    Toast.makeText(getApplicationContext(), "start httpd success", Toast.LENGTH_SHORT).show();
                    break;
                case TOAST_STOP_HTTPD:
                    Toast.makeText(getApplicationContext(), "stop httpd success", Toast.LENGTH_SHORT).show();
                    break;
                case TOAST_ERROR:
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    if(true){
                        state = (TextView) findViewById(R.id.downLoadState);
                        state.setText("Happen error");
                    }
                    break;
                case TOAST_STOP_CLIENT:
                    Toast.makeText(getApplicationContext(), "stop client success", Toast.LENGTH_SHORT).show();
                    break;
                case TOAST_START_HTTPD_CLIENT:
                    //Toast.makeText(getApplicationContext(), "client start httpd success", Toast.LENGTH_SHORT).show();//kang del
                    break;
                case STOP_CLIENT:
                    Intent mIntent = new Intent(mContext, MinaClient.class);
                    stopService(mIntent);
                    //Toast.makeText(getApplicationContext(), "wifi disConnect", Toast.LENGTH_SHORT).show();//kang del
                    if(null != LogUpload.getInstance().getDialog()){
                        LogUpload.getInstance().getDialog().dismiss();
                    }
                    setDownloadState("disconnect");
                    break;
                case START_CLIENT_ALREADY:
                    Toast.makeText(getApplicationContext(), "client session already connect", Toast.LENGTH_SHORT).show();
                    break;
                case APP_VERSION:
                    bundle = msg.getData();
                    info = bundle.getString("version");
                    setVesrion(info);
                    break;
                case DOWNLOAD_STATE:
                    bundle = msg.getData();
                    info = bundle.getString("path");
                    setDownloadState(info);
                    break;
                case UPLOAD_FILE_RESULT:
                    bundle = msg.getData();
                    if((null != bundle) && (bundle.getInt("result") == 1)){
                        state.setText("Upload file success, file size:"+size +"Kb.\n" +"path: "+ uploadPath + ".\n"+"Note:/storage/emulated/0/是内置SD卡路径");
                    }else{
                        state.setText("Upload file fail!");
                    }
                    break;

                default:
                    break;
            }
        }
    }

}

