package com.angelatech.yeyelive.thirdLogin;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.will.common.log.DebugLogs;


/**
 * User: cbl
 * Date: 2016/1/4
 * facebook 登录
 * Time: 19:56
 */
public class FbProxy {
    private static CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private Activity activity;
    private LoginButton loginButton;
    private Handler handler;
    public static final int FB_BASE = 0X09;
    public static final int FB_LOGIN_SUCCESS = FB_BASE + 1; //成功
    public static final int FB_LOGIN_CANCEL = FB_BASE + 2;  //取消
    public static final int FB_LOGIN_ERROR = FB_BASE + 3;   //失败


    public FbProxy(LoginButton loginButton, Handler handler) {
        //this.activity = activity;
        this.loginButton = loginButton;
        this.handler = handler;
    }

    public FbProxy(Activity activity){
        this.activity = activity;
    }

    /**
     * 返回 CallbackManager
     *
     * @return
     */
    public static CallbackManager init() {
        callbackManager = CallbackManager.Factory.create();//创建回调工厂
        return callbackManager;
    }

    public void login() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        loginButton.setReadPermissions("user_friends", "email", "public_profile");
        loginButton.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                DebugLogs.e("fbLogin---->onSuccess");
                getUserInfo();
            }

            @Override
            public void onCancel() {
                DebugLogs.e("fbLogin---->onCancel");
                Message message = handler.obtainMessage();
                message.what = FB_LOGIN_CANCEL;
                message.obj = "cancel";
                handler.sendMessage(message);
            }

            @Override
            public void onError(FacebookException error) {
                DebugLogs.e("fbLogin---->onError-->" + error.getMessage());
                Message message = handler.obtainMessage();
                message.what = FB_LOGIN_ERROR;
                message.obj = error;
                handler.sendMessage(message);
            }
        });

        //刷新获取个人用户资料
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                getUserInfo();
                //It`s possible that we were waiting for Profile to be populated in order to post a status update.
            }
        };
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        boolean enableButton = AccessToken.getCurrentAccessToken() != null;
        //Profile profile = Profile.getCurrentProfile();//获取登陆成功以后的数据

        if (AccessToken.getCurrentAccessToken() != null) {
            Message message = handler.obtainMessage();
            message.what = FB_LOGIN_SUCCESS;
            message.obj = AccessToken.getCurrentAccessToken().getToken();
            handler.sendMessage(message);
        }
    }

    /**
     * 获取朋友列表
     */
    private void getFriendsList() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friendlists",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.e("获取我的好友列表", response.toString());
                    }
                }
        ).executeAsync();
    }

    /**
     * 拉好友
     */
    public void inviteFriend(){
        String appLinkUrl, previewImageUrl;
        appLinkUrl = "https://fb.me/1745967612350627";
        previewImageUrl = "http://liveapi.iamyeye.com/upload/test.png";
        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(activity, content);
        }
    }

}
