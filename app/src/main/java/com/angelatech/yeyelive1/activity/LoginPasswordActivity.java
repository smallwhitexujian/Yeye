package com.angelatech.yeyelive1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.angelatech.yeyelive1.Constant;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.activity.function.Login;
import com.angelatech.yeyelive1.activity.function.PhoneLogin;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.CommonParseListModel;
import com.angelatech.yeyelive1.model.CountrySelectItemModel;
import com.angelatech.yeyelive1.model.LoginServerModel;
import com.angelatech.yeyelive1.model.LoginUserModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.util.StringHelper;
import com.angelatech.yeyelive1.util.Utility;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.string.security.Md5;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;

/**
 * User: cbl
 * Date: 2016/7/30
 * Time: 14:05
 * 手机登录 密码方式
 */
public class LoginPasswordActivity extends HeaderBaseActivity {
    private final static int MSG_LOGIN_SUCC = 1;
    private final int MSG_LOGIN_NOW = 2;
    private TextView mSelectCountry, mAreaText, tv_find_password, login_btn;
    private EditText ed_pass_word, ed_phoneNumber;
    private LoginUserModel autoLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login_password);
        initView();
        setView();
    }

    private void initView() {
        mAreaText = (TextView) findViewById(R.id.area_text);
        mSelectCountry = (TextView) findViewById(R.id.select_country);
        tv_find_password = (TextView) findViewById(R.id.tv_find_password);
        ed_phoneNumber = (EditText) findViewById(R.id.ed_PNumber);
        ed_pass_word = (EditText) findViewById(R.id.ed_pass_word);
        login_btn = (TextView) findViewById(R.id.login_btn);
    }

    private void setView() {
        headerLayout.showTitle(R.string.activity_login_password);
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityHelper.jumpActivityDefault(LoginPasswordActivity.this, LoginActivity.class);
                finish();
            }
        });
        mAreaText.setText(StringHelper.formatStr(getString(R.string.phone_login_area_prefix), getString(R.string.phone_login_default_country_area_num), ""));
        mSelectCountry.setText(getString(R.string.phone_login_default_country));
        mSelectCountry.setOnClickListener(this);
        tv_find_password.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        autoLogin = StartActivityHelper.getTransactionSerializable_1(this);
        if (autoLogin != null) {
            CacheDataManager.loginUser = null;
            ed_phoneNumber.setText(autoLogin.phone);
            ed_pass_word.setText(autoLogin.password);
            if (autoLogin.country.length() > 0) {
                mSelectCountry.setText(autoLogin.country);
                mAreaText.setText(StringHelper.formatStr(getString(R.string.phone_login_area_prefix), autoLogin.countryCode, ""));
            }
            uiHandler.sendEmptyMessage(MSG_LOGIN_NOW);
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_LOGIN_SUCC: //密码登录成功流程
                BasicUserInfoDBModel model = (BasicUserInfoDBModel) msg.obj;
                LoginServerModel loginServerModel = new LoginServerModel(Long.valueOf(model.userid), model.token);
                new Login(this).attachIM(loginServerModel);
                try {
                    if (Login.checkUserInfo(model.userid)) {
                        ToastUtils.showToast(this, getString(R.string.login_suc));
                        StartActivityHelper.jumpActivity(this, Intent.FLAG_ACTIVITY_CLEAR_TASK, null, TabMenuActivity.class, null);
                    } else {
                        StartActivityHelper.jumpActivityDefault(this, ProfileActivity.class);
                    }
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_LOGIN_NOW: //自动登录
                LoadingDialog.showLoadingDialog(this, null);
                login(autoLogin.countryCode + autoLogin.phone, autoLogin.password);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.select_country:
                StartActivityHelper.jumpActivityForResult(this, CountrySelectActivity.class, 1);
                break;
            case R.id.tv_find_password:
                StartActivityHelper.jumpActivity(this, RegisterFindPWDActivity.class, RegisterFindPWDActivity.FROM_TYPE_FIND_PASSWORD);
                break;
            case R.id.login_btn:
                Utility.closeKeybord(ed_pass_word, this);
                String loginUserId = ed_phoneNumber.getText().toString();
                String loginUserPassword = ed_pass_word.getText().toString();
                if (loginUserId.startsWith("0")) {
                    loginUserId = loginUserId.replaceFirst("0", "");
                }
                login(StringHelper.stringMerge(mAreaText.getText().toString().replace("+", ""), loginUserId), loginUserPassword);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            CountrySelectItemModel selectItemModel = data.getParcelableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            if (selectItemModel != null) {
                mAreaText.setText(StringHelper.formatStr(getString(R.string.phone_login_area_prefix), selectItemModel.num, ""));
                mSelectCountry.setText(selectItemModel.country);
            }
        }
    }

    /**
     * 登录
     */
    private void login(String userId, String password) {
        if (!userId.isEmpty() && !password.isEmpty()) {
            LoadingDialog.showLoadingDialog(this, null);
            new PhoneLogin(this).loginPwd(userId, Md5.md5(password), httpCallback);
        }
    }

    /**
     * 保存密码回调
     * 保存密码到本地数据库
     */
    private HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            LoadingDialog.cancelLoadingDialog();
        }

        @Override
        public void onSuccess(String response) {
            LoadingDialog.cancelLoadingDialog();
            if (response != null) {
                CommonParseListModel<BasicUserInfoDBModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonParseListModel<BasicUserInfoDBModel>>() {
                }.getType());
                if (datas != null) {
                    if (HttpFunction.isSuc(datas.code)) {
                        BasicUserInfoDBModel userInfoDBModel = datas.data.get(0);
                        if (CacheDataManager.getInstance().loadUser(userInfoDBModel.userid) != null) {
                            CacheDataManager.getInstance().deleteMessageRecord(userInfoDBModel.userid);
                        }
                        userInfoDBModel.loginType = Constant.Login_phone;
                        CacheDataManager.getInstance().save(userInfoDBModel);
                        uiHandler.obtainMessage(MSG_LOGIN_SUCC, userInfoDBModel).sendToTarget();
                    } else {
                        //错误提示
                        onBusinessFaild(datas.code);
                    }
                }
            }
        }
    };

    /**
     * 点击物理返回按钮**
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            StartActivityHelper.jumpActivityDefault(LoginPasswordActivity.this, LoginActivity.class);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
