package com.angelatech.yeyelive.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.CommDialog;

/**
 * 钱包页面
 */
public class PayActivity extends Activity implements View.OnClickListener {
    private ImageView btn_back;
    private TextView txt_coin;
    private BasicUserInfoDBModel userInfo;
    private LinearLayout layout_diamond;
    private LinearLayout ly_qcode,ly_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
        setView();
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        layout_diamond = (LinearLayout) findViewById(R.id.layout_diamond);
        txt_coin = (TextView) findViewById(R.id.txt_coin);
        ly_qcode =(LinearLayout) findViewById(R.id.ly_qcode);
        ly_card = (LinearLayout) findViewById(R.id.ly_card);
        btn_back.setOnClickListener(this);
        layout_diamond.setOnClickListener(this);
        ly_qcode.setOnClickListener(this);
        ly_card.setOnClickListener(this);
    }

    private void setView() {
        userInfo = CacheDataManager.getInstance().loadUser();
        txt_coin.setText(userInfo.diamonds);
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
                CommDialog dialog = new CommDialog();
                CommDialog.Callback callback = new CommDialog.Callback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOK() {

                    }
                };
                dialog.CommDialog(this, getString(R.string.pwd_desc), true,  callback,getString(R.string.now_set),getString(R.string.not_set));
                break;
        }
    }
}
