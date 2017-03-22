package com.angelatech.yeyelive1.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.activity.function.PhoneLogin;
import com.angelatech.yeyelive1.activity.function.Register;
import com.angelatech.yeyelive1.db.BaseKey;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.CountrySelectItemModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.ErrorHelper;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.util.StringHelper;
import com.angelatech.yeyelive1.util.VerificationUtil;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.will.common.log.DebugLogs;
import com.will.common.string.security.Md5;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 手机登录注册 验证码方式登录
 */
public class SetPayPwdActivity extends HeaderBaseActivity {
    private final int MSG_REFRESH_TIME = 1;
    private final int MSG_REFRESH_TIME_FIN = 2;
    public final int MSG_FIND_PASSWORD_SUCCESS = 19;
    private final int MSG_FIND_PASSWORD_ERROR = 20;
    private final int MSG_LEGAL_INPUT_PHONE = 6;
    private final int MSG_SEND_CODE_PHONE = 22;
    private String loginUserId, password, countryCode, phoneCode, confirWord;
    private final int TOTAL_TIME = 60;
    private int coutTime = 0;
    private BasicUserInfoDBModel userInfo;
    private EditText mInputPhone, mVerificationCode, ed_pass_word, confir_word;
    private TextView mLoginBtn, mSendBtn, mSelectCountry, mAreaText, mHitText;

    // 定时器相关
    private TimerTask timerTask;
    private Timer timer;

    private PhoneLogin mPhoneLogin;
    private boolean isRunTimer = false;

    private SmsContent contentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_pwd);
        //注册短信变化监听
        contentObserver = new SmsContent(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, contentObserver);
        initView();
        setView();
        mPhoneLogin = new PhoneLogin(this);
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        mInputPhone = (EditText) findViewById(R.id.input_phone);
        mVerificationCode = (EditText) findViewById(R.id.input_verification_code);
        mLoginBtn = (TextView) findViewById(R.id.login_btn);
        mSendBtn = (TextView) findViewById(R.id.send_btn);
        mSelectCountry = (TextView) findViewById(R.id.select_country);
        mAreaText = (TextView) findViewById(R.id.area_text);
        mHitText = (TextView) findViewById(R.id.hint_textview);
        ed_pass_word = (EditText) findViewById(R.id.ed_pass_word);
        confir_word = (EditText) findViewById(R.id.confir_word);
        headerLayout.showTitle(getString(R.string.set_pay_pwd));
    }

    private void setView() {
        userInfo = CacheDataManager.getInstance().loadUser();
        mAreaText.setText(StringHelper.formatStr(getString(R.string.phone_login_area_prefix), getString(R.string.phone_login_default_country_area_num), ""));
        mSelectCountry.setText(getString(R.string.phone_login_default_country));
        mInputPhone.addTextChangedListener(watcher);
        ed_pass_word.addTextChangedListener(watcher);
        mVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                uiHandler.obtainMessage(MSG_LEGAL_INPUT_PHONE, s.toString()).sendToTarget();
            }
        });

        mLoginBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mSelectCountry.setOnClickListener(this);
        setIsWork();
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            uiHandler.sendEmptyMessage(MSG_SEND_CODE_PHONE);
        }
    };

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_REFRESH_TIME:
                mSendBtn.setText(getString(R.string.phone_login_retry_send, "" + msg.obj));
                break;
            case MSG_SEND_CODE_PHONE:
                setIsWork();
                break;
            case MSG_REFRESH_TIME_FIN:
                mSendBtn.setText(getString(R.string.phone_login_send_code));
                mSendBtn.setEnabled(true);
                mSendBtn.setTextColor(0xFF222222);
                break;
            case MSG_LEGAL_INPUT_PHONE:
                setIsWork();
                mHitText.setText("");
                break;
            case Register.REGISTER_SUCCESS:
            case MSG_FIND_PASSWORD_SUCCESS:
                ToastUtils.showToast(this, getString(R.string.pay_pwd_success));
                finish();
                break;
            case MSG_FIND_PASSWORD_ERROR:
            case Register.REGISTER_ERROR:
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(this, ErrorHelper.getErrorHint(this, msg.obj.toString()));
                mLoginBtn.setEnabled(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                mLoginBtn.setEnabled(false);
                LoadingDialog.showLoadingDialog(this, null);
                findPassword();
                break;
            case R.id.send_btn:
                countryCode = mAreaText.getText().toString().replace("+", "");
                loginUserId = mInputPhone.getText().toString();
                if (loginUserId.startsWith("0")) {
                    loginUserId = loginUserId.replaceFirst("0", "");
                }
                LoadingDialog.showLoadingDialog(this, null);
                mPhoneLogin.getCode(CommonUrlConfig.GetPhoneCode, StringHelper.stringMerge(countryCode, loginUserId), sendCode);
                stopTimer();
                startTimer();
                break;
            case R.id.select_country:
                StartActivityHelper.jumpActivityForResult(this, CountrySelectActivity.class, 1);
                break;
        }

    }

    /**
     * 设置密码
     */
    private void findPassword() {
        countryCode = mAreaText.getText().toString().replace("+", "");
        loginUserId = mInputPhone.getText().toString();
        if (loginUserId.startsWith("0")) {
            loginUserId = loginUserId.replaceFirst("0", "");
        }
        phoneCode = mVerificationCode.getText().toString();
        password = ed_pass_word.getText().toString();
        confirWord = confir_word.getText().toString();
        if (!password.equals(confirWord)) {
            LoadingDialog.cancelLoadingDialog();
            ToastUtils.showToast(this, getString(R.string.password_error));
        } else if (!phoneCode.isEmpty() && !password.isEmpty()) {
            mPhoneLogin.setPayPwd(StringHelper.stringMerge(countryCode, loginUserId), phoneCode, Md5.md5(password), userInfo.userid, userInfo.token, new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    LoadingDialog.cancelLoadingDialog();
                }

                @Override
                public void onSuccess(String response) {
                    LoadingDialog.cancelLoadingDialog();
                    DebugLogs.e("response9:" + response);
                    Map result = JsonUtil.fromJson(response, Map.class);
                    if (result != null) {
                        if (HttpFunction.isSuc(result.get("code").toString())) {
                            CacheDataManager.getInstance().update(BaseKey.ISPWDpassword, 1, userInfo.userid);
                            uiHandler.sendEmptyMessage(MSG_FIND_PASSWORD_SUCCESS);
                        } else {
                            uiHandler.obtainMessage(MSG_FIND_PASSWORD_ERROR, result.get("code")).sendToTarget();
                        }
                    }
                }
            });
        }
        mLoginBtn.setEnabled(true);
    }

    private HttpBusinessCallback sendCode = new HttpBusinessCallback() {
        @Override
        public void onSuccess(String response) {
            LoadingDialog.cancelLoadingDialog();
            Map item = JsonUtil.fromJson(response, Map.class);
            if (item != null && !item.get("code").equals("1000")) {
                onBusinessFaild(item.get("code").toString());
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(SetPayPwdActivity.this, getString(R.string.success));
                    }
                });
            }
        }

        @Override
        public void onFailure(Map<String, ?> errorMap) {
            super.onFailure(errorMap);
            LoadingDialog.cancelLoadingDialog();
        }
    };

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    /***
     * 私有方法区
     */
    //启动定时器
    private void startTimer() {
        coutTime = 0;
        isRunTimer = true;
        timer = new Timer();
        // 1秒钟执行一次
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (coutTime++ >= TOTAL_TIME) {
                    uiHandler.sendEmptyMessage(MSG_REFRESH_TIME_FIN);
                    isRunTimer = false;
                    stopTimer();
                } else {
                    uiHandler.obtainMessage(MSG_REFRESH_TIME, (TOTAL_TIME - coutTime)).sendToTarget();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
        uiHandler.obtainMessage().sendToTarget();
        mSendBtn.setEnabled(false);
        mSendBtn.setTextColor(0xFFA6A6A6);
    }

    //停止定时器
    private void stopTimer() {
        isRunTimer = false;
        // 取消定时任务
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        // 取消定时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timerTask = null;
        timer = null;
    }

    private void setIsWork() {
        mHitText.setText("");
        loginUserId = mInputPhone.getText().toString();
        password = ed_pass_word.getText().toString();
        confirWord = confir_word.getText().toString();
        if (!"".equals(loginUserId)) {
            phoneCode = mVerificationCode.getText().toString();
            if (!isRunTimer) {
                mSendBtn.setEnabled(true);
                mSendBtn.setTextColor(0xFF222222);
            } else {
                mSendBtn.setEnabled(false);
                mSendBtn.setTextColor(0xFFA6A6A6);
            }
            if (!"".equals(phoneCode) && password != null && !password.equals("")) {
                mLoginBtn.setEnabled(true);
                mLoginBtn.setBackgroundResource(R.drawable.common_btn_bg);
                mLoginBtn.setTextColor(0xFF222222);
            } else {
                mLoginBtn.setEnabled(false);
                mLoginBtn.setBackgroundResource(R.drawable.common_btn_unuse_bg);
                mLoginBtn.setTextColor(0xFFA6A6A6);
            }
        } else {
            mSendBtn.setEnabled(false);
//            mSendBtn.setBackgroundResource(R.drawable.common_btn_unuse_bg);
            mSendBtn.setTextColor(0xFFA6A6A6);
            mLoginBtn.setEnabled(false);
            mLoginBtn.setBackgroundResource(R.drawable.common_btn_unuse_bg);
            mLoginBtn.setTextColor(0xFFA6A6A6);
        }
    }

    private class SmsContent extends ContentObserver {
        private Cursor cursor = null;

        public SmsContent(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //读取收件箱中指定号码的短信
            Uri outMMS = Uri.parse("content://sms/inbox");
            cursor = getContentResolver().query(outMMS, null, null, null, "date DESC");//按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
            if (cursor != null && cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put("read", "1"); //修改短信为已读模式
                cursor.moveToNext();
                int bodyColumn = cursor.getColumnIndex("body");
                String smsBody = cursor.getString(bodyColumn);
                mVerificationCode.setText(VerificationUtil.getDynamicPassword(smsBody));
            }
            //在用managedQuery的时候，不能主动调用close()方法，
            // 否则在Android 4.0+的系统上， 会发生崩溃
            if (Build.VERSION.SDK_INT < 14 && cursor != null) {
                cursor.close();
            }
        }
    }
}
