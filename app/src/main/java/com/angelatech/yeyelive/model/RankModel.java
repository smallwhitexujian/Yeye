package com.angelatech.yeyelive.model;

import java.io.Serializable;

/**
 *
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 *
 *
 * 作者: Created by: xujian on Date: 16/9/22.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.model
 */

public class RankModel implements Serializable{
    public String num;          //排名?
    public String id;           //用户id
    public String imageurl;     //用户头像
    public String name;         //用户昵称
    public String sex;          //用户性别
    public String number;       //用户奉献的金币
    public String isv;          //是否加V

    @Override
    public String toString() {
        return "RankModel{" +
                "num='" + num + '\'' +
                ", id='" + id + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", number=" + number +
                '}';
    }
}
