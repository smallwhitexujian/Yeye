package com.angelatech.yeyelive.view.banner;

import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.angelatech.yeyelive.handler.CommonDoHandler;
import com.angelatech.yeyelive.handler.CommonHandler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author: jjfly
 * Since: 2016年05月06日 10:01
 * Desc: banner
 * FIXME:
 */
public class Banner<T extends View> implements CommonDoHandler {

    private final int SLEEP_PERIOD = 2;
    private final int DELAY_PERIOD = 3;
    private final int MSG_SWITCH_PAGE = 1;

    private ViewPager mViewPager;
    public static int viewPagePosition = 0;
    private CommonHandler<Banner> mCommonHandler;
    private List<T> views;
    public static boolean isAutoPlay = true;
    private ScheduledExecutorService scheduledExecutorService;

    public Banner(ViewPager viewPager, List<T> views) {
        mViewPager = viewPager;
        this.views = views;
        mCommonHandler = new CommonHandler(this);
    }

    public void showBanner() {
        BannerAdapter adapter = new BannerAdapter(views);
        mViewPager.setAdapter(adapter);
        if (isAutoPlay) {
            startPlay();
        }
    }

    /**
     * *执行轮播图切换任务
     * *@author
     */

    private class SlideShowTask implements Runnable {
        @Override
        public void run() {
            synchronized (mViewPager) {
                if (viewPagePosition >= views.size()){
                    viewPagePosition = 0;
                }
                else{
                    viewPagePosition++;
                }
                mCommonHandler.obtainMessage(MSG_SWITCH_PAGE, viewPagePosition).sendToTarget();

            }

        }
    }

    /**
     * * 开始轮播图切换
     */
    private void startPlay() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), DELAY_PERIOD,
                SLEEP_PERIOD, TimeUnit.SECONDS);
    }

    /**
     * 停止轮播图切换
     */

    private void stopPlay() {
        scheduledExecutorService.shutdown();
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_SWITCH_PAGE:
                mViewPager.setCurrentItem((Integer) msg.obj);
                break;
        }
    }

}
