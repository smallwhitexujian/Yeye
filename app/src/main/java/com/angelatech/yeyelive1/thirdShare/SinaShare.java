package com.angelatech.yeyelive1.thirdShare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.angelatech.yeyelive1.util.AccessTokenKeeper;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.angelatech.yeyelive1.R;
import com.will.view.ToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * User: cbl
 * Date: 2016/4/6
 * Time: 13:49
 */
public class SinaShare {
    public static String App_Key = "670023890";
    public static String App_Secret = "a5b48980c0c95f0c0b8054187fe74922";
    private Activity activity;
    private String title;
    private String summary;
    private String url;
    private Bitmap bitmap = null;
    protected static IWeiboShareAPI weiboShareAPI = null;


    public static final int SHARE_CLIENT = 1;
    public static final int SHARE_ALL_IN_ONE = 2;

    private int mShareType = SHARE_CLIENT;

    public static final String REDIRECT_URL = "http://www.sina.com"; //回调接口
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog,"
                    + "invitation_write";
    private ShareListener listener;
    public final static int SHARE_TYPE_SINA = 11;

    public SinaShare(Activity activity, String title, String text, String url, Bitmap bmp) {
        this.activity = activity;
        this.title = title;
        this.summary = text;
        this.url = url;
        this.bitmap = bmp;
        initApi();
    }

    public void registerCallback(ShareListener shareListener) {
        this.listener = shareListener;
    }

    public IWeiboShareAPI initApi() {
        weiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, App_Key);
        if (weiboShareAPI.isWeiboAppInstalled() && weiboShareAPI.isWeiboAppSupportAPI()) {
            weiboShareAPI.registerApp();
            return weiboShareAPI;
        }
        return null;
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    public void share(boolean hasText, boolean hasImage,
                      boolean hasWebPage, boolean hasMusic, boolean hasVideo, boolean hasVoice) {
        if (weiboShareAPI != null && weiboShareAPI.isWeiboAppSupportAPI()) {
            int supportApi = weiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                sendMultiMessage(hasText, hasImage, hasWebPage, hasMusic, hasVideo, hasVoice);
            } else {
                sendSingleMessage(hasText, hasImage, hasWebPage, hasMusic, hasVideo/*, hasVoice*/);
            }
        } else {
            ToastUtils.showToast(activity, activity.getString(R.string.not_weibo));
        }
    }


    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebPage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
    public void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebPage,
                                 boolean hasMusic, boolean hasVideo, boolean hasVoice) {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }

        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }

        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (hasWebPage) {
            weiboMessage.mediaObject = getWebpageObj();
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj();
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj();
        }
        if (hasVoice) {
            weiboMessage.mediaObject = getVoiceObj();
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        if (mShareType == SHARE_CLIENT) {
            weiboShareAPI.sendRequest(activity, request);
        } else if (mShareType == SHARE_ALL_IN_ONE) {
            AuthInfo authInfo = new AuthInfo(activity, App_Key, REDIRECT_URL, SCOPE);
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(activity);
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            weiboShareAPI.sendRequest(activity, request, authInfo, token, weiboAuthListener);
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     */
    public void sendSingleMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
                                  boolean hasMusic, boolean hasVideo/*, boolean hasVoice*/) {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (hasText) {
            weiboMessage.mediaObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.mediaObject = getImageObj();
        }
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj();
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj();
        }
        /*if (hasVoice) {
            weiboMessage.mediaObject = getVoiceObj();
        }*/

        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        weiboShareAPI.sendRequest(activity, request);
    }


    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = summary;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        if (bitmap != null) {
            ImageObject imageObject = new ImageObject();
            imageObject.setImageObject(bitmap);
            return imageObject;
        }
        return null;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = summary;

        // 设置 Bitmap 类型的图片到视频对象里
        mediaObject.setThumbImage(BitmapFactory.decodeResource(activity.getResources(), R.drawable.logo_sinaweibo));
        mediaObject.actionUrl = url;
        mediaObject.defaultText = summary;
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj() {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = title;
        musicObject.description = summary;

        //Bitmap bitmap = null;
        //BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);

        // 设置 Bitmap 类型的图片到视频对象里
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        musicObject.setThumbImage(bitmap);
        musicObject.actionUrl = url;
        musicObject.dataUrl = "www.weibo.com";
        musicObject.dataHdUrl = "www.weibo.com";
        musicObject.duration = 10;
        musicObject.defaultText = "Music 默认文案";
        return musicObject;
    }


    /**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getVideoObj() {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = title;
        videoObject.description = summary;
        //Bitmap bitmap = null;
        // BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_video_thumb);
        // 设置 Bitmap 类型的图片到视频对象里  设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。

        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os);
            //System.out.println("kkkkkkk    size  "+ os.toByteArray().length );
        } catch (Exception e) {
            e.printStackTrace();
            //LogUtil.e("Weibo.BaseMediaObject", "put thumb failed");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        videoObject.setThumbImage(bitmap);
        videoObject.actionUrl = url;
        videoObject.dataUrl = "www.weibo.com";
        videoObject.dataHdUrl = "www.weibo.com";
        videoObject.duration = 10;
        videoObject.defaultText = "Vedio 默认文案";
        return videoObject;
    }

    /**
     * 创建多媒体（音频）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private VoiceObject getVoiceObj() {
        // 创建媒体消息
        VoiceObject voiceObject = new VoiceObject();
        voiceObject.identify = Utility.generateGUID();
        voiceObject.title = title;
        voiceObject.description = summary;
        //Bitmap bitmap = null;
        // BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);
        // 设置 Bitmap 类型的图片到视频对象里
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        voiceObject.setThumbImage(bitmap);
        voiceObject.actionUrl = url;
        voiceObject.dataUrl = "www.weibo.com";
        voiceObject.dataHdUrl = "www.weibo.com";
        voiceObject.duration = 10;
        voiceObject.defaultText = "Voice 默认文案";
        return voiceObject;
    }

    /**
     * 微博 分享回调
     */
    private WeiboAuthListener weiboAuthListener = new WeiboAuthListener() {
        @Override
        public void onComplete(Bundle bundle) {
            Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
            AccessTokenKeeper.writeAccessToken(activity, newToken);
            if (listener != null) {
                listener.callBackSuccess(SHARE_TYPE_SINA);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (listener != null) {
                listener.callbackError(SHARE_TYPE_SINA);
            }
        }

        @Override
        public void onCancel() {
            if (listener != null) {
                listener.callbackCancel(SHARE_TYPE_SINA);
            }
        }
    };
}
