package com.will.socket;

import com.framework.socket.factory.SocketModuleManager;
import com.framework.socket.out.TcpSocketConnectorCallback;

/**
 * socket 连接器处理类
 */
public abstract class SocketConnectHandle implements TcpSocketConnectorCallback{


    @Override
    public void retryOverlimit(int i) {

    }

    @Override
    public void connectFaild(int i) {

    }

    @Override
    public void connectSuc(SocketModuleManager socketModuleManager, int i) {

    }
}
