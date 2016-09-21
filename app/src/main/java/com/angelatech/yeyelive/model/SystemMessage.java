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
 * 作者: Created by: xujian on Date: 16/9/21.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.model
 */

public class SystemMessage {
    private CommonDao<SystemMessageDBModel> commonDao ;

    public SystemMessage(){
        commonDao = new CommonDao<>(App.sDatabaseHelper,SystemMessageDBModel.class);
    }


    public void add(SystemMessageDBModel systemMessageDBModel){
        commonDao.add(systemMessageDBModel);
    }

    public List<SystemMessageDBModel> load(String userId, long startRow, long maxRows){
        Map<String,Object> eqs = new HashMap<>();
        eqs.put("uid",userId);
        String orderByKey = "localtime";
        try {
            return commonDao.queryByConditionLimit(orderByKey,false,eqs,startRow,maxRows);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean haveNewSystemMsg(Context context){
        SPreferencesTool sp = SPreferencesTool.getInstance();
        int flag = sp.getIntValue(context,SPreferencesTool.SharedPreferencesConfigs.PREFERENCES_SYSTEM_MSG);
        if(flag == -1){
            return false;
        }
        return true;
    }

    public void addUnReadTag(Context context){
        SPreferencesTool sp = SPreferencesTool.getInstance();
        sp.putValue(context, SPreferencesTool.SharedPreferencesConfigs.PREFERENCES_SYSTEM_MSG,1);
    }

    public void clearUnReadTag(Context context){
        SPreferencesTool sp = SPreferencesTool.getInstance();
        sp.putValue(context, SPreferencesTool.SharedPreferencesConfigs.PREFERENCES_SYSTEM_MSG,-1);
    }
}
