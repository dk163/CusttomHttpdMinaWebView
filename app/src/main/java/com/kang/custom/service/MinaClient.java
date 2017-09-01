package com.kang.custom.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.communication.server.clientImpl.CommandHandleClient;
import com.communication.server.constant.Constant;
import com.communication.server.handler.ClientConnector;
import com.communication.server.session.CSession;
import com.communication.server.session.ClientSessionManager;
import com.kang.custom.util.LogUtils;
import com.kang.custom.receiver.NetworkConnectChangedReceiver;

/**
 * Created by kang on 2017/7/12.
 */

public class MinaClient extends Service implements Runnable {
    private final String TAG = MinaClient.class.getSimpleName();
    private volatile static MinaClient instance;
    private static ClientConnector client;
    private static volatile boolean isInstance = false;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;

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
        LogUtils.i(TAG, "MinaClient create");

        networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        registerBroadcastReceiver();

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
        unregisterReceiver(networkConnectChangedReceiver);
        super.onDestroy();

        CSession session = ClientSessionManager.getInstance().getSession(Constant.MINA_PORT);
        if(null != session){
            session.close(true);
        }
        client = null;
        setIsClientInstance(false);
        LogUtils.i(TAG, "stopClient end");
    }

    public static boolean isClientInstance() {
        return isInstance;
    }

    public static void setIsClientInstance(boolean isInit) {
        isInstance = isInit;
    }

    private void registerBroadcastReceiver() {
        LogUtils.i(TAG, "register receiver");

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectChangedReceiver, filter);
    }
}
