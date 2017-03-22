package com.angelatech.yeyelive1.activity.function;

import android.content.Context;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.will.common.tool.DeviceTool;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 手机登录注册
 */
public class PhoneLogin extends Login {


    public PhoneLogin(Context context) {
        super(context);
    }

    /**
     * 获取短信验证码
     */
    public void getCode(String url, String phone, HttpBusinessCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        httpGet(url, params, callback);
    }

    /**
     * 验证码登录
     *
     * @param url
     * @param phone
     * @param code
     * @param callback
     */
    public void phoneLogin(String url, String phone, String code, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", code);
        params.put("deviceid", DeviceTool.getUniqueID(mContext));
        params.put("sources", SOURCES_ANDROID + "");//android or ios
        httpGet(url, params, callback);
    }

    /**
     * 账号密码登录
     *
     * @param phone
     * @param password
     * @param callback
     */
    public void loginPwd(String phone, String password, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password", password);
        params.put("deviceid", DeviceTool.getUniqueID(mContext));
        params.put("sources", SOURCES_ANDROID + "");//android or ios
        httpGet(CommonUrlConfig.LoginPwd, params, callback);
    }

    /**
     * 找回密码
     *
     * @param phone    手机
     * @param code     验证码
     * @param password 密码
     * @param callback 回调
     */
    public void findPassword(String phone, String code, String password, HttpBusinessCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", code);
        params.put("password", password);
        httpGet(CommonUrlConfig.FindPassword, params, callback);
    }

    public void setPayPwd(String phone, String code, String password,String userid, String token,HttpBusinessCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userid);
        params.put("token", token);
        params.put("payPassword", password);
        params.put("verifycode",code);
        params.put("phone",phone);
        httpGet(CommonUrlConfig.UpdatePayPassword, params, callback);
    }
}
