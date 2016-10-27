package com.will.notification.pendingintent;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.will.notification.interfaces.PendingIntentNotification;

import java.io.Serializable;


public class ActivityPendingIntent implements PendingIntentNotification {
    private final Class<?> mActivity;
    protected Context mContext;
    private int mIntentFlags = Intent.FLAG_ACTIVITY_SINGLE_TOP;


    public ActivityPendingIntent(Context context, Class<?> activity) {
        this.mContext = context;
        this.mActivity = activity;
    }

    @Override
    public void setIntentFlag(int flag) {
        mIntentFlags = flag;
    }

    @Override
    public PendingIntent onSettingPendingIntent(int identifier,String action,Bundle bundle) {
        Intent intentActivity = new Intent(mContext, mActivity);
        intentActivity.addFlags(mIntentFlags);
        intentActivity.setPackage(mContext.getPackageName());
        if (bundle != null) {
            intentActivity.putExtras(bundle);
        }
        return PendingIntent.getActivity(mContext, identifier, intentActivity,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public PendingIntent onSettingPendingIntent2(int identifier,String action, String key,Serializable serializable) {
        Intent intentActivity = new Intent(mContext, mActivity);
        intentActivity.addFlags(mIntentFlags);
        intentActivity.setAction(action);
        intentActivity.setPackage(mContext.getPackageName());
        if (key != null && serializable != null) {
            intentActivity.putExtra(key,serializable);
        }
        return PendingIntent.getActivity(mContext, identifier, intentActivity,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public PendingIntent onSettingPendingIntent3(int identifier,String action,String key,Parcelable parcelable) {
        Intent intentActivity = new Intent(mContext, mActivity);
        intentActivity.addFlags(mIntentFlags);
        intentActivity.setPackage(mContext.getPackageName());
        if (key != null && parcelable != null) {
            intentActivity.putExtra(key,parcelable);
        }
        return PendingIntent.getActivity(mContext, identifier, intentActivity,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public PendingIntent onSettingPendingIntent4(int identifier,String action,String key,Serializable parcelable) {
        Intent intentActivity = new Intent(mContext, mActivity);
        intentActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intentActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intentActivity.setPackage(mContext.getPackageName());
        if (key != null && parcelable != null) {
            intentActivity.putExtra(key,parcelable);
        }
        return PendingIntent.getActivity(mContext, identifier, intentActivity,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }
}
