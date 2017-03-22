package com.angelatech.yeyelive1.activity.function;

import android.content.Context;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/6/16
 * Time: 10:07
 * 个人回放 处理函数
 */
public class PlayRecord extends HttpFunction {

    public static final String HAVE_LIVING = "1";
    public static final String HAVE_NO_LIVE = "0";

    public PlayRecord(Context context) {
        super(context);
    }

    /**
     * 获取个人视频
     *
     * @param userId    用户id
     * @param userToken 用户token
     * @param callback  回调
     */
    public void getUserRecord(String userId, String userToken,int pageSize,int pageIndex,
                              HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", userToken);
        params.put("userid", userId);
        params.put("pagesize", String.valueOf(pageSize));
        params.put("pageindex", String.valueOf(pageIndex));
        httpGet(CommonUrlConfig.PersonalVideoList, params, callback);
    }

    /**
     * 获取个人视频
     *
     * @param userId    用户id
     * @param userToken 用户token
     * @param callback  回调
     */
    public void getUserRecord(String userId, String userToken,String touserid,int pageSize,int pageIndex,
                              HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", userToken);
        params.put("userid", userId);
        params.put("touserid",touserid);
        params.put("pagesize", String.valueOf(pageSize));
        params.put("pageindex", String.valueOf(pageIndex));
        httpGet(CommonUrlConfig.PersonalVideoList, params, callback);
    }


    /**
     * 删除播放记录
     *
     * @param userId    用户id
     * @param userToken 用户token
     * @param videoId   视频id
     * @param callback  回调
     */
    public void deleteRecord(String userId, String userToken, int videoId, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", userToken);
        params.put("userid", userId);
        params.put("videoid", String.valueOf(videoId));
        httpGet(CommonUrlConfig.PersonalRecordDel, params, callback);
    }
}
