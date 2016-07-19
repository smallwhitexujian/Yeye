package com.will.yeye.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.will.common.string.Encryption;
import com.will.yeye.CommonUrlConfig;
import com.will.yeye.activity.base.HeaderBaseActivity;
import com.will.yeye.activity.function.Feedback;
import com.will.yeye.db.model.BasicUserInfoDBModel;
import com.will.yeye.util.CacheDataManager;
import com.will.yeye.util.ErrorHelper;
import com.will.yeye.R;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/4/15
 * Time: 16:47
 */
public class FeedbackActivity extends HeaderBaseActivity {
    private EditText et_feedback, et_phone_mail;
    private TextView tv_input_limit;
    private final int MSG_INPUT_FEEDBACK_LIMIT = 1;
    private final int FEEDBACK_LEN_LIMIT = 200;
    private BasicUserInfoDBModel model;
    private final int FEEDBACK_SUCCESS = 2;
    private final int FEEDBACK_ERROR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
    }

    private void initView() {
        et_feedback = (EditText) findViewById(R.id.et_feedback);
        et_phone_mail = (EditText) findViewById(R.id.et_phone_mail);
        tv_input_limit = (TextView) findViewById(R.id.tv_input_limit);

        headerLayout.showTitle(getString(R.string.activity_feedback_title));
        headerLayout.showRightTextButton(R.color.color_black,
                R.string.button_submit, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goFeedback();
                    }
                });
        headerLayout.showLeftBackButton();
        et_feedback.addTextChangedListener(textWatcher);

        CacheDataManager cacheDataManager = CacheDataManager.getInstance();
        model = cacheDataManager.loadUser();
    }

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

            editStart = et_feedback.getSelectionStart();
            editEnd = et_feedback.getSelectionEnd();
            if (temp.length() > FEEDBACK_LEN_LIMIT) {
                editable.delete(editStart - 1, editEnd);
            }
            uiHandler.obtainMessage(MSG_INPUT_FEEDBACK_LIMIT, 0, 0, editable.toString()).sendToTarget();
        }

    };


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_INPUT_FEEDBACK_LIMIT:
                tv_input_limit.setText(((String) msg.obj).length() + "/" + FEEDBACK_LEN_LIMIT);
                break;
            case FEEDBACK_SUCCESS:
                ToastUtils.showToast(this, getString(R.string.success));
                finish();
                break;
            case FEEDBACK_ERROR:
                ToastUtils.showToast(this, ErrorHelper.getErrorHint(FeedbackActivity.this, msg.obj.toString()));
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 反馈提交
     */
    private void goFeedback() {
        String comment = et_feedback.getText().toString();
        String contact = et_phone_mail.getText().toString();
        if (comment.length() == 0 || contact.length() == 0) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("token", model.token);
        map.put("userid", model.userid);
        map.put("comment", Encryption.utf8ToUnicode(comment));
        map.put("contact", Encryption.utf8ToUnicode(contact));
        Feedback feedback = new Feedback(FeedbackActivity.this);
        feedback.httpPost(CommonUrlConfig.UserFeedback, map, httpCallback);
    }

    HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            uiHandler.obtainMessage(FEEDBACK_ERROR).sendToTarget();
        }

        @Override
        public void onSuccess(String response) {
            uiHandler.obtainMessage(FEEDBACK_SUCCESS).sendToTarget();
        }
    };
}
