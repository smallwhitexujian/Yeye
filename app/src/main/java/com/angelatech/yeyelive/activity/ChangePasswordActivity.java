package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.UserSet;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.common.string.json.JsonUtil;
import com.will.common.string.security.Md5;
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
            LoadingDialog.showSysLoadingDialog(this, getString(R.string.now_submit));
            HttpBusinessCallback callback = new HttpBusinessCallback() {
                @Override
                public void onSuccess(String response) {
                    LoadingDialog.cancelLoadingDialog();
                    Map map = JsonUtil.fromJson(response, Map.class);
                    if (map != null) {
                        if (HttpFunction.isSuc(map.get("code").toString())) {
                            model.token = map.get("token").toString();
                            CacheDataManager.getInstance().update(BaseKey.USER_TOKEN, model.token, model.userid);
                            finish();
                        } else {
                            onBusinessFaild(map.get("code").toString());
                        }
                    }
                }

                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    LoadingDialog.cancelLoadingDialog();
                }
            };
            new UserSet(this).ChangePassword(model.userid, model.token,
                    Md5.md5(ed_old_password.getText().toString()),
                    Md5.md5(ed_new_password.getText().toString()), callback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
