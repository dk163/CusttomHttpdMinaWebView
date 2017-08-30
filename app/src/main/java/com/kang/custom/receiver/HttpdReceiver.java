package com.kang.custom.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import com.communication.server.constant.Constant;
import com.communication.server.handler.ServerAcceptor;
import com.communication.server.httpd.NanoHTTPd;
import com.kang.custom.service.MinaServer;


/**
 * Created by kang on 2017/7/11.
 */

public class HttpdReceiver extends BroadcastReceiver {
    private final String TAG = HttpdReceiver.class.getSimpleName();
    private final static String HTTPD_START = "httpd_start_msg";
    private final static String HTTPD_STOP = "httpd_stop_msg";
    private final static String BOOT_COMPLETE_ACTION = "android.intent.action.BOOT_COMPLETED";
    private NanoHTTPd na;
    private ServerAcceptor mServerAcceptor;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action: " + action);

        if(action.equalsIgnoreCase(BOOT_COMPLETE_ACTION)){
            // start httpd service
            Log.i(TAG, "boot complete");
            Intent intent1 = new Intent(context, MinaServer.class);
            context.startService(intent1);
        }
        if(action.equalsIgnoreCase(HTTPD_START)){
            if(mServerAcceptor == null){
                mServerAcceptor = new ServerAcceptor();
            }

            try {
                if(na == null){
                    na = new NanoHTTPd(Constant.HTTPD_PORT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "start com.communication.server.httpd");;
        }else if (action.equalsIgnoreCase(HTTPD_STOP)){
            if(na != null){
                na.stop();
            }
            Log.i(TAG, "stop com.communication.server.httpd");
        }
    }

}
