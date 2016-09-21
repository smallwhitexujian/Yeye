package com.angelatech.yeyelive.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.db.model.SystemMessageDBModel;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = DBConfig.DB_NAME;
    private static final int DB_VERSION = DBConfig.DB_VERSION;
    private static List<Class<?>> mClasses = new ArrayList<>();

    static {
        mClasses.add(BasicUserInfoDBModel.class);
    }


    private Map<String, Dao> daos = new HashMap();
    private static DatabaseHelper instance;


    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (mClasses == null) {
            throw new RuntimeException();
        }
    }

    private DatabaseHelper(Context context, String DBName, int DBVersion) {
        super(context, DBName, null, DBVersion);
        if (mClasses == null) {
            throw new RuntimeException();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            if (mClasses != null) {
                for (Class clazz : mClasses) {
                    TableUtils.createTable(connectionSource, clazz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            if (oldVersion < 16){
                TableUtils.dropTable(connectionSource,SystemMessageDBModel.class,true);
            }
            if (oldVersion == 16){
                DatabaseUpdateHelper.upgradeTable(database,connectionSource,SystemMessageDBModel.class, DatabaseUpdateHelper.OPERATION_TYPE.ADD);
            }else{
                if (mClasses != null) {
                    for (Class clazz : mClasses) {
                        TableUtils.dropTable(connectionSource, clazz, true);
                    }
                }
                onCreate(database, connectionSource);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static DatabaseHelper open(Context context) {
        synchronized (DatabaseHelper.class) {
            if (instance == null) {
                synchronized (DatabaseHelper.class) {
                    if (instance == null) {
                        instance = new DatabaseHelper(context);
                    }
                }
            }
            return instance;
        }
    }

    public Dao getDao(Class clazz) throws SQLException {
        synchronized (DatabaseHelper.class) {
            Dao dao = null;
            String className = clazz.getSimpleName();

            if (daos.containsKey(className)) {
                dao = daos.get(className);
            }
            if (dao == null) {
                dao = super.getDao(clazz);
                daos.put(className, dao);
            }
            return dao;
        }
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }

}
