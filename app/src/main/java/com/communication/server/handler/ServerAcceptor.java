package com.communication.server.handler;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.communication.server.constant.Constant;
import com.communication.server.filter.ServerMessageCodecFactory;

public class ServerAcceptor {

	private NioSocketAcceptor acceptor;
//	private NioDatagramAcceptor acceptor;
	private ServerCIoHandler mServerIoHandler;
	private final String TAG = ServerAcceptor.class.getSimpleName();
	
	public ServerAcceptor() {
		mServerIoHandler = new ServerCIoHandler();
		
		try {
			bind();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "mServerAcceptor e:" + e.toString());
		}
	}
	
	public void bind() throws IOException {

		Log.i(TAG, "bind ServerAcceptor");
		//acceptor = new NioDatagramAcceptor();//UDP
		acceptor = new NioSocketAcceptor();
		acceptor.getSessionConfig().setReadBufferSize(4096);
		acceptor.getSessionConfig().setTcpNoDelay(true);
		acceptor.getFilterChain().addLast("executor", new ExecutorFilter());
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.setHandler(mServerIoHandler);
  /*      DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        chain.addLast("codec", new ProtocolCodecFilter(
                new PrefixedStringCodecFactory(Charset.forName("UTF-8"))));*/
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerMessageCodecFactory()));

		acceptor.setReuseAddress(true);
		try {
			acceptor.bind(new InetSocketAddress(Constant.PORT));
			Log.i(TAG, "bind end");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void unbind() {
		acceptor.unbind();
	}

}
