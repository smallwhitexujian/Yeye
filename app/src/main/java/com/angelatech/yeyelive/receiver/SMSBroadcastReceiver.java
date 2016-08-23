package com.angelatech.yeyelive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: cbl
 * Date: 2016/8/23
 * Time: 13:55
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static MessageListener mMessageListener;
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public SMSBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            Object[] puds = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : puds) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = smsMessage.getDisplayOriginatingAddress();
                //短信内容
                String content = smsMessage.getDisplayMessageBody();
                long date = smsMessage.getTimestampMillis();
                Date timeDate = new Date(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(timeDate);
            }
        }

    }

    //回调接口
    public interface MessageListener {
        void onReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        SMSBroadcastReceiver.mMessageListener = messageListener;
    }
}
