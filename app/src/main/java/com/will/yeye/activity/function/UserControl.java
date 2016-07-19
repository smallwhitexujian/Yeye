package com.will.yeye.activity.function;

import android.content.Context;

import com.will.yeye.CommonUrlConfig;
import com.will.yeye.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;

/**
 *  黑名单操作
 */
public class UserControl extends HttpFunction {

   // 拉黑
    public static final int PULL_TO_BLACKLIST  = 2;
    //取消拉黑
    public static final int REMOVE_FROM_BLACKLIST = 3;


    public static final int SOURCE_REPORT = 0;//默认

    public UserControl(Context context){
        super(context);
    }

    //加入取消黑名单
    public void ctlBlacklist(String userid,String token,String touserid,int type,HttpBusinessCallback httpCallback){
        HashMap<String, String> map = new HashMap<>();
        map.put("userid",userid);
        map.put("token", token);
        map.put("touserid",touserid);
        map.put("type",type+"");
        httpGet(CommonUrlConfig.UserPullBlack, map, httpCallback);
    }

    //拉黑列表
    public void loadBlacklist(String userid, String token, long dateSort, int pageIndex, int pagesize, HttpBusinessCallback httpCallback){
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("userid",userid);
        if (dateSort > 0) {
            map.put("datesort", String.valueOf(dateSort));
        }
        map.put("pageindex", String.valueOf(pageIndex));
        map.put("pagesize", String.valueOf(pagesize));
        httpGet(CommonUrlConfig.FriendBlacklist, map, httpCallback);
    }


    //用户举报
    public void report(String userid,String token,String source,String sourceid,String content,HttpBusinessCallback httpCallback){
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("userid",userid);
        map.put("source", source);
        map.put("sourceid",sourceid);
        map.put("content",content);
        httpGet(CommonUrlConfig.BarReport, map, httpCallback);
    }



}
