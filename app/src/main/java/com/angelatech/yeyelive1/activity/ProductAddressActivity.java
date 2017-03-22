package com.angelatech.yeyelive1.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.activity.function.MainEnter;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.CommonModel;
import com.angelatech.yeyelive1.model.MyProductModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;

/**
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * <p>
 * <p>
 * 作者: Created by: xujian on Date: 2016/11/30.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive1.activity
 */

public class ProductAddressActivity extends HeaderBaseActivity {
    private EditText name, phone_str, address;
    private BasicUserInfoDBModel userinfo;
    private MainEnter mainEnter;
    private MyProductModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_address);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            model = (MyProductModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        }
        initView();
        initData();
    }

    private void initData() {
        mainEnter = new MainEnter(this);
        userinfo = CacheDataManager.getInstance().loadUser();
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        if (model.useraddress == null) {
            headerLayout.showTitle(getString(R.string.address_tips));
        } else {
            headerLayout.showTitle(getString(R.string.edit_address_tips));
        }
        name = (EditText) findViewById(R.id.name);
        phone_str = (EditText) findViewById(R.id.phone_str);
        address = (EditText) findViewById(R.id.address);
        if (model.username != null) {
            name.setText(model.username);
        }
        if (model.phone != null) {
            phone_str.setText(model.phone);
        }
        if (model.useraddress != null) {
            address.setText(model.useraddress);
        }
        Button btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_save:
                String strName = name.getText().toString();
                String strPhone = phone_str.getText().toString();
                String strAddress = address.getText().toString();
                mainEnter.UserOrderEidt(CommonUrlConfig.UserOrderEidt, userinfo.userid, userinfo.token, model.id, strAddress, strName, strPhone, callback);
                break;
        }
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
        }

        @Override
        public void onSuccess(String response) {
            DebugLogs.d("---------->" + response);
            final CommonModel commonModel = JsonUtil.fromJson(response, CommonModel.class);
            if (commonModel != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (commonModel.code.equals("1000")) {
                            ToastUtils.showToast(ProductAddressActivity.this, getString(R.string.return_success));
                            finish();
                        }
                    }
                });
            }
        }
    };
}
