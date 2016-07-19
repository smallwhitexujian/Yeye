package com.framework.socket.exception;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjfly on 15-12-30.
 */
public class SocketException extends Exception {

    /*****
     *
     *  1、socket 参数错误
     *  2、
     *  3、
     *  4、
     *  5、
     *
     */


    public SocketException(String msg){
        super(msg);
    }

    public SocketException(int code){
        super(SOCK_EXCEPTION_MAP.get(code+""));
    }


    //socket
    public static final int SOCK_EXCEPTION_SOCKETCONFIG_ERROR = 1;//socket ip 或者 端口 错误
    public static final int SOCK_EXCEPTION_PROTOCOL_NULL = 2;//协议空
    public static final int SOCK_EXCEPTION_BUSSINESS_NULL = 3;//
    public static final int SOCK_EXCEPTION_CONNECTCOMPONENT_NULL = 4;
    public static final int SOCK_EXCEPTION_ = 3;
    public static final int SOCK_EXCEPTION_6 = 2;
    public static final int SOCK_EXCEPTION_7 = 3;
    public static final int SOCK_EXCEPTION_8 = 2;
    public static final int SOCK_EXCEPTION_9 = 3;

    public static final Map<String,String> SOCK_EXCEPTION_MAP = new HashMap<String,String>();

    static {
        SOCK_EXCEPTION_MAP.put(SOCK_EXCEPTION_SOCKETCONFIG_ERROR+"","socket config error ");
        SOCK_EXCEPTION_MAP.put(SOCK_EXCEPTION_PROTOCOL_NULL+"","protocol null point exception ");
        SOCK_EXCEPTION_MAP.put(SOCK_EXCEPTION_BUSSINESS_NULL+"","");
        SOCK_EXCEPTION_MAP.put(SOCK_EXCEPTION_CONNECTCOMPONENT_NULL+"","");
        SOCK_EXCEPTION_MAP.put("","");
    }


}
