package com.will.notification.pendingintent;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.will.notification.interfaces.PendingIntentNotification;

import java.io.Serializable;


public class BroadCastPendingIntent implements PendingIntentNotification {
    protected Context mContext;
    protected int mIntentFlags = Intent.FLAG_ACTIVITY_SINGLE_TOP;

    public BroadCastPendingIntent(Context context) {
        this.mContext = context;
    }

    @Override
    public void setIntentFlag(int flag) {
        mIntentFlags = flag;
    }

    @Override
    public PendingIntent onSettingPendingIntent(int identifier,String action,Bundle bundle) {
        Intent intentBroadcast = new Intent(action);
        intentBroadcast.addFlags(mIntentFlags);
        intentBroadcast.setAction(action);
        intentBroadcast.setPackage(mContext.getPackageName());
        if (bundle != null) {
            intentBroadcast.putExtras(bundle);
        }
        return PendingIntent.getBroadcast(mContext, identifier, intentBroadcast,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public PendingIntent onSettingPendingIntent2(int identifier,String action,String key, Serializable serializable) {
        Intent intentBroadcast = new Intent(action);
        intentBroadcast.addFlags(mIntentFlags);
        intentBroadcast.setAction(action);
        intentBroadcast.setPackage(mContext.getPackageName());
        if (key != null && serializable != null) {
            intentBroadcast.putExtra(key,serializable);
        }
        return PendingIntent.getBroadcast(mContext, identifier, intentBroadcast,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public PendingIntent onSettingPendingIntent3(int identifier,String action,String key,Parcelable parcelable) {
        Intent intentBroadcast = new Intent(action);
        intentBroadcast.addFlags(mIntentFlags);
        intentBroadcast.setAction(action);
        intentBroadcast.setPackage(mContext.getPackageName());
        if (key != null && parcelable != null) {
            intentBroadcast.putExtra(key,parcelable);
        }
        return PendingIntent.getBroadcast(mContext, identifier, intentBroadcast,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }


}
