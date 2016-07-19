/***********************
 * 提供Tcp 的服务
 * 
 * 服务包括
 * 		1、连接tcp socket
 * 		2、管理心跳
 * 
 * 
 ***********************/


package com.framework.socket;

import com.framework.socket.heartbeat.Heartbeat;


public interface TcpSocketService {
	boolean connect();//socket连接
	void recv();//接收
	boolean send(final byte[] parcel);//发送
	void takeCareHeartbeat(Heartbeat heartbeat);//心跳
	int getRunStatus();//拿到运行状态
	void disconnect();//关闭连接
}
