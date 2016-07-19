package com.framework.socket.heartbeat;

import com.framework.socket.TcpSocketService;


public abstract class HeartbeatComponent implements Heartbeat{

    protected final int PERIOD = 10000;
    protected TcpSocketService mTcpSocketServer;
    protected HeartbeatComponent(TcpSocketService tcpSocketServer) {
    	if(tcpSocketServer == null){
    		throw new RuntimeException("");
    	}
		this.mTcpSocketServer = tcpSocketServer;
    }

    //发送心跳包
    @Override
    public void doHeartbeat(){

    }

    //关闭心跳包
    @Override
    public void doneHeartbeat(){

    }

    //获取心跳周期
    @Override
    public int obtainPeriod(){
        return PERIOD;
    }
    
    @Override
    public byte[] obtainHeartbeatParcel() {
    	
    	return null;
    }
    
    protected boolean send(byte[] parcel){
    	if(parcel != null){
    		return mTcpSocketServer.send(parcel);
    	}
    	return false;
    }

}
