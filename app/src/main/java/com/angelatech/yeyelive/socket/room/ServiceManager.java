package com.angelatech.yeyelive.socket.room;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.angelatech.yeyelive.GlobalDef;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.service.IService;
import com.framework.socket.model.SocketConfig;
import com.will.common.log.DebugLogs;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;

/**
 * Created by Shanli_pc on 2016/3/16.
 */
public class  ServiceManager {
    private IService bindService;
    private Activity activity;

    private int barId;
    private SocketConfig socketConfig;
    private Handler handler;
    private BasicUserInfoDBModel userModel ;

    public ServiceManager(Activity activity, SocketConfig socketConfig,int barId,Handler handler,BasicUserInfoDBModel userModel) {
        this.activity = activity;
        this.handler = handler;
        this.socketConfig = socketConfig;
        this.barId = barId;
        this.userModel = userModel;
        bindService();
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Intent intent = new Intent(activity, IService.class);
        intent.setAction(App.SERVICE_ACTION);
        activity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑
     */
    public void unBindService() {
       if(bindService != null) {
           activity.unbindService(conn);
       }
    }

    /**
     * 连接服务状态
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            DebugLogs.e("server----->onServiceDisconnected()");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DebugLogs.e("server----->onServiceConnected()");
            IService.MyBinder binder = (IService.MyBinder) service;
            bindService = binder.getIService();
            connectionService();
        }
    };

    /**
     * 连接服务
     */
    public void connectionService() {
        if (bindService != null && socketConfig != null) {
            bindService.setRoomHander(handler);
            bindService.startRoomConnection(socketConfig, barId, Integer.parseInt( userModel.userid), userModel.token);
        } else {
            handler.obtainMessage(GlobalDef.WM_ROOM_LOGIN_OUT).sendToTarget();
        }
    }

    //主播上麦
    public void sendRTMP_WM_SDP(String uri,String offer)
    {
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_SDP, "{\"type\":\"rtmp\",\"uri\":\"" +
                    uri + "\",\"offer\":\"" + offer + "\"}");
        }
    }

    /**
     * @param index    麦序
     * @param liveUrl  地址
     * @param isPuller 是否抱麦
     */
    public void upMic(int index, String liveUrl, boolean isPuller, String userIdx) {
        if (!isPuller) {
            userIdx = "0"; //自己上麦
        }
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_SHOWMIC, "{\"micorder\":" + index + ",\"puller\":" + userIdx + ",\"url\":\"" + liveUrl + "\"}");
        }
    }

    /**
     * 抱麦
     *
     * @param userIdx userIdx
     */
    public void hugMic(String userIdx) {
        String jsonStr = "{\"to\":" + userIdx + "}";
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_OTHERMIC, jsonStr);
        }
    }

    /**
     * 下麦
     */
    public void downMic() {
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_LIVE_STOP, "");
        }
    }

    /**
     * 发喇叭
     *
     * @param message 喇叭
     */
    public void sendRadioBroadcast(String message) {
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_OUT_RadioBroadcast, "{\"message\":\"" + message + "\"}");
        }
    }

    /**
     * 自由上麦
     * @param message 1允许 0禁止
     */
    public void sendFreeWheatSetting(String message){
        if (bindService != null){
            bindService.sendMessage(GlobalDef.WM_SETTING_FREEWHEAT,"{\"allow\":" + message + "}");
        }
    }

    /**
     * 发送聊天消息
     *
     * @param message 消息
     */
    public void sendRoomMessage(String message) {
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_MESSAGE, "{\"Message\":\"" + message + "\"}");
        }
    }

    /**
     * 发送礼物
     *
     * @param toId   接收人
     * @param giftId 礼物id
     * @param number 数量
     */
    public void sendGift(int toId, int giftId, int number) {
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_SENDGIFT, "{\"to\":" + toId + ",\"giftid\":" + giftId + ",\"number\":" + number + "}");
        }
    }

    /**
     * 点心
     *
     * @param number 数量
     */
    public void sendLove(int number) {
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_LIKENUM, "{\"num\":" + number + "}");
        }
    }

    /**
     * 获取在线列表
     */
    public void getOnlineListUser() {
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_RECEIVE_PEOPLE, "");
        }
    }

    /**
     * 申请加入房间 结果
     *
     * @param userId userId 字符
     * @param result result 字符
     */
    public void applyToJoinRoom(String userId, int result) {
        String jsonStr = "{\"userid\":" + userId + ",\"result\":" + result + "}";
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_RESULTS_APPLICATION, jsonStr);
        }
    }

    /**
     * 申请加入房间
     *
     * @param userIdx
     */
    public void joinRoom(String userIdx, String roomId) {
        String jsonStr = "{\"userid\":" + userIdx + ",\"barid\":" + roomId + "}";
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_APPLICATION_TO_JOIN, jsonStr);
        }
    }

    /**
     * 申请列表
     */
    public void applyList() {
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_IMPROVES_LIST, null);
        }
    }

    /**
     * 提升管理
     *
     * @param userId userId
     */
    public void improveManagement(String userId) {
        String jsonStr = "{\"userid\":" + userId + "}";
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_MANAGEMENT_IMPROVE, jsonStr);
        }
    }

    /**
     * 降级管理
     *
     * @param userId userId
     */
    public void demotionManagement(String userId) {
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_MANAGEMENT_DEMOTION, "{\"userid\":" + userId + "}");
        }
    }

    /**
     * 将成员移除 管理者
     */
    public void memberOut(String jsonStr) {
        DebugLogs.e("---server--memberOut--->");
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_MEMBERS_GETOUT, jsonStr);
        }
    }

    /**
     * 踢人
     *
     * @param userIdx userId
     */
    public void kickedOut(String userIdx) {
        DebugLogs.e("---server--kickedOut--->" + userIdx);
        String jsonStr = "{\"to\":" + userIdx + "}";
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_Kicking, jsonStr);
        }
    }

    /**
     * 退出
     */
    public void loginOut() {
        DebugLogs.e("---server--login out--->");
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_LOGIN_OUT, null);
        }
    }

    /**
     * 红包
     *
     * @param redId   redId
     * @param message message
     */
    public void redReceived(String redId, String message) {
        String jsonStr = "{\"id\":" + redId + ",\"message\":\"" + message + "\"}";
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_RECEIVED, jsonStr);
        }
    }

    /**
     * 禁言
     *
     * @param userIdx userId
     */
    public void silence(String userIdx, boolean silence) {
        int isSilence = 0;
        if (silence) {
            isSilence = 1;
        }
        String jsonStr = "{\"to\":" + userIdx + ",\"silence\":" + isSilence + "}";
        if (bindService != null) {
            bindService.sendMessage(GlobalDef.WM_ROOM_SILENCE, jsonStr);
        }
    }

//    public void sendOfferSdp(String sessionId, final SessionDescription sdp, final String type) {
//        JSONObject json = new JSONObject();
//        try {
//            json.put("sessionid",sessionId);
//            json.put("type", type);
//            json.put("offer", sdp.description);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        bindService.sendMessage(GlobalDef.WM_SDP,json.toString());
//    }

//    public void sendLocalIceCandidate(String sessionId, final IceCandidate candidate) {
//        JSONObject json = new JSONObject();
//        try {
//            json.put("sessionid",sessionId);
//            json.put("sdpMLineIndex", candidate.sdpMLineIndex);
//            json.put("sdpMid", candidate.sdpMid);
//            json.put("candidate", candidate.sdp);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        bindService.sendMessage(GlobalDef.WM_CANDIDATE,json.toString());
//    }

    /**
     * 退出房间
     */
    public void quitRoom() {
        if (bindService != null) {
            unBindService();
            bindService.quitRoom();
            bindService = null;
        }
    }
}