package com.will.yeye.thirdLogin;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.will.yeye.wxapi.SimpleWxUIListener;
import com.will.yeye.wxapi.WXEntryActivity;
import com.will.yeye.wxapi.WXInterface;

/**
 * User: cbl
 * Date: 2016/1/4
 * Time: 20:32
 */
public class WxProxy {
    private WXInterface wxApi;
    private Activity activity;
    private Handler handler;
    public static final int WX_BASE = 0X12;
    public static final int WX_LOGIN = WX_BASE + 1;
    public static final int WX_BIND = WX_BASE + 2;


    private SimpleWxUIListener loginSimpleWxUIListener = new SimpleWxUIListener() {
        @Override
        public void callBackLogin(String code) {
            Message message = handler.obtainMessage();
            message.what = WX_LOGIN;
            message.obj = code;
            handler.sendMessage(message);
        }
    };


    private SimpleWxUIListener bindloginSimpleWxUIListener = new SimpleWxUIListener() {
        @Override
        public void callbackBind(String code) {
            Message message = handler.obtainMessage();
            message.what = WX_BIND;
            message.obj = code;
            handler.sendMessage(message);
        }
    };


    public WxProxy(Activity activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
    }

    public void login() {
        wxApi = new WXInterface(activity);
        if (!wxApi.isWXAppInstalled()) {
            //没有安装微信客户端
            return;
        }
        WXInterface.WXType = "LOGIN";
        //设置回调
        WXEntryActivity.wxUIListener = loginSimpleWxUIListener;
        wxApi.login();
    }

    public void bind() {
        wxApi = new WXInterface(activity);
        if (!wxApi.isWXAppInstalled()) {
            //没有安装微信客户端
            return;
        }
        WXInterface.WXType = "BIND";
        WXEntryActivity.wxUIListener = bindloginSimpleWxUIListener;
        wxApi.bind();
    }


}
