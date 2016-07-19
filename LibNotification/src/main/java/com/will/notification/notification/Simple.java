package com.will.notification.notification;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class Simple extends Builder {
    public Simple(Context context,NotificationCompat.Builder builder, int identifier, String tag) {
        super(context,builder, identifier, tag);
    }

    @Override
    public void build() {
        super.build();
        super.notificationNotify();
    }
}
