package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.ErrorHelper;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.common.string.Encryption;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/4/7
 * Time: 14:17
 */
public class EditActivity extends HeaderBaseActivity {
    private LinearLayout layout_nickName, layout_sign, layout_email;
    private ImageView iv_delete, iv_delete_email;
    private EditText tv_nickName, tv_email;
    private EditText et_sign;
    private String type = "1";
    private BasicUserInfoDBModel model;
    private String nickName;
    private String userSign, userEmail;
    private TextView tv_input_limit;

    private final int USER_SIGN_LEN_LIMIT = 70;
    private final int USER_NAME_LEN_LIMIT = 24;
    private final int USER_EMAIL_LEN_LIMIT = 50;
    private final int MSG_INPUT_USER_NAME_LIMIT = 1;
    private final int MSG_INPUT_USER_SIGN_LIMIT = 2;
    private final int MSG_INPUT_USER_EMAIL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        initView();
        setView();
    }

    private void initView() {
        model = CacheDataManager.getInstance().loadUser();
        layout_nickName = (LinearLayout) findViewById(R.id.layout_nickName);
        layout_sign = (LinearLayout) findViewById(R.id.layout_sign);
        tv_nickName = (EditText) findViewById(R.id.tv_nickName);
        et_sign = (EditText) findViewById(R.id.et_sign);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        iv_delete_email = (ImageView) findViewById(R.id.iv_delete_email);
        tv_email = (EditText) findViewById(R.id.tv_email);
        tv_input_limit = (TextView) findViewById(R.id.tv_input_limit);
        layout_email = (LinearLayout) findViewById(R.id.layout_email);
        type = getIntent().getStringExtra("type");
    }

    private void setView() {
        headerLayout.showRightTextButton(R.color.color_222222, R.string.button_save, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.closeKeybord(tv_nickName, EditActivity.this);
                saveUserInfo();
            }
        });
        headerLayout.showLeftBackButton(R.id.backBtn, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.closeKeybord(tv_nickName, EditActivity.this);
                finish();
            }
        });
        iv_delete.setOnClickListener(this);
        iv_delete_email.setOnClickListener(this);
        et_sign.addTextChangedListener(textWatcher);
        tv_nickName.addTextChangedListener(textWatcher);
        tv_email.addTextChangedListener(textWatcher);
        if (type.equals("1")) {
            layout_email.setVisibility(View.GONE);
            layout_sign.setVisibility(View.GONE);
            layout_nickName.setVisibility(View.VISIBLE);
            tv_nickName.setText(model.nickname);
            tv_nickName.selectAll();
            headerLayout.showTitle(R.string.userinfo_user_nickname);
            //tv_input_limit.setVisibility(View.GONE);
        } else if (type.equals("2")) {
            layout_nickName.setVisibility(View.GONE);
            layout_email.setVisibility(View.GONE);
            layout_sign.setVisibility(View.VISIBLE);
            et_sign.setText(model.sign);
            headerLayout.showTitle(R.string.userinfo_user_sign);
            //tv_input_limit.setVisibility(View.VISIBLE);
        } else {
            layout_email.setVisibility(View.VISIBLE);
            layout_nickName.setVisibility(View.GONE);
            layout_sign.setVisibility(View.GONE);
            tv_email.setText(model.email);
            headerLayout.showTitle(R.string.user_info_mail);
        }

        Utility.openKeybord(tv_nickName, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_delete:
                tv_nickName.setText("");
                break;
            case R.id.iv_delete_email:
                tv_email.setText("");
                break;
        }
    }

    private void saveUserInfo() {
        if (type.equals("1")) {
            userSign = model.sign;
            userEmail = model.email;
            nickName = tv_nickName.getText().toString();
            if (nickName.length() == 0) {
                ToastUtils.showToast(this, getString(R.string.user_name_input));
                return;
            }
        } else if (type.equals("2")) {
            nickName = model.nickname;
            userEmail = model.email;
            userSign = et_sign.getText().toString();
        } else {
            nickName = model.nickname;
            userSign = model.sign;
            userEmail = tv_email.getText().toString();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("token", model.token);
        map.put("userid", model.userid);
        map.put("nickname", Encryption.utf8ToUnicode(nickName));
        map.put("sign", Encryption.utf8ToUnicode(userSign));
        map.put("email", Encryption.utf8ToUnicode(userEmail));
        map.put("sex", model.sex);
        new HttpFunction(this).httpPost(CommonUrlConfig.UserInformationEdit, map, httpCallback);
    }

    private HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {

        }

        @Override
        public void onSuccess(String response) {
            if (response != null) {
                CommonModel common = JsonUtil.fromJson(response, CommonModel.class);
                if (common != null) {
                    if (HttpFunction.isSuc(common.code)) {
                        if (type.equals("1")) {
                            CacheDataManager.getInstance().update(BaseKey.USER_NICKNAME, nickName, model.userid);
                        } else if (type.equals("2")){
                            CacheDataManager.getInstance().update(BaseKey.USER_SIGN, userSign, model.userid);
                        }
                        else{
                            CacheDataManager.getInstance().update(BaseKey.USER_EMAIL, tv_email, model.userid);
                        }
                        finish();
                    } else {
                        //错误提示
                        ToastUtils.showToast(EditActivity.this, ErrorHelper.getErrorHint(EditActivity.this, common.code));
                    }
                }
            }
        }
    };

    /*监听输入事件*/
    private TextWatcher textWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (type.equals("1")) {
                editStart = tv_nickName.getSelectionStart();
                editEnd = tv_nickName.getSelectionEnd();
                if (temp.length() > USER_NAME_LEN_LIMIT) {
                    editable.delete(editStart - 1, editEnd);
                }
                uiHandler.obtainMessage(MSG_INPUT_USER_NAME_LIMIT, 0, 0, editable.toString()).sendToTarget();
            } else if (type.equals("2")) {
                editStart = et_sign.getSelectionStart();
                editEnd = et_sign.getSelectionEnd();
                if (temp.length() > USER_SIGN_LEN_LIMIT) {
                    editable.delete(editStart - 1, editEnd);
                }
                uiHandler.obtainMessage(MSG_INPUT_USER_SIGN_LIMIT, 0, 0, editable.toString()).sendToTarget();
            } else {
                editStart = tv_email.getSelectionStart();
                editEnd = tv_email.getSelectionEnd();
                if (temp.length() > USER_EMAIL_LEN_LIMIT) {
                    editable.delete(editStart - 1, editEnd);
                }
                uiHandler.obtainMessage(MSG_INPUT_USER_EMAIL, 0, 0, editable.toString()).sendToTarget();
            }
        }
    };

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_INPUT_USER_NAME_LIMIT:
                String str = ((String) msg.obj).length() + "/" + USER_NAME_LEN_LIMIT;
                tv_input_limit.setText(str);
                if (((String) msg.obj).length() > 0) {
                    headerLayout.setRightTextButton(ContextCompat.getColor(this, R.color.color_222222));
                } else {
                    headerLayout.setRightTextButton(ContextCompat.getColor(this, R.color.color_cccccc));
                }
                break;
            case MSG_INPUT_USER_SIGN_LIMIT:
                String str2 = ((String) msg.obj).length() + "/" + USER_SIGN_LEN_LIMIT;
                tv_input_limit.setText(str2);
                if (((String) msg.obj).length() > 0) {
                    headerLayout.setRightTextButton(ContextCompat.getColor(this, R.color.color_222222));
                } else {
                    headerLayout.setRightTextButton(ContextCompat.getColor(this, R.color.color_cccccc));
                }
                break;
            case MSG_INPUT_USER_EMAIL:
                String str3 = ((String) msg.obj).length() + "/" + USER_EMAIL_LEN_LIMIT;
                tv_input_limit.setText(str3);
                if (((String) msg.obj).length() > 0 && VerificationUtil.isEmail(msg.obj.toString())) {
                    headerLayout.setRightTextButton(ContextCompat.getColor(this, R.color.color_222222));
                } else {
                    headerLayout.setRightTextButton(ContextCompat.getColor(this, R.color.color_cccccc));
                }
                break;
        }
    }
}
