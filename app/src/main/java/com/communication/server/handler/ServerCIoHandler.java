package com.communication.server.handler;

import android.util.Log;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.communication.server.constant.Constant;
import com.communication.server.filter.ServerMessageCodecFactory;
import com.communication.server.impl.CommandManger;
import com.communication.server.session.ServerSessionManager;

/**
 * Created by rd0551 on 2017/5/22.
 */

public class ServerCIoHandler extends IoHandlerAdapter {
    private String TAG = "ServerCIoHandler";
    private NioSocketAcceptor acceptor;

/*    public void startMinaServer(){
        Log.i(TAG, "startUDPServer");
        //acceptor = new NioDatagramAcceptor();//UDP
        acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setReadBufferSize(4096);
        acceptor.getSessionConfig().setTcpNoDelay(true);
        acceptor.getFilterChain().addLast("executor", new ExecutorFilter());
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.setHandler(this);
  *//*      DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        chain.addLast("codec", new ProtocolCodecFilter(
                new PrefixedStringCodecFactory(Charset.forName("UTF-8"))));*//*
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerMessageCodecFactory()));

        acceptor.setReuseAddress(true);
        try {
            acceptor.bind(new InetSocketAddress(Constant.PORT));
            Log.i(TAG, "bind end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
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
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
        byte[] bytes = (byte[])message;
        int port = ((InetSocketAddress)session.getLocalAddress()).getPort();
        Log.i(TAG, "port "+port);
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
