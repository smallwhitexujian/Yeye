package com.angelatech.yeyelive1.view.banner;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class BannerAdapter<T extends View> extends PagerAdapter {

    private List<T> mViews;

    public BannerAdapter(List<T> views) {
        this.mViews = views;
    }

    /**
     * 销毁对象
     *
     * @param position 将要被销毁对象的索引位置
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        //container.removeView(mViews.get(position % mViews.size()));

        container.removeView((View) view);
    }

    /**
     * 初始化一个对象
     *
     * @param position 将要被创建的对象的索引位置
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 先把对象添加到viewpager中，再返回当前对象
        //container.addView(mViews.get(position % mViews.size()));
        //return mViews.get(position % mViews.size());
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    /**
     * 复用对象 true 复用对象 false 用的是object
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}