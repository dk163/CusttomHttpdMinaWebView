package com.communication.server.filter;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 *  服务端接收消息解码，可在此解密消息
 */
public class ServerMessageDecoder extends ProtocolDecoderAdapter {
	CharsetDecoder mDecoder = Charset.forName("utf-8").newDecoder();
	
	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		byte b[] = new byte[in.limit()];
		in.get(b, 0, in.limit());
		out.write(b);
	}
}