package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.angelatech.yeyelive.activity.function.Binding;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CountrySelectItemModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.StringHelper;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.common.log.Logger;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 手机绑定
 */
public class PhoneBindActivity extends HeaderBaseActivity {
    private final int MSG_REFRESH_TIME = 1;
    private final int MSG_REFRESH_TIME_FIN = 2;
    public final int MSG_BINDING_SUCC = 3;
    private final int MSG_ILLEGAL_INPUT_PHONE = 5;
    private final int MSG_LEGAL_INPUT_PHONE = 6;
    private final int MSG_ILLEGAL_INPUT_CODE = 7;
    private final int MSG_LEGAL_INPUT_CODE = 8;

    private CountrySelectItemModel selectItemModel;
    private final int TOTAL_TIME = 60;
    private int coutTime = 0;
    private EditText mInputPhone, mVerificationCode;
    private TextView mLoginBtn, mSendBtn, mSelectCountry, mAreaText, mHitText;
    // 定时器相关
    private TimerTask timerTask;
    private Timer timer;
    private Binding mBinding;
    private boolean isRunTimer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        initView();
        setView();
        mBinding = new Binding(this, uiHandler);
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.phone_bind_title));
        headerLayout.showLeftBackButton();
        mInputPhone = (EditText) findViewById(R.id.input_phone);
        mVerificationCode = (EditText) findViewById(R.id.input_verification_code);
        mLoginBtn = (TextView) findViewById(R.id.login_btn);
        mSendBtn = (TextView) findViewById(R.id.send_btn);
        mSelectCountry = (TextView) findViewById(R.id.select_country);
        mAreaText = (TextView) findViewById(R.id.area_text);
        mHitText = (TextView) findViewById(R.id.hint_textview);

    }

    private void setView() {
        mAreaText.setText(getString(R.string.phone_login_default_country_area_num));
        mSelectCountry.setText(getString(R.string.phone_login_default_country));
        mInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkPhoneNum(s.toString())) {
                    uiHandler.obtainMessage(MSG_LEGAL_INPUT_PHONE, s.toString()).sendToTarget();
                } else {
                    uiHandler.sendEmptyMessage(MSG_ILLEGAL_INPUT_PHONE);

                }

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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_REFRESH_TIME:
                mSendBtn.setText(getString(R.string.phone_login_retry_send, "" + msg.obj));
                break;
            case MSG_REFRESH_TIME_FIN:
                mSendBtn.setText(getString(R.string.phone_login_send_code));
                mSendBtn.setEnabled(true);
                mSendBtn.setTextColor(0xFF222222);
                break;
            case MSG_ILLEGAL_INPUT_PHONE:
                //设置发送验证码按钮不可用
                setIsWork();
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
            case Binding.MSG_BIND_FAILD:
                ToastUtils.showToast(this, getString(R.string.phone_bind_faild));
                finish();
                break;
            case Binding.MSG_BIND_SUCC:
                ToastUtils.showToast(this, getString(R.string.phone_bind_suc));
                finish();
                break;
            case MSG_BINDING_SUCC:
                stopTimer();
                startTimer();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        String phone = mInputPhone.getText().toString();
        if (phone.startsWith("0")) {
            phone = phone.replaceFirst("0", "");
        }
        String code = mVerificationCode.getText().toString();
        String areaNum = mAreaText.getText().toString();
        switch (v.getId()) {
            case R.id.login_btn:
                if (!phone.isEmpty()) {
                    BasicUserInfoDBModel infoDBModel = CacheDataManager.getInstance().loadUser();
                    mBinding.bindPhone(infoDBModel.userid, infoDBModel.token, StringHelper.stringMerge(cutAreaNum(areaNum), phone), code);
                } else {
                    mInputPhone.setText("");
                    mVerificationCode.setText("");
                }
                break;
            case R.id.send_btn:
                if (!phone.isEmpty()) {
                    LoadingDialog.showLoadingDialog(this);
                    HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
                        @Override
                        public void onFailure(Map<String, ?> errorMap) {

                        }

                        @Override
                        public void onSuccess(String response) {
                            Logger.e(response);
                            LoadingDialog.cancelLoadingDialog();
                            Map result = JsonUtil.fromJson(response, Map.class);
                            String code = (String) result.get("code");
                            if (HttpFunction.isSuc(code)) {
                                //啥都不做
                                uiHandler.obtainMessage(MSG_BINDING_SUCC).sendToTarget();
                            } else {
                                //网络请求失败，提示
                                onBusinessFaild(code);
                            }
                        }
                    };
                    mBinding.getCode(CommonUrlConfig.GetPhoneCode, StringHelper.stringMerge(cutAreaNum(areaNum), phone), httpCallback);
                } else {
                    mInputPhone.setText("");
                }
                break;
            case R.id.select_country:
                StartActivityHelper.jumpActivityForResult(this, CountrySelectActivity.class, 1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            selectItemModel = data.getParcelableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            if (selectItemModel != null) {
                mAreaText.setText(selectItemModel.num);
                mSelectCountry.setText(selectItemModel.country);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.closeKeybord(mVerificationCode, this);
        Utility.closeKeybord(mInputPhone, this);
        stopTimer();
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

    private void clearInput() {
        mInputPhone.setText("");
        mVerificationCode.setText("");
    }

    private void restore() {
        clearInput();
        mSendBtn.setText(getString(R.string.phone_login_send_code));
        setIsWork();
    }

    private void setIsWork() {
        mHitText.setText("");
        String phonenum = mInputPhone.getText().toString();
        if (!"".equals(phonenum)) {
            String code = mVerificationCode.getText().toString();
            if (!isRunTimer) {
                mSendBtn.setEnabled(true);
                mSendBtn.setTextColor(0xFF222222);
            } else {
                mSendBtn.setEnabled(false);
                mSendBtn.setTextColor(0xFFA6A6A6);
//                mSendBtn.setBackgroundResource(R.drawable.common_btn_bg);
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

    private boolean checkPhoneNum(String phoneNum) {
        return phoneNum != null && phoneNum.length() == 11;
    }

    private boolean checkCode(String code) {
        return code != null;
    }


    private String cutAreaNum(String areaNum) {
        if (areaNum == null || "".equals(areaNum)) {
            return getString(R.string.phone_login_default_country_area_num);
        }
        return areaNum.replace("+", "");
    }
}