package com.angelatech.yeyelive.util;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.will.common.log.DebugLogs;

/**
 * Created by xujian on 16/3/14.
 * 系统流媒体声音开关，耳机状态监听，
 */
public class roomSoundState {
    private AudioManager audioManager;
    private static roomSoundState instance = null;
    boolean phoneState = false;                      // 是否接听电话状态 false 当前状态空闲
    private static int currentVolume;

    public static roomSoundState getInstance() {
        if (instance == null) {
            instance = new roomSoundState();
        }
        return instance;
    }

    private roomSoundState() {

    }

    public void init(Context context) {
        getCallPhoneListener(context);
    }

    //获取手机电话状态
    private void getCallPhoneListener(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    //设置系统声音开关 true 表示禁音状态
    private void setSound(Boolean state) {
//        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, state);
        if (state) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);//tempVolume:音量绝对值
        }
    }

    private class PhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:// 来电状态
                    DebugLogs.e("来电话了");
                    setSound(true);
                    phoneState = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 接听状态
                    DebugLogs.e("在接电话");
                    setSound(true);
                    phoneState = true;
                    return;
                case TelephonyManager.CALL_STATE_IDLE:// 挂断后回到空闲状态
                    setSound(false);
                    phoneState = false;
                    break;
                default:
                    break;
            }
        }
    }
}

