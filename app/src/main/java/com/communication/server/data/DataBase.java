package com.communication.server.data;

/**
 * Created by rd0551 on 2017/7/12.
 */

public class DataBase {
    private int msg_id = -1;
    private int token = -1;
    private int rval = -1;
    private String type = "";

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public int getRval() {
        return rval;
    }

    public void setRval(int rval) {
        this.rval = rval;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
