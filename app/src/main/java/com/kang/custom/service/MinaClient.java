package com.kang.custom.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.communication.server.clientImpl.CommandHandleClient;
import com.communication.server.constant.Constant;
import com.communication.server.handler.ClientConnector;
import com.communication.server.impl.CommandHandle;
import com.communication.server.session.CSession;
import com.communication.server.session.ClientSessionManager;

import org.apache.mina.core.session.IoSession;

/**
 * Created by rd0551 on 2017/7/12.
 */

public class MinaClient extends Service implements Runnable {
    private final String TAG = MinaClient.class.getSimpleName();
    private volatile static MinaClient instance;
    private static ClientConnector client;
    private static volatile boolean isInstance = false;

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
        isInstance = true;

        Thread thread = new Thread(this);
        thread.start();

        CommandHandleClient.getInstance().setContext(getApplicationContext());
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
        //client.connector.dispose(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(client == null){
            Log.e(TAG, "stopClient client = null");
            return;
        }
        CSession session = ClientSessionManager.getInstance().getSession(Constant.MINA_PORT);
        if( session != null){
            session.close(true);
        }
        client = null;
        isInstance = false;
        Log.i(TAG, "stopClient");
    }

    public static boolean isClientInstance() {
        return isInstance;
    }
}
