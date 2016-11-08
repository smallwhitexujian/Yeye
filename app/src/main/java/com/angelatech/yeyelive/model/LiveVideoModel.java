package com.angelatech.yeyelive.model;

import java.io.Serializable;

/**
 * User: cbl
 * Date: 2016/6/16
 * Time: 14:02
 * 热门列表
 */
public class LiveVideoModel implements Serializable {

    public static final int TYPE_LIVE = 1;
    public static final int TYPE_RECORD = 2;

    public String userid;//用户id
    public String nickname;//昵称
    public String headurl;//头像
    public String introduce;
    public String area;
    public String isv = "0";  //加V 标识
    public String barcoverurl; //封面
    public int type = TYPE_LIVE; //1 是直播 2 是录像
    public String sex = "0";
    public String password;
    public String ispwdroom;
    public String isticket = "0";//是否有门票
    public String ticketprice = "0";
}
