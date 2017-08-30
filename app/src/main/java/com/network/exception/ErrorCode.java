package com.network.exception;

/**
 * Created by rd0273 on 2016/4/29.
 */
public final class ErrorCode {
    public static final int UNKNOWN = -1;
    public static final int NO_ERROR = 0;

    public static int getServiceErrCode(int code) {
        return Service.MAIN_ERR_CODE+ Math.abs(code);
    }

    public static int getServerCode(int code) {
        return Server.MAIN_ERR_CODE+ code;
    }

    /**
     * common error
     */
    public interface Common {
        int MAIN_ERR_CODE = 10000;
        int LOGIC_ERROR = MAIN_ERR_CODE +1;
        int INVALID_PARAM = MAIN_ERR_CODE +2;
    }

    public interface Network {
        int MAIN_ERR_CODE = 20000;
        int NETWORK_DISABLED = MAIN_ERR_CODE + 1;
        int NETWORK_REQUEST_FAILED = MAIN_ERR_CODE + 2;
        int CAN_NOT_CONNECT_TO_SERVER = MAIN_ERR_CODE + 3;
    }

    /**
     * 服务器自身异常:服务器内部错误等未知服务器错误.
     * http code 500 - 599
     */
    public interface Server {
        int MAIN_ERR_CODE = 30000;
        /** 服务器返回值为空 */
        int NO_RESPONSE_DATA = MAIN_ERR_CODE + 1;
        /** 查询数据时返回为空 */
        int RECORD_NOT_FOUND = MAIN_ERR_CODE + 2;
        /** 服务器返回数据有错误,格式不对,缺少数据等.*/
        int INVALID_RESPONSE = MAIN_ERR_CODE + 3;
    }
    /**
     * 服务返回给客户端的异常,也包括服务器自身内部错误.
     */
    public interface Service {
        int MAIN_ERR_CODE = 40000;
        int NO_CONTENT_LENGTH = MAIN_ERR_CODE + 500; //HTTP POST无长度字段Content-Length
        int NO_COOKIES = MAIN_ERR_CODE + 501; //HTTP消息没有cookie字段
        int NO_SESSION_PARAMS = MAIN_ERR_CODE + 502; // HTTP消息没有找到session参数
        int NO_TOKEN = MAIN_ERR_CODE + 503; // HTTP消息没有找到token参数
        int SESSION_EXPIRED = MAIN_ERR_CODE + 504; // HTTP session参数失效
        int INVALID_TOKEN = MAIN_ERR_CODE + 505; //HTTP消息token参数不合法
        int DB_QUERY_FAILURE = MAIN_ERR_CODE + 506; // 数据库查询失败
        int KICKED_BY_SERVER = MAIN_ERR_CODE + 507; //HTTP帐号被踢下线
        int INVALID_URL_PARAMS = MAIN_ERR_CODE + 508; //url其它参数错误
        int REQUIRE_UPDATE = MAIN_ERR_CODE + 512;


        int INVALID_SMS_CODE = MAIN_ERR_CODE + 601;
        int INVALID_VERIFY_CODE = MAIN_ERR_CODE + 603;

        int INVALID_USER_NAME_OR_PASSWORD= MAIN_ERR_CODE + 620;
    }

    public final class Db {
        int MAIN_ERR_CODE = 50000;
        int OPEN_DB_FAILED = MAIN_ERR_CODE + 1;
        int VALIDATE_FAILED = MAIN_ERR_CODE + 2;
        int MODEL_NOT_FOUND= MAIN_ERR_CODE+3;
    }

}
