package com.angelatech.yeyelive.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xujian on 16/2/14.
 * 跑马灯动画工具类
 *
 */
public class MarqueeUilts {
    public static int Duratuion = 6700;//设置动画执行的时间
    private AnimationSet mAnimationSet;
    private ArrayList<HashMap<String,Object>> mData = new ArrayList<>();
    public static String CONTEXT = "context";
    public static String BITMAP = "bitmap";
    private LinearLayout mLayout;
    private Context context;

    /**
     * 绘制动画，
     * translate 水平移动动画
     *      Animation.RELATIVE_TO_PARENT 相对于父级View ,1.0f表示100%
     *      Animation.RELATIVE_TO_SELF 相对于自身View高宽 ,-1f表示-100%
     * @return 返回AnimationSet动画级
     */
    private AnimationSet initAnimationSet(){
        final AnimationSet as = new AnimationSet(false);
        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_SELF,-1.0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        ta.setInterpolator(new LinearInterpolator());
        ta.setDuration(Duratuion);
        as.addAnimation(ta);
        as.setRepeatCount(2);
        return as;
    }

    public MarqueeUilts(Context context, ArrayList<HashMap<String,Object>> data , LinearLayout layout){
        this.mData = data;
        this.mLayout = layout;
        this.context = context;
    }

    /**
     * 动画监听，用来控制跑马灯执行次数和显示内容。
     */
    public void Start(){
        mAnimationSet = initAnimationSet();
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mLayout.removeAllViews();
                mLayout.invalidate();
                if (mData.size() > 0){
                    Spanned content = (Spanned)mData.get(0).get(CONTEXT);
                    TextView tv= new TextView(context);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv.setText(content);
                    tv.setSingleLine();
                    tv.setPadding(0,0,20,0);
                    mLayout.addView(tv,0,layoutParams);
                    Bitmap bt = (Bitmap)mData.get(0).get(BITMAP);
                    if (bt != null) {
                        ImageView mImg = new ImageView(context);
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                                mLayout.getHeight()-20,mLayout.getHeight()-20);
                        mImg.setImageBitmap(bt);
                        mLayout.addView(mImg,layoutParams2);
                    }
                    mData.remove(0);
                }
                mLayout.startAnimation(animation);
                mLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mData.size() > 0) {
                    animation.start();
                }
                mLayout.setVisibility(View.GONE);
                mLayout.removeAllViews();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLayout.startAnimation(mAnimationSet);
    }
    /**
     * 启动动画
     */
    public void restartAnim(){
        if (mAnimationSet != null && mAnimationSet.hasEnded() && mData.size() == 0){
            mAnimationSet.start();
            mLayout.startAnimation(mAnimationSet);
        }
    }
    public void restart(){
        if (mAnimationSet != null ){
            mAnimationSet.start();
            mLayout.startAnimation(mAnimationSet);
        }
    }
    /**
     * 停止启动动画
     */
    public void stopAnim() {
        if (mAnimationSet != null) {
            mAnimationSet.cancel();
        }
    }

    /**
     * 动画监听急回调
     */
    public AnimationSet StartAnim(final callBack callBack){
        mAnimationSet = initAnimationSet();
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //启动动画时
                callBack.Start(animation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //结束动画
                callBack.End(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //重启
            }
        });
        return mAnimationSet;
    }

    public interface callBack{
        void Start(Animation animation);

        void End(Animation animation);
    }
}
