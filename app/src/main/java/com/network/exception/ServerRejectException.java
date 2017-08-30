package com.network.exception;

/**
 * exception caused by server reject client's request for some reason.
 * Created by rd0273 on 2016/4/29.
 */
public class ServerRejectException extends CodedException{
    public ServerRejectException(int code, String message) {
        super(code, message);
    }

    public ServerRejectException(int code, String msg, Throwable e) {
        super(code, msg, e);
    }

    public ServerRejectException(int code, Throwable throwable) {
        super(code, throwable);
    }
}
