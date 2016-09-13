package com.angelatech.yeyelive.thirdShare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.will.common.log.DebugLogs;
import com.angelatech.yeyelive .R;


/**
 * User: cbl
 * Date: 2016/1/5
 * Time: 16:45
 */
public class FbShare {

    private Activity activity;
    private boolean canPresentShareDialog;
    private ShareDialog shareDialog;
    public static CallbackManager callbackManager;
    private Profile profile;
    private ShareListener listener;
    private Handler handler;

    private static final int FacebookShareBase = 0x69;
    public static final int Facebook_SUCCESS = FacebookShareBase + 1;
    public static final int Facebook_CANCEL = FacebookShareBase + 2;
    public static final int Facebook_ERROR = FacebookShareBase + 3;

    public final static int SHARE_TYPE_FACEBOOK = 1;

    public FbShare(Activity activity, ShareListener listener) {
        this.activity = activity;
        this.listener = listener;
        init();
    }

    public FbShare(Activity activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
        init();
    }

    public void registerCallback(ShareListener shareListener) {
        this.listener = shareListener;
    }

    public void init() {
        FacebookSdk.sdkInitialize(activity);
        callbackManager = CallbackManager.Factory.create();
        profile = Profile.getCurrentProfile();
        shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, shareCallback);
        canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
    }

    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            DebugLogs.e("HelloFacebook" + "Canceled");
            if (listener != null) {
                listener.callbackCancel(SHARE_TYPE_FACEBOOK);
            }
            if (handler != null) {
                handler.obtainMessage(Facebook_CANCEL).sendToTarget();
            }
        }

        @Override
        public void onError(FacebookException error) {
            DebugLogs.e("HelloFacebook" + String.format("Error: %s", error.toString()));
            if (listener != null) {
                listener.callbackError(SHARE_TYPE_FACEBOOK);
            }
            if (handler != null) {
                handler.obtainMessage(Facebook_ERROR).sendToTarget();
            }
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            DebugLogs.e("HelloFacebook" + "Success!");
            if (listener != null) {
                listener.callBackSuccess(SHARE_TYPE_FACEBOOK);
            }
            if (handler != null) {
                handler.obtainMessage(Facebook_SUCCESS).sendToTarget();
            }
        }
    };

    /**
     * 分享
     * @param shareTitle 分享标题
     * @param Description 文字描述
     * @param url 连接
     * @param imageUrl 图片
     */
    public void postStatusUpdate(String shareTitle, String Description,String url, String imageUrl) {
        canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
        if (url == null) {
            shareTitle = activity.getString(R.string.shareTitle);
            Description = activity.getString(R.string.shareDescription);
            url = CommonUrlConfig.facebookURL;
        }

        ShareLinkContent.Builder builder = new ShareLinkContent.Builder();
        builder.setContentTitle(shareTitle)
                .setContentDescription(Description)
                .setContentUrl(Uri.parse(url));
        if (imageUrl != null) {
            builder.setImageUrl(Uri.parse(imageUrl));
        }
        ShareLinkContent linkContent = builder.build();
        if (canPresentShareDialog) {
            if (shareDialog.getShouldFailOnDataError()) {//正常显示分享
                shareDialog.show(linkContent);
            } else {
                shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);
            }
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        }
    }

    /**
     * 上传图片 到Facebook
     * 这些照片必须小于12MB的大小
     *
     * @param bitmap
     */
    public void postPhoto(Bitmap bitmap) {
        canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        if (canPresentShareDialog) {
            if (!shareDialog.getShouldFailOnDataError()) {
                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            } else {
                shareDialog.show(content);
            }
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(content, shareCallback);
        }
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

}
