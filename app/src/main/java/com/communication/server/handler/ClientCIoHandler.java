package com.communication.server.handler;

import android.util.Log;

import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.communication.server.clientImpl.CommandMangerClient;
import com.communication.server.session.CSession;
import com.communication.server.session.ClientSessionManager;
import com.kang.custom.util.LogUtils;

/**
 * 客户端请求的入口，所有请求都首先经过它分发处理
 */
public class ClientCIoHandler extends IoHandlerAdapter {
	private final String TAG = ClientCIoHandler.class.getSimpleName();
	
	public void sessionCreated(IoSession session) throws Exception {
		int port = ((InetSocketAddress)session.getRemoteAddress()).getPort();
		CSession wrapperSession = new CSession(session);
		ClientSessionManager.getInstance().addSession(port, wrapperSession);
		Log.i(TAG, "sessionCreated");

	}

	public void sessionOpened(IoSession session) throws Exception {
	}
	
	public void messageReceived(IoSession session, Object message) throws Exception {
		int port = ((InetSocketAddress)session.getRemoteAddress()).getPort();
		byte[] bytes = (byte[])message;

		LogUtils.e(TAG, "port: "+port);
		CommandMangerClient.getInstance().process(port, bytes);
	}
	
	public void sessionClosed(IoSession session) throws Exception {

	}
	
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
	}
	
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		Log.e(TAG,"ClientCIoHandler exceptionCaught() from " + session.getRemoteAddress() + ";id: " + session.getId(), cause);
	}
	
	public void messageSent(IoSession session, Object message) throws Exception {
		//LogUtils.d("ClientCIoHandler messageSent() from " + session.getRemoteAddress() + ";id: " + session.getId());
	}
	
	// 发送到服务端
	public static void SendToServer(int port, IoBuffer ib) {
		if (ClientSessionManager.getInstance().getSession(port) != null) {
			ClientSessionManager.getInstance().getSession(port).write(ib);
		}
	}
	
}