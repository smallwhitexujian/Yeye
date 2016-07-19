package com.angelatech.yeyelive.count;

import android.content.Context;


public class AppCounter {

    private static AppCounter instance;
    private final String ACCOUNT_TIME_STAMP = "accountTimeStamp";

    private AppCounter() {

    }

    public static AppCounter getInstance() {
        if (instance == null) {
            synchronized (AppCounter.class) {
                if (instance == null) {
                    instance = new AppCounter();
                }
            }
        }
        return instance;
    }

    /**
     * 统计策略
     */
    private boolean markStrategy(Context context) {

        return false;
    }

    public void mark(Context context, long userId) {
        //不符合策略则不进行统计
        if (!markStrategy(context)) {
            return;
        }

    }

}
