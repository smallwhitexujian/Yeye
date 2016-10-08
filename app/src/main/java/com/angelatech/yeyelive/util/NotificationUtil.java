package com.angelatech.yeyelive.util;


import android.app.Activity;
import android.app.Notification;
import android.content.Context;

import com.angelatech.yeyelive .R;
import com.will.notification.Load;
import com.will.notification.NoticeManager;

import java.io.Serializable;


public class NotificationUtil {

    public static final int MSG_REQUEST_CODE = 1;//聊天消息

    public static final int SYSTEMASSISTANT_REQUEST_CODE = 2;//系统小秘书
    public static final int CODE_SYSTEM_NOTICE = 3;//系统公告

    public static final int NOTICE_LIVE = 4;
    public static final int NOTICE_FEEDBACK = 5;
    public static final int NOTICE_SHOW_PERSON_MSG = 6;//个人消息提醒
    public static final int NOTICE_FANS_MSG = 7;//个人消息提醒

    public static void launchNotifyDefault(Context context, int id, String ticker, String title, String msg, Class<? extends Activity> activity) {
        Load load = NoticeManager.with(context).load();
        load.title(title)
                .message(msg)
                .identifier(id)//int 唯一标识
                .smallIcon(R.drawable.ic_launcher)//小图标(这个图标需要提供多个分辨率的图片)
                .largeIcon(R.drawable.ic_launcher)//大图标(需要提供多个分辨率的图片)
                .ticker(ticker)//可有可无
                .autoCancel(true)//是否可以别取消
                .flags(Notification.DEFAULT_ALL)//可以不用动
                .clickActivity(null, activity)//参数一：action可以为null,参数二:Class<Activity>类型(可以不设置该方法)
                .simple()//普通通知可以默认使用
                .build();//显示通知
    }

    public static void launchNotifyDefault(Context context, int id, int smallId, int largeId, String ticker, String title, String msg, Class<? extends Activity> activity) {
        Load load = NoticeManager.with(context).load();
        load.title(title)
                .message(msg)
                .identifier(id)//int 唯一标识
                .smallIcon(smallId)//小图标(这个图标需要提供多个分辨率的图片)
                .largeIcon(largeId)//大图标(需要提供多个分辨率的图片)
                .ticker(ticker)//可有可无
                .autoCancel(true)//是否可以别取消
                .flags(Notification.DEFAULT_ALL)//可以不用动
                .clickActivity(null, activity)//参数一：action可以为null,参数二:Class<Activity>类型(可以不设置该方法)
                .simple()//普通通知可以默认使用
                .build();//显示通知
    }

    public static void launchNoticeWithData(Context context, int id, String ticker, String title, String msg, Class<? extends Activity> activity,String key,Serializable serializable){
        Load load = NoticeManager.with(context).load();
        load.title(title)
                .message(msg)
                .identifier(id)//int 唯一标识
                .smallIcon(R.drawable.ic_launcher)//小图标(这个图标需要提供多个分辨率的图片)
                .largeIcon(R.drawable.ic_launcher)//大图标(需要提供多个分辨率的图片)
                .ticker(ticker)//可有可无
                .autoCancel(true)//是否可以别取消
                .clickActivity1(null,activity,key,serializable)
                .flags(Notification.DEFAULT_ALL)//可以不用动
                .simple()//普通通知可以默认使用
                .build();//显示通知
    }


    public static void lauchNotifyOnlyShow(Context context, int id, String ticker, String title, String msg, String bigText) {
        Load load = NoticeManager.with(context).load();
        load.title(title)
                .message(msg)
                .identifier(id)//int 唯一标识
                .smallIcon(R.drawable.ic_launcher)//小图标(这个图标需要提供多个分辨率的图片)
                .largeIcon(R.drawable.ic_launcher)//大图标(需要提供多个分辨率的图片)
                .ticker(ticker)//可有可无
                .bigTextStyle(bigText)//4.0以上才支持(可以不设置,如果设置有些系统上会覆盖message,只显示该内容)
                .autoCancel(true)//是否可以别取消
                .flags(Notification.DEFAULT_ALL)//可以不用动
                .simple()//普通通知可以默认使用
                .build();//显示通知
    }

    public static void lauchNotifyOnlyShow(Context context, int id, int smallId, int largeId, String ticker, String title, String msg, String bigText) {
        Load load = NoticeManager.with(context).load();
        load.title(title)
                .message(msg)
                .identifier(id)//int 唯一标识
                .smallIcon(smallId)//小图标(这个图标需要提供多个分辨率的图片)
                .largeIcon(largeId)//大图标(需要提供多个分辨率的图片)
                .ticker(ticker)//可有可无
                .bigTextStyle(bigText)//4.0以上才支持(可以不设置,如果设置有些系统上会覆盖message,只显示该内容)
                .autoCancel(true)//是否可以别取消
                .flags(Notification.DEFAULT_ALL)//可以不用动
                .simple()//普通通知可以默认使用
                .build();//显示通知
    }


    public static void clearNotify(Context context, int code) {
        NoticeManager noticeManager = NoticeManager.with(context);
        noticeManager.cancel(code);

    }

    public static void clearAllNotify(Context context) {
        NoticeManager noticeManager = NoticeManager.with(context);
        noticeManager.cancelAll();
    }


}
