package com.angelatech.yeyelive.model;


/**
 * Created by jjfly on 16-3-3.
 */

public class BasicUserInfoModel{


    /***
     *    {
     "userid": "10000",
     "idx": "6000105",
     "token": "B33BA99BCD73E735D5561953898C909E",
     "nickname": "bee1458656521",
     "sex": "0",
     "headurl": "",
     "sign": "",
     "diamonds": "0",
     "fansNum": "0",
     "followNum": "0",
     "Intimacy": "0"
     }
     */


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
