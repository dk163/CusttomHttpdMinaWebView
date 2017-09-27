package com.communication.server.data;

/**
 * Created by kang on 2017/9/26.
 */

public class SystemInfo extends DataBase{

    private String sysVer = "nodefine";
    private String imei = "1234567890";

    public SystemInfo(int msg_id, String sysVer, String imei) {
        super(msg_id);
        this.sysVer = sysVer;
        this.imei = imei;
    }

    public String getSysVer() {
        return sysVer;
    }

    public String getImei() {
        return imei;
    }
}
