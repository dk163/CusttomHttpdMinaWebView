package com.communication.server.impl;

public class CommandResource {
	public static final byte ERR_SUCCESS = 0x00;//delete NightVison log
	public static final byte ERR_FAIL = 0x01;//delete NightVison log
	public static final byte SYS_CMD_STARTHTTPD = 0x0001;//httpd
	public static final byte SYS_CMD_STARTMTKLOG = 0x0002;//start mtklog
	public static final byte SYS_CMD_STOPMTKLOG = 0x0003;//stop mtklog
	public static final byte SYS_CMD_CLEARMTKLOG = 0x0004;//clear mtklog
	public static final byte SYS_CMD_CLEARLOG = 0x0005;//delete NightVison log
	public static final byte SYS_CMD_PUSHFILE = 0x0006;//push file
}
