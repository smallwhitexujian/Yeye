package com.will.yeye.service;

/**
 *
 *
 *
 *
 */
public class IServiceValues {

    private static final String CMD_PREFIX = "com.will.maracuyalive.ISERVICE_CMD_";
    private static final int CMD_VALUE_PREFIX = 0xff;

    //向service发送命令类型 action 定义（ACTION_CMD_name1_name2...)
    public static final String ACTION_CMD_WAY = CMD_PREFIX+"WAY";
    public static final String ACTION_CMD_TEST = CMD_PREFIX+"TEST";


    //命令码
    public static final int CMD_LOGIN = CMD_VALUE_PREFIX+1;//im登录
    public static final int CMD_EXIT_LOGIN = CMD_VALUE_PREFIX+2;//im推出
    public static final int CMD_ACCOUNT = CMD_VALUE_PREFIX+3;//统计
    public static final int CMD_KICK_OUT = CMD_VALUE_PREFIX + 4;//踢出


    /*************************
     * key 部分
     *********************/
    //获取命令码的key
    public static final String KEY_BROADCAST_CMD_VALUE = "cmd";
    //获取签名的key
    public static final String KEY_BROADCAST_SIGN = "sign";

}
