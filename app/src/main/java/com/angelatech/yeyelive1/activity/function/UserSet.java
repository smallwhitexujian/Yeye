package com.angelatech.yeyelive1.activity.function;

import android.content.Context;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/7/29
 * Time: 15:41
 * 用户设置操作
 */
public class UserSet extends HttpFunction {

    public UserSet(Context context) {
        super(context);
    }

    public void ChangePassword(String userId, String token, String oldPassword,
                               String newPassword, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("token", token);
        params.put("oldpwd", oldPassword);
        params.put("newpwd", newPassword);
        params.put("sources", HttpFunction.SOURCES_ANDROID + "");//android or ios
        httpGet(CommonUrlConfig.ChangePassword, params, callback);
    }
}
