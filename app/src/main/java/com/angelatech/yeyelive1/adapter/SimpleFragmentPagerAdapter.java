package com.angelatech.yeyelive1.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.fragment.LiveVideoHotFragment;
import com.angelatech.yeyelive1.fragment.NewFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<String> tabTitles = new ArrayList<>();
    private Map<String, Fragment> fragments = new HashMap<>();
    private Context context;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        init();
    }


    private void init() {
        if (context != null) {
            String hotTab = context.getString(R.string.live_hot);
            String followTab = context.getString(R.string.live_follow);
            String newTab = context.getString(R.string.live_new);

            tabTitles.add(newTab);
            tabTitles.add(hotTab);
            tabTitles.add(followTab);

            fragments.put(newTab, NewFragment.newInstance(0));
            fragments.put(hotTab, LiveVideoHotFragment.newInstance(1));
            fragments.put(followTab, LiveVideoHotFragment.newInstance(2));
        }
    }

    @Override
    public Fragment getItem(int position) {
        String title = tabTitles.get(position);
        Fragment fragment = fragments.get(title);

        if (context.getString(R.string.live_hot).equals(title)) {
            if (fragment != null) {
                return fragment;
            }
            return LiveVideoHotFragment.newInstance(1);
        }
        if (context.getString(R.string.live_new).equals(title)) {
            if (fragment != null) {
                return fragment;
            }
            return LiveVideoHotFragment.newInstance(3);
        }
        if (context.getString(R.string.live_follow).equals(title)) {
            if (fragment != null) {
                return fragment;
            }
            return LiveVideoHotFragment.newInstance(2);
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

}