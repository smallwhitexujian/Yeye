package com.framework.socket.model;

/**
 * Created by jjfly on 15-12-29.
 */
public class TcpSocketConnectorConfig {

    private int maxRetrayTime = 5;//重连次数
    private int laucherDelay = 1000;//定时器启动时候延时
    private int period = 10000;//定时器定时周期


    public int getMaxRetrayTime() {
        return maxRetrayTime;
    }

    public void setMaxRetrayTime(int maxRetrayTime) {
        this.maxRetrayTime = maxRetrayTime;
    }

    public int getLaucherDelay() {
        return laucherDelay;
    }

    public void setLaucherDelay(int laucherDelay) {
        this.laucherDelay = laucherDelay;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
