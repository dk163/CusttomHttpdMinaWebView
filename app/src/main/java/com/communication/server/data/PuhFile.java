package com.communication.server.data;

/**
 * Created by rd0551 on 2017/8/21.
 */

public class PuhFile extends DataBase{
    String path = "";
    String ip = "";
    String fileName = "";

    public PuhFile(int msg_id, String path, String ip, String fileName) {
        super(msg_id);
        this.path = path;
        this.ip = ip;
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public String getIp() {
        return ip;
    }

    public String getFileName() {
        return fileName;
    }
}
