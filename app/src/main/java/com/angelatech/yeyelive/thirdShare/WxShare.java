package com.angelatech.yeyelive.thirdShare;

import android.app.Activity;
import android.graphics.Bitmap;

import com.angelatech.yeyelive.wxapi.WXInterface;
import com.sina.weibo.sdk.utils.ImageUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.angelatech.yeyelive.wxapi.WXEntryActivity;

/**
 * User: cbl
 * Date: 2016/1/5
 * Time: 16:46
 * 微信分享
 */
public class WxShare {

    public final static int SHARE_TYPE_WX = 8;

    private Activity activity;
    private SendMessageToWX.Req send;
    private IWXAPI wxApi;
    private WXMediaMessage wxMediaMessage;
    private ShareListener listener;

    /**
     * @param activity 上下文
     * @param listener 分享监听
     */
    public WxShare(Activity activity, ShareListener listener) {
        this.activity = activity;
        this.listener = listener;
        init();
    }

    public void registerCallback(ShareListener shareListener) {
        this.listener = shareListener;
    }

    private void init() {
        wxApi = WXAPIFactory.createWXAPI(activity, WXInterface.WX_APP_ID, true);
        send = new SendMessageToWX.Req();
        WXEntryActivity.shareListener = listener;
        wxMediaMessage = new WXMediaMessage();
    }

    /**
     * 分享文本
     *
     * @param text  文本
     * @param scene 1 朋友圈 0微信好友
     */
    public void shareText(String text, int scene) {
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        // 用WXTextObject对象初始化一个WXMediaMessage对象
        wxMediaMessage.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Will be ignored";
        wxMediaMessage.description = text;
        // 构造一个Req
        send.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        send.message = wxMediaMessage;
        send.scene = scene;
        // 调用api接口发送数据到微信
        wxApi.sendReq(send);
    }

    /**
     * 分享图片
     * @param bmp 图片
     * @param text 文本
     * @param scene 1 朋友圈 0微信好友
     */
    public void shareImage(Bitmap bmp, String text, int scene) {
        wxMediaMessage.mediaObject = new WXImageObject(bmp);
        wxMediaMessage.description = text;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 60, 60 *( bmp.getHeight() / bmp.getWidth()), true);
        if(bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
        }
        wxMediaMessage.setThumbImage(thumbBmp);
        send.transaction = "img";
        send.message = wxMediaMessage;
        send.scene = scene;
        wxApi.sendReq(send);
    }

    /**
     * 分享 地址
     *
     * @param title  title
     * @param text   文本
     * @param url    链接
     * @param bitmap 图片
     * @param scene  1 是朋友圈  0 是微信好友
     */
    public void SceneWebPage(String title, String text, String url, Bitmap bitmap, int scene) {

        if (bitmap == null){
            return;
        }
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = title;
        msg.description = text;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
        msg.setThumbImage(thumbBmp);
        send.transaction = "webpage";
        send.message = msg;
        send.scene = scene;
        wxApi.sendReq(send);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
