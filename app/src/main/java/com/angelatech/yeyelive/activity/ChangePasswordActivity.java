package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.UserSet;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;

/**
 * User: cbl
 * Date: 2016/7/29
 * Time: 15:10
 */
public class ChangePasswordActivity extends HeaderBaseActivity {

    private EditText ed_old_password, ed_new_password;
    private TextView tv_submit;
    private BasicUserInfoDBModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        setView();
    }

    private void initView() {
        ed_old_password = (EditText) findViewById(R.id.ed_old_password);
        ed_new_password = (EditText) findViewById(R.id.ed_new_password);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
    }

    private void setView() {
        headerLayout.showTitle(getString(R.string.change_password));
        headerLayout.showLeftBackButton();
        model = CacheDataManager.getInstance().loadUser();
        tv_submit.setOnClickListener(this);
    }

    @Override
    public void doHandler(Message msg) {
        super.doHandler(msg);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_submit:
                submitChange();
                break;
        }
    }

    /**
     * 提交修改密码
     * web 服务器等待测试
     */
    private void submitChange() {
        if (ed_old_password.getText().toString().isEmpty() || ed_new_password.getText().toString().isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.can_not_empty));
        } else {
            HttpBusinessCallback callback = new HttpBusinessCallback() {
                @Override
                public void onSuccess(String response) {

                }

                @Override
                public void onFailure(Map<String, ?> errorMap) {

                }
            };
            new UserSet(this).ChangePassword(model.userid, model.token, ed_old_password.getText().toString(),
                    ed_new_password.getText().toString(), callback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
