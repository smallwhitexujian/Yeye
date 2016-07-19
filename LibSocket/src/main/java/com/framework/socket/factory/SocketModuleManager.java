package com.framework.socket.factory;

import com.framework.socket.heartbeat.Heartbeat;
import com.framework.socket.out.Selector;
import com.framework.socket.out.TcpSocketCallback;
import com.framework.socket.out.TcpSocketConnectorCallback;

public interface SocketModuleManager {
    void startSocket(TcpSocketConnectorCallback tcpSocketConnectorCallback, TcpSocketCallback tcpSocketCallback, Selector selector);//启动socket
    boolean send(byte[] data);//发送数据
    void stopSocket();//停止socket
    int getRunStatus();//获取运行状态
    void takeCareHeartbeat(Heartbeat heartbeat);
}
