package com.will.yeye.socket.room;


import com.framework.socket.factory.SocketModuleManager;
import com.framework.socket.heartbeat.Heartbeat;
import com.will.common.log.DebugLogs;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class RoomHeartbeat implements Heartbeat {

    private SocketModuleManager mSocketModuleManager;
    private byte[] mHeartbeatParcel;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private volatile boolean isRun = false;
    private int DELAY_TIME = 100;//延迟0.1秒

    public RoomHeartbeat(SocketModuleManager socketModuleManager, byte[] heartbeatParcel){
        this.mSocketModuleManager = socketModuleManager;
        this.mHeartbeatParcel = heartbeatParcel;
    }

    @Override
    public byte[] obtainHeartbeatParcel() {
        return new byte[0];
    }


    @Override
    public void doHeartbeat(){
        if(mSocketModuleManager == null || mHeartbeatParcel == null){
            return;
        }
        if(isRun){
            return;
        }
        isRun = true;
        closeTimerTask();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
                try {
                    DebugLogs.e("jj心跳");
                    mSocketModuleManager.send(mHeartbeatParcel);
                } catch (Exception e) {
                    DebugLogs.e("jj心跳异常");
                    e.printStackTrace();
                    doneHeartbeat();
                }
            }
        };
        mTimer.schedule(mTimerTask,DELAY_TIME,obtainPeriod());
    }

    private void closeTimerTask(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if(mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    public void doneHeartbeat(){
        closeTimerTask();
        isRun = false;
    }

    //获取心跳周期
    @Override
    public int obtainPeriod(){
        return 20000;
    }
}
