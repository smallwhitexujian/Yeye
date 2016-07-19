package com.will.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by moon.zhong on 2015/2/12.
 */
public class IndexView extends View {
    /*绘制索引字母所需的画笔*/
    private Paint mPaint ;

    /*索引的字母，可以添加自己想要的*/
    private String mIndex[] = {"↑","☆","A","B","C","D","E","F","G","H","I",
            "J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"};
    /*获取View的高度，以便平分索引字母*/
    private int mViewHeight ,mViewWidth;
    /*字体的大小*/
    private float mTextSize ;
    /*判断是否已经被触摸*/
    private boolean mTouched = false ;
    /*测量字体的大小*/
    private Rect mTextBound ;

    /*回调选择的索引*/
    private OnIndexSelectListener listener ;

    /*窗口，浮动View的容器，比Activity的显示更高一层*/
    private WindowManager mWindowManager ;
    /*用于显示浮动的字体，类似toast*/
    private View mFloatView ;
    /*悬浮View的宽度*/
    private int mOverlyWidth;
    /*悬浮View的高度*/
    private int mOverlyHeight ;

    public IndexView(Context context) {
        this(context, null);
    }

    public IndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView() ;
    }

    /*做些初始化工作*/
    private void initView(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG) ;
        mTextBound = new Rect() ;

        /*设置浮动选中的索引*/
        /*获取windowManager*/
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        /*overly 视图，通过LayoutInflater 获取*/
        mFloatView = LayoutInflater.from(getContext()).inflate(R.layout.overlay_indexview,null) ;
        /*开始让其不可见*/
        mFloatView.setVisibility(INVISIBLE);
        /*转换 高度 和宽度为Sp*/
        mOverlyWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,70,getResources().getDisplayMetrics()) ;
        mOverlyHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,70,getResources().getDisplayMetrics()) ;
        post(new Runnable() {
            @Override
            public void run() {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(mOverlyWidth,mOverlyHeight,
                        WindowManager.LayoutParams.TYPE_APPLICATION,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT) ;
                mWindowManager.addView(mFloatView,layoutParams);
            }
        }) ;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mTouched){
            canvas.drawColor(0x30000000);
        }
        for (int i = 0 ; i < mIndex.length ; i ++){
            mPaint.setColor(0xff000000);
            mPaint.setTextSize(mTextSize * 3.0f / 4.0f);
            mPaint.setTypeface(Typeface.DEFAULT) ;
            mPaint.getTextBounds(mIndex[i],0,mIndex[i].length(),mTextBound);
            float formX = mViewWidth/2.0f - mTextBound.width()/2.0f ;
            float formY = mTextSize*i + mTextSize/2.0f + mTextBound.height()/2.0f ;
            canvas.drawText(mIndex[i],formX,formY,mPaint);
            mPaint.reset();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.v("zgy","======onAttachedToWindow======="+mTextSize) ;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 测量本身的大小，这里只是测量宽度
     * @param widthMeaSpec 传入父View的测量标准
     * @return 测量的宽度
     */
    private int measureWidth(int widthMeaSpec){
        /*定义view的宽度*/
        int width ;
        /*获取当前 View的测量模式*/
        int mode = MeasureSpec.getMode(widthMeaSpec) ;
        /*
        * 获取当前View的测量值，这里得到的只是初步的值，
        * 我们还需根据测量模式来确定我们期望的大小
        * */
        int size = MeasureSpec.getSize(widthMeaSpec) ;
        /*
        * 如果，模式为精确模式
        * 当前View的宽度，就是我们
        * 的size ；
        * */
        if(mode == MeasureSpec.EXACTLY){
            width = size ;
        }else {
            /*否则的话我们就需要结合padding的值来确定*/
            int desire = size + getPaddingLeft() + getPaddingRight() ;
            if(mode == MeasureSpec.AT_MOST){
                width = Math.min(desire,size) ;
            }else {
                width = desire ;
            }
        }
        mViewWidth = width ;
        return width ;
    }

    /**
     * 测量本身的高度
     * @param heightMeaSpec
     * @return 高度
     */
    private int measureHeight(int heightMeaSpec){
        int height ;
        int mode = MeasureSpec.getMode(heightMeaSpec) ;
        int size = MeasureSpec.getSize(heightMeaSpec) ;
        if(mode == MeasureSpec.EXACTLY){
            height = size ;
        }else {
            int desire = size + getPaddingTop() + getPaddingBottom() ;
            if(mode == MeasureSpec.AT_MOST){
                height = Math.min(desire,size) ;
            }else {
                height = desire ;
            }
        }
        mViewHeight = height ;
        mTextSize = mViewHeight*1.0f/mIndex.length ;
        Log.v("zgy","======mTextSize======="+mTextSize) ;
        return height ;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY() ;
        int index = (int) (y / mTextSize);
        if(index >= 0 && index < mIndex.length){
            Log.v("zgy","======index======="+index) ;
            selectItem(index);
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            mTouched = true ;
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){

        }else {
            mFloatView.setVisibility(INVISIBLE);
            mTouched = false ;
        }
        invalidate();
        /*过滤点其他触摸事件*/
        return true;
    }

    private void selectItem(int position){
        mFloatView.setVisibility(VISIBLE);
        ((TextView)mFloatView).setText(mIndex[position]);
        if(listener != null){
            listener.onItemSelect(position,mIndex[position]);
        }
    }

    public void setListener(OnIndexSelectListener listener) {
        this.listener = listener;
    }

    /*定义一个回调接口*/
    public interface OnIndexSelectListener{
        /*返回选中的位置，和对应的索引名*/
        void  onItemSelect(int position, String value) ;
    }
}
