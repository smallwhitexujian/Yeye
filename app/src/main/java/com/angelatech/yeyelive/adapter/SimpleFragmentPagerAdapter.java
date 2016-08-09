package com.angelatech.yeyelive.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.fragment.LiveVideoHotFragment;

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
            tabTitles.add(hotTab);
            tabTitles.add(followTab);
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
        } else {
            if (fragment != null) {
                return fragment;
            }
            return LiveVideoHotFragment.newInstance(2);
        }
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