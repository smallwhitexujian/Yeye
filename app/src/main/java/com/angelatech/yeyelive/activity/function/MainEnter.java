package com.angelatech.yeyelive.activity.function;

import android.content.Context;

import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjfly on 16-3-21.
 * 房间接口
 */
public class MainEnter extends HttpFunction {
    public MainEnter(Context context) {
        super(context);
    }

    /**
     * 获取所有的房间列表
     */
    public void loadRoomList(String url, BasicUserInfoDBModel userInfo, int pageIndex, int pagesize, long time, int type, HttpBusinessCallback callback) {
        if (userInfo == null || callback == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        params.put("type", String.valueOf(type));
        if (time > 0) {
            params.put("datesort", "" + time);
        }
        params.put("pageindex", pageIndex + "");
        params.put("pagesize", pagesize + "");
        httpGet(url, params, callback);
    }


    /**
     * 获取所有的房间列表
     */
    public void loadRoomList(String url, BasicUserInfoDBModel userInfo, int pageIndex, int pagesize, long time, HttpBusinessCallback callback) {
        if (userInfo == null || callback == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        if (time > 0) {
            params.put("datesort", "" + time);
        }
        params.put("pageindex", pageIndex + "");
        params.put("pagesize", pagesize + "");
        httpGet(url, params, callback);
    }

    /**
     * 获取个人资料
     */
    public void loadUserInfo(String url, String userid, String touserid, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        if (!userid.equals(touserid)) {
            params.put("touserid", touserid);
        }
        params.put("token", token);
        httpGet(url, params, callback);
    }

    /**
     * 排行榜
     */
    public void loadSevenRank(String url, String userid, String token,String roomid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("roomid",roomid);
        httpGet(url, params, callback);
    }
}




