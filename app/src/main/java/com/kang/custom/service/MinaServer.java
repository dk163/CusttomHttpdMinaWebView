package com.kang.custom.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.communication.server.handler.ServerAcceptor;
import com.communication.server.impl.CommandHandle;

/**
 * Created by kang on 2017/7/12.
 */

public class MinaServer extends Service implements Runnable{
    private final String TAG = MinaServer.class.getSimpleName();
    private static MinaServer instance;
    private static ServerAcceptor mServerAcceptor;

    public MinaServer() {
    }

    public static MinaServer getInstance() {
        if (instance == null) {
            synchronized (MinaServer.class) {
                if (instance == null) {
                    instance = new MinaServer();
                }
            }
        }
        return instance;
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.i(TAG, "MinaServer create");

        Thread thread = new Thread(this);
        thread.start();

        CommandHandle.getInstance().setContext(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if(mServerAcceptor != null) {
            mServerAcceptor.unbind();
            mServerAcceptor = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        if(mServerAcceptor == null) {
            Log.i(TAG, "create ServerAcceptor");
            mServerAcceptor = new ServerAcceptor();
            }
        }

    public void stopServer(){
        if(mServerAcceptor != null) {
            mServerAcceptor.unbind();
            mServerAcceptor = null;
        }
        //this.stopSelf();
        Log.i(TAG, "stop minaServer");
    }
}
