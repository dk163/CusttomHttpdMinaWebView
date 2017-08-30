package com.network.exception;

/**
 * Created by rd0273 on 2016/4/29.
 */
public class NetworkException extends CodedException {
    public NetworkException(String message, Throwable e) {
        super(ErrorCode.Network.MAIN_ERR_CODE, message, e);
    }
}
