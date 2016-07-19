package com.angelatech.yeyelive.activity.function;

import android.content.Context;

import com.will.common.tool.DeviceTool;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 手机登录注册
 *
 */
public class PhoneLogin extends Login {


    public PhoneLogin(Context context){
        super(context);
    }

    /**
     * 获取短信验证码
     */
    public void getCode(String url,String phone,HttpBusinessCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone",phone);
        httpGet(url,params,callback);
    }


    public void phoneLogin(String url,String phone, String code,HttpBusinessCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", code);
        params.put("deviceid", DeviceTool.getUniqueID(mContext));
        params.put("sources", SOURCES_ANDROID+"");//android or ios
        httpGet(url,params,callback);
    }


    //添加登录判断
    public String preCallLogin(String phone){
        if(phone == null){

        }

        return null;
    }

}
