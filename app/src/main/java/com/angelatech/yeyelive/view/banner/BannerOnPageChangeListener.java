package com.angelatech.yeyelive.view.banner;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Author: jjfly
 * Since: 2016年05月06日 15:29
 * Desc:
 * FIXME:
 */
public class BannerOnPageChangeListener<T extends View> implements ViewPager.OnPageChangeListener {

    private int preEnablePosition = 0; // 前一个被选中的点的索引位置 默认情况下为0
    private TextView mDescriptionTextView;
    private List<String> mDescription;
    private LinearLayout mDots;//点布局
    private ViewPager viewPager;

    public BannerOnPageChangeListener(ViewPager viewPager, List<String> description,
                                      TextView descriptionTextView,
                                      LinearLayout dots) {
        this.viewPager = viewPager;
        this.mDescription = description;
        this.mDescriptionTextView = descriptionTextView;
        this.mDots = dots;

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 根据索引设置图片的描述
        try {
            if (mDescriptionTextView != null) {
                mDescriptionTextView.setText(mDescription.get(position));
            }
        } catch (Exception e) {
            Log.e("", "");
        }
        // 把上一个点设置为被选中
        mDots.getChildAt(preEnablePosition).setEnabled(false);
        // 根据索引设置那个点被选中
        mDots.getChildAt(position).setEnabled(true);
        preEnablePosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 1:// 手势滑动，空闲中
                Banner.isAutoPlay = false;
                break;
            case 2:// 界面切换中
                Banner.isAutoPlay = true;
                break;

//            case 0:// 滑动结束，即切换完毕或者加载完毕
//                // 当前为最后一张，此时从右向左滑，则切换到第一张
//                if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !Banner.isAutoPlay) {
//                    viewPager.setCurrentItem(0);
//                }
//
//                // 当前为第一张，此时从左向右滑，则切换到最后一张
//                else if (viewPager.getCurrentItem() == 0 && !Banner.isAutoPlay) {
//                    viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
//                }
//                break;

        }
    }
}
