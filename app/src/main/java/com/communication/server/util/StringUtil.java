package com.communication.server.util;

import org.apache.mina.core.buffer.IoBuffer;

import java.nio.ByteOrder;

public class StringUtil {

	public static final int ResultCode = 1;
	public static final int COMMAND_LEGTH = 16;
	public static final String[] ss12 = new String[] { "0", "+1 ", "-1", "+2", "-2" };
	public static final String[] ss22 = new String[] { "+2", "+1 ", "0", "-1", "-2" };
	public static final int[] ss33 = new int[] { 0, 1, 10, 2, 3 };
	// public static final String CAMERA_STATUS = "camera_status";
	public static final String SENDKEY = "titlename";
	public static final String SENDID = "titleid";
	public static final String SENDPOS = "titlepos";
	public static final String SELFISH_PHOTO = "selfish";

	public static final int RECORD_START = 0x10;
	public static final int RECORD_STOP = 0x11;
	public static final int SDCARD_FULL = 0x12;
	public static final int UPDATE_RECORDTIME = 0x13;

	public static int bytesToInt(byte[] src, int offset) {
		int len = src.length;
		int value = -1;
		
		if (1 == len) {
			value = (int) ((src[offset] & 0xFF));
		}
		else if (2 == len) {
			value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8));
		}
		else if (3 == len) {
			value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16));
		}
		else if (4 == len) {
			value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
		}
		
		return value;
	}
	
	public static int lBytesToInt(byte[] b) {
    	int s = 0;
    	for (int i = 0; i < 3; i++) {
	    	if (b[3-i] >= 0) {
	    		s = s + b[3-i];
	    	} else {
	    		s = s + 256 + b[3-i];
	    	}
    		s = s * 256;
    	}
    	if (b[0] >= 0) {
    		s = s + b[0];
    	} else {
    		s = s + 256 + b[0];
    	}
    	return s;
    }
	
	/**
     *  covert int to 4 length byte 
     * 
     * @param s int
     * @return byte[]
     * */
	public static byte[] intTo4byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);
        targets[1] = (byte) ((res >> 8) & 0xff);
        targets[2] = (byte) ((res >> 16) & 0xff);
        targets[3] = (byte) (res >>> 24);
        return targets;
    }
	
	/**
     *  covert int to 2 length byte 
     * 
     * @param s int
     * @return byte[]
     * */
	public static byte[] intTo2byte(int res) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (res & 0xff);
        targets[1] = (byte) ((res >> 8) & 0xff);
        /*
        targets[2] = (byte) ((res >> 16) & 0xff);
        targets[3] = (byte) (res >>> 24);
        */
        return targets;
    }
	
	public static long bytesToUnsignedInt(byte[] src, int offset) {
		return IoBuffer.wrap(src).order(ByteOrder.LITTLE_ENDIAN).getUnsignedInt();
	}

	public static byte[] intToBytes(int value, int value2, int value3, int value4) {
		byte[] byte_src = new byte[6];
		byte_src[5] = (byte) (value4 & 0xFF);
		byte_src[4] = (byte) ((value3 >> 8) & 0xFF);
		byte_src[3] = (byte) (value3 & 0xFF);
		byte_src[2] = (byte) ((value2 >> 8) & 0xFF);
		byte_src[1] = (byte) (value2 & 0xFF);
		byte_src[0] = (byte) ((value & 0xFF));
		return byte_src;
	}

	public static byte[] irToBytes(int value, int value2, int value3, int value4, int value5) {
		byte[] byte_src = new byte[6];
		byte_src[5] = (byte) (value5 & 0xFF);
		byte_src[4] = (byte) (value4 & 0xFF);
		byte_src[3] = (byte) (value3 & 0xFF);
		byte_src[2] = (byte) (value2 & 0xFF);
		byte_src[1] = (byte) ((value >> 8) & 0xFF);
		byte_src[0] = (byte) ((value & 0xFF));
		return byte_src;
	}

	public static void hexbyte(byte[] data1) {
		byte[] data = new byte[9];
		if (data1 != null) {
			int length = data1.length / 9;
			for (int i = 0; i < length; i++) {
				for (int j = 0; j < 9; j++) {
					data[j] = data1[i * data.length + j];
				}

			}
		}
	}


	public static String printHexString(byte[] b, int len) {
		String ret = "";
		for (int i = 0; i < len; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase() + " ";
		}
		return ret;
	}
	
	public static byte crc(byte[] data)
	{
		byte ret = 0;
	    for (int i = 0; i < data.length; i++)
	        ret ^= data[i];
	    return ret;
	}

}
