package com.angelatech.yeyelive1.db.model;


import com.angelatech.yeyelive1.db.DBConfig;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 系统消息数据库模型
 */
@DatabaseTable(tableName = DBConfig.TABLE_SYSTEM_MESSAGE)
public class SystemMessageDBModel implements Serializable {

    @DatabaseField(generatedId = true)
    private long id;//

    @DatabaseField(columnName = "type_code")//类别
    public int type_code;
    @DatabaseField(columnName = "data")//内容  msg
    public String data;

    @DatabaseField(columnName = "content")//内容
    public String content;

    @DatabaseField(columnName = "localtime")//本地插入时间
    public long localtime;

    @DatabaseField(columnName = "datetime")//系统推出时间
    public String datetime;

    @DatabaseField(columnName = "uid")//用户的id
    public String uid;//用户id

    @DatabaseField(columnName = "_data")//是否有连接
    public String _data;//固定字段{url,}

    @DatabaseField(columnName = "isread")//是否读取
    public String isread;

    @Override
    public String toString() {
        return "SystemMessageDBModel{" +
                "id=" + id +
                ", type_code=" + type_code +
                ", data='" + data + '\'' +
                ", content='" + content + '\'' +
                ", localtime=" + localtime +
                ", datetime='" + datetime + '\'' +
                ", uid='" + uid + '\'' +
                ", _data='" + _data + '\'' +
                ", isread='" + isread + '\'' +
                '}';
    }
}
