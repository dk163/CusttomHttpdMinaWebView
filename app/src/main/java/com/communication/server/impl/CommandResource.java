package com.communication.server.impl;

public class CommandResource {
	public static final byte ERR_FAIL = 0x0000;
	public static final byte SYS_CMD_STARTHTTPD = 0x0001;//httpd
	public static final byte SYS_CMD_STARTMTKLOG = 0x0002;//start mtklog
	public static final byte SYS_CMD_STOPMTKLOG = 0x0003;//stop mtklog
	public static final byte SYS_CMD_CLEARMTKLOG = 0x0004;//clear mtklog
	public static final byte SYS_CMD_CLEARLOG = 0x0005;//delete NightVison log
	public static final byte SYS_CMD_PUSHFILE = 0x0006;//push file
	public static final byte SYS_CMD_ZIPMTKLOG = 0x0007;//zip mtk log
	public static final byte SYS_CMD_ZIPLOG = 0x0008;//zip log
	public static final byte SYS_CMD_SYSINFO = 0x0009;//system info
}
