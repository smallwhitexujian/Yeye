package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.Login;
import com.angelatech.yeyelive.activity.function.Register;
import com.angelatech.yeyelive.activity.function.Start;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.WebTransportModel;
import com.angelatech.yeyelive.thirdLogin.FbProxy;
import com.angelatech.yeyelive.thirdLogin.LoginManager;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.NomalAlertDialog;
import com.facebook.login.widget.LoginButton;
import com.will.common.tool.DeviceTool;
import com.will.common.tool.network.NetWorkUtil;
import com.will.view.ToastUtils;

/**
 * 手机登陆
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final int MSG_LOGIN_SUCC = 1;
    public static final int MSG_LOGIN_ERR = -1;
    public static final int MSG_GOTO_LOGIN = 2;
    public static final int MSG_ANIMATION = 3;

    private TextView mPhoneLogin, tv_register;
    private TextView mLinceseLink;
    private LoginManager loginManager;
    private ImageView iv_logo;
    private LinearLayout layout_login;

    private com.facebook.CallbackManager callbackManager;
    private LoginButton loginButton;
    private boolean isLogin = false;

    private Start mStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        initData();
        initView();
        setView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        callbackManager = FbProxy.init();
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        mLinceseLink = (TextView) findViewById(R.id.license_link);
        mPhoneLogin = (TextView) findViewById(R.id.phone_login);
        tv_register = (TextView) findViewById(R.id.tv_register);
        loginButton = (LoginButton) findViewById(R.id.facebook_login);
        layout_login = (LinearLayout) findViewById(R.id.layout_login);
    }

    private void setView() {
        new LoginManager(this, loginButton, uiHandler).login(LoginManager.LoginType.FACE_BOOK);
        mPhoneLogin.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        mLinceseLink.setText(Html.fromHtml("<u>" + getString(R.string.lisence_title) + "</u>"));
        mLinceseLink.setOnClickListener(this);
    }

    private void initData() {
        if (App.isLogin) {
            //外部服务器登陆成功，保证外部服务器成功直接进入首页
            StartActivityHelper.jumpActivityDefault(this, MainActivity.class);
            finish();
        } else {
            if (!NetWorkUtil.isNetworkConnected(this)) {
                NomalAlertDialog.alwaysShow2(this, getString(R.string.seting_newwork),
                        getString(R.string.not_newwork), getString(R.string.ok),
                        new NomalAlertDialog.HandlerDialog() {
                            @Override
                            public void handleOk() {
                                setNetwork();
                            }

                            @Override
                            public void handleCancel() {
                            }
                        }
                );
            } else {
                mStart = new Start(this, backgroundHandler);
                backgroundHandler.postDelayed(mStart, 100);
            }
        }
    }

    /**
     * 点击物理返回按钮**
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.phone_login:
                StartActivityHelper.jumpActivityDefault(this, LoginPasswordActivity.class);
                break;
            case R.id.license_link:
                WebTransportModel webTransportModel = new WebTransportModel();
                webTransportModel.url = CommonUrlConfig.Agreement;
                webTransportModel.title = getString(R.string.lisence_title);
                StartActivityHelper.jumpActivity(this, WebActivity.class, webTransportModel);
                break;
            case R.id.tv_register:

                StartActivityHelper.jumpActivity(this, RegisterFindPWDActivity.class, RegisterFindPWDActivity.FROM_TYPE_REGISTER);
                break;
        }
    }

    private void setNetwork() {
        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
    }

    //接受回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean limit(String id, String password) {
        if (id == null || "".equals(id)) {
            return false;
        }
        //其他限制
        return !(password == null || "".equals(password));
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_GOTO_LOGIN:
                uiHandler.obtainMessage(MSG_ANIMATION).sendToTarget();
                break;
            case MSG_ANIMATION:
                layout_login.setVisibility(View.VISIBLE);
                initAnimation();
                break;
            case MSG_LOGIN_SUCC:
                ToastUtils.showToast(LoginActivity.this, getString(R.string.login_suc));
                BasicUserInfoDBModel userInfo = CacheDataManager.getInstance().loadUser();
                if (userInfo != null && userInfo.userid != null && userInfo.nickname != null) {
                    if (Login.checkUserInfo(userInfo.userid)) {
                        StartActivityHelper.jumpActivity(this, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK, null, MainActivity.class, null);
                    }
                } else {
                    StartActivityHelper.jumpActivityDefault(this, ProfileActivity.class);
                    finish();
                }
                break;
            case MSG_LOGIN_ERR:
                ToastUtils.showToast(this, getString(R.string.login_faild));
                break;
            case FbProxy.FB_LOGIN_SUCCESS:
                Log.e("success--->", "success");
                if (!isLogin) {
                    CacheDataManager.getInstance().deleteAll();
                    isLogin = true;
                    new Register(this, uiHandler).fbRegister((String) msg.obj, DeviceTool.getUniqueID(LoginActivity.this));
                }
                break;
            case FbProxy.FB_LOGIN_ERROR:
                Log.e("error--->", "error");
                break;
        }
    }

    private void initAnimation() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.logo_animation);
        iv_logo.startAnimation(animation);
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        Animation animation2 = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_out_login);
        layout_login.startAnimation(animation2);
//            }
//        }).start();
    }
}
