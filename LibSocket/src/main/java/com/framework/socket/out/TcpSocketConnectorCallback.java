package com.framework.socket.out;

import com.framework.socket.factory.SocketModuleManager;


public interface TcpSocketConnectorCallback {
	void retryOverlimit(int connectTime);// 连接超过次数用于提示统计
	void connectFaild(int connectTime);// 连接失败
	void connectSuc(SocketModuleManager socketModuleManager, int connectTime);// 连接成功
}