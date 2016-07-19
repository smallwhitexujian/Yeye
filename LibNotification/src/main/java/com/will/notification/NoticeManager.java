package com.will.notification;

import android.content.Context;

public class NoticeManager {
    private static NoticeManager mSingleton = null;
    public final Context mContext;

    private NoticeManager(Context context) {
        this.mContext = context;
    }

    public static NoticeManager with(Context context) {
        if (mSingleton == null) {
            synchronized (NoticeManager.class) {
                if (mSingleton == null) {
                    mSingleton = new NoticeManager(context);
                }
            }
        }
        return mSingleton;
    }

    public Load load() {
        return new Load(mContext);
    }

    public void cancel(int identifier) {
        android.app.NotificationManager notifyManager = (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancel(identifier);
    }

    public void cancel(String tag, int identifier) {
        android.app.NotificationManager notifyManager = (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancel(tag, identifier);
    }

    public void cancelAll(){
        android.app.NotificationManager notifyManager = (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancelAll();
    }
}
