package com.angelatech.yeyelive.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.will.common.log.Logger;
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive .R;
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
            fragments.put(hotTab, new LiveVideoHotFragment(CommonUrlConfig.LiveVideoHot));
            fragments.put(followTab, new LiveVideoHotFragment(CommonUrlConfig.LiveVideoFollow));
        }
    }

    @Override
    public Fragment getItem(int position) {
        Logger.e("==================getItem=====");
        String title = tabTitles.get(position);
        Fragment fragment = fragments.get(title);

        if (context.getString(R.string.live_hot).equals(title)) {
            if (fragment != null) {
                return fragment;
            }
            return new LiveVideoHotFragment(CommonUrlConfig.LiveVideoHot);
        } else {
            if (fragment != null) {
                return fragment;
            }
            return new LiveVideoHotFragment(CommonUrlConfig.LiveVideoFollow);
        }
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //第一次的代码
        //return tabTitles[position];
        //第二次的代码
        /**
         Drawable image = context.getResources().getDrawable(imageResId[position]);
         image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
         SpannableString sb = new SpannableString(" " + tabTitles[position]);
         ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
         sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         return sb;*/

        return tabTitles.get(position);
//        return null;
    }

}