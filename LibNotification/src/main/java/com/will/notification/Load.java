package com.will.notification;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.text.Spanned;

import com.will.notification.notification.Progress;
import com.will.notification.notification.Simple;
import com.will.notification.pendingintent.ActivityPendingIntent;
import com.will.notification.pendingintent.BroadCastPendingIntent;

import java.io.Serializable;


/*****
 * load 加载器
 */

public class Load {
    private NotificationCompat.Builder builder;
    private String title;
    private String message;
    private Spanned messageSpanned;
    private String tag;
    private int notificationId;
    private int smallIcon;
    private Context mContext;

    public Load(Context context) {
        this.mContext = context;
        builder = new NotificationCompat.Builder(mContext);
        builder.setContentIntent(PendingIntent.getBroadcast(mContext, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public Load identifier(int identifier) {
        if (identifier <= 0) {
            throw new IllegalStateException("Identifier Should Not Be Less Than Or Equal To Zero!");
        }
        this.notificationId = identifier;
        return this;
    }

    public Load tag(String tag) {
        this.tag = tag;
        return this;
    }

    public Load title(int title) {
        if (title <= 0) {
            throw new IllegalArgumentException("Resource ID Should Not Be Less Than Or Equal To Zero!");
        }

        this.title = mContext.getResources().getString(title);
        this.builder.setContentTitle(this.title);
        return this;
    }

    public Load title(String title) {
        if (title == null) {
            throw new IllegalStateException("Title Must Not Be Null!");
        }

        if (title.trim().length() == 0) {
            throw new IllegalArgumentException("Title Must Not Be Empty!");
        }

        this.title = title;
        this.builder.setContentTitle(this.title);
        return this;
    }

    public Load message(int message) {
        this.message = mContext.getResources().getString(message);
        this.builder.setContentText(this.message);
        return this;
    }

    public Load message(String message) {
        this.message = message;
        this.builder.setContentText(message);
        return this;
    }

    public Load message(Spanned messageSpanned) {
        this.messageSpanned = messageSpanned;
        this.builder.setContentText(messageSpanned);
        return this;
    }

    public Load ticker(int ticker) {
        if (ticker <= 0) {
            throw new IllegalArgumentException("Resource ID Should Not Be Less Than Or Equal To Zero!");
        }

        this.builder.setTicker(mContext.getResources().getString(ticker));
        return this;
    }

    public Load ticker(String ticker) {
        if (ticker == null) {
            throw new IllegalStateException("Ticker Must Not Be Null!");
        }

        if (ticker.trim().length() == 0) {
            throw new IllegalArgumentException("Ticker Must Not Be Empty!");
        }

        this.builder.setTicker(ticker);
        return this;
    }

    public Load when(long when) {
        if (when <= 0) {
            throw new IllegalArgumentException("Resource ID Should Not Be Less Than Or Equal To Zero!");
        }

        this.builder.setWhen(when);
        return this;
    }

    public Load bigTextStyle(int bigTextStyle) {
        if (bigTextStyle <= 0) {
            throw new IllegalArgumentException("Resource ID Should Not Be Less Than Or Equal To Zero!");
        }

        return bigTextStyle(mContext.getResources().getString(
                bigTextStyle), null);
    }

    public Load bigTextStyle(int bigTextStyle,int summaryText) {
        if (bigTextStyle <= 0) {
            throw new IllegalArgumentException("Resource ID Should Not Be Less Than Or Equal To Zero!");
        }

        return bigTextStyle(mContext.getResources().getString(
                bigTextStyle), mContext.getResources().getString(
                summaryText));
    }


    public Load bigTextStyle(String bigTextStyle) {
        if (bigTextStyle.trim().length() == 0) {
            throw new IllegalArgumentException("Big Text Style Must Not Be Empty!");
        }

        return bigTextStyle(bigTextStyle, null);
    }

    public Load bigTextStyle(String bigTextStyle, String summaryText) {
        if (bigTextStyle.trim().length() == 0) {
            throw new IllegalArgumentException("Big Text Style Must Not Be Empty!");
        }

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(bigTextStyle);
        if (summaryText != null) {
            bigStyle.setSummaryText(summaryText);
        }
        this.builder.setStyle(bigStyle);
        return this;
    }

    public Load bigTextStyle(Spanned bigTextStyle, String summaryText) {
        if (bigTextStyle.length() == 0) {
            throw new IllegalArgumentException("Big Text Style Must Not Be Empty!");
        }

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(bigTextStyle);
        if (summaryText != null) {
            bigStyle.setSummaryText(summaryText);
        }
        this.builder.setStyle(bigStyle);
        return this;
    }

    public Load inboxStyle(String[] inboxLines,String title, String summary) {
        if (inboxLines.length <= 0) {
            throw new IllegalArgumentException("Inbox Lines Must Have At Least One Text!");
        }

        if (title.trim().length() == 0) {
            throw new IllegalArgumentException("Title Must Not Be Empty!");
        }

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (String inboxLine : inboxLines) {
            inboxStyle.addLine(inboxLine);
        }
        inboxStyle.setBigContentTitle(title);
        if (summary != null) {
            inboxStyle.setSummaryText(summary);
        }
        this.builder.setStyle(inboxStyle);
        return this;
    }


    public Load bigPictureStyle(Bitmap bigPic){
        if(bigPic == null){
            throw new IllegalArgumentException("Bitmap Null Ponit !");
        }
        NotificationCompat.BigPictureStyle picStyle = new NotificationCompat.BigPictureStyle();
        picStyle.bigPicture(bigPic);
        this.builder.setStyle(picStyle);
        return this;
    }

    public Load autoCancel(boolean autoCancel) {
        this.builder.setAutoCancel(autoCancel);
        return this;
    }

    public Load ongoing(boolean ongoing) {
        this.builder.setOngoing(ongoing);
        return this;
    }

    public Load smallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
        this.builder.setSmallIcon(smallIcon);
        return this;
    }

    public Load largeIcon(Bitmap bitmap) {
        this.builder.setLargeIcon(bitmap);
        return this;
    }

    public Load largeIcon(int largeIcon) {
        if (largeIcon <= 0) {
            throw new IllegalArgumentException("Resource ID Should Not Be Less Than Or Equal To Zero!");
        }

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), largeIcon);
        this.builder.setLargeIcon(bitmap);
        return this;
    }

    public Load group(String groupKey) {
        if (groupKey.trim().length() == 0) {
            throw new IllegalArgumentException("Group Key Must Not Be Empty!");
        }

        this.builder.setGroup(groupKey);
        return this;
    }

    public Load groupSummary(boolean groupSummary) {
        this.builder.setGroupSummary(groupSummary);
        return this;
    }

    public Load number(int number) {
        this.builder.setNumber(number);
        return this;
    }

    public Load vibrate( long[] vibrate) {
        for (long aVibrate : vibrate) {
            if (aVibrate <= 0) {
                throw new IllegalArgumentException("Vibrate Time " + aVibrate + " Invalid!");
            }
        }

        this.builder.setVibrate(vibrate);
        return this;
    }

    public Load lights(int color, int ledOnMs, int ledOfMs) {
        if (ledOnMs < 0) {
            throw new IllegalStateException("Led On Milliseconds Invalid!");
        }

        if (ledOfMs < 0) {
            throw new IllegalStateException("Led Off Milliseconds Invalid!");
        }

        this.builder.setLights(color, ledOnMs, ledOfMs);
        return this;
    }

    public Load sound( Uri sound) {
        this.builder.setSound(sound);
        return this;
    }

    public Load onlyAlertOnce(boolean onlyAlertOnce) {
        this.builder.setOnlyAlertOnce(onlyAlertOnce);
        return this;
    }

    public Load addPerson( String uri) {
        if (uri.length() == 0) {
            throw new IllegalArgumentException("URI Must Not Be Empty!");
        }
        this.builder.addPerson(uri);
        return this;
    }

    public Load button(int icon,  String title,  PendingIntent pendingIntent) {
        this.builder.addAction(icon, title, pendingIntent);
        return this;
    }

    public Load button(NotificationCompat.Action action) {
        this.builder.addAction(action);
        return this;
    }


    public Load clickActivity1(String action,Class<? extends Activity> activity,String key,Serializable serializable){
        this.builder.setContentIntent(new ActivityPendingIntent(mContext,activity).onSettingPendingIntent2(notificationId,action,key,serializable));
        return this;
    }

    public Load clickActivity2(String action,Class<? extends Activity> activity,String key,Parcelable parcelable){
        this.builder.setContentIntent(new ActivityPendingIntent(mContext,activity).onSettingPendingIntent3(notificationId,action,key,parcelable));
        return this;
    }

    public Load clickActivity(String action,Class<? extends Activity> activity, Bundle bundle) {
        this.builder.setContentIntent(new ActivityPendingIntent(mContext,activity).onSettingPendingIntent(notificationId,action, bundle));
        return this;
    }

    public Load clickActivity(String action,Class<? extends Activity> activity) {
        clickActivity(action,activity, null);
        return this;
    }

    public Load clickBroadCast(String action,Bundle bundle) {
        this.builder.setContentIntent(new BroadCastPendingIntent(mContext).onSettingPendingIntent(notificationId,action,bundle));
        return this;
    }

    /***提供PendingIntent****/
    public Load click(PendingIntent pendingIntent) {
        this.builder.setContentIntent(pendingIntent);
        return this;
    }


    /**
     * 优先级
     * @param priority
     * @return
     */
    public Load priority(int priority) {
        if (priority <= 0) {
            throw new IllegalArgumentException("Priority Should Not Be Less Than Or Equal To Zero!");
        }
        this.builder.setPriority(priority);
        return this;
    }

    public Load flags(int defaults) {
        this.builder.setDefaults(defaults);
        return this;
    }


    public Load dismiss(String action,Class<?> activity, Bundle bundle) {
        this.builder.setDeleteIntent(new ActivityPendingIntent(mContext,activity).onSettingPendingIntent(notificationId,action,bundle));
        return this;
    }

    public Load dismiss(String action,Class<?> activity) {
        dismiss(action,activity, null);
        return this;
    }

    public Load dismiss(String action,Bundle bundle) {
        this.builder.setDeleteIntent(new BroadCastPendingIntent(mContext).onSettingPendingIntent(notificationId,action,bundle));
        return this;
    }


    public Load dismiss( PendingIntent pendingIntent) {
        this.builder.setDeleteIntent(pendingIntent);
        return this;
    }

    /******************************************/

    public Simple simple() {
        notificationShallContainAtLeastThoseSmallIconValid();
        return new Simple(mContext,builder, notificationId, tag);
    }

    public Progress progress() {
        notificationShallContainAtLeastThoseSmallIconValid();
        return new Progress(mContext,builder, notificationId, tag);
    }

    //异常
    private void notificationShallContainAtLeastThoseSmallIconValid() {
        if (smallIcon <= 0) {
            throw new IllegalArgumentException("This is required. Notifications with an invalid icon resource will not be shown.");
        }
    }
}
