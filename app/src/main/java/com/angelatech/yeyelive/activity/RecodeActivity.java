package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.will.common.log.DebugLogs;
import com.xj.frescolib.View.FrescoDrawee;
import com.xj.frescolib.View.FrescoRoundView;

/**
 * 二维码页面
 */
public class RecodeActivity extends Activity implements View.OnClickListener {

    private ImageView btn_back, img_sex;
    private FrescoRoundView user_face;
    private FrescoDrawee img_recode;
    private TextView txt_username, txt_userid;
    private BasicUserInfoDBModel userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recode);
        initView();
        setView();
    }

    private void setView() {
        userInfo = CacheDataManager.getInstance().loadUser();
        btn_back.setOnClickListener(this);
        txt_username.setText(userInfo.nickname);
        txt_userid.setText("ID:" + userInfo.idx);
        user_face.setImageURI(userInfo.headurl);
        if (Constant.SEX_MALE.equals(userInfo.sex)) {
            img_sex.setImageResource(R.drawable.icon_information_boy);
        } else {
            img_sex.setImageResource(R.drawable.icon_information_girl);
        }
        createqrcode();
    }

    private void createqrcode() {
        String recodeurl = CommonUrlConfig.createqrcode + "?userid=" + userInfo.userid + "&toUserId=" + userInfo.userid + "&token=" + userInfo.token;
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            int type = (Integer) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_INT);
            if (type == 1) {
                recodeurl += "&type=charge";
            }
        }
        DebugLogs.e("recodeurl:" + recodeurl);
        img_recode.setImageURI(recodeurl);
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        img_sex = (ImageView) findViewById(R.id.img_sex);
        img_recode = (FrescoDrawee) findViewById(R.id.img_recode);
        user_face = (FrescoRoundView) findViewById(R.id.user_face);
        txt_userid = (TextView) findViewById(R.id.txt_userid);
        txt_username = (TextView) findViewById(R.id.txt_username);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
