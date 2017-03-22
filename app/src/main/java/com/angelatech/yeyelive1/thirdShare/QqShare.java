package com.angelatech.yeyelive1.thirdShare;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.angelatech.yeyelive1.thirdLogin.QQProxy;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

/**
 * User: cbl
 * Date: 2016/5/19
 * Time: 10:37
 * Qq 分享
 */
public class QqShare {
    //QZone分享， SHARE_TO_QQ_TYPE_DEFAULT 图文，SHARE_TO_QQ_TYPE_IMAGE 纯图
    private int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
    private Activity activity;
    private Handler handler;
    private static final int QQShareBase = 0x67;
    public static final int QQShare_SUCCESS = QQShareBase + 1;
    public static final int QQShare_CANCEL = QQShareBase + 2;
    public static final int QQShare_ERROR = QQShareBase + 3;

    private ShareListener listener;
    public final static int SHARE_TYPE_QQ = 10;
    private Tencent tencent;

    public QqShare(Activity activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
        tencent = Tencent.createInstance(QQProxy.QQ_APP_ID, activity);
    }

    public void registerCallback(ShareListener shareListener) {
        this.listener = shareListener;
    }

    public QqShare(Activity activity, ShareListener listener) {
        this.activity = activity;
        this.listener = listener;
        tencent = Tencent.createInstance(QQProxy.QQ_APP_ID, activity);
    }

    //分享好友
    public void shareToFriend(String title, String text, String url, String imageUrl) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, text);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "");
        tencent.shareToQQ(activity, params, iUiListener);
    }


    public void shareToQzone(String title, String summary, String url, String imageUrl) {
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        if (shareType != QzoneShare.SHARE_TO_QZONE_TYPE_APP) {
            //app分享不支持传目标链接
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
        }
        // 支持传多个imageUrl
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add(imageUrl);

        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        QzoneShare qzoneShare = new QzoneShare(activity, tencent.getQQToken());
        qzoneShare.shareToQzone(activity, params, iUiListener);
    }

    public IUiListener iUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            if (handler != null) {
                handler.sendEmptyMessage(QQShare_SUCCESS);
            }
            if (listener != null) {
                listener.callBackSuccess(SHARE_TYPE_QQ);
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (handler != null) {
                handler.sendEmptyMessage(QQShare_ERROR);
            }
            if (listener != null) {
                listener.callbackError(SHARE_TYPE_QQ);
            }
        }

        @Override
        public void onCancel() {
            if (handler != null) {
                handler.sendEmptyMessage(QQShare_CANCEL);
            }
            if (listener != null) {
                listener.callbackCancel(SHARE_TYPE_QQ);
            }
        }
    };
}
