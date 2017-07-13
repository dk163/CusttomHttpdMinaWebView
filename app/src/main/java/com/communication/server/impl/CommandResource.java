package com.communication.server.impl;

public class CommandResource {
	public static final String MSG_ID_OBD_DATA = "273";
	public static final String MSG_ID_DOWNLOAD_OTA_DATA = "274";
	
	//kang
	public static final int AMBA_MIFI_STARTSESSION = 0x0320;//mifi 发起,DVR 回复
	public static final int AMBA_MIFI_SET_UPGRADE_FILE_STAT = 0x0321;//DVR 通知,mifi回复
	public static final int AMBA_MIFI_UPLOGD_FILE = 0x0322;//通知MIFI下载碰撞加锁文件
	public static final int AMBA_MIFI_UPLOGD_WAKEUP_FILE = 0x0323;//通知MIFI下载DVR被唤醒录制的小视频
	public static final int AMBA_MIFI_HEARTBEAT = 0x0324;//mifi DVR heartbeat
	public static final int AMBA_MIFI_NOTIFICATION = 0x0325;//唤醒通知,碰撞、4G
}
