package com.will.socket;

import com.framework.socket.model.SocketConfig;
import com.framework.socket.out.Selector;

/**
 *   单个socket信息选择器
 *
 *
 */
public class SingleSocketConfigSelector implements Selector {


    private SocketConfig mSocketConfig;

    public SingleSocketConfigSelector(){

    }

    public SingleSocketConfigSelector(String host, int port, int timeout){
        mSocketConfig = new SocketConfig();
        mSocketConfig.setHost(host);
        mSocketConfig.setPort(port);
        mSocketConfig.setTimeout(timeout);
    }

    public SingleSocketConfigSelector(SocketConfig socketConfig){
        this.mSocketConfig = socketConfig;
    }


    @Override
    public SocketConfig getSocketInfo() {
        return mSocketConfig;
    }

    @Override
    public void addSocketConfig(SocketConfig socketConfig) {
        this.mSocketConfig = socketConfig;
    }
}
