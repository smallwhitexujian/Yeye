package com.will.yeye.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;
import com.will.common.string.json.JsonUtil;
import com.will.yeye.CommonUrlConfig;
import com.will.yeye.activity.base.HeaderBaseActivity;
import com.will.yeye.activity.function.Binding;
import com.will.yeye.application.App;
import com.will.yeye.db.model.BasicUserInfoDBModel;
import com.will.yeye.qqapi.BaseUiListenerImpl;
import com.will.yeye.qqapi.QQModel;
import com.will.yeye.thirdLogin.FbProxy;
import com.will.yeye.thirdLogin.LoginManager;
import com.will.yeye.thirdLogin.QQProxy;
import com.will.yeye.thirdLogin.WxProxy;
import com.will.yeye.util.CacheDataManager;
import com.will.yeye.util.ClearCacheUtil;
import com.will.yeye.util.StartActivityHelper;
import com.will.yeye.util.StringHelper;
import com.will.yeye.view.CommDialog;
import com.will.yeye.web.HttpFunction;
import com.will.yeye.R;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置界面
 */
public class SettingActivity extends HeaderBaseActivity {
    private final String IS_BIND_QQ_KEY = "isQq";
    private final String IS_BIND_WEICHAT_KEY = "isWx";
    private final String IS_BIND_PHONE_KEY = "isPh";
    private final String IS_BIND_FACEBOOK_KEY = "isFb";

    private final int MSG_CHECK_CALL_SUC = 1;
    private final int MSG_CHECK_CALL_FAIL = 0;

    private ImageView notifyTurn;

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;

    private TextView bindQQ, bindPhone, bindWeichat, bind_facebook;
    private LinearLayout bindQQLayout, bindPhoneLayout,
            bindWeichatLayout, clearCacheLayout,
            feedbackLayout, aboutLayout, blacklistLayout;

    private HttpFunction settingFunction = new HttpFunction(SettingActivity.this);
    private BasicUserInfoDBModel userInfo = CacheDataManager.getInstance().loadUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        setView();
        load();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //接受回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.setting_title));
        headerLayout.showLeftBackButton(R.id.backBtn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notifyTurn = (ImageView) findViewById(R.id.notify_turn);

        bindQQ = (TextView) findViewById(R.id.bind_qq);
        bindQQLayout = (LinearLayout) findViewById(R.id.bind_qq_layout);

        bindPhone = (TextView) findViewById(R.id.bind_phone);
        bindPhoneLayout = (LinearLayout) findViewById(R.id.bind_phone_layout);

        bindWeichat = (TextView) findViewById(R.id.bind_weichat);
        bindWeichatLayout = (LinearLayout) findViewById(R.id.bind_weichat_layout);

        bind_facebook= (TextView) findViewById(R.id.bind_facebook);

        clearCacheLayout = (LinearLayout) findViewById(R.id.clear_cache_layout);

        feedbackLayout = (LinearLayout) findViewById(R.id.feedback_layout);

        aboutLayout = (LinearLayout) findViewById(R.id.about_us_layout);

        blacklistLayout = (LinearLayout) findViewById(R.id.blacklist_layout);

        loginButton = (LoginButton) findViewById(R.id.facebook_login);
    }

    private void setView() {
        bindPhoneLayout.setOnClickListener(this);
        bindQQLayout.setOnClickListener(this);
        bindWeichatLayout.setOnClickListener(this);
        clearCacheLayout.setOnClickListener(this);
        notifyTurn.setOnClickListener(this);
        feedbackLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        blacklistLayout.setOnClickListener(this);
        callbackManager = FbProxy.init();
        new LoginManager(this, loginButton, uiHandler).login(LoginManager.LoginType.FACE_BOOK);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_phone_layout:
                StartActivityHelper.jumpActivityDefault(this, PhoneBindActivity.class);
                break;
            case R.id.bind_qq_layout:
                new QQProxy(SettingActivity.this, uiHandler).bind();
                break;
            case R.id.bind_weichat_layout:
                new WxProxy(SettingActivity.this, uiHandler).bind();
                break;
            case R.id.notify_turn:
                setLiveNotify();
                break;
            case R.id.about_us_layout:
                StartActivityHelper.jumpActivityDefault(this, AboutUsActivity.class);
                break;
            case R.id.feedback_layout:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.blacklist_layout:
                startActivity(new Intent(this, BlacklistActivity.class));
                break;
            case R.id.clear_cache_layout:
                CommDialog commDialog = new CommDialog();
                CommDialog.Callback callback = new CommDialog.Callback() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onOK() {
                        ClearCacheUtil.clearFile();
                        ClearCacheUtil.clearImageCache();
                    }
                };
                commDialog.CommDialog(this, getString(R.string.setting_clear_cache_dialog), true, callback);
                break;
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_CHECK_CALL_SUC:
                setUI((Map) msg.obj);
                break;
            case WxProxy.WX_BIND:
                new Binding(this, uiHandler).bindWeichat(userInfo.userid, userInfo.token, (String) msg.obj);
                break;
            case BaseUiListenerImpl.MSG_QQ_BIND:
                QQModel qqModel = (QQModel) msg.obj;
                new Binding(this, uiHandler).bindQQ(userInfo.userid, userInfo.token, qqModel.access_token, qqModel.openid);
                break;
            case Binding.MSG_BIND_SUCC:
                load();
                break;
            case Binding.MSG_BIND_FAILD:
                ToastUtils.showToast(this, getString(R.string.setting_bind_faild));
                break;
            case FbProxy.FB_LOGIN_SUCCESS:
                Log.e("success--->", "success");
                Profile profile = (Profile) msg.obj;
                Response(profile);
                break;
            case FbProxy.FB_LOGIN_ERROR:
                Log.e("error--->", "error");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void load() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {

            }

            @Override
            public void onSuccess(String response) {
                Map map = JsonUtil.fromJson(response, Map.class);
                if(map == null){
                    //服务器异常
                    return;
                }
                if (HttpFunction.isSuc((String) map.get("code"))) {
                    uiHandler.obtainMessage(MSG_CHECK_CALL_SUC, map.get("data")).sendToTarget();
                } else {
                    onBusinessFaild((String) map.get("code"));
                }
            }
        };
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userid", userInfo.userid);
            params.put("token", userInfo.token);
            settingFunction.httpGet(CommonUrlConfig.UserBindingCheck, params, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUI(Map map) {
        //String isBindQQ = (String) map.get(IS_BIND_QQ_KEY);
        String isBindWeichat = (String) map.get(IS_BIND_WEICHAT_KEY);
        String isBindPhone = (String) map.get(IS_BIND_PHONE_KEY);
        String isBindFacebook = (String) map.get(IS_BIND_FACEBOOK_KEY);

//        if ((HttpFunction.TRUE + "").equals(isBindQQ)) {
//            bindQQ.setText(getString(R.string.setting_binded));
//        } else {
//            bindQQ.setText(getString(R.string.setting_go_bind));
//        }
        if ((HttpFunction.TRUE + "").equals(isBindWeichat)) {
            bindWeichat.setText(getString(R.string.setting_binded));
        } else {
            bindWeichat.setText(getString(R.string.setting_go_bind));
        }
        if ((HttpFunction.TRUE + "").equals(isBindFacebook)) {
            bind_facebook.setText(getString(R.string.setting_binded));
        } else {
            bind_facebook.setText(getString(R.string.setting_go_bind));
        }
        if (isBindPhone != null && !"0".equals(isBindPhone)) {
            bindPhone.setText(StringHelper.replaceStr(isBindPhone, 3, 4, '*'));
        } else {
            bindPhone.setText(getString(R.string.setting_go_bind));
        }
    }

    private void setLiveNotify() {
        if (App.isLiveNotify) {
            App.isLiveNotify = false;
            notifyTurn.setImageResource(R.drawable.btn_me_switch_s);
        } else {
            App.isLiveNotify = true;
            notifyTurn.setImageResource(R.drawable.btn_me_switch_n);
        }
    }

    /**
     * 获取返回结果
     */
    private void Response(Profile profiles) {
        CacheDataManager cacheDataManager = CacheDataManager.getInstance();
        BasicUserInfoDBModel model = cacheDataManager.loadUser();
        new Binding(SettingActivity.this, uiHandler).bindFacebook(model.userid, model.token, profiles);
    }
}
