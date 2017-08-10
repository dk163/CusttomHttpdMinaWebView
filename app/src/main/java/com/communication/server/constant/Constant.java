package com.communication.server.constant;

public class Constant {
	public  static   String HEARTBEAT_KEY = "heartbeat";
	public  static String SESSION_KEY = "account";

	public  static int MINA_PORT = 12345;
	public  static int HTTPD_PORT = 8080;
	public  static String MINA_IP = "192.168.43.1";
	public  static String HTTPDIPPORT="http://192.168.43.1:8080/sdcard/";
//	public  static String MINA_IP = "127.0.0.1";
//	public  static String HTTPDIPPORT="http://127.0.0.1:8080";

	public static final String CMD_CONNECT_SERVER = "{\"msg_id\":1}";
	public static final String CMD_START_MTKLOG = "{\"msg_id\":2}";
	public static final String CMD_STOP_MTKLOG = "{\"msg_id\":3}";
	public static final String CMD_CLEAR_MTKLOG = "{\"msg_id\":4}";
	public static final String CMD_CLEAR_LOG= "{\"msg_id\":5}";

	public static final String ACTION_MTKLOG = "com.mediatek.mtklogger.ADB_CMD";

	public static void setPORT(int PORT) {
		Constant.MINA_PORT = PORT;
	}

	public static void setHttpdPort(int httpdPort) {
		HTTPD_PORT = httpdPort;
	}

	public static void setIP(String IP) {
		Constant.MINA_IP = IP;
	}

	public static void setHTTPIPPORT(String HTTPIPPORT) {
		Constant.HTTPDIPPORT = HTTPIPPORT;
	}
}