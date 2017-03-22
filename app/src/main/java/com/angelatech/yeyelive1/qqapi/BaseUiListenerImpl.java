package com.angelatech.yeyelive1.qqapi;

import android.os.Handler;
import android.os.Message;

import com.angelatech.yeyelive1.util.JsonUtil;
import com.tencent.tauth.UiError;
import com.will.common.log.DebugLogs;

import org.json.JSONObject;


/**
 *  jjfly
 */
public class BaseUiListenerImpl extends BaseUiListener {


    private static  final int BASE_PREFIX = 0xFF;
    public static final int MSG_QQ_LOGIN = BASE_PREFIX+1;
    public static final int MSG_QQ_BIND = BASE_PREFIX+2;
    public static final int MSG_QQ_LOGIN_ERROR = BASE_PREFIX+3;
    public static final int MSG_QQ_LOGIN_CANCEL = BASE_PREFIX+4;

    public static final String TYPE_LOGIN = "LOGIN";
    public static final String TYPE_BIND = "BIND";

    private Handler handler;
    private String type;


    public BaseUiListenerImpl(Handler handler,String type){
        this.handler = handler;
        this.type = type;
    }

    //qq登录回调
    @Override
    protected void doComplete(JSONObject values) {
        DebugLogs.e(values.toString());
        QQModel results = JsonUtil.fromJson(values.toString(), QQModel.class);
        if (type.equals(TYPE_LOGIN)) {
            Message message = handler.obtainMessage();
            message.what = MSG_QQ_LOGIN;
            message.obj = results;
            handler.sendMessage(message);
        } else {
            Message message = handler.obtainMessage();
            message.what = MSG_QQ_BIND;
            message.obj = results;
            handler.sendMessage(message);
        }
    }

    @Override
    public void onError(UiError e) {
        Message message = handler.obtainMessage();
        message.what = MSG_QQ_LOGIN_ERROR;
        message.obj = e.errorMessage;
        handler.sendMessage(message);
    }

    @Override
    public void onCancel() {
        handler.sendEmptyMessage(MSG_QQ_LOGIN_CANCEL);
    }

}