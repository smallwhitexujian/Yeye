package com.angelatech.yeyelive.socket.im.dispatch;

import android.content.Context;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.MessageNotificationActivity;
import com.angelatech.yeyelive.activity.MessageOfficialActivity;
import com.angelatech.yeyelive.activity.Qiniupush.widget.DebugLogs;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.db.model.SystemMessageDBModel;
import com.angelatech.yeyelive.model.CommonParseModel;
import com.angelatech.yeyelive.model.ReceiveBroadcastModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.model.SystemBroadModel;
import com.angelatech.yeyelive.model.SystemMessage;
import com.angelatech.yeyelive.model.SystemMessageType;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.NotificationUtil;
import com.google.gson.reflect.TypeToken;
import com.will.common.tool.time.DateTimeTool;

import org.json.JSONException;
import org.json.JSONObject;


/**
 */
public class BroadCastDispatch extends Dispatchable {

    private final int CODE_LESS_BALANCE = 0;
    private final int CODE_USER_NOTICE = 1;
    private final int CODE_SYSTEM_NOTICE_ACTIVITIES = 2;
    private final int CODE_SYSTEM_MSG = 3;
    private final int CODE_SYSTEM_UPDATE = 4;

    public static final int CODE_LOGIN_OUT = 5;
    private Context mContext;
    private String mContent;//内容
    private int mTypeCode;
    private SystemMessage systemMessage = new SystemMessage();

    public BroadCastDispatch(Context context) {
        mContext = context;
    }


    @Override
    public void dispatch(int type, byte[] datas) {
        String dataStr = getDataStr(datas);
        CommonParseModel<ReceiveBroadcastModel> broadcastModel = JsonUtil.fromJson(dataStr, new TypeToken<CommonParseModel<ReceiveBroadcastModel>>() {
        }.getType());
        DebugLogs.e("IM系统消息通知====" + broadcastModel.msg + "-----" + broadcastModel.data);
        DebugLogs.e("IM系统消息通知====" + broadcastModel.time);
        DebugLogs.e("IM系统消息通知====" + broadcastModel.data.url);
        //“code”: 0余额不足1用户喇叭,2系统公告,3系统小秘书,4系统升级消息
        try {
            int code = Integer.parseInt(broadcastModel.code);
            switch (code) {
                //余额不足
                case CODE_LESS_BALANCE:
                    break;
                //系统通知（）
                case CODE_SYSTEM_MSG:
                    String msg = broadcastModel.msg;
                    //发通知
                    SystemMessageDBModel systemMessageDBModel = parseJson(msg, broadcastModel);
                    //保存数据
                    String ticker = mContext.getString(R.string.notify_default_ticker);
                    String title = mContext.getString(R.string.notify_default_title);
                    if (mContent == null || "".equals(mContent.trim())) {
                        mContent = title;
                    }
                    switch (systemMessageDBModel.type_code) {
                        case SystemMessageType.NOTICE_LIVE://直播通知只显示提示通知跳转房间
                            int requestCode = NotificationUtil.NOTICE_LIVE;
                            long nowTime = DateTimeTool.GetDateTimeNowlong(); //毫秒
                            long startTime = broadcastModel.time * 1000;
                            long intervalTime = DateTimeTool.getCompareValue(startTime, nowTime, DateTimeTool.FORMAT_MINUTE);
                            //如果在房间或者关闭通知则不发送通知
                            if (intervalTime > 30 || !App.isLiveNotify || App.chatRoomApplication != null) {
                                return;
                            }
                            try {
                                SystemBroadModel.LiveBroadCast result = JsonUtil.fromJson(systemMessageDBModel.data, SystemBroadModel.LiveBroadCast.class);
                                if (result == null) {
                                    return;
                                }
                                RoomModel roomModel = new RoomModel();
                                roomModel.setId(Integer.parseInt(result.roomid));
                                roomModel.setIp(result.roomip.split(":")[0]);
                                roomModel.setPort(Integer.parseInt(result.roomip.split(":")[1]));
                                roomModel.setRoomType(App.LIVE_WATCH);
                                BasicUserInfoDBModel user = new BasicUserInfoDBModel();
                                user.userid = result.uid;
                                user.headurl = result.headurl;
                                user.nickname = result.nickname;
                                roomModel.setUserInfoDBModel(user);
                                String content = mContext.getString(R.string.notify_live_content, result.nickname);
                                NotificationUtil.launchNoticeWithData(mContext, requestCode, ticker, title, content, ChatRoomActivity.class, TransactionValues.UI_2_UI_KEY_OBJECT, roomModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case SystemMessageType.NOTICE_LIVE_FEEDBACK:
                            checkReadOrNot();
                            requestCode = NotificationUtil.NOTICE_FEEDBACK;
                            systemMessage.add(systemMessageDBModel);
                            SystemBroadModel.FeedbackBroadCast feedbackBroadCast = JsonUtil.fromJson(systemMessageDBModel.data, SystemBroadModel.FeedbackBroadCast.class);
                            if (feedbackBroadCast != null) {
                                String content = feedbackBroadCast.msg;
                                mContent = content == null ? mContent : content;
                                NotificationUtil.lauchNotifyOnlyShow(mContext, requestCode, ticker, title, mContent, mContent);
                            }
                            break;
                        case SystemMessageType.NOTICE_SHOW_PERSON_MSG://针对个人的推送
                            checkReadOrNot();
                            systemMessage.add(systemMessageDBModel);
                            try {
                                JSONObject msgJsonObj = new JSONObject(systemMessageDBModel.data);
                                mContent = msgJsonObj.getString("msg");
                                NotificationUtil.lauchNotifyOnlyShow(mContext, NotificationUtil.NOTICE_SHOW_PERSON_MSG, ticker, title, mContent, mContent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case SystemMessageType.NOTICE_FANS_MSG://粉丝关注
                            checkReadOrNot();
                            try {
                                JSONObject msgJsonObj = new JSONObject(systemMessageDBModel.data);
                                mContent = msgJsonObj.getString("nickname") + "关注了你";
                                systemMessageDBModel.content = mContent;
                                if (App.isfansNotify) {
                                    NotificationUtil.launchNotifyDefault(mContext, NotificationUtil.NOTICE_FANS_MSG, ticker, title, mContent, MessageOfficialActivity.class);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            systemMessage.add(systemMessageDBModel);
                            break;
                    }
                    mContent = "";//还原
                    break;
                //系统公告：活动
                case CODE_SYSTEM_NOTICE_ACTIVITIES:
                    //发通知
                    checkReadOrNot();
                    int requestSystemNoticeCode = NotificationUtil.CODE_SYSTEM_NOTICE;
                    String message = mContext.getString(R.string.notify_default_message);
                    SystemMessageDBModel noticeMessageDBModel = parseJson(broadcastModel);
                    systemMessage.add(noticeMessageDBModel);
                    switch (noticeMessageDBModel.type_code) {
                        case SystemMessageType.NOTICE_TO_ALL:
                            String msgStr = message;
                            try {
                                JSONObject msgJsonObj = new JSONObject(noticeMessageDBModel.data);
                                msgStr = msgJsonObj.getString("msg");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (App.isofficialNotify) {
                                NotificationUtil.launchNotifyDefault(mContext, requestSystemNoticeCode, message, message, msgStr, MessageOfficialActivity.class);
                            }
                            break;
                    }
                    break;
                //更新
                case CODE_SYSTEM_UPDATE:
                    break;
                //用户通知
                case CODE_USER_NOTICE:
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean validateParcel(byte[] parcel) {
        return true;
    }

    private SystemMessageDBModel parseJson(String jsonStr, CommonParseModel<ReceiveBroadcastModel> commonParseModel) {
        SystemMessageDBModel systemMessageDBModel = null;
        try {
            systemMessageDBModel = new SystemMessageDBModel();
            JSONObject jsonObject = new JSONObject(jsonStr);
            String typeCode = jsonObject.getString("type_code");
            mTypeCode = Integer.parseInt(typeCode);
            systemMessageDBModel.type_code = mTypeCode;
            String data = jsonObject.getString("data");
            systemMessageDBModel.data = data;
            systemMessageDBModel.datetime = String.valueOf(commonParseModel.time);
            JSONObject msgJsonObj = new JSONObject(data);
            mContent = msgJsonObj.getString("msg");
            systemMessageDBModel.isread = "0";
            systemMessageDBModel._data = commonParseModel.data.url;
            if (CacheDataManager.getInstance().loadUser() != null && CacheDataManager.getInstance().loadUser().userid != null) {
                systemMessageDBModel.uid = CacheDataManager.getInstance().loadUser().userid;
            }
            systemMessageDBModel.content = mContent;
            systemMessageDBModel.localtime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return systemMessageDBModel;
    }

    private SystemMessageDBModel parseJson(CommonParseModel<ReceiveBroadcastModel> commonParseModel) {
        SystemMessageDBModel systemMessageDBModel = new SystemMessageDBModel();
        systemMessageDBModel.localtime = System.currentTimeMillis();
        if (CacheDataManager.getInstance().loadUser() != null && CacheDataManager.getInstance().loadUser().userid != null) {
            systemMessageDBModel.uid = CacheDataManager.getInstance().loadUser().userid;
        }
        try {
            systemMessageDBModel._data = commonParseModel.data.url;
            JSONObject jsonObject = new JSONObject(commonParseModel.msg);
            String typeCode = jsonObject.getString("type_code");
            mTypeCode = Integer.parseInt(typeCode);
            systemMessageDBModel.type_code = mTypeCode;
            String data = jsonObject.getString("data");
            systemMessageDBModel.data = data;
            systemMessageDBModel.datetime = String.valueOf(commonParseModel.time);
            systemMessageDBModel.isread = "0";
            JSONObject msgJsonObj = new JSONObject(data);
            mContent = msgJsonObj.getString("msg");
            systemMessageDBModel.content = mContent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return systemMessageDBModel;
    }

    //判断广播消息是否位读
    private void checkReadOrNot() {
        if (!MessageNotificationActivity.class.getSimpleName().equals(App.topActivity)) {
            systemMessage.addUnReadTag(mContext);
        }
    }
}
