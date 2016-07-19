package com.angelatech.yeyelive.util;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.will.common.log.DebugLogs;

/**
 * Created by xujian on 16/3/14.
 * 系统流媒体声音开关，耳机状态监听，
 */
public class roomSoundState {
    private AudioManager audioManager;
    private static roomSoundState instance = null;
    private Context mContext ;
    public boolean isSoundOpen =  false;                    // 房间声音开关   false 表示当前声音关闭状态
    public boolean phoneState = false;                      // 是否接听电话状态 false 当前状态空闲
    private int headsetState = 0;                            // 检测耳机是否有插入状态 0.表示没有插入耳机

    public static roomSoundState getInstance(){
        if (instance == null){
            instance = new roomSoundState();
        }
        return instance;
    }

    public roomSoundState(){

    }

    public void init(Context context){
        this.mContext =context;
        getCallPhoneListener();
    }
    //获取手机电话状态
    public void getCallPhoneListener(){
        TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
        //audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

//    //设置系统声音开关 true 表示禁音状态
//    private void setSound(Boolean state){
//        audioManager.setStreamMute(AudioManager.STREAM_MUSIC ,state);
//    }

    class PhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:// 来电状态
                    DebugLogs.e("来电话了");
                   // setSound(true);

                    ChatRoom.closeChatRoom();
                    phoneState = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 接听状态
                    DebugLogs.e("在接电话");
                    //setSound(true);
                    ChatRoom.closeChatRoom();
                    phoneState = true;
                    return;
                case TelephonyManager.CALL_STATE_IDLE:// 挂断后回到空闲状态
                   // setSound(false);
                    phoneState = false;
                    break;
                default:
                    break;
            }
        }
    }

}
