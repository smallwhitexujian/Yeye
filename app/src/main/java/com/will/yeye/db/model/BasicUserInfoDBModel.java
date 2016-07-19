package com.will.yeye.db.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 用户信息数据库
 */

@DatabaseTable(tableName = "CacheUser")
public class BasicUserInfoDBModel implements Serializable {

    @DatabaseField(columnName="id",generatedId=true)
    private int id;//

    @DatabaseField(columnName="userid",unique=true)
    public String userid;
    @DatabaseField(columnName="idx")
    public String idx;
    @DatabaseField(columnName="token")
    public String token;
    @DatabaseField(columnName="nickname")
    public String nickname;
    @DatabaseField(columnName="sex")
    public String sex;
    @DatabaseField(columnName="headurl")
    public String headurl;
    @DatabaseField(columnName="sign")
    public String sign;
    @DatabaseField(columnName="diamonds")
    public String diamonds;
    @DatabaseField(columnName="fansNum")
    public String fansNum;
    @DatabaseField(columnName="followNum")
    public String followNum;

    @DatabaseField(columnName="intimacy")
    public String Intimacy;


}
