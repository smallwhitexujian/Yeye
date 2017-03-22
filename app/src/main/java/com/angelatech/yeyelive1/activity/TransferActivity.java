package com.angelatech.yeyelive1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.base.BaseActivity;
import com.angelatech.yeyelive1.db.BaseKey;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.fragment.payPwdDialogFragment;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.view.CommDialog;
import com.xj.frescolib.View.FrescoRoundView;

import java.util.Map;


//转账页面
public class TransferActivity extends BaseActivity {

    private ImageView btn_back;
    private FrescoRoundView user_face;
    private TextView txt_username, txt_userid;
    private BasicUserInfoDBModel userInfo;
    private BasicUserInfoDBModel baseInfo;

    private Button btn_submit_pay;
    private EditText txt_money, txt_remark;
    private final int MSG_FIND_TRANSFER_ERROR = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        initView();
        findView();
        initData();
    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            baseInfo = (BasicUserInfoDBModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        }

        txt_username.setText(baseInfo.nickname);
        txt_userid.setText("ID:" + baseInfo.idx);
        user_face.setImageURI(baseInfo.headurl);
    }

    private void findView() {
        userInfo = CacheDataManager.getInstance().loadUser();
        btn_back.setOnClickListener(this);
        btn_submit_pay.setOnClickListener(this);
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        user_face = (FrescoRoundView) findViewById(R.id.user_face);
        txt_userid = (TextView) findViewById(R.id.txt_userid);
        txt_username = (TextView) findViewById(R.id.txt_username);
        btn_submit_pay = (Button) findViewById(R.id.btn_submit_pay);
        txt_money = (EditText) findViewById(R.id.txt_money);
        txt_remark = (EditText) findViewById(R.id.txt_remark);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_submit_pay:
                if (txt_money.getText() != null) {
                    pay();
                }
                break;
        }
    }

    private void pay() {
        final float money = Float.parseFloat(txt_money.getText().toString());
        final String remark = txt_remark.getText().toString();

        payPwdDialogFragment.Callback callback = new payPwdDialogFragment.Callback() {
            @Override
            public void onCancel(String code) {
                //收起键盘
            }

            @Override
            public void onEnter(String response) {
                Map result = JsonUtil.fromJson(response, Map.class);
                Intent intent = new Intent(TransferActivity.this, TransferCompleteActivity.class);
                int code = Integer.parseInt(result.get("code").toString());
                String msg = "";
                intent.putExtra("type", code);
                if (code == 1000) {
                    userInfo.voucher = result.get("data").toString();
                    CacheDataManager.getInstance().update(BaseKey.USER_VOUCHER,  userInfo.voucher,  userInfo.userid);
                    msg = baseInfo.nickname + getString(R.string.txt_successfully);
                }
                else if(code == 6002){
                    uiHandler.sendEmptyMessage(MSG_FIND_TRANSFER_ERROR);
                    return;
                }
                else {
                    switch (code) {
                        case 6005:
                            msg = getString(R.string.lack_of_balance);
                            break;

                        default:
                            msg = getString(R.string.transfer_failure);
                            break;
                    }
                }
                intent.putExtra("msg", msg);
                startActivity(intent);
                finish();
            }
        };
        payPwdDialogFragment paypwdDialogFragment = new payPwdDialogFragment(TransferActivity.this, callback, baseInfo.userid, remark, money);
        paypwdDialogFragment.show(getFragmentManager(), "");
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_FIND_TRANSFER_ERROR:
                CommDialog dialog = new CommDialog();
                CommDialog.Callback callback = new CommDialog.Callback() {
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onOK() {
                        StartActivityHelper.jumpActivityDefault(TransferActivity.this, SetPayPwdActivity.class);
                    }
                };
                dialog.CommDialog(TransferActivity.this, getString(R.string.pwd_desc), true, callback, getString(R.string.now_set), getString(R.string.not_set));
                break;
        }
    }

}
