package com.will.socket;

import com.framework.socket.model.SocketConfig;
import com.framework.socket.out.Selector;

import java.util.ArrayList;
import java.util.List;

/**
 *  循环 获取socketconfig 选择器
 *
 */
public class LoopSocketConfigSelector implements Selector {


    private List<SocketConfig> mSocketConfigList ;//可用列表
    private volatile int index = 0;

    public LoopSocketConfigSelector(String host, int port, int timeout){
        if(mSocketConfigList == null){
            mSocketConfigList = new ArrayList<>();
        }
        SocketConfig config = new SocketConfig();
        config.setHost(host);
        config.setPort(port);
        config.setTimeout(timeout);
        mSocketConfigList.add(config);
    }


    public void appendSocketConfig(String host,int port,int timeout){
        if(host == null || port < 0 || timeout < 0){
            throw new RuntimeException("参数异常");
        }
        synchronized (this){
            SocketConfig config = new SocketConfig();
            config.setHost(host);
            config.setPort(port);
            config.setTimeout(timeout);
            mSocketConfigList.add(config);
        }
    }

    /**
     *  循环取socket
     */
    public SocketConfig next(){
        if(mSocketConfigList == null || mSocketConfigList.isEmpty()){
            return  null;
        }
        synchronized (this){
            int size = mSocketConfigList.size();
            if(index >= size){
                index = 0;
            }
            SocketConfig config = mSocketConfigList.get(index);
            index++;
            return config;
        }
    }


    @Override
    public SocketConfig getSocketInfo() {
        return  next();
    }

    @Override
    public void addSocketConfig(SocketConfig socketConfig) {
        if(socketConfig == null){
            return;
        }
        synchronized (this){
            mSocketConfigList.add(socketConfig);
        }
    }
}
