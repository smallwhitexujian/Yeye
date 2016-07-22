package com.angelatech.yeyelive.activity.base;

import android.view.LayoutInflater;
import android.view.View;

import com.angelatech.yeyelive .R;
import com.angelatech.yeyelive.view.HeaderLayout;

/**
 * 带有头部的baseActivity
 */
public class HeaderBaseActivity extends BaseActivity {

    protected HeaderLayout headerLayout;

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View topView = inflater.inflate(layoutResID, null);
        headerLayout = (HeaderLayout) topView.findViewById(R.id.headerLayout);
        super.setContentView(topView);
    }
}
