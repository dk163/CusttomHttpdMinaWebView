package com.network.exception;

/**
 * server internal exception,eg: server not running,  no response, return bad data...
 * Created by rd0273 on 2016/4/29.
 */
public class ServerInternalException extends CodedException {

    public ServerInternalException(int code, String message) {
        super(code, message);
    }

    public ServerInternalException(int code, String msg, Throwable e) {
        super(code, msg, e);
    }

    public ServerInternalException(int code, Throwable throwable) {
        super(code, throwable);
    }
}
