package com.communication.server.filter;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;


/**
 * 服务端发送消息前编码，可在此加密消息
 */
public class ServerMessageEncoder extends ProtocolEncoderAdapter {
	CharsetEncoder mEncoder = Charset.forName("utf-8").newEncoder();
	
	@Override
	public void encode(IoSession iosession, Object message, ProtocolEncoderOutput out) throws Exception {
		IoBuffer buff = IoBuffer.allocate(500).setAutoExpand(true);
		buff.put(message.toString().getBytes());
		//buff.putObject(message);
		buff.flip();
		out.write(buff);
	}
}
