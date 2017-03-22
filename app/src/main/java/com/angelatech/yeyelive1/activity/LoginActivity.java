package com.angelatech.yeyelive1.activity;

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

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.activity.base.BaseActivity;
import com.angelatech.yeyelive1.activity.function.Login;
import com.angelatech.yeyelive1.activity.function.MainEnter;
import com.angelatech.yeyelive1.activity.function.Register;
import com.angelatech.yeyelive1.activity.function.Start;
import com.angelatech.yeyelive1.application.App;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.CommonListResult;
import com.angelatech.yeyelive1.model.VoucherModel;
import com.angelatech.yeyelive1.model.WebTransportModel;
import com.angelatech.yeyelive1.service.IServiceHelper;
import com.angelatech.yeyelive1.service.IServiceValues;
import com.angelatech.yeyelive1.thirdLogin.FbProxy;
import com.angelatech.yeyelive1.thirdLogin.LoginManager;
import com.angelatech.yeyelive1.thirdLogin.WxProxy;
import com.angelatech.yeyelive1.util.BroadCastHelper;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.util.Utility;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.view.NomalAlertDialog;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.LoginButton;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.tool.DeviceTool;
import com.will.common.tool.network.NetWorkUtil;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

/**
 * 手机登陆
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final int MSG_LOGIN_SUCC = 1;
    public static final int MSG_LOGIN_ERR = -1;
    public static final int MSG_GOTO_LOGIN = 2;
    private static final int MSG_ANIMATION = 3;

    private TextView tv_register;
    private TextView mLinceseLink;
    private ImageView iv_logo, iv_we_chat, mPhoneLogin;
    private LinearLayout layout_login;

    private com.facebook.CallbackManager callbackManager;
    private LoginButton loginButton;
    private boolean isLogin = false;
    private Utility utility;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        initView();
        setView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadingDialog.cancelLoadingDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        callbackManager = FbProxy.init();
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        mLinceseLink = (TextView) findViewById(R.id.license_link);
        mPhoneLogin = (ImageView) findViewById(R.id.phone_login);
        iv_we_chat = (ImageView) findViewById(R.id.iv_we_chat);
        tv_register = (TextView) findViewById(R.id.tv_register);
        loginButton = (LoginButton) findViewById(R.id.facebook_login);
        layout_login = (LinearLayout) findViewById(R.id.layout_login);
    }

    private void setView() {
        new LoginManager(this, loginButton, uiHandler).login(LoginManager.LoginType.FACE_BOOK);
        mPhoneLogin.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        iv_we_chat.setOnClickListener(this);
        mLinceseLink.setText(Html.fromHtml("<u>" + getString(R.string.lisence_title) + "</u>"));
        mLinceseLink.setOnClickListener(this);
        MainEnter mainEnter = new MainEnter(this);
        utility = new Utility();
        mainEnter.configImage(CommonUrlConfig.configImage, new HttpBusinessCallback() {
            @Override
            public void onSuccess(final String response) {
                super.onSuccess(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonListResult<VoucherModel> responseData = JsonUtil.fromJson(response, new TypeToken<CommonListResult<VoucherModel>>() {
                        }.getType());
                        if (responseData != null && responseData.code.equals("1000")) {
                            for (int i = 0; i < responseData.data.size(); i++) {
                                String url = responseData.data.get(i).value;
                                int fileName = 1000 + i;
                                utility.setSaveImage(url, String.valueOf(fileName));
                            }
                        }
                    }
                });
            }
        });
    }

    private void initData() {
        if (App.isLogin) {
            //外部服务器登陆成功，保证外部服务器成功直接进入首页
            StartActivityHelper.jumpActivityDefault(this, TabMenuActivity.class);
            finish();
        } else {
            if (!NetWorkUtil.isNetworkConnected(this)) {
                new NomalAlertDialog().alwaysShow2(this, getString(R.string.setting_network),
                        getString(R.string.not_network), getString(R.string.ok),
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
                Start mStart = new Start(this, backgroundHandler);
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
                DebugLogs.e("phone---->start");
                StartActivityHelper.jumpActivityDefault(this, LoginPasswordActivity.class);
                finish();
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
            case R.id.iv_we_chat:
                LoadingDialog.showLoadingDialog(LoginActivity.this,null);
                DebugLogs.e("wechat---->start");
                WxProxy wxProxy = new WxProxy(this, uiHandler);
                wxProxy.login();
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

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_GOTO_LOGIN: //不是ui 主线程
                uiHandler.obtainMessage(MSG_ANIMATION).sendToTarget();
                break;
            case MSG_ANIMATION:
                layout_login.setVisibility(View.VISIBLE);
                initAnimation();
                break;
            case MSG_LOGIN_SUCC: //注册成功流程
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(LoginActivity.this, getString(R.string.login_suc));
                BasicUserInfoDBModel userInfo = CacheDataManager.getInstance().loadUser();
                if ( userInfo.userid != null && userInfo.nickname != null) {
                    if (Login.checkUserInfo(userInfo.userid)) {
                        StartActivityHelper.jumpActivity(this, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK, null, TabMenuActivity.class, null);
                        finish();
                    }
                } else {
                    StartActivityHelper.jumpActivityDefault(this, ProfileActivity.class);
                    finish();
                }
                break;
            case MSG_LOGIN_ERR:
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(this, getString(R.string.login_faild));
                Intent exitIntent = IServiceHelper.getBroadcastIntent(IServiceValues.ACTION_CMD_WAY,
                        IServiceValues.CMD_EXIT_LOGIN);
                BroadCastHelper.sendBroadcast(this, exitIntent);
                break;
            case FbProxy.FB_LOGIN_SUCCESS:
                LoadingDialog.showLoadingDialog(LoginActivity.this,null);
                Log.e("success--->", "success");
                Bundle bundle = new Bundle();
                bundle.putString("limit", "1000");
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/me/invitable_friends",
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                FacebookRequestError requestError = response.getError();
                                FacebookException exception = (requestError == null) ? null : requestError.getException();
                                if (response.getJSONObject() == null && exception == null) {
                                    exception = new FacebookException("result error");
                                }
                                if (exception != null) {
                                    DebugLogs.d("GraphRequest error :" + exception.getMessage() + ",trace :" + exception.getStackTrace());
                                } else {
                                    final JSONObject graphObject = response.getJSONObject();
                                    DebugLogs.d("---------->" + graphObject.toString());
                                }
                            }
                        }
                ).executeAsync();
                if (!isLogin) {
                    CacheDataManager.getInstance().deleteAll();
                    isLogin = true;
                    new Register(this, uiHandler).fbRegister((String) msg.obj, DeviceTool.getUniqueID(LoginActivity.this));



//                    new GraphRequest(
//                            AccessToken.getCurrentAccessToken(),
//                            "read_custom_friendlists",
//                            null,
//                            HttpMethod.GET,
//                            new GraphRequest.Callback() {
//                                public void onCompleted(GraphResponse response) {
//                                    DebugLogs.e("GraphResponse"+response.toString());
//            /* handle the result */
//                                }
//                            }
//                    ).executeAsync();
                }
                break;
            case FbProxy.FB_LOGIN_ERROR:
                LoadingDialog.showLoadingDialog(LoginActivity.this,null);
                Log.e("error--->", "error");
                AccessToken.setCurrentAccessToken(null);
                LoadingDialog.cancelLoadingDialog();
                break;
            case WxProxy.WX_LOGIN:
                LoadingDialog.cancelLoadingDialog();
                LoadingDialog.showLoadingDialog(LoginActivity.this,null);
                DebugLogs.e("======微信注册======");
                new Register(this, uiHandler).wxRegister(msg.obj.toString(), DeviceTool.getUniqueID(this));
                break;
        }
    }

    private void initAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        iv_logo.startAnimation(animation);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.fade_out_login);
        layout_login.startAnimation(animation2);
    }
}
