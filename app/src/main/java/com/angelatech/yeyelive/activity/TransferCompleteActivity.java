package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.util.StartActivityHelper;

public class TransferCompleteActivity extends BaseActivity {

    private ImageView btn_back, img_state;
    private TextView txt_msg, txt_remark;
    private Button btn_close;
    private BasicUserInfoDBModel baseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_complete);
        initView();
        findView();
        initData();
    }

    private void initData() {

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            int type =  bundle.getInt("type",1000);
            if(type == 1000) {
                img_state.setImageResource(R.drawable.icon_transfer_tips_succe);
                txt_remark.setVisibility(View.VISIBLE);
                btn_close.setText("继续转账");
                txt_msg.setText("转账成功");
                txt_remark.setText(bundle.getString("msg"));
            }
            else {
                img_state.setImageResource(R.drawable.icon_transfer_tips_fail);
                txt_remark.setVisibility(View.GONE);
                txt_msg.setText(bundle.getString("msg"));
                btn_close.setText("重新转账");
            }
        }

    }

    private void findView() {
        btn_back.setOnClickListener(this);
        btn_close.setOnClickListener(this);
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        img_state = (ImageView) findViewById(R.id.img_state);
        txt_msg = (TextView) findViewById(R.id.txt_msg);
        txt_remark = (TextView) findViewById(R.id.txt_remark);
        btn_close = (Button) findViewById(R.id.btn_close);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }

}
