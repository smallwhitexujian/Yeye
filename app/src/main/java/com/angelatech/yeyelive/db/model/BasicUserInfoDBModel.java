package com.angelatech.yeyelive.db.model;


import com.angelatech.yeyelive.db.DBConfig;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 用户信息数据库
 */

@DatabaseTable(tableName = DBConfig.TABLE_BASIC_USER_INFO)
public class BasicUserInfoDBModel implements Serializable {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;//
    @DatabaseField(columnName = "userid", unique = true)
    public String userid;
    @DatabaseField(columnName = "idx")
    public String idx;
    @DatabaseField(columnName = "token")
    public String token;
    @DatabaseField(columnName = "nickname")
    public String nickname;
    @DatabaseField(columnName = "sex")
    public String sex;
    @DatabaseField(columnName = "headurl")
    public String headurl;
    @DatabaseField(columnName = "sign")
    public String sign;
    @DatabaseField(columnName = "diamonds")
    public String diamonds;
    @DatabaseField(columnName = "fansNum")
    public String fansNum;
    @DatabaseField(columnName = "followNum")
    public String followNum;
    @DatabaseField(columnName = "intimacy")
    public String Intimacy;
    //yeye券
    @DatabaseField(columnName = "voucher")
    public String voucher;

    /**
     * 我的视频
     */
    @DatabaseField(columnName = "videoNum")
    public String videoNum;
    /**
     * 加V标识
     */
    @DatabaseField(columnName = "isv")
    public String isv = "0";

    /**
     * 登录方式
     */
    @DatabaseField(columnName = "loginType")
    public String loginType = "0";
    /**
     * 直播收门票权限
     */
    @DatabaseField(columnName = "isticket")
    public String isticket = "0";

    /**
     * 直播密码房
     */
    @DatabaseField(columnName = "ispwdroom")
    public String ispwdroom = "0";
    /**
     * 邮箱
     */
    @DatabaseField(columnName = "email")
    public String email = "";
    @DatabaseField(columnName = "role")
    public int role = 0; //1 是巡管
    @Override
    public String toString() {
        return "BasicUserInfoDBModel{" +
                "id=" + id +
                ", userid='" + userid + '\'' +
                ", idx='" + idx + '\'' +
                ", token='" + token + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex='" + sex + '\'' +
                ", headurl='" + headurl + '\'' +
                ", sign='" + sign + '\'' +
                ", diamonds='" + diamonds + '\'' +
                ", voucher='" + voucher + '\'' +
                ", fansNum='" + fansNum + '\'' +
                ", followNum='" + followNum + '\'' +
                ", Intimacy='" + Intimacy + '\'' +
                ", videoNum='" + videoNum + '\'' +
                ", isv='" + isv + '\'' +
                ", loginType='" + loginType + '\'' +
                ", ispwdroom='" + ispwdroom + '\'' +
                '}';
    }
}
