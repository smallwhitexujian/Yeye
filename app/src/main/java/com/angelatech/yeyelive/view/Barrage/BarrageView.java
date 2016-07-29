package com.angelatech.yeyelive.view.Barrage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * User: cbl
 * Date: 2016/7/29
 * Time: 16:15
 * 弹幕 view
 */
public class BarrageView extends RelativeLayout {
    private Context mContext;
    private BarrageHandler mHandler = new BarrageHandler();
    private Random random = new Random(System.currentTimeMillis());
    public static final long BARRAGE_GAP_MIN_DURATION = 1000;//两个弹幕的最小间隔时间
    public static final long BARRAGE_GAP_MAX_DURATION = 2000;//两个弹幕的最大间隔时间
    public int maxSpeed = 10000;//速度，ms
    public int minSpeed = 5000;//速度，ms
    public int maxSize = 30;//文字大小，dp
    public int minSize = 15;//文字大小，dp

    private int totalHeight = 0;
    private int lineHeight = 0;//每一行弹幕的高度
    private int totalLine = 0;//弹幕的行数
    private String[] itemText = {"是否需要帮忙", "what are you 弄啥来", "哈哈哈哈哈哈哈", "抢占沙发。。。。。。", "************", "是否需要帮忙", "我不会轻易的狗带", "嘿嘿", "这是我见过的最长长长长长长长长长长长的评论"};
    private int textCount;

    public BarrageView(Context context) {
        this(context, null);
    }

    public BarrageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        textCount = itemText.length;
        int duration = (int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
        mHandler.sendEmptyMessageDelayed(0, duration);
    }

    class BarrageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            generateItem();
            //每个弹幕产生的间隔时间随机
            int duration = (int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
            this.sendEmptyMessageDelayed(0, duration);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        totalHeight = getMeasuredHeight();
        lineHeight = getLineHeight();
        totalLine = totalHeight / lineHeight;
    }

    private void generateItem() {
        BarrageItem item = new BarrageItem();
        String tx = itemText[(int) (Math.random() * textCount)];
        int sz = (int) (minSize + (maxSize - minSize) * Math.random());
        item.textView = new TextView(mContext);
        item.textView.setText(tx);
        item.textView.setTextSize(sz);
        item.textView.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        item.textMeasuredWidth = (int) getTextWidth(item, tx, sz);
        item.moveSpeed = (int) (minSpeed + (maxSpeed - minSpeed) * Math.random());
        if (totalLine <= 0) {
            totalHeight = getMeasuredHeight();
            lineHeight = getLineHeight();
            totalLine = totalHeight / lineHeight;
        }
        item.verticalPos = random.nextInt(totalLine) * lineHeight;
        showBarrageItem(item);
    }


    private void showBarrageItem(final BarrageItem item) {
        int leftMargin = this.getWidth();
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_TOP);
        params.topMargin = item.verticalPos;
        this.addView(item.textView, params);
        Animation anim = generateTranslateAnim(item, leftMargin);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                item.textView.clearAnimation();
                BarrageView.this.removeView(item.textView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        item.textView.startAnimation(anim);
    }

    private TranslateAnimation generateTranslateAnim(BarrageItem item, int leftMargin) {
        TranslateAnimation anim = new TranslateAnimation(leftMargin, -item.textMeasuredWidth, 0, 0);
        anim.setDuration(item.moveSpeed);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setFillAfter(true);
        return anim;
    }

    /**
     * 计算TextView中字符串的长度
     *
     * @param text 要计算的字符串
     * @param Size 字体大小
     * @return TextView中字符串的长度
     */
    public float getTextWidth(BarrageItem item, String text, float Size) {
        Rect bounds = new Rect();
        TextPaint paint;
        paint = item.textView.getPaint();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /**
     * 获得每一行弹幕的最大高度
     *
     * @return
     */
    private int getLineHeight() {
        BarrageItem item = new BarrageItem();
        String tx = itemText[0];
        item.textView = new TextView(mContext);
        item.textView.setText(tx);
        item.textView.setTextSize(maxSize);

        Rect bounds = new Rect();
        TextPaint paint;
        paint = item.textView.getPaint();
        paint.getTextBounds(tx, 0, tx.length(), bounds);
        return bounds.height();
    }
}
