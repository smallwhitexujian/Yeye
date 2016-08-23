package com.angelatech.yeyelive.activity;

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

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.PhoneLogin;
import com.angelatech.yeyelive.activity.function.Register;
import com.angelatech.yeyelive.model.CountrySelectItemModel;
import com.angelatech.yeyelive.model.LoginUserModel;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.StringHelper;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.common.string.json.JsonUtil;
import com.will.common.string.security.Md5;
import com.will.common.tool.DeviceTool;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 手机登录注册 验证码方式登录
 */
public class RegisterFindPWDActivity extends HeaderBaseActivity {
    public final static int FROM_TYPE_REGISTER = 1; //1注册
    public final static int FROM_TYPE_FIND_PASSWORD = 2;//找回密码
    private int fromType;
    private final int MSG_REFRESH_TIME = 1;
    private final int MSG_REFRESH_TIME_FIN = 2;
    public final int MSG_FIND_PASSWORD_SUCCESS = 19;
    private final int MSG_LEGAL_INPUT_PHONE = 6;
    private final int MSG_SEND_CODE_PHONE = 22;
    private final int MSG_ILLEGAL_INPUT_CODE = 7;
    private final int MSG_LEGAL_INPUT_CODE = 8;
    private CountrySelectItemModel selectItemModel;
    private String phone, password, countryCode;
    private final int TOTAL_TIME = 60;
    private int coutTime = 0;

    private EditText mInputPhone, mVerificationCode, ed_pass_word;
    private TextView mLoginBtn, mSendBtn, mSelectCountry, mAreaText, mHitText;

    // 定时器相关
    private TimerTask timerTask;
    private Timer timer;

    private PhoneLogin mPhoneLogin;
    private boolean isRunTimer = false;

    SmsContent contentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        //注册短信变化监听
        contentObserver = new SmsContent(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, contentObserver);
        initView();
        setView();
        mPhoneLogin = new PhoneLogin(this);
    }

    private void initView() {
        fromType = StartActivityHelper.getInt(this);
        headerLayout.showLeftBackButton();
        mInputPhone = (EditText) findViewById(R.id.input_phone);
        mVerificationCode = (EditText) findViewById(R.id.input_verification_code);
        mLoginBtn = (TextView) findViewById(R.id.login_btn);
        mSendBtn = (TextView) findViewById(R.id.send_btn);
        mSelectCountry = (TextView) findViewById(R.id.select_country);
        mAreaText = (TextView) findViewById(R.id.area_text);
        mHitText = (TextView) findViewById(R.id.hint_textview);
        ed_pass_word = (EditText) findViewById(R.id.ed_pass_word);

        if (fromType == FROM_TYPE_REGISTER) {
            headerLayout.showTitle(getString(R.string.activity_register));
            ed_pass_word.setHint(getString(R.string.set_password));
        } else {
            headerLayout.showTitle(getString(R.string.find_password));
            ed_pass_word.setHint(getString(R.string.reset_password));
        }
    }

    private void setView() {
        mAreaText.setText(StringHelper.formatStr(getString(R.string.phone_login_area_prefix), getString(R.string.phone_login_default_country_area_num), ""));
        mSelectCountry.setText(getString(R.string.phone_login_default_country));
        mInputPhone.addTextChangedListener(new TextWatcher() {
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
        });
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
            case MSG_LEGAL_INPUT_CODE:
                setIsWork();
                mHitText.setText("");
                break;
            case MSG_ILLEGAL_INPUT_CODE:
                setIsWork();
                break;
            case Register.REGISTER_SUCCESS:
            case MSG_FIND_PASSWORD_SUCCESS:
                LoginUserModel loginUserModel = new LoginUserModel();
                loginUserModel.phone = phone;
                loginUserModel.password = password;
                loginUserModel.countryCode = countryCode;
                StartActivityHelper.jumpActivity(this, LoginPasswordActivity.class, loginUserModel);
                break;
            case Register.REGISTER_ERROR:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                mLoginBtn.setEnabled(false);
                if (fromType == FROM_TYPE_REGISTER) {
                    Register();
                } else {
                    findPassword();
                }
                break;
            case R.id.send_btn:
                countryCode = mAreaText.getText().toString().replace("+", "");
                phone = mInputPhone.getText().toString();
                LoadingDialog.showLoadingDialog(this);
                mPhoneLogin.getCode(CommonUrlConfig.GetPhoneCode, StringHelper.stringMerge(countryCode, phone), sendCode);
                stopTimer();
                startTimer();
                break;
            case R.id.select_country:
                StartActivityHelper.jumpActivityForResult(this, CountrySelectActivity.class, 1);
                break;
            case Register.REGISTER_SUCCESS:

                break;
        }

    }

    /**
     * 注册登录
     */
    private void Register() {
        String code = mVerificationCode.getText().toString();
        password = ed_pass_word.getText().toString();
        if (phone.startsWith("0")) {
            phone = phone.replaceFirst("0", "");
        }
        if (!code.isEmpty() && !password.isEmpty()) {
            if (VerificationUtil.isContainLetterNumber(password)) {
                new Register(this, uiHandler).phoneRegister(StringHelper.stringMerge(countryCode, phone), code,
                        Md5.md5(password), DeviceTool.getUniqueID(RegisterFindPWDActivity.this));
            } else {
                ToastUtils.showToast(this, getString(R.string.password_error));
            }
        }
        mLoginBtn.setEnabled(true);
    }

    /**
     * 找回密码
     */
    private void findPassword() {
        String code = mVerificationCode.getText().toString();
        countryCode = mAreaText.getText().toString().replace("+", "");
        password = ed_pass_word.getText().toString();
        if (phone.startsWith("0")) {
            phone = phone.replaceFirst("0", "");
        }
        if (!code.isEmpty() && !password.isEmpty()) {
            if (VerificationUtil.isContainLetterNumber(password)) {
                mPhoneLogin.findPassword(StringHelper.stringMerge(countryCode, phone), code, Md5.md5(password), new HttpBusinessCallback() {
                    @Override
                    public void onFailure(Map<String, ?> errorMap) {
                        LoadingDialog.cancelLoadingDialog();
                    }

                    @Override
                    public void onSuccess(String response) {
                        LoadingDialog.cancelLoadingDialog();
                        Map result = JsonUtil.fromJson(response, Map.class);
                        if (result != null) {
                            if (HttpFunction.isSuc(result.get("code").toString())) {
                                uiHandler.sendEmptyMessage(MSG_FIND_PASSWORD_SUCCESS);
                            } else {
                                onBusinessFaild(result.get("code").toString());
                            }
                        }
                    }
                });
            } else {
                ToastUtils.showToast(this, getString(R.string.password_error));
            }
        }
        mLoginBtn.setEnabled(true);
    }

    private HttpBusinessCallback sendCode = new HttpBusinessCallback() {
        @Override
        public void onSuccess(String response) {
            LoadingDialog.cancelLoadingDialog();
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
            selectItemModel = data.getParcelableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
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
        phone = mInputPhone.getText().toString();
        if (!"".equals(phone)) {
            String code = mVerificationCode.getText().toString();
            if (!isRunTimer) {
                mSendBtn.setEnabled(true);
                mSendBtn.setTextColor(0xFF222222);
            } else {
                mSendBtn.setEnabled(false);
                mSendBtn.setTextColor(0xFFA6A6A6);
            }
            if (!"".equals(code)) {
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

    class SmsContent extends ContentObserver {
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
