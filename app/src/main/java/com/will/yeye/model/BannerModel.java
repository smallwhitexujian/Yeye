package com.will.yeye.model;

/**
 * Author: jjfly
 * Since: 2016年05月06日 10:47
 * Desc: banner 模型
 * FIXME:
 */
public class BannerModel<T> {

    public static final String TYPE_WEB = "0";

    public String extype;
    public String imageurl;
    public String url;
    public T extra;


    @Override
    public String toString() {
        return "BannerModel{" +
                "extype='" + extype + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", url='" + url + '\'' +
                ", extra=" + extra +
                '}';
    }
}
