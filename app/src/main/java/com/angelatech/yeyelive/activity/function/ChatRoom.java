package com.angelatech.yeyelive.activity.function;

import android.content.Context;
import android.content.Intent;

import com.angelatech.yeyelive.model.RoomModel;
import com.will.common.string.Encryption;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shanli_pc on 2016/3/16.
 */
public class ChatRoom extends HttpFunction {


    public ChatRoom(Context context) {
        super(context);
    }

    private static void preEnterChatRoom(Context context) {
        //关闭以前房间
        closeChatRoom();
    }

    //进ChatRoom房间
    public static void enterChatRoom(Context context, RoomModel roomModel) {

        preEnterChatRoom(context);
//        Intent intent = new Intent(context, ChatRoomActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("room", roomModel);
//        intent.putExtras(bundle);
        StartActivityHelper.jumpActivity(context, ChatRoomActivity.class, roomModel);
        // context.startActivity(intent);
    }

    /**
     * 进入挂机房间
     * @param context
     */
    public static void enterHookChatRoom(Context context) {
        if (App.chatRoomApplication != null) {
            Intent intent = new Intent(context, App.chatRoomApplication.getClass());
            context.startActivity(intent);
        }
    }

    /**
     * 退出房间
     */
    public static void closeChatRoom() {
        if (App.chatRoomApplication != null) {
            try {
                App.chatRoomApplication.finish();
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取礼物列表
     */
    public void loadGiftList(String url, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
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

    /**
     * 开播前拿一些需要的信息
     */
    public void LiveVideoBroadcast(String url, BasicUserInfoDBModel userInfo, String introduce, String area, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        params.put("introduce", Encryption.utf8ToUnicode(introduce));
        params.put("area", Encryption.utf8ToUnicode(area));
        httpGet(url, params, callback);
    }

    /**
     * 观看录播计数
     */
    public void ClickToWatch(String url, BasicUserInfoDBModel userInfo, String vid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        params.put("vid", vid);
        httpGet(url, params, callback);
    }

    /**
     * 保存直播录像
     */
    public void LiveQiSaveVideo(String url, BasicUserInfoDBModel userInfo, String liveid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        params.put("liveid", liveid);
        httpGet(url, params, callback);
    }
}
