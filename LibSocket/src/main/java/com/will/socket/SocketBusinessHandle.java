package com.will.socket;

import com.framework.socket.out.TcpSocketCallback;

/***
 * socket 业务处理类，需要自己实现
 */
public abstract class SocketBusinessHandle implements TcpSocketCallback {

//    private boolean

    @Override
    public void onReceiveParcel(byte[] bytes) {

    }

    @Override
    public void onLostConnect() {

    }

    @Override
    public void onReadTaskFinish() {

    }
}
