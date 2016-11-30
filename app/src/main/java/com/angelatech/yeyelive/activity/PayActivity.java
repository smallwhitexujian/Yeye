package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.CommDialog;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.will.common.log.DebugLogs;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

import java.util.Map;

/**
 * 钱包页面
 */

public class PayActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btn_back;
    private TextView txt_coin, txt_voucher;
    private BasicUserInfoDBModel userInfo;
    private LinearLayout layout_diamond;
    private LinearLayout ly_qcode, ly_card, ly_wallet_collection, ly_transfer;
    private MainEnter mainEnter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
        setView();
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        layout_diamond = (LinearLayout) findViewById(R.id.layout_diamond);
        txt_coin = (TextView) findViewById(R.id.txt_coin);
        ly_qcode = (LinearLayout) findViewById(R.id.ly_qcode);
        ly_card = (LinearLayout) findViewById(R.id.ly_card);
        ly_wallet_collection = (LinearLayout) findViewById(R.id.ly_wallet_collection);
        ly_transfer = (LinearLayout) findViewById(R.id.ly_transfer);
        txt_voucher = (TextView) findViewById(R.id.txt_voucher);
        ly_transfer.setOnClickListener(this);
        ly_wallet_collection.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        layout_diamond.setOnClickListener(this);
        ly_qcode.setOnClickListener(this);
        ly_card.setOnClickListener(this);
    }

    private void setView() {
        mainEnter = new MainEnter(this);
        userInfo = CacheDataManager.getInstance().loadUser();
        CheckPayPassword();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfo = CacheDataManager.getInstance().loadUser();
        txt_coin.setText(userInfo.diamonds);
        txt_voucher.setText(userInfo.voucher);
    }

    //检查设置安全密码 CheckPayPassword
    private void CheckPayPassword() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                LoadingDialog.cancelLoadingDialog();
            }

            @Override
            public void onSuccess(String response) {
                DebugLogs.e("response" + response);
                JSONObject jsobj;
                try {
                    jsobj = new JSONObject(response);
                    String code = jsobj.optString("code");
                    if (code.equals("1000")) {//设置了支付密码
                        uiHandler.obtainMessage(1).sendToTarget();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        mainEnter.CheckPayPassword(CommonUrlConfig.CheckPayPassword, userInfo.userid, userInfo.token, callback);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case 1:
                CommDialog dialog = new CommDialog();
                CommDialog.Callback callback = new CommDialog.Callback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOK() {
                        StartActivityHelper.jumpActivityDefault(PayActivity.this, SetPayPwdActivity.class);
                    }
                };
                dialog.CommDialog(PayActivity.this, getString(R.string.pwd_desc), true, callback, getString(R.string.now_set), getString(R.string.not_set));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.layout_diamond:
                StartActivityHelper.jumpActivityDefault(PayActivity.this, RechargeActivity.class);
                break;
            case R.id.ly_qcode:
                StartActivityHelper.jumpActivityDefault(PayActivity.this, TestScanActivity.class);
                break;
            case R.id.ly_card:

                break;
            case R.id.ly_wallet_collection:
                StartActivityHelper.jumpActivity(PayActivity.this, RecodeActivity.class, 1);
                break;
            case R.id.ly_transfer:
                StartActivityHelper.jumpActivityDefault(PayActivity.this, TransferAccountsActivity.class);
                break;
        }
    }
}
