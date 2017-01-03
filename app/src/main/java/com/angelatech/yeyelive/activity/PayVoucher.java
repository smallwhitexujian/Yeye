package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.view.View;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.view.HeaderLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xujian on 17-1-3.
 */

public class PayVoucher extends HeaderBaseActivity {
    @BindView(R.id.headerLayout)
    HeaderLayout headerLayout;
    @BindView(R.id.view1)
    View view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        ButterKnife.bind(this);

    }
}
