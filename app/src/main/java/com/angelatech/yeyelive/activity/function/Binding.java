package com.angelatech.yeyelive.activity.function;

import android.content.Context;
import android.os.Handler;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.Profile;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 绑定类
 */
public class Binding extends HttpFunction {


    private static final int PREFIX = 0xEF;
    public static final int MSG_BIND_SUCC = PREFIX + 1;
    public static final int MSG_BIND_FAILD = PREFIX + 2;

    public static final int BIND_PHONE = 1;
    public static final int BIND_WEICHAT = 2;
    public static final int BIND_QQ = 3;

    private Handler handler;


    public Binding(Context context, Handler handler) {
        super(context);
        this.handler = handler;
    }

    public void getCode(String url,String phone,HttpBusinessCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone",phone);
        params.put("type","1");
        httpGet(url,params,callback);
    }


    //手机绑定
    public void bindPhone(String userid, String token, String accounts, String code) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("type", BIND_PHONE + "");

        params.put("accounts", accounts);
        params.put("code", code);
        params.put("sources", SOURCES_ANDROID + "");
        bind(CommonUrlConfig.UserBindingAccounts, params, BIND_PHONE);
    }


    //qq绑定
    public void bindQQ(String userid, String token, String accessToken, String openid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("type", BIND_QQ + "");

        params.put("accessToken", accessToken);
        params.put("openid", openid);
        params.put("sources", SOURCES_ANDROID + "");
        bind(CommonUrlConfig.UserBindingAccounts, params, BIND_QQ);
    }


    //微信绑定
    public void bindWeichat(String userid, String token, String wxcode) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("type", BIND_WEICHAT + "");

        params.put("wxcode", wxcode);
        params.put("sources", SOURCES_ANDROID + "");
        System.out.print("=====" + params);
        bind(CommonUrlConfig.UserBindingAccounts, params, BIND_WEICHAT);
    }

    /**
     * facebook 绑定
     * @param userid
     * @param token
     * @param profiles
     */
    public void bindFacebook(String userid, String token, Profile profiles) {
        HashMap<String, String> params = new HashMap<>();
        params.put("fbsid", profiles.getId());
        params.put("code", "");
        params.put("accounts", "");
        params.put("type", "4");
        params.put("userid", userid);
        params.put("token", token);
        bind(CommonUrlConfig.UserBindingAccounts, params, BIND_PHONE);
    }

    private void bind(String url, Map<String, String> params, final int type) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {

            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    Map map = JsonUtil.fromJson(response, Map.class);
                    if (map != null) {
                        String code = (String) map.get("code");
                        String message = (String) map.get("message");
                        if (isSuc(code)) {
                            //正确的结果
                            if (type == BIND_WEICHAT) {

                            }
                            if (type == BIND_QQ) {

                            }
                            if (type == BIND_PHONE) {

                            }
                            handler.obtainMessage(MSG_BIND_SUCC).sendToTarget();
                        } else {
                            // onErrorCode(code);
                            handler.obtainMessage(MSG_BIND_FAILD).sendToTarget();
                        }
                    }
                }
            }
        };
        httpGet(url, params, callback);
    }

}
