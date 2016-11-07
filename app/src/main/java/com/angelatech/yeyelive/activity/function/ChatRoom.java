package com.angelatech.yeyelive.activity.function;

import android.app.Activity;
import android.content.Context;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.fragment.LockChooseDialogFragment;
import com.angelatech.yeyelive.fragment.TicketsDialogFragment;
import com.angelatech.yeyelive.model.GiftModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.common.log.DebugLogs;
import com.will.common.string.Encryption;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shanli_pc on 2016/3/16.
 * 房间入口
 */
public class ChatRoom extends HttpFunction {

    static Context context;
    static Activity activity;

    public ChatRoom(Context context) {
        super(context);
        ChatRoom.activity = (Activity) context;
    }

    //根据礼物ID获取礼物链接
    public GiftModel getGifPath(int giftIndex) {
        for (int i = 0; i < App.giftdatas.size(); i++) {
            if (App.giftdatas.get(i).getID() == giftIndex) {
                return App.giftdatas.get(i);
            }
        }
        return null;
    }

    private static void preEnterChatRoom(Context context) {
        //关闭以前房间
        closeChatRoom();
    }

    public static void enterPWDChatRoom(final Context context, final RoomModel roomModel, final String roompwd) {
        LockChooseDialogFragment.Callback callback = new LockChooseDialogFragment.Callback() {
            @Override
            public void onCancel() {
                //收起键盘
            }

            @Override
            public void onEnter(String password) {
                if (roompwd.equals(password)) {
                    preEnterChatRoom(context);
                    StartActivityHelper.jumpActivity(context, ChatRoomActivity.class, roomModel);
                } else {
                    ToastUtils.showToast(context,R.string.roompwd_err);
                }
            }
        };

        LockChooseDialogFragment lockChooseDialogFragment = new LockChooseDialogFragment(callback, App.roompwd);
        lockChooseDialogFragment.show(activity.getFragmentManager(), "");
    }

    //进ChatRoom房间
    //增加门票功能
    //增加密码房功能
    public static void enterChatRoom(final Context context, final RoomModel roomModel) {
        ChatRoom chatRoom = new ChatRoom(context);

        chatRoom.getRoomTickets(roomModel.getLoginUser().Userid, roomModel.getLoginUser().Token,
                String.valueOf(roomModel.getId()), new HttpBusinessCallback() {
                    @Override
                    public void onSuccess(String response) {
                        DebugLogs.e("response" + response);
                        Map map = JsonUtil.fromJson(response, Map.class);
                        if (map != null) {
                            if (HttpFunction.isSuc(map.get("code").toString())) {
                                String ticket = map.get("data").toString();
                                if (!ticket.equals("0")) {//需要门票
                                    TicketsDialogFragment ticketsDialogFragment = new TicketsDialogFragment();
                                    roomModel.setTicket(ticket);
                                    ticketsDialogFragment.setRoomModel(roomModel);
                                    ticketsDialogFragment.show(activity.getFragmentManager(), "");
                                } else {
                                    preEnterChatRoom(context);
                                    StartActivityHelper.jumpActivity(context, ChatRoomActivity.class, roomModel);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Map<String, ?> errorMap) {

                    }
                });
    }

    /**
     * 获取房间门票信息
     *
     * @param userId   uid
     * @param token    token
     * @param roomId   房间id
     * @param callback 回调
     */
    private void getRoomTickets(String userId, String token, String roomId, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("userid", userId);
        params.put("toroomid", roomId);
        httpGet(CommonUrlConfig.PayTicketsIsPay, params, callback);
    }

    /**
     * 门票列表
     *
     * @param userId   uid
     * @param token    token
     * @param callback 回调
     */
    public void getPayTicketsList(String userId, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("userid", userId);
        httpGet(CommonUrlConfig.PayTicketslist, params, callback);
    }

    /**
     * 门票更新
     *
     * @param userId   uid
     * @param token    token
     * @param price    价格
     * @param callback 回调
     *                 废弃
     */
    public void updatePayTicketsUpt(String userId, String token, String price, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("userid", userId);
        params.put("price", price);
        httpGet(CommonUrlConfig.PayTicketsUpt, params, callback);
    }

    /**
     * 门票支付
     *
     * @param userId   uid
     * @param token    token
     * @param roomId   房间id
     * @param callback 回调
     */
    public void payTicketsIsIns(String userId, String token, String roomId, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("userid", userId);
        params.put("toroomid", roomId);
        httpGet(CommonUrlConfig.PayTicketsIsIns, params, callback);
    }

    /**
     * 门票结算数据 主播停播请求
     *
     * @param userId   udi
     * @param token    token
     * @param callback 回调
     */
    public void payTicketsSet(String userId, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("userid", userId);
        httpGet(CommonUrlConfig.PayTicketsSet, params, callback);
    }

    /**
     * 退出房间
     */
    public static void closeChatRoom() {
        if (App.chatRoomApplication != null) {
            try {
                App.chatRoomApplication.exitRoom();
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
    public void UserIsFollow(String url, String token, String userid, String tuserid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("touserid", tuserid);
        params.put("userid", userid);
        httpGet(url, params, callback);
    }

    /**
     * 关注/取消关注
     */
    public void UserFollow(String url, String token, String userid, String fuserid, int type, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("fuserid", fuserid);
        params.put("userid", userid);
        params.put("type", String.valueOf(type));
        httpGet(url, params, callback);
    }

    /**
     * 开播前拿一些需要的信息
     */
    public void LiveVideoBroadcast(String url, BasicUserInfoDBModel userInfo, String introduce, String area, String price, String pwd, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        params.put("price", price);
        params.put("pwd", pwd);
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
    public void LiveQiSaveVideo(String url, BasicUserInfoDBModel userInfo, String liveid, int playnum, int issave, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        params.put("liveid", liveid);
        params.put("playnum", String.valueOf(playnum));//观看人数
        params.put("issave", String.valueOf(issave));//是否保存,1删除 0保存
        httpGet(url, params, callback);
    }

    /**
     * 升级
     */
    public void upApk(String url, String versionCode, HttpBusinessCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("version", versionCode);
        params.put("os", "2");//2.表示Android 。1表示IOS
        httpGet(url, params, callback);
    }

    /**
     * 统计活跃
     */
    public void setMark(String url, String userId, String device, String model, String edition, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("Device", device);
        params.put("model", model);//机型
        params.put("edition", edition);//版本号
        httpGet(url, params, callback);
    }

    /**
     * 发送红包
     */
    public void PayReward(String url, String vid, String amount, String content, String userid, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("vid", vid);
        params.put("amount", amount);
        params.put("content", Encryption.utf8ToUnicode(content));
        params.put("userid", userid);
        params.put("token", token);
        httpGet(url, params, callback);
    }

    /**
     * 获取封面
     */
    public void getRoomInfo(String url, String userid, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        httpGet(url, params, callback);
    }

}
