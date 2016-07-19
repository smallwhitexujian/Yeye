package com.angelatech.yeyelive.activity.function;

import android.content.Context;

import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjfly on 16-3-21.
 */
public class MainEnter extends HttpFunction {


    public MainEnter(Context context) {
        super(context);
    }

    /**
     * 获取所有的房间列表
     */
    public void loadRoomList(String url, String userid, String token, int pageIndex, int pagesize, long time, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        if (time > 0) {
            params.put("datesort", "" + time);
        }
        params.put("pageindex", pageIndex + "");
        params.put("pagesize", pagesize + "");
        httpGet(url, params, callback);
    }

    /**
     * 获取所有的房间列表
     */
    public void loadRoomList(String url, BasicUserInfoDBModel userInfo, int pageIndex, int pagesize, long time, HttpBusinessCallback callback) {
        if(userInfo == null || callback == null){
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        if (time > 0) {
            params.put("datesort", "" + time);
        }
        params.put("pageindex", pageIndex + "");
        params.put("pagesize", pagesize + "");
        httpGet(url, params, callback);
    }

    public void loadUserInfo(String url, String userid, String touserid, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        if (!userid.equals(touserid)) {
            params.put("touserid", touserid);
        }
        params.put("token", token);
        httpGet(url, params, callback);
    }


    //进ChatRoom房间
    public void enterChatRoom(Context context, RoomModel roomModel) {
        preEnterChatRoom(context);
        StartActivityHelper.jumpActivity(context, ChatRoomActivity.class, roomModel);
    }

    public void enterChatRoom(Context context, Map map) {
        preEnterChatRoom(context);
        RoomModel roomModel = new RoomModel();
        StartActivityHelper.jumpActivity(context, ChatRoomActivity.class, roomModel);
    }


    private void preEnterChatRoom(Context context) {
        //关闭以前房间
        closeChatRoom();
    }


    /**
     * 进入挂机房间
     *
     * @param context
     */
    public void enterHookChatRoom(Context context) {

    }

    /**
     * 退出房间
     */
    public void closeChatRoom() {

    }


}




