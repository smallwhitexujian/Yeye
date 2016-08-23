package com.angelatech.yeyelive;


import java.util.UUID;

public class Contants {
    public static String appkey = "20acf824f7afc38";
    public static String appsecret = "320fdb32a78149dc9fa15edc6b8c16a8";
    public static String accessToken;//accessToken 通过调用授权接口得到
    public static final String LIVE_URL="http://yeyelive.s.qupai.me";
    public static final String space = UUID.randomUUID().toString().replace("-",""); //存储目录 建议使用用户ID(uid)之类的信息
}
