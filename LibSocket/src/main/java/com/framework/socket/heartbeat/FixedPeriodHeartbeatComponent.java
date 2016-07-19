package com.framework.socket.heartbeat;


import java.util.Timer;
import java.util.TimerTask;

import com.framework.socket.TcpSocketService;


/**
 * 定周期 心跳包
 */
public class FixedPeriodHeartbeatComponent extends  HeartbeatComponent{


	private Timer mTimer;
    private TimerTask mTimerTask;
    private int DELAY_TIME = 100;//延迟两秒
    
    protected FixedPeriodHeartbeatComponent(TcpSocketService tcpSocketServer) {
    	super(tcpSocketServer);
    }

    @Override
    public void doHeartbeat(){
        closeTimerTask();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
               send(obtainHeartbeatParcel());
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
    }


}
