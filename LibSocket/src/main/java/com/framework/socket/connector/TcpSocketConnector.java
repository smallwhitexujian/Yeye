package com.framework.socket.connector;

import com.framework.socket.TcpSocketService;
import com.framework.socket.factory.SocketModuleManager;
import com.framework.socket.heartbeat.HeartbeatComponent;

public interface TcpSocketConnector {
	
	 void connect(SocketModuleManager socketModuleManager);
	 void connectWithHeartbeat(SocketModuleManager socketModuleManager, final HeartbeatComponent heartbeatComponent);
	 TcpSocketService obtainTcpSocketServer();
	 void disconnect();
	 
}
