package com.angelatech.yeyelive.activity.function;

import android.content.Context;
import android.os.Handler;

import com.angelatech.yeyelive.model.LoginServerModel;
import com.facebook.Profile;
import com.google.gson.reflect.TypeToken;
import com.will.common.string.Encryption;
import com.will.common.string.json.JsonUtil;
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.activity.LoginActivity;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/1/8
 * Time: 13:10
 */
public class Register extends Login {
    private Handler handler;
    private final static int BASE_REGISTER = 0X39;
    public final static int REGISTER_SUCCESS = BASE_REGISTER + 1;
    public final static int REGISTER_ERROR = BASE_REGISTER + 2;
    private int maxRequest = 5;


    public Register(Context context,Handler handler) {
        super(context);
        this.handler = handler;
    }

    /**
     * 微信注册
     *
     * @param code 微信授权码
     */
    public void wxRegister(String code, String deviceid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("deviceid", deviceid);
        params.put("sources", SOURCES_ANDROID + "");
        register(CommonUrlConfig.weixinRegister, params);
    }

    /**
     * qq 注册
     *
     * @param accessToken toekn
     * @param openid      openid
     */
    public void qqRegister(String accessToken, String openid, String deviceid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("openid", openid);
        params.put("deviceid", deviceid);
        params.put("sources", SOURCES_ANDROID + "");
        register(CommonUrlConfig.qqRegister,params);
    }
    /**
     * facebook 注册
     */
    public void fbRegister(Profile profiles, String deviceid ) {
        HashMap<String, String> params = new HashMap<>();
        params.put("fbid", profiles.getId());
        params.put("deviceid", deviceid);
        params.put("nickname", Encryption.utf8ToUnicode(profiles.getName()));
        params.put("headurl", Encryption.utf8ToUnicode(profiles.getProfilePictureUri(100, 100).toString()));
        params.put("sex","1");
        params.put("sources", "2");
        register(CommonUrlConfig.facebookLogin,params);
    }

    private void register(String url,HashMap<String, String> params){
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    CommonParseListModel<BasicUserInfoDBModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonParseListModel<BasicUserInfoDBModel>>() {
                    }.getType());
                    if (datas != null) {
                        String code = datas.code;
                        if (isSuc(code)) {
                            //正确的结果
                            BasicUserInfoDBModel model = datas.data.isEmpty() ? null : datas.data.get(0);
                            long id = model == null ? -1 : CacheDataManager.getInstance().save(model);
                            if (id != -1) {
                                try {
                                    LoginServerModel loginServerModel = new LoginServerModel(Long.valueOf(model.userid),model.token);
                                    attachIM(loginServerModel);
                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                }
                                handler.obtainMessage(LoginActivity.MSG_LOGIN_SUCC).sendToTarget();
                            } else {
                                handler.obtainMessage(LoginActivity.MSG_LOGIN_ERR).sendToTarget();
                            }
                        } else {
                            onBusinessFaild(code);
                        }
                    }
                }
            }
        };
        httpGet(url, params, callback);
    }

}
