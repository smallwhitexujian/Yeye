package com.will.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by cool3xp on 2015/11/10.
 */
public class LoveView extends View {
    //private Bitmap[] bLove;
    private int numLove = 0, width = 0, height = 0, bWidth = 0, bHeight = 0;//, frames = 0
    private Random random = new Random();
    private ArrayList<Love> loves = new ArrayList<Love>();
    private Paint paint = new Paint();
    //private long startTime;
    //private float fps;
    private ValueAnimator animator;
    private String TAG = "LoveView";
    private int sWidth, sHeight;
    private boolean isPlay = false;
    private Object lockPlay = new Object();
    private ArrayList<Bitmap> bitMaps = new ArrayList<>();

    public LoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoveView(Context context) {
        super(context);
    }

    public LoveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    public void addBitmap(Bitmap bitmap) {
        bitMaps.add(bitmap);
    }

    private void initLoveView() {
        if (bitMaps.size() == 0 || animator != null) {
            return;
        }
        Bitmap bitmap = bitMaps.get(0);
        bWidth = bitmap.getWidth();
        bHeight = bitmap.getHeight();
        bitmap=null;
        animator = ValueAnimator.ofFloat(0, 1);
//        bLove=new Bitmap[2];
//        bLove[0] = BitmapFactory.decodeResource(getResources(), R.drawable.love);
//        bLove[1] = BitmapFactory.decodeResource(getResources(), R.drawable.violet_heart);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                synchronized (lockPlay) {
                    if (!isPlay) {
                        return;
                    }
                }
                Iterator<Love> it = loves.iterator();
                while (it.hasNext()) {
                    Love love = it.next();
                    if (love.x + love.speedY <= bHeight * love.scale) {
                        //love.x = bHeight * love.scale;//bHeight * love.scale;
                        love.x = love.x - love.speedX;
                    } else if (love.x + love.speedX >= width - bWidth * love.scale) {
                        //love.x = width - bWidth * love.scale;
                        love.speedX = -love.speedX;
                        love.x = love.x - love.speedX;
                    } else {
                        love.x = love.x + love.speedX;
                    }
                    love.y = love.y - love.speedY;
                    if (love.scale < 1) {
                        love.scale += 0.05f;
                    }
                }
                invalidate();
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(2000);

        loves.clear();
        numLove = 0;
        //frames = 0;
        //startTime = System.currentTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        synchronized (lockPlay) {
            if (!isPlay) {
                return;
            }
        }
        Iterator<Love> it = loves.iterator();
        while (it.hasNext()) {
            Love love = it.next();
            if (love.y - love.speedY <= 0) {
                it.remove();
                synchronized (lockPlay) {
                    if (loves.size() == 0) {
                        animator.cancel();
                        isPlay = false;
                    }
                }
                numLove--;
            } else {
                //如果爱星距离控件顶部1/3的位置，开始隐藏
                if (love.y > height / 3) {
                    paint.setAlpha(255);
                } else {
                    // 动态计算透明度
                    float k = love.y / (height / 3);
                    paint.setAlpha((int) (k * 255));
                }
                if (love.scale < 1) {
                    sWidth = (int) (bWidth * love.scale);
                    sHeight = (int) (bHeight * love.scale);
                    Bitmap bDraw = Bitmap.createScaledBitmap(love.bitmap, sWidth, sHeight, true);
                    canvas.drawBitmap(bDraw, love.x, love.y, paint);
                    bDraw.recycle();
                    bDraw=null;
                } else {
                    canvas.drawBitmap(love.bitmap, love.x, love.y, paint);
                }

            }
        }
//        ++frames;
//        long nowTime = System.currentTimeMillis();
//        long deltaTime = nowTime - startTime;
//        if (deltaTime > 1000) {
//            float secs = deltaTime / 1000f;
//            fps = frames / secs;
//            Log.d(TAG, "fps:" + fps);
//            startTime = nowTime;
//            frames = 0;
//        }
    }

    public void addLove(int number) {
        if (bitMaps.size() == 0 || number == 0) {
            return;
        }
        synchronized (lockPlay) {
            if (!isPlay) {
                initLoveView();
                animator.start();
                isPlay = true;
            }
        }
        if (numLove <= 35) {
            width = getWidth();
            height = getHeight();
            for (int i = 0; i < number; ++i) {
                Love love = new Love();
//                float speedY = random.nextFloat() * 10;
//                while (speedY < 1) {
//                    speedY = random.nextFloat() * 10;
//                }
                love.bitmap = bitMaps.get(random.nextInt(bitMaps.size()));
                love.speedY = 8.f;
                love.x = width / 2 + bWidth;
                love.y = height - 10;
                love.scale = 0.05f;
                float speedX = 0;
                while (speedX == 0) {
                    speedX = random.nextFloat() - 0.8f ;
                }
                love.speedX = speedX * 4;
                loves.add(love);
            }
            numLove += number;
        }
    }

    public void onDestroy (){
        if(animator!=null) {
            animator.cancel();
            animator.removeAllListeners();
            animator=null;
        }

        if(paint!=null){
            paint=null;
        }
        if(random!=null){
            random=null;
        }

        if(lockPlay!=null){
            lockPlay=null;
        }

        if(bitMaps!=null&&bitMaps.size()>0) {
            Iterator<Bitmap> iter = bitMaps.iterator();
            while (iter.hasNext()) {
                iter.next().recycle();
            }
            bitMaps.clear();
            bitMaps=null;
        }
    }

    private class Love {
        public Bitmap bitmap;
        /**
         * 上升速度
         */
        public float speedY;
        /**
         * 平移速度
         */
        public float speedX;
        /**
         * 气泡x坐标
         */
        public float x;
        /**
         * 气泡y坐标
         */
        public float y;
        /**
         * 缩放比例
         */
        public float scale;
    }
}