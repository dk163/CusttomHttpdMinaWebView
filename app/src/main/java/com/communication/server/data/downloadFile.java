package com.communication.server.data;

/**
 * Created by rd0551 on 2017/8/21.
 */

public class downloadFile extends DataBase{
    private String path = "";
    private String ip = "";
    private String fileName = "";

    public downloadFile(int msg_id, String path, String ip, String fileName) {
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
