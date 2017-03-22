package com.angelatech.yeyelive1.model;

import java.util.List;

/**
 * User: cbl
 * Date: 2016/6/16
 * Time: 11:33
 */
public class CommonVideoModel<T, K> {

    public String code = "-1";     //状态码
    public String message;  //错误消息
    public String msg;      //消息
    public long time = 0;       //时间
    public int index;//页码
    public int type = 0;

    public List<T> livedata;
    public List<K> videodata;

    @Override
    public String toString() {
        return "CommonVideoModel{" +
                "livedata=" + livedata +
                ", videodata=" + videodata +
                '}';
    }
}
