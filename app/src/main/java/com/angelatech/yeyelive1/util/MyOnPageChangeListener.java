package com.angelatech.yeyelive1.util;

import android.app.Activity;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.angelatech.yeyelive1.activity.TabActivity;

/**
 *
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
 *
 *
 * 作者: Created by: xujian on Date: 16/9/21.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive1.util
 */

public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    private int position_two;
    private View view;
    private String objName = "";
    private int c_width;

    public MyOnPageChangeListener(String objName) {
        this.objName = objName;
    }

    public void InitWidth(ImageView ivBottomLine, Activity ac) {
        view = ivBottomLine;
        bottomLineWidth = view.getLayoutParams().width;
        DisplayMetrics dm = new DisplayMetrics();
        ac.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        switch (objName) {
            case "Fragment":
                position_one = screenW / 3;
                position_two = screenW / 3 * 2;
                c_width = screenW / 3;
                break;
            case "TabActivity":
                position_one = bottomLineWidth ;
                position_two = bottomLineWidth ;
                c_width = bottomLineWidth;
                break;
        }
        ivBottomLine.getLayoutParams().width = c_width;
        Matrix matrix = new Matrix();
        matrix.postTranslate(c_width, 0);
        ivBottomLine.setImageMatrix(matrix);// 设置动画初始位置
    }

    @Override
    public void onPageSelected(int arg0) {
        int currIndex = 0;
        switch (objName) {
            case "TabActivity":
                currIndex = TabActivity.currIndex;
                break;
        }
        Animation animation = null;
        switch (arg0) {
            case 0:
                if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, 0, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(position_two, 0, 0, 0);
                }
                break;
            case 1:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, position_one, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(position_two, position_one, 0, 0);
                }
                break;
            case 2:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, position_two, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, position_two, 0, 0);
                }
                break;
        }

        switch (objName) {
            case "TabActivity":
                TabActivity.currIndex = arg0;
                break;
        }
        if (animation != null) {
            animation.setFillAfter(true);
            animation.setDuration(300);
            view.startAnimation(animation);
        }

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        switch (objName) {
            case "TabActivity":
                if (arg0 == 0) {
                    TabActivity.Tab_1.setSelected(true);
                    TabActivity.Tab_2.setSelected(false);
                    TabActivity.Tab_1.setTextColor(0xFFD80C18);
                    TabActivity.Tab_2.setTextColor(0xFF949494);
                } else if (arg0 == 1) {
                    TabActivity.Tab_2.setSelected(true);
                    TabActivity.Tab_1.setSelected(false);
                    TabActivity.Tab_2.setTextColor(0xFFD80C18);
                    TabActivity.Tab_1.setTextColor(0xFF949494);
                }
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }
}
