package com.will.yeye.view.banner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.will.yeye.R;

import java.util.ArrayList;
import java.util.List;

/**
 * User: cbl
 * Date: 2016/5/17
 * Time: 14:45
 * view pager 圆点
 */
public class BannerPoint {
    private List<View> pointViews = new ArrayList<>();
    private Context context;

    public BannerPoint(Context context) {
        this.context = context;
    }

    /**
     * 添加圆点
     * @param pointGroup 父组件
     * @param pageSize 圆点总数
     */
    public void AddPoint(LinearLayout pointGroup, int pageSize) {
        ImageView imageView;
        for (int i = 0; i < pageSize; i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.point_background);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            pointGroup.addView(imageView, layoutParams);
            pointViews.add(imageView);
        }
    }
}
