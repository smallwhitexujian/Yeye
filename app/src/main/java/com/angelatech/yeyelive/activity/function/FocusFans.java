package com.angelatech.yeyelive.activity.function;

import android.content.Context;

import com.angelatech.yeyelive.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/4/6
 * Time: 17:27
 */
public class FocusFans extends HttpFunction {

    //1 关注 2 粉丝
    public static final int TYPE_FOCUS = 1;
    public static final int TYPE_FANS = 2;

    public static final String FOLLOWED = "1";
    public static final String NO_FOLLOW = "0";

    public FocusFans(Context context) {
        super(context);
    }


    /**
     * 获取是否关注
     */
    public void UserIsFollow(String url,String token,String userid,String tuserid,HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("tuserid", tuserid);
        params.put("userid", userid);
        httpGet(url, params, callback);
    }

    /**
     * 关注/取消关注
     */
    public void UserFollow(String url,String token,String userid,String fuserid,int type,HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("fuserid", fuserid);
        params.put("userid", userid);
        params.put("type", String.valueOf( type));
        httpGet(url, params, callback);
    }

}
