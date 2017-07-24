package com.communication.server.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.communication.server.constant.Constant;
import com.communication.server.filter.ServerMessageCodecFactory;
import com.communication.server.impl.CommandHandle;

public class ServerAcceptor {

	private NioSocketAcceptor acceptor;
//	private NioDatagramAcceptor acceptor;
	private ServerCIoHandler mServerIoHandler;
	private final String TAG = ServerAcceptor.class.getSimpleName();
	private ServerAcceptorHandler mAcceptorHandler;
	private final int TOAST_SHOW = 0;
	
	public ServerAcceptor() {
		mServerIoHandler = new ServerCIoHandler();
		mAcceptorHandler = new ServerAcceptorHandler(Looper.getMainLooper());

		try {
			bind();
			mAcceptorHandler.sendEmptyMessage(TOAST_SHOW);
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
			acceptor.bind(new InetSocketAddress(Constant.MINA_PORT));
			Log.i(TAG, "bind end");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void unbind() {
		acceptor.unbind();
	}

	private class ServerAcceptorHandler extends Handler{
		public ServerAcceptorHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.i(TAG, "msg what: " + msg.what);
			switch (msg.what){
				case TOAST_SHOW:
					Toast.makeText(CommandHandle.getInstance().getContext(),"mina server bind end", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	}
}
