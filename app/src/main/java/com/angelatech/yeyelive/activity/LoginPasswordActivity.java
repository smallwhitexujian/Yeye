package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.Login;
import com.angelatech.yeyelive.activity.function.PhoneLogin;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.CountrySelectItemModel;
import com.angelatech.yeyelive.model.LoginUserModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.StringHelper;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.string.json.JsonUtil;
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
    private CountrySelectItemModel selectItemModel;
    private EditText ed_pass_word, ed_phoneNumber;

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
        headerLayout.showLeftBackButton();
        mAreaText.setText(StringHelper.formatStr(getString(R.string.phone_login_area_prefix), getString(R.string.phone_login_default_country_area_num), ""));
        mSelectCountry.setText(getString(R.string.phone_login_default_country));
        mSelectCountry.setOnClickListener(this);
        tv_find_password.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        LoginUserModel loginUserModel = StartActivityHelper.getTransactionSerializable_1(this);
        if (loginUserModel != null) {
            ed_phoneNumber.setText(loginUserModel.phone);
            ed_pass_word.setText(loginUserModel.password);
            uiHandler.obtainMessage(MSG_LOGIN_NOW, loginUserModel).sendToTarget();
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_LOGIN_SUCC:
                BasicUserInfoDBModel model = (BasicUserInfoDBModel) msg.obj;
                try {
                    if (Login.checkUserInfo(model.userid)) {
                        ToastUtils.showToast(this, getString(R.string.login_suc));
                        StartActivityHelper.jumpActivity(this, Intent.FLAG_ACTIVITY_CLEAR_TASK, null, MainActivity.class, null);
                    } else {
                        StartActivityHelper.jumpActivityDefault(this, ProfileActivity.class);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_LOGIN_NOW:
                LoadingDialog.showSysLoadingDialog(LoginPasswordActivity.this, getString(R.string.login_now));
                LoginUserModel loginUserModel = (LoginUserModel) msg.obj;
                login(loginUserModel.countryCode + loginUserModel.phone, loginUserModel.password);
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
                String phone = ed_phoneNumber.getText().toString();
                String password = ed_pass_word.getText().toString();
                login(StringHelper.stringMerge(mAreaText.getText().toString().replace("+", ""), phone), password);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            selectItemModel = data.getParcelableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
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
            LoadingDialog.showSysLoadingDialog(this,getString(R.string.login_now));
            new PhoneLogin(this).loginPwd(userId, Md5.md5(password), httpCallback);
        }
    }

    /**
     * 保存密码回调
     * 保存密码到本地数据库
     */
    HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
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
}
