package com.angelatech.yeyelive1.model;


import java.io.Serializable;

/**
 * Created by jjfly on 16-3-3.
 * 用户的个人信息
 */

public class BasicUserInfoModel implements Serializable {
    public String Userid;
    public String Token;
    public String headurl;
    public String nickname;

    public String userlevel;
    public String vip;

    //
    public String sex;
    public String birthday;
    public String coin;
    public String diamonds;
    public String viplevel;
    public String isfollow;
    public boolean isout = false;
    public String isv = "0";
    public int role = 0; //1 是巡管
    @Override
    public String toString() {
        return "BasicUserInfoModel{" +
                "Userid='" + Userid + '\'' +
                ", Token='" + Token + '\'' +
                ", headurl='" + headurl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userlevel='" + userlevel + '\'' +
                ", vip='" + vip + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", coin='" + coin + '\'' +
                ", diamonds='" + diamonds + '\'' +
                ", viplevel='" + viplevel + '\'' +
                '}';
    }
}
