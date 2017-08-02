package com.kang.custom.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import com.communication.server.constant.Constant;
import com.communication.server.httpd.NanoHTTPd;
import com.communication.server.session.CSession;
import com.communication.server.session.ClientSessionManager;
import com.kang.custom.service.MinaClient;
import com.kang.custom.service.MinaServer;
import com.kang.customhttpdmina.R;

public class MainActivity extends AppCompatActivity{
    private final String TAG = "MainActivity";
    public final static int TOAST_START_HTTPD = 0;
    public final static int TOAST_STOP_HTTPD = 1;
    private static NanoHTTPd na;
    //private Intent mIntent;
    private Context mContext;
    //private MyApplication myApplication;
    private static Constant constant;
    private static String tmp = "";
    public static MainHandler getmHandler() {
        return mHandler;
    }

    private static MainHandler mHandler;
    private static MinaClient mc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        //myApplication = (MyApplication)getApplication();
        constant = new Constant();
        mHandler =  new MainHandler(Looper.getMainLooper());

        //mina port 8081
        Button startServer = (Button) findViewById(R.id.startServer);
        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etPort = (EditText)findViewById(R.id.serverEditTextPort);
                tmp = etPort.getText().toString();
               if(!(tmp.isEmpty()) && (tmp.length() !=0)){
                    constant.setPORT(Integer.parseInt(tmp));//获取输入端口
                }

                Intent mIntent = new Intent(mContext, MinaServer.class);
                startService(mIntent);
            }
        });
        Button stopServer = (Button)findViewById(R.id.stopServer);
        stopServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MinaServer.getInstance().stopServer();
                Intent mIntent = new Intent(mContext, MinaServer.class);
                stopService(mIntent);
                //MinaServer.getInstance().onDestroy();
            }
        });

        Button startClient = (Button)findViewById(R.id.startClient);
        startClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CSession cs = ClientSessionManager.getInstance().getSession(Constant.MINA_PORT);
                if((cs != null) && cs.isConnected()){
                    Log.i(TAG, "already session Connected");
                    cs.close(true);
                }
                EditText edConIp = (EditText)findViewById(R.id.edConIp);
                tmp = edConIp.getText().toString();
                if(!(tmp.isEmpty()) && ((tmp.length()) != 0)){
                    constant.setIP(tmp);//获取clent ip
                }
                mc = new MinaClient();
                //mc.stopClient();
                Intent mIntent = new Intent(mContext, MinaClient.class);
                //stopService(mIntent);
                startService(mIntent);
            }
        });
        Button stopClient = (Button)findViewById(R.id.stopClient);
        stopClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mc != null) mc.stopClient();
                Intent mIntent = new Intent(mContext, MinaClient.class);
                stopService(mIntent);

            }
        });

        //httpd port 8080
        Button startHttpd = (Button)findViewById(R.id.startHttpd);
        startHttpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    EditText edHttpd = (EditText) findViewById(R.id.edHttpdPort);
                    tmp = edHttpd.getText().toString();
                    if(!(tmp.isEmpty()) && (tmp.length() != 0)){
                        constant.setHttpdPort(Integer.parseInt(tmp));//获取httpd port
                    }

                    try {
                        if(na == null){
                            na = new NanoHTTPd(Constant.HTTPD_PORT);
                            mHandler.sendEmptyMessage(TOAST_START_HTTPD);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                Log.i(TAG, "start com.communication.server.httpd");
            }
        });

        Button stopHttpd = (Button)findViewById(R.id.stopHttpd);
        stopHttpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(na != null){
                    na.stop();
                }
                Log.i(TAG, "stop com.communication.server.httpd");
            }
        });

        Button startWebView = (Button)findViewById(R.id.startWebView);
        startWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edHttpdUrl = (EditText) findViewById(R.id.edHttpdUrl);//httpd server url
                tmp = edHttpdUrl.getText().toString();
                if(!(tmp.isEmpty()) && (tmp.length() != 0)){
                    constant.setHTTPIPPORT("http://"+tmp+":8080");
                    Log.i(TAG, "httpd server ip: "+ ("http://"+tmp+":8080"));
                }

                Intent intent = new Intent();//download file to /mnt/sdcard/MyFavorite
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(Constant.HTTPDIPPORT);
                intent.setData(content_url);
                startActivity(intent);
//                mIntent = new Intent(mContext, WebViewActivity.class);
//                startActivity(mIntent);
                Log.i(TAG, "open webview");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity onDestroy:");
        MinaServer.getInstance().stopServer();
        if(mc != null )mc.stopClient();

        if(na != null) na.stop();
    }
	 public class MainHandler extends Handler{

         public MainHandler(Looper looper) {
             super(looper);
         }

         @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TOAST_START_HTTPD:
                    Toast.makeText(getApplicationContext(), "start httpd success", Toast.LENGTH_SHORT).show();
                    break;
                case TOAST_STOP_HTTPD:
                    Toast.makeText(getApplicationContext(), "stop httpd success", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}

