package com.angelatech.yeyelive1.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
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
            if (baseInfo !=null && baseInfo.headurl!=null){
                userface.setImageURI(baseInfo.headurl);
                txt_popularity.setText(String.valueOf( baseInfo.Intimacy));
            }
        }
    }
}
