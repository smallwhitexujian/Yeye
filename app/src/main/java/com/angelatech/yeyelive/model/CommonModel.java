package com.angelatech.yeyelive.model;

import java.io.Serializable;

/**
 * Created by xujian on 15/8/26.
 */
public class CommonModel implements Serializable  {
    public String code="-1";     //状态码
    public String message;  //错误消息
    public String msg;      //消息
    public long time;       //时间
    public int index;//页码
    public String pernumber;//排行榜金币
    public String perimageurl;//排行榜头像

    @Override
    public String toString() {
        return "CommonModel{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", msg='" + msg + '\'' +
                ", time=" + time +
                '}';
    }
}
