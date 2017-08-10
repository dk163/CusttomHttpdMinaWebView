package com.communication.server.handler;

import android.util.Log;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import com.communication.server.constant.Constant;
import com.communication.server.filter.ServerMessageCodecFactory;
import com.communication.server.impl.CommandManger;
import com.communication.server.session.CSession;
import com.communication.server.session.ServerSessionManager;

/**
 * Created by rd0551 on 2017/5/22.
 */

public class ServerCIoHandler extends IoHandlerAdapter {
    private String TAG = "ServerCIoHandler";

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        CSession wrapperSession = new CSession(session);
        //int port = ((InetSocketAddress)session.getRemoteAddress()).getPort();
        int port = ((InetSocketAddress)session.getLocalAddress()).getPort();
        ServerSessionManager.getInstance().addSession(port, wrapperSession);
        Log.i(TAG, "sessionCreated");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        Log.i(TAG, "sessionOpened");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        Log.e(TAG, "exceptionCaught: ", cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
        byte[] bytes = (byte[])message;
        int port = ((InetSocketAddress)session.getLocalAddress()).getPort();
        CSession wrapperSession = new CSession(session);
        ServerSessionManager.getInstance().addSession(port, wrapperSession);
        Log.e(TAG, "port: "+port);
        CommandManger.getInstance().process(port, bytes);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        super.inputClosed(session);
    }

    public static void SendToClient(int port, IoBuffer ib) {
        if (ServerSessionManager.getInstance().getSession(port) != null) {
            ServerSessionManager.getInstance().getSession(port).write(ib);
        }
    }
}
