package com.angelatech.yeyelive.util;


import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.dao.CommonDao;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CacheDataManager {

    private static CacheDataManager instance;
    private CommonDao<BasicUserInfoDBModel> mDao;

    private CacheDataManager() {
        mDao = new CommonDao<>(App.sDatabaseHelper, BasicUserInfoDBModel.class);
    }

    public static CacheDataManager getInstance() {
        if (instance == null) {
            synchronized (CacheDataManager.class) {
                if (instance == null) {
                    instance = new CacheDataManager();
                }
            }
        }
        return instance;
    }

    public int save(BasicUserInfoDBModel userInfoDBModel) {
        return mDao.add(userInfoDBModel);
    }


    //
    public BasicUserInfoDBModel loadUser() {
        BasicUserInfoDBModel model = null;
        try {
            model = mDao.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public BasicUserInfoDBModel loadUser(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put(BaseKey.USER_USERID, userId);
        try {
            return mDao.queryByConditionSingle(BaseKey.USER_USERID, true, map);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(String key, Object value, String userId) {
        Map<String, Object> eqs = new HashMap<>();
        eqs.put(BaseKey.USER_USERID, userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put(key, value);
        try {
            mDao.update(eqs, updates);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessageRecord(String userId) {
        Map<String, Object> eqs = new HashMap<>();
        eqs.put(BaseKey.USER_USERID, userId);
        try {
            mDao.delete(eqs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteAll() {
        mDao.deleteAll(BasicUserInfoDBModel.class);
    }

}
