package com.angelatech.yeyelive1.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.activity.RankActivity;
import com.angelatech.yeyelive1.activity.SearchActivity;
import com.angelatech.yeyelive1.adapter.SimpleFragmentPagerAdapter;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.will.common.tool.view.DisplayTool;

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
 * 作者: Created by: xujian on Date: 2016/10/18.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive1.activity
 */
public class ListFragment extends BaseFragment {
    private View view;
    private SimpleFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TextView hotTab, followTab, newTab;
    private Drawable drawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        initView();
        setView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initView() {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        hotTab = (TextView) view.findViewById(R.id.hot_textview);
        followTab = (TextView) view.findViewById(R.id.follow_textview);
        newTab = (TextView) view.findViewById(R.id.new_textview);
        ImageView searchIcon = (ImageView) view.findViewById(R.id.search_icon);
        ImageView Rank_icon = (ImageView) view.findViewById(R.id.Rank_icon);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        hotTab.setOnClickListener(this);
        followTab.setOnClickListener(this);
        newTab.setOnClickListener(this);
        searchIcon.setOnClickListener(this);
        Rank_icon.setOnClickListener(this);
    }

    private void setView() {
        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.btn_navigation_bar_hot_n);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        pagerAdapter = new SimpleFragmentPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                clearTabColor();
                clearTabTextSize();
                String hotStr = getString(R.string.live_hot);
                String followStr = getString(R.string.live_follow);
                String newStr = getString(R.string.live_new);
                final float textSize = DisplayTool.dip2px(getActivity(), 18);
                if (hotStr.equals(pagerAdapter.getPageTitle(position))) {
                    hotTab.setCompoundDrawables(null, null, null, drawable);
                    hotTab.setTextSize(textSize);
                    hotTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_d80c18));
                }
                if (followStr.equals(pagerAdapter.getPageTitle(position))) {
                    followTab.setCompoundDrawables(null, null, null, drawable);
                    followTab.setTextSize(textSize);
                    followTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_d80c18));
                }
                if (newStr.equals(pagerAdapter.getPageTitle(position))) {
                    newTab.setCompoundDrawables(null, null, null, drawable);
                    newTab.setTextSize(textSize);
                    newTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_d80c18));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        clearTabColor();
        clearTabTextSize();
        viewPager.setCurrentItem(1);
        hotTab.setCompoundDrawables(null, null, null, drawable);
        hotTab.setTextSize(DisplayTool.dip2px(getActivity(), 18));
        hotTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_d80c18));
    }

    private void clearTabColor() {
        hotTab.setCompoundDrawables(null, null, null, null);
        hotTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_999999));
        followTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_999999));
        newTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_999999));
        followTab.setCompoundDrawables(null, null, null, null);
        newTab.setCompoundDrawables(null, null, null, null);
    }

    private void clearTabTextSize() {
        final float textSize = DisplayTool.dip2px(getActivity(), 18);
        hotTab.setTextSize(textSize);
        followTab.setTextSize(textSize);
        newTab.setTextSize(textSize);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_icon:
                StartActivityHelper.jumpActivityDefault(getActivity(), SearchActivity.class);
                break;
            case R.id.Rank_icon:
                StartActivityHelper.jumpActivityDefault(getActivity(), RankActivity.class);
                break;
            case R.id.hot_textview:
                viewPager.setCurrentItem(1);
                clearTabColor();
                hotTab.setCompoundDrawables(null, null, null, drawable);
                hotTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_d80c18));
                break;
            case R.id.follow_textview:
                viewPager.setCurrentItem(2);
                clearTabColor();
                followTab.setCompoundDrawables(null, null, null, drawable);
                followTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_d80c18));
                break;
            case R.id.new_textview:
                viewPager.setCurrentItem(0);
                clearTabColor();
                newTab.setCompoundDrawables(null, null, null, drawable);
                newTab.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_d80c18));
                break;
        }
    }
}
