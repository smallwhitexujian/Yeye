package com.will.notification.model;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.net.Uri;

/****************
 *  通知的model
 *
 *
 *
 ****************/


public class NotificationModel {
    private int identifier;//通知标识符
    private String title;//标题
    private String message;//消息内容
    private String bigTextStyle;//
    private String ticker;//
    private int background;//背景
    private int smallIcon;//小
    private int colorLight;//
    private int ledOnMs;//
    private int ledOffMs;//
    private long when;//
    private long[] vibratorTime;//
    private boolean autoCancel;//
    private Bitmap largeIcon;//
    private Uri sound;//声音
    private PendingIntent clickPendingIntent;//
    private PendingIntent dismissPendingIntent;//


}
