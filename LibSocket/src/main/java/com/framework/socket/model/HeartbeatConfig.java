package com.framework.socket.model;


public class HeartbeatConfig {

    private byte[] heartbeatParcel;
    private int perod = 10000;//周期

    public HeartbeatConfig(){

    }

    public HeartbeatConfig(byte[] heartbeatParcel,int perod,boolean isOpen){
        this.heartbeatParcel = heartbeatParcel;
        this.perod = perod;
    }

    public byte[] getHeartbeatParcel() {
        return heartbeatParcel;
    }

    public void setHeartbeatParcel(byte[] heartbeatParcel) {
        this.heartbeatParcel = heartbeatParcel;
    }

    public int getPerod() {
        return perod;
    }

    public void setPerod(int perod) {
        this.perod = perod;
    }
}
