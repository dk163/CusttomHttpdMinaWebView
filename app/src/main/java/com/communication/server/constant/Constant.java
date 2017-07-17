package com.communication.server.constant;

public class Constant {
//	private static  String CIM_SERVER_IP = "192.168.42.1";
	public  static   String HEARTBEAT_KEY = "heartbeat";
	public  static String SESSION_KEY = "account";
//	private static  int CIM_SERVER_PORT1 = 7878;
//	private static  int CIM_SERVER_PORT2 = 8787;
//	private static  int CIM_CLIENT_PORT1 = 7878;
//	private static  int CIM_CLIENT_PORT2 = 8787;

	public  static int MINA_PORT = 8081;
	public  static int HTTPD_PORT = 8080;
	public  static String MINA_IP = "192.168.43.1";
	public  static String HTTPDIPPORT="http://192.168.43.1:8080";

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