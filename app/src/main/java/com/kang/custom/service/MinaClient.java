package com.kang.custom.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.communication.server.handler.ClientConnector;
import com.communication.server.impl.CommandHandle;

import org.apache.mina.core.session.IoSession;

/**
 * Created by rd0551 on 2017/7/12.
 */

public class MinaClient extends Service implements Runnable {
    private final String TAG = MinaClient.class.getSimpleName();
    private volatile static MinaClient instance;
    private ClientConnector client;

    public MinaClient(){

    }

    public static MinaClient getInstance() {
        if (instance == null) {
            synchronized (MinaClient.class) {
                if (instance == null) {
                    instance = new MinaClient();
                }
            }
        }
        return instance;
    }

    public void onCreate() {
        // TODO Auto-generated method stub
        Log.i(TAG, "MinaClient create");

        Thread thread = new Thread(this);
        thread.start();

        CommandHandle.getInstance().setContext(getApplicationContext());
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        client = new ClientConnector();
        client.connector.dispose(true);
    }

    public void stopClient(){
        if(client == null){
            Log.e(TAG, "stopClient client = null");
            return;
        }
        IoSession is = client.getSession();
        if( is != null){
            client.getSession().close(true);
        }
        Log.i(TAG, "stopClient");
    }
}
