package com.framework.socket;

import com.framework.socket.heartbeat.Heartbeat;


public interface TcpSocket {

	int CONNECTNULL = -1;// 原始状态
	int CONNECTLOST = -2;// 丢失连接
	int CONNECTFAILD = 0;// 连接失败
	int CONNECTINIT = 1;// 初始化中....
	int CONNECTING = 2;// 连接中
	int CONNECTED = 3;// 连接成功
	int CONNECTCLOSE = 4;

	
	boolean connect();//socket连接
	int getRunStatus();
	void onLostConnect();
	void recv();//
	boolean send(final byte[] src, final int start, final int len);
	
	void takeCareHeartbeat(Heartbeat heartbeat);//接收心跳
	
	void disconnect();
}
