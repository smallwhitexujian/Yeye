package com.will.notification.interfaces;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

public interface    PendingIntentNotification {

    PendingIntent onSettingPendingIntent(int identifier,String action, Bundle bundle);
    PendingIntent onSettingPendingIntent2(int identifier,String action,String key,Serializable serializable);
    PendingIntent onSettingPendingIntent3(int identifier,String action,String key, Parcelable parcelable);
    void setIntentFlag(int flag);

}
