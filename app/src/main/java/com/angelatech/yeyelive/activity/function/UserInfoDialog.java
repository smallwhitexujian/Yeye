package com.angelatech.yeyelive.activity.function;

import android.content.Context;

import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class UserInfoDialog extends UserControl{


    public static final String HAVE_FOLLOW = "1";
    public static final String HAVE_NO_FOLLOW = "0";

    public static final String HAVE_NOTICE = "1";
    public static final String HAVE_NO_NOTICE = "0";

    public UserInfoDialog(Context context) {
        super(context);
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

    public void userNoticeEdit(String url,String token,String userid,String touserid,HttpBusinessCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("touserid",touserid);
        httpGet(url, params, callback);
    }


    /**
     * 获取是否关注
     */
    public void UserIsFollow(String url,String token,String userid,String tuserid,HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("touserid", tuserid);
        params.put("userid", userid);
        httpGet(url, params, callback);
    }

    public void getBillList(String url,String token,String userid,String pagesize,String pageindex,HttpBusinessCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put("userId", userid);
        params.put("token", token);
        params.put("pagesize", pagesize);
        params.put("pageindex", pageindex);
        httpGet(url,params,callback);
    }

    public boolean isFollow(String followCode){
        return !HAVE_NO_FOLLOW.equals(followCode);
    }

    public boolean isNotice(String noticeCode){
        return !HAVE_NO_NOTICE.equals(noticeCode);
    }



}
