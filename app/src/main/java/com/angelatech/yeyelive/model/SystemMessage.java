package com.angelatech.yeyelive.model;

import android.content.Context;

import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.dao.CommonDao;
import com.angelatech.yeyelive.db.model.SystemMessageDBModel;
import com.angelatech.yeyelive.util.SPreferencesTool;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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
 * <p>
 * <p>
 * 作者: Created by: xujian on Date: 16/9/21.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.model
 */

public class SystemMessage {
    private CommonDao<SystemMessageDBModel> commonDao;
    private static SystemMessage instance;
    public static SystemMessageDBModel systemMessageDBModel = null;

    public SystemMessage() {
        commonDao = new CommonDao<>(App.sDatabaseHelper, SystemMessageDBModel.class);
    }

    public static SystemMessage getInstance() {
        if (instance == null) {
            synchronized (SystemMessage.class) {
                if (instance == null) {
                    instance = new SystemMessage();
                }
            }
        }
        return instance;
    }

    public void add(SystemMessageDBModel systemMessageDBModel) {
        commonDao.add(systemMessageDBModel);
    }

    public List<SystemMessageDBModel> load(String type_code, long startRow, long maxRows) {
        Map<String, Object> eqs = new HashMap<>();
        eqs.put("type_code", type_code);
        String orderByKey = "localtime";
        try {
            return commonDao.queryByConditionLimit(orderByKey, false, eqs, startRow, maxRows);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //更新某一列数据
    public void update(String key, String value, String uid) {
        try {
            Map<String, Object> eqs = new HashMap<>();
            eqs.put("uid", uid);
            commonDao.updateName(key, value, eqs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //查询这个消息是否已读
    public List<SystemMessageDBModel> getQuerypot(String key, String uid, String type_code) {
        try {
            Map<String, Object> eqs = new HashMap<>();
            eqs.put("uid", uid);
            eqs.put("type_code", type_code);
            eqs.put("isread", 0);
            return commonDao.queryByCondition(key, true, eqs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public SystemMessageDBModel getQueryForFirst() {
        if (systemMessageDBModel == null) {
            try {
                systemMessageDBModel = commonDao.queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return systemMessageDBModel;
    }

    public boolean haveNewSystemMsg(Context context) {
        SPreferencesTool sp = SPreferencesTool.getInstance();
        int flag = sp.getIntValue(context, SPreferencesTool.SharedPreferencesConfigs.PREFERENCES_SYSTEM_MSG);
        if (flag == -1) {
            return false;
        }
        return true;
    }

    public void addUnReadTag(Context context) {
        SPreferencesTool sp = SPreferencesTool.getInstance();
        sp.putValue(context, SPreferencesTool.SharedPreferencesConfigs.PREFERENCES_SYSTEM_MSG, 1);
    }

    public void clearUnReadTag(Context context) {
        SPreferencesTool sp = SPreferencesTool.getInstance();
        sp.putValue(context, SPreferencesTool.SharedPreferencesConfigs.PREFERENCES_SYSTEM_MSG, -1);
    }
}
