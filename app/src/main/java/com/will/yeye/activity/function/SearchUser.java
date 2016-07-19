package com.will.yeye.activity.function;

import android.content.Context;

import com.will.yeye.CommonUrlConfig;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 搜索用户
 */
public class SearchUser extends FocusFans {

    public SearchUser(Context context) {
        super(context);
    }

    public void searchUser(String userid, String token, String key, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("search", key);
        params.put("token", token);
        httpGet(CommonUrlConfig.UserSearch, params, callback);
    }

}
