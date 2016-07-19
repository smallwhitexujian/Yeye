package com.will.notification.notification;


import android.content.Context;
import android.support.v4.app.NotificationCompat;


public class Progress extends Builder {
    public Progress(Context context,NotificationCompat.Builder builder, int identifier, String tag) {
        super(context,builder, identifier, tag);
    }

    @Override
    public void build() {
        super.build();
        super.notificationNotify();
    }

    public Progress update(int identifier, int progress, int max, boolean indeterminate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setProgress(max, progress, indeterminate);

        notification = builder.build();
        notificationNotify(identifier);
        return this;
    }

    public Progress value(int progress, int max, boolean indeterminate) {
        builder.setProgress(max, progress, indeterminate);
        return this;
    }

}
