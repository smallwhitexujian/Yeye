package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.xj.frescolib.View.FrescoRoundView;

/**
 * 知名度详情页面
 */
public class PopularityActivity extends Activity {

    private ImageView backBtn;
    private TextView txt_popularity;
    private FrescoRoundView userface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popularity);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.backBtn);
        txt_popularity = (TextView) findViewById(R.id.txt_popularity);
        userface = (FrescoRoundView) findViewById(R.id.user_face);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            BasicUserInfoDBModel baseInfo = (BasicUserInfoDBModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            userface.setImageURI(baseInfo.headurl);
            txt_popularity.setText(String.valueOf( baseInfo.Intimacy));
        }

    }
}
