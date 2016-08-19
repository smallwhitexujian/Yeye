package com.angelatech.yeyelive.model;

/**
 * User: cbl
 * Date: 2016/6/16
 * Time: 13:31
 * 直播 model
 */
public class LiveModel extends LiveVideoModel {

    public String roomid;//房间id
    public String roomidx;//房间idx
    public String onlinenum;//在线
    public String roomserverip;//开播地址
    public String livestate;
    public String isticket = "0";//是否有门票
    public String rtmpserverip;
    public String os;

}
