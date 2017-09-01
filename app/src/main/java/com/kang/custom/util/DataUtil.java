package com.kang.custom.util;

import android.util.Log;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public final class DataUtil {

	private static final String TAG = "DataUtil";

	public static boolean isNonEmptyByteArray(byte[] from) {
		for (byte b : from) {
			if (b != 0)
				return true;
		}
		return false;
	}

	public static byte[] shortToByte(short value) {
		byte[] result = new byte[2];
		result[0] = (byte) ((value >> 8) & 0xFF);
		result[1] = (byte) (value & 0xFF);
		return result;
	}

	/**
	 * big endian 2 bytes to short.
	 * 
	 */
	// public static short bytesToShort(byte[] bytes) {
	// return (short) (((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF));
	// }

	public static byte[] intToBytes(int value) {
		// ByteBuffer.allocate(4).putInt(yourInt).array();
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

    public static byte[] intToBytes2(int value) {
        // ByteBuffer.allocate(4).putInt(yourInt).array();
        return new byte[] {(byte) (value >>> 8), (byte) value };
    }
    
	public static String bytesToHexString(byte[] data) {
		if (data == null)
			return "";
		StringBuilder s = new StringBuilder(data.length * 2);
		for (byte b : data) {
			s.append(String.format("%02x", b & 0xff));
		}
		return s.toString();
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexStringToBytes(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String bytesToHexString(byte[] src, int len) {
		StringBuilder stringBuilder = new StringBuilder("");
		if ((src == null) || (src.length <= 0) || (len <= 0)) {
			return null;
		}
		for (int i = 0; i < len; ++i) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String md5Hash(String str) {
		byte[] md5Bytes = md5(str.getBytes());
		return bytesToHexString(md5Bytes);
	}

	public static byte[] md5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "md5 error", e);
		}
		return hash;
	}

	/**
	 * big endian 4 bytes to int.
	 * 
	 * @param bytes
	 *            source
	 * @return int result
	 */
	public static int bytesToInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}

	public static short bytesToShort(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getShort();
	}

	public static long bytesToLong(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getLong();
	}

	public static float bytesToFloat(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getFloat();
	}

	public static byte[] read2Byte(byte[] src, int offset) {
		byte[] data = new byte[2];
		System.arraycopy(src, offset, data, 0, 2);
		return data;
	}

	public static byte[] read4Byte(byte[] src, int offset) {
		byte[] data = new byte[4];
		System.arraycopy(src, offset, data, 0, 4);
		return data;
	}

	public static byte[] read3Byte(byte[] src, int offset) {
		byte[] data = new byte[3];
		System.arraycopy(src, offset, data, 0, 3);
		return data;
	}

	public static byte[] readNByte(byte[] src, int offset) {
		int length = src.length;
		byte[] data = new byte[length];
		System.arraycopy(src, offset, data, 0, length);
		return data;
	}

	public static short readShort(byte[] src, int offset) {
		byte[] data = new byte[2];
		System.arraycopy(src, offset, data, 0, 2);
		return bytesToShort(data);
	}

	public static int readInt(byte[] src, int offset) {
		byte[] data = new byte[4];
		System.arraycopy(src, offset, data, 0, 4);
		return bytesToInt(data);
	}

	public static short unsignByteToShort(byte b) {
		byte[] data = new byte[2];
		data[0] = 0;
		data[1] = b;
		return bytesToShort(data);
	}

	public static byte[] doubleToBytes(double value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}

	public static byte[] longToBytes(long value) {
//		byte[] bytes = new byte[8];
//		ByteBuffer.wrap(bytes).putLong(value);
//		return bytes;
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	public static String byteToHex(byte b) {
		return String.format("%02x", b & 0xFF);
	}

	public static byte checksum(byte[] data, int offset) {
		int sum = 0;
		for (int i = offset; i < data.length; i++) {
			sum += (data[i] & 0xFF);
		}
		return (byte) (sum & 0xFF);
	}
	
    /**
     * calculate CRC16 from data[0] to data[DataNum]
     * @param Data
     * @param DataNum
     * @return
     */
    public static byte[] bleCalCrc16(byte[] Data, int DataNum) // 
    {
        int Crc=0, i,j;
        int data;
        byte[] ret = new byte[2];
    	for(j=0; j<DataNum; j++){
    		//Crc = Crc ^ (uint32)*Data++ << 8;
    		data = 0x000000ff & Data[j];
    		Crc = Crc ^ data << 8;    		
    		for(i = 0; i < 8; ++i) {
    	    	if((Crc & 0x08000) != 0){
    				Crc = Crc << 1 ^ 0x01021;
    			} else {
    				Crc = Crc << 1;
    			}
    		}
    	}
        byte[] bytes = intToBytes(Crc & 0x0FFFF);
        System.arraycopy(bytes, 2, ret, 0, 2);
        return ret;
    }

	public static byte[] bigLittleEndianConvert(byte[] data, int len) {
		byte temp;
		for (int i = 0; i < len / 2; i++) {
			temp = data[i];
			data[i] = data[len - 1 - i];
			data[len - 1 - i] = temp;
		}
		return data;
	}
}
