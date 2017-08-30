package com.network.exception;

/**
 * Exception with error code.
 * Created by rd0273 on 2016/4/29.
 */
public class CodedException extends Exception {
    private int mCode;

    public CodedException(int code, String message){
        super(message);
        mCode = code;
    }

    public CodedException(Throwable e){
        super(e);
    }

    public CodedException(String message){
        super(message);
    }

    public CodedException(String message, Throwable e){
        super(message, e);
    }

    public CodedException(int code, String msg, Throwable e) {
        super(msg, e);
        mCode = code;
    }

    public CodedException(int code, Throwable throwable) {
        super(throwable);
        mCode = code;
    }

    public int getMainErrCode(){
        return (mCode / 10000) * 10000;
    }

    public int getSubErrCode(){
        return mCode % 10000;
    }

    public void setErrCode(int code){
        mCode = code;
    }

    public int getErrCode(){
        return mCode;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ", CODE="+mCode;
    }
}