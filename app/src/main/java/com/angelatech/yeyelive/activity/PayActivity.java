package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.ActionSheetDialog;
import com.angelatech.yeyelive.view.CommDialog;

/**
 * 钱包页面
 */

public class PayActivity extends BaseActivity implements View.OnClickListener {
    private static final int MSG_NO_SET_PWD = 1;
    private TextView txt_coin, txt_voucher;
    private BasicUserInfoDBModel userInfo;
    private LinearLayout layout_diamond;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
        setView();
    }

    private void initView() {
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        layout_diamond = (LinearLayout) findViewById(R.id.layout_diamond);
        txt_coin = (TextView) findViewById(R.id.txt_coin);
        LinearLayout ly_qcode = (LinearLayout) findViewById(R.id.ly_qcode);
        LinearLayout ly_card = (LinearLayout) findViewById(R.id.ly_card);
        LinearLayout ly_wallet_collection = (LinearLayout) findViewById(R.id.ly_wallet_collection);
        LinearLayout ly_transfer = (LinearLayout) findViewById(R.id.ly_transfer);
        txt_voucher = (TextView) findViewById(R.id.txt_voucher);
        ImageView btn_more = (ImageView) findViewById(R.id.btn_more);
        LinearLayout ly_voucher = (LinearLayout) findViewById(R.id.ly_voucher);
        ly_transfer.setOnClickListener(this);
        ly_wallet_collection.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        layout_diamond.setOnClickListener(this);
        ly_qcode.setOnClickListener(this);
        ly_card.setOnClickListener(this);
        ly_voucher.setOnClickListener(this);
        btn_more.setOnClickListener(this);
    }

    private void setView() {
        userInfo = CacheDataManager.getInstance().loadUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfo = CacheDataManager.getInstance().loadUser();
        txt_coin.setText(userInfo.diamonds);
        txt_voucher.setText(userInfo.voucher);
        if (userInfo.ispaypassword == 0) {
            uiHandler.obtainMessage(MSG_NO_SET_PWD).sendToTarget();
        }
        int Coins = (int)Double.parseDouble(App.configOnOff.get(0).value);
        if(Coins == 1){
            layout_diamond.setVisibility(View.VISIBLE);
        }else{
            layout_diamond.setVisibility(View.GONE);
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_NO_SET_PWD:
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
            case R.id.ly_transfer://转账
                StartActivityHelper.jumpActivityDefault(PayActivity.this, TransferAccountsActivity.class);
                break;
            case R.id.ly_voucher:
                StartActivityHelper.jumpActivityDefault(PayActivity.this, PayVoucher.class);
                break;
            case R.id.btn_more:
                ActionSheetDialog dialog = new ActionSheetDialog(PayActivity.this);
                dialog.builder();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.addSheetItem(getString(R.string.txt_tradelist), ActionSheetDialog.SheetItemColor.BLACK_222222,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                StartActivityHelper.jumpActivityDefault(PayActivity.this, UesrVoucherBillListActivity.class);
                            }
                        }).addSheetItem(getString(R.string.chage_my_pwd), ActionSheetDialog.SheetItemColor.BLACK_222222,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                StartActivityHelper.jumpActivityDefault(PayActivity.this, SetPayPwdActivity.class);
                            }
                        });
                dialog.show();
                break;
        }
    }
}
