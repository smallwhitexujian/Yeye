package com.angelatech.yeyelive.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * User: cbl
 * Date: 2016/4/22
 * Time: 14:12
 * viewPager adapter
 */
public class CustomerPageAdapter<T extends View>  extends PagerAdapter {
    private List<T> listView;
    private Context context;

    public CustomerPageAdapter(Context context, List<T> listView) {
        this.context = context;
        this.listView = listView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public int getCount() {
        return listView.size();
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(listView.get(position));
        return listView.get(position);
    }
}
