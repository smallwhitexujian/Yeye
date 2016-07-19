package com.will.common.tool;

import android.telephony.SmsManager;

import java.util.List;

public class SendSMSTool {
	
	
	// 发消息
	public static void sendSmsMessage(String phoneNumber, String content) {
		SmsManager smsManager = SmsManager.getDefault();
		// 判断短信内容的长度，如果长度大于70就会出错
		if (content.length() >= 70) {
			List<String> list = smsManager.divideMessage(content);
			for (String mMsg : list) {
				smsManager.sendTextMessage(phoneNumber, null, mMsg, null, null);
			}
		} else {
			smsManager.sendTextMessage(phoneNumber, null, content, null, null);
		}
	}

}
