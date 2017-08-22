package com.communication.server.data;

/**
 * Created by rd0551 on 2017/7/12.
 */

public class DataBase {
    private int msg_id = -1;

    public DataBase(int msg_id) {
        this.msg_id = msg_id;
    }

    public int getMsg_id() {
        return msg_id;
    }
}
