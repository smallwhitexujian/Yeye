/*
 * Copyright (C) 2015, 程序亦非猿
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.angelatech.yeyelive.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.angelatech.yeyelive .R;

import java.util.Random;


public class PeriscopeLayout extends RelativeLayout {
    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dce = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速
    private Interpolator[] interpolators;

    private int mHeight;
    private int mWidth;
    private LayoutParams lp;
    private Drawable[] drawables;
    private Random random = new Random();
    private int dHeight;
    private int dWidth;

    public PeriscopeLayout(Context context) {
        super(context);
        init(context);
    }

    public PeriscopeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PeriscopeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PeriscopeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context mContext) {
        //初始化显示的图片
        drawables = new Drawable[10];
        Drawable love_0 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_1);
        Drawable love_1 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_2);
        Drawable love_2 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_3);
        Drawable love_3 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_4);
        Drawable love_4 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_5);
        Drawable love_5 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_6);
        Drawable love_6 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_7);
        Drawable love_7 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_8);
        Drawable love_8 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_10);
        Drawable love_9 = ContextCompat.getDrawable(mContext,R.drawable.icon_room_love_9);

        drawables[0] = love_0;
        drawables[1] = love_1;
        drawables[2] = love_2;
        drawables[3] = love_3;
        drawables[4] = love_4;
        drawables[5] = love_5;
        drawables[6] = love_6;
        drawables[7] = love_7;
        drawables[8] = love_8;
        drawables[9] = love_9;
        //获取图的宽高 用于后面的计算
        //注意 我这里3张图片的大小都是一样的,所以我只取了一个
        dHeight = love_0.getIntrinsicHeight();
        dWidth = love_0.getIntrinsicWidth();

        //底部 并且 水平居中
        lp = new LayoutParams(dWidth, dHeight);
        lp.addRule(CENTER_HORIZONTAL, TRUE);//这里的TRUE 要注意 不是true
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);

        // 初始化插补器
        interpolators = new Interpolator[4];
        interpolators[0] = line;
        interpolators[1] = acc;
        interpolators[2] = dce;
        interpolators[3] = accdec;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    public void addHeart() {
        ImageView imageView = new ImageView(getContext());
        //随机选一个
        imageView.setImageDrawable(drawables[random.nextInt(9)]);
        imageView.setLayoutParams(lp);
        addView(imageView);
        Animator set = getAnimator(imageView);
        set.addListener(new AnimEndListener(imageView));
        set.start();
    }

    private Animator getAnimator(View target) {
        AnimatorSet set = getEnterAnimtor(target);
        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);
        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, bezierValueAnimator);
        finalSet.setInterpolator(interpolators[random.nextInt(4)]);
        finalSet.setTarget(target);
        return finalSet;
    }

    private AnimatorSet getEnterAnimtor(final View target) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha, scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

    private ValueAnimator getBezierValueAnimator(View target) {
        //初始化一个贝塞尔计算器- - 传入
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(2), getPointF(1));
        //这里最好画个图 理解一下 传入了起点 和 终点
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((mWidth - dWidth) / 2, mHeight - dHeight), new PointF(random.nextInt(getWidth()), 0));
        animator.addUpdateListener(new BezierListenr(target));
        animator.setTarget(target);
        animator.setDuration(1500);
        return animator;
    }

    /**
     * 获取中间的两个 点
     *
     */
    private PointF getPointF(int scale) {
        try {
            PointF pointF = new PointF();
            pointF.x = random.nextInt((mWidth - 100));//减去100 是为了控制 x轴活动范围,看效果 随意~~
            //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
            pointF.y = random.nextInt((mHeight - 100)) / scale;
            return pointF;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    private class BezierListenr implements ValueAnimator.AnimatorUpdateListener {
        private View target;

        public BezierListenr(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            try{
                //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
                PointF pointF = (PointF) animation.getAnimatedValue();
                target.setX(pointF.x);
                target.setY(pointF.y);
                // 这里顺便做一个alpha动画
                target.setAlpha(1 - animation.getAnimatedFraction());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
        }
    }
}
