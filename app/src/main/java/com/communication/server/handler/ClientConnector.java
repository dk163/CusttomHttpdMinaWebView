package com.communication.server.handler;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.communication.server.clientImpl.CommandHandleClient;
import com.communication.server.constant.Constant;
import com.communication.server.data.PuhFile;
import com.communication.server.filter.ServerMessageCodecFactory;
import com.communication.server.session.CSession;
import com.communication.server.session.ClientSessionManager;
import com.kang.custom.util.LogUtils;
import com.google.gson.Gson;

public class ClientConnector {

    /** The number of message to receive */
    public static final int MAX_RECEIVED = 10;
    /** The connector */
    public static NioSocketConnector connector;
    
    public final String TAG = "ClientConnector";
	private ClientCIoHandler mClientHandler;
	private static ClientAcceptorHandler mAcceptorHandler;
	private CSession session = null;

	private final int MSG_SHOW_CONNECT = 0;
	
    /**
     * Create the ClientConnector's instance
     */
    public ClientConnector() {
    	connector = new NioSocketConnector();
    	connector.getSessionConfig().setTcpNoDelay(true);
        connector.getSessionConfig().setReadBufferSize(4096);
		connector.getFilterChain().addLast("executor", new ExecutorFilter());
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerMessageCodecFactory()));
		
		mClientHandler = new ClientCIoHandler();
		connector.setHandler(mClientHandler);
		connector.setConnectTimeout(5);

		LogUtils.i(TAG, "connector.getSessionConfig() getReceiveBufferSize: "+ connector.getSessionConfig().getReceiveBufferSize());
		LogUtils.i(TAG, "connector.getSessionConfig() getSendBufferSize: "+ connector.getSessionConfig().getSendBufferSize());
		LogUtils.i(TAG, "connector.getSessionConfig() getSendBufferSize: "+ connector.getSessionConfig().getReadBufferSize());
		LogUtils.i(TAG, "ClientConnector enter");
		mAcceptorHandler = new ClientAcceptorHandler(Looper.getMainLooper());
		connectServer();

	    try {
			Thread.sleep(365 * 24 * 60 * 60 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private void connectServer(){
		ConnectFuture connFuture = connector.connect(new InetSocketAddress(Constant.MINA_IP, Constant.MINA_PORT));
		if(connFuture == null)
			return;
		connFuture.awaitUninterruptibly();
		connFuture.addListener(new IoFutureListener<ConnectFuture>() {
			public void operationComplete(ConnectFuture future) {
				if (future.isConnected()) {
					LogUtils.i(TAG, "connectServer connect");
					mAcceptorHandler.sendEmptyMessage(MSG_SHOW_CONNECT);
				} else {
					LogUtils.e(TAG, "Not connected...exiting");
				}
			}
		});
	}


	private class ClientAcceptorHandler extends Handler {
		public ClientAcceptorHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String cmd = "";
			LogUtils.i(TAG, "msg what: " + msg.what);
			session = ClientSessionManager.getInstance().getSession(Constant.MINA_PORT);
			if(session == null){
				LogUtils.e(TAG, " client session is null");
				return;
			}
			switch (msg.what) {
				case MSG_SHOW_CONNECT:
					if(null != session){
						ClientSessionManager.getInstance().getSession(Constant.MINA_PORT).write(IoBuffer.wrap((Constant.CMD_CONNECT_SERVER).getBytes()));
						LogUtils.i(TAG, "start connect server");
					}
					break;
				case Constant.MSG_START_MTKLOG:
					if(null != session){
						ClientSessionManager.getInstance().getSession(Constant.MINA_PORT).write(IoBuffer.wrap((Constant.CMD_START_MTKLOG).getBytes()));
						LogUtils.i(TAG, "start mtklog");
					}
					break;
				case Constant.MSG_STOP_MTKLOG:
					if(null != session){
						ClientSessionManager.getInstance().getSession(Constant.MINA_PORT).write(IoBuffer.wrap((Constant.CMD_STOP_MTKLOG).getBytes()));
						LogUtils.i(TAG, "stop mtklog");
					}
					break;
				case Constant.MSG_CLEAR_MTKLOG:
					if(null != session){
						ClientSessionManager.getInstance().getSession(Constant.MINA_PORT).write(IoBuffer.wrap((Constant.CMD_CLEAR_MTKLOG).getBytes()));
						LogUtils.i(TAG, "clear mtklog");
					}
					break;
				case Constant.MSG_CLEAR_LOG:
					if(null != session){
						ClientSessionManager.getInstance().getSession(Constant.MINA_PORT).write(IoBuffer.wrap((Constant.CMD_CLEAR_LOG).getBytes()));
						LogUtils.i(TAG, "clear custom log");
					}
					break;
				case Constant.MSG_PUSH_FILE:
					if(null != session){
						final String fileName =  "CustomLog.apk";
						File file = new File(getInnerSDCardPath()+File.separator+"CustomLog"+File.separator+fileName);
						LogUtils.i(TAG, "file path: " + file.getAbsolutePath());
						cmd = makeJson(6, file.getAbsolutePath(), fileName );//push file msg_id
						LogUtils.i(TAG, "push file cmd: " + cmd);
						ClientSessionManager.getInstance().getSession(Constant.MINA_PORT).write(IoBuffer.wrap(cmd.getBytes()));
						LogUtils.i(TAG, "push file");
					}
					break;

				default:
					break;
			}
		}
	}

	public static Handler getClientAcceptorHandler(){
		if(null != mAcceptorHandler){
			return mAcceptorHandler;
		}else{
			return null;
		}
	}

	private String makeJson(int msg_id, String path, String fileName){
		String ip = getWifiIP();
		PuhFile pf =  new PuhFile(msg_id, path, ip, fileName);

		final String jsp = (new Gson()).toJson(pf);

		return jsp;
	}

	/**
	 * 获取内置SD卡路径
	 * @return
	 */
	private String getInnerSDCardPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 遍历文件夹下的所有文件
	 */
	private void RecursionFile(File file){
		if(file.isFile()){
			LogUtils.e(TAG, "push file, Recursion dir");
			return;
		}
	}

	/**
	 * 得到wifi连接的IP地址
	 * @param
	 * @return
	 */
	private  String getWifiIP(){
		WifiManager wifiManager = (WifiManager)(CommandHandleClient.getInstance().getContext().getApplicationContext()).getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddr = wifiInfo.getIpAddress();
		String ipStr = int2string(ipAddr);
		return ipStr;
	}

	/**
	 * 输入int 得到String类型的ip地址
	 * @param i
	 * @return
	 */
	private  String int2string(int i){
		return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF );
	}
}
