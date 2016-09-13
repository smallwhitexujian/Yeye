package com.angelatech.yeyelive.wxapi;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.angelatech.yeyelive.model.WxOrder;

/**
 *
 */
public class WXInterface {

    //    //微信APPID
    public static String WX_APP_ID = "wx1789e78e5e6cf29a";
    //    //微信AppSecret
    public static String WX_AppSecret = "ca67e4f0fa7a058d233872316d328c0f";

    public static final String PARTNER_ID_WEIXIN = "1330307301";//商户号
    public static final String WEIXIN_PAY_RESULT_ACTION = "weixin.pay.result.action";
    public final String PACKAGE_VALUE = "Sign=WXPay";


    private SendAuth.Req req = new SendAuth.Req();

    //朋友圈
    public int WXSceneTimeline = SendMessageToWX.Req.WXSceneTimeline;

    //收藏夹
    public int WXSceneFavorite = SendMessageToWX.Req.WXSceneFavorite;

    //私聊
    public int WXSceneSession = SendMessageToWX.Req.WXSceneSession;

    private SendMessageToWX.Req msgreq = new SendMessageToWX.Req();
    public IWXAPI api;
    public static String WXType = "LOGIN";
    private Context context;

    public enum WXTypes {
        LOGIN, BIND
    }


    public WXInterface(Context context, String WX_APP_ID, String WX_AppSecret) {
        this.context = context;
        WXInterface.WX_APP_ID = WX_APP_ID;
        WXInterface.WX_AppSecret = WX_AppSecret;
        regToWx();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
    }

    public WXInterface(Context context) {
        this.context = context;
        regToWx();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
    }


    //注册微信接口
    private void regToWx() {
        api = WXAPIFactory.createWXAPI(context, WX_APP_ID, true);
        api.registerApp(WX_APP_ID);
    }

    public void login() {
        WXType = "LOGIN";
        api.sendReq(req);
    }

    public void bind() {
        WXType = "BIND";
        api.sendReq(req);
    }

    //分享文本
    public void SceneText(String text, int scene) {
        WXTextObject txtobj = new WXTextObject();
        txtobj.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.description = text;
        msg.mediaObject = txtobj;
        msgreq.transaction = "text";
        msgreq.message = msg;
        msgreq.scene = scene;
        api.sendReq(msgreq);
    }

    //分享图片
    public void ScenePic(Bitmap bmp, String text, int scene) {
        WXImageObject imgobj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();

        msg.mediaObject = imgobj;
        msg.description = text;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 60, 60, true);
        bmp.recycle();
        msg.setThumbImage(thumbBmp);
        msgreq.transaction = "img";
        msgreq.message = msg;
        msgreq.scene = scene;
        api.sendReq(msgreq);
    }

    //分享网页
    public void SceneWebpage(String url, String title, String text, Bitmap bmp, int scene) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = text;
        if (bmp != null){
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 60, 60, true);
            bmp.recycle();
            msg.setThumbImage(thumbBmp);
            msgreq.transaction = "webpage";
            msgreq.message = msg;
            msgreq.scene = scene;
            api.sendReq(msgreq);
        }
    }


    public boolean isWXAppInstalled() {
        return !(api == null || !api.isWXAppInstalled());
    }

    public class WXModel {

        public String access_token;

        public String expires_in;
        public String refresh_token;
        public String openid;
        public String scope;
        public String unionid;

        public String errcode;
        public String errmsg;
    }

    /**
     * 微信充值
     *
     * @param WxOrder
     */
    public void payWxClient(WxOrder WxOrder) {
        PayReq request = new PayReq();
        request.appId = WX_APP_ID;
        request.partnerId = PARTNER_ID_WEIXIN;
        request.prepayId = WxOrder.prepayid;
        request.packageValue = PACKAGE_VALUE;
        request.nonceStr = WxOrder.noncestr;
        request.timeStamp = WxOrder.timestamp;
        request.sign = WxOrder.sign;
        api.sendReq(request);
    }

}
