package com.angelatech.yeyelive.db;


import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.db.model.SystemMessageDBModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjfly on 16-5-31.
 * 数据库配置
 */
public class DBConfig {

    //db_name
    public static final String DB_NAME = "yeye.db";
    //db_version
    public static final int DB_VERSION = 17;

    //table_name
    public static final String TABLE_BASIC_USER_INFO = "CacheUser";
    public static final String TABLE_SYSTEM_MESSAGE = "SystemMessage";


    //table class list
    public static final List<Class<?>> DB_CLASSES = new ArrayList<>();

    static {
        DB_CLASSES.add(BasicUserInfoDBModel.class);
        DB_CLASSES.add(SystemMessageDBModel.class);
    }

}
