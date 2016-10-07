package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.adapter.MyFragmentPagerAdapter;
import com.angelatech.yeyelive.fragment.SevenRankFragment;
import com.angelatech.yeyelive.util.MyOnPageChangeListener;

import java.util.ArrayList;

/**
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * <p>
 * <p>
 * 作者: Created by: xujian on Date: 16/9/21.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 * 系统消息
 */

public class TabActivity extends HeaderBaseActivity  {
    private ArrayList<Fragment> fragmentList;
    public static int currIndex = 0;
    private ViewPager mAbSlidingTabView;
    private MyOnPageChangeListener mypageChanger;
    private ImageView iv_bottom_line;
    private LinearLayout L1;
    public static TextView Tab_1, Tab_2;
    private Fragment fragment_Tab_1, fragment_Tab_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        fragmentList = new ArrayList<>();
        fragment_Tab_1 = SevenRankFragment.newInstance(1);
        fragmentList.add(fragment_Tab_1);
        initView(getString(R.string.rank_title), getString(R.string.rank_seven_dedicated), getString(R.string.rank_history_dedicated), fragmentList);
    }

    public void initView(String titlebar, String tab1, String tab2, ArrayList<Fragment> fragmentList) {
        iv_bottom_line = (ImageView) findViewById(R.id.iv_bottom_line);
        mAbSlidingTabView = (ViewPager) findViewById(R.id.mAbSlidingTabView);
        L1 = (LinearLayout) findViewById(R.id.L1);
        Tab_1 = (TextView) findViewById(R.id.Tab_1);
        Tab_2 = (TextView) findViewById(R.id.Tab_2);
        Tab_1.setText(tab1);
        Tab_2.setText(tab2);
        headerLayout.showTitle(titlebar);
        headerLayout.showLeftBackButton();
        mypageChanger = new MyOnPageChangeListener("TabActivity");
        mypageChanger.InitWidth(iv_bottom_line, TabActivity.this);
        Tab_1.setOnClickListener(this);
        Tab_2.setOnClickListener(this);
        mAbSlidingTabView.setAdapter(new MyFragmentPagerAdapter(TabActivity.this.getSupportFragmentManager(), fragmentList));
        mAbSlidingTabView.setCurrentItem(0);
        mypageChanger.onPageSelected(0);
        Tab_1.setSelected(true);
        Tab_2.setSelected(false);
        mAbSlidingTabView.addOnPageChangeListener(mypageChanger);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.Tab_1:
                mypageChanger.onPageSelected(0);
                mAbSlidingTabView.setCurrentItem(0);
                Tab_1.setSelected(true);
                Tab_2.setSelected(false);
                Tab_1.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));
                Tab_2.setTextColor(ContextCompat.getColor(this, R.color.font_grey));
                break;
            case R.id.Tab_2:
                mypageChanger.onPageSelected(1);
                mAbSlidingTabView.setCurrentItem(1);
                Tab_1.setSelected(false);
                Tab_2.setSelected(true);
                Tab_1.setTextColor(ContextCompat.getColor(this, R.color.font_grey));
                Tab_2.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
