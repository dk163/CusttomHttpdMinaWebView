package com.communication.server.data;

/**
 * Created by kang on 2017/8/31.
 */

public class AppData extends DataBase{
    private String version = "null";

    public AppData(int msg_id) {
        super(msg_id);
    }

    public AppData(int msg_id, String version) {
        super(msg_id);
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
