package com.communication.server.handler;

import android.util.Log;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.communication.server.constant.Constant;
import com.communication.server.filter.ServerMessageCodecFactory;
import com.communication.server.session.ClientSessionManager;
import com.communication.server.session.ServerSessionManager;

public class ClientConnector {

    /** The number of message to receive */
    public static final int MAX_RECEIVED = 10;
    /** The connector */
    public static NioSocketConnector connector;
    
    public final String TAG = "ClientIoHandler";
	private ClientCIoHandler mClientHandler;
	public  IoSession session;
	
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

		Log.d(TAG, "ClientConnector enter");
		
		connectServer();

		Log.d(TAG, "all connect socket ok");
	    try {
			Thread.sleep(365 * 24 * 60 * 60 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public IoSession getSession() {
		return (session == null)?null:session;
	}

	private void connectServer(){
		ConnectFuture connFuture = connector.connect(new InetSocketAddress(Constant.MINA_IP, Constant.MINA_PORT));
		if(connFuture == null)
			return;
		connFuture.awaitUninterruptibly();
		connFuture.addListener(new IoFutureListener<ConnectFuture>() {
			public void operationComplete(ConnectFuture future) {
				if (future.isConnected()) {
					Log.i(TAG, "connectServer connect");
					session = future.getSession();
					try {
						sendData();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Log.e(TAG, "Not connected...exiting");
				}
			}
		});
	}

	public void sendData() throws Exception{
		String msg = "{\"msg_id\":1}";//open httpd server
		byte [] d =  msg.getBytes();
		session.write(IoBuffer.wrap(d));
	}
}
