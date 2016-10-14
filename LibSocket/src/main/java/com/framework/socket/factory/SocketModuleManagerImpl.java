package com.framework.socket.factory;


import com.framework.socket.TcpSocket;
import com.framework.socket.TcpSocketService;
import com.framework.socket.connector.TcpSocketConnector;
import com.framework.socket.connector.TcpSocketConnectorImpl;
import com.framework.socket.heartbeat.Heartbeat;
import com.framework.socket.model.TcpSocketConnectorConfig;
import com.framework.socket.out.Selector;
import com.framework.socket.out.TcpSocketCallback;
import com.framework.socket.out.TcpSocketConnectorCallback;
import com.framework.socket.protocol.Protocol;



public class SocketModuleManagerImpl implements SocketModuleManager{
	
	private TcpSocketConnector mTcpSocketConnector;
	
	private Selector mSelector;
	private TcpSocketConnectorCallback mTcpSocketConnectorCallback;
	private TcpSocketConnectorConfig mTcpSocketConnectorConfig;
	private TcpSocketCallback mTcpSocketCallback;
	private Protocol mProtocol;
	private boolean mReconnection = false;
	
	
	public SocketModuleManagerImpl(TcpSocketConnectorConfig config,Protocol protocol) {
		mReconnection = true;
		this.mTcpSocketConnectorConfig = config;
		this.mProtocol = protocol;
	}
	
	@Override
	public void startSocket(TcpSocketConnectorCallback tcpSocketConnectorCallback,TcpSocketCallback tcpSocketCallback,Selector selector) {
		this.mTcpSocketConnectorCallback = tcpSocketConnectorCallback;
		this.mTcpSocketCallback = tcpSocketCallback;
		this.mSelector = selector;
		Builder builder = new Builder();
		mTcpSocketConnector = builder
				.setTcpConnectorConfig(mTcpSocketConnectorConfig)
				.setSelector(mSelector)
				.setTcpConnectorCallback(mTcpSocketConnectorCallback)
				.setTcpSocketCallback(mTcpSocketCallback)
				.setProtocol(mProtocol)
				.createTcpSocketConnector();
		mTcpSocketConnector.connect(this);
	}

	@Override
	public boolean send(byte[] data) {
		if(mTcpSocketConnector == null){
			return false;
		}
		TcpSocketService tcpSocketServer = mTcpSocketConnector.obtainTcpSocketServer();
		if(tcpSocketServer == null){
			return false;
		}
		return tcpSocketServer.send(data);
	}

	@Override
	public void stopSocket() {
		 mReconnection = false;
		if(mTcpSocketConnector != null){
			mTcpSocketConnector.disconnect();
		}
	}

	@Override
	public int getRunStatus() {
		if(mTcpSocketConnector != null){
			TcpSocketService tcpSocketServer = mTcpSocketConnector.obtainTcpSocketServer();
			if(tcpSocketServer != null){
				tcpSocketServer.getRunStatus();
			}
		}
		return TcpSocket.CONNECTLOST;
	}

	@Override
	public void takeCareHeartbeat(Heartbeat heartbeat) {
		if(mTcpSocketConnector == null){
			return;
		}
		TcpSocketService tcpSocketServer = mTcpSocketConnector.obtainTcpSocketServer();
		if(tcpSocketServer != null ){
			tcpSocketServer.takeCareHeartbeat(heartbeat);
		}
	}


	public Selector getSelector() {
		return mSelector;
	}

	public void setSelector(Selector mSelector) {
		this.mSelector = mSelector;
	}






	//
	public static class Builder {
		private Selector mSelector;
		private TcpSocketConnectorCallback mTcpSocketConnectorCallback;
		private TcpSocketConnectorConfig mTcpSocketConnectorConfig;
		private TcpSocketCallback mTcpSocketCallback;//业务接口
		private Protocol mProtocol;
		
		
		public Builder() {
			
		}
		
		public Builder setSelector(Selector selector){
			this.mSelector = selector;
			return this;
		}
		
		public Builder setTcpConnectorCallback(TcpSocketConnectorCallback tcpSocketConnectorCallback){
			this.mTcpSocketConnectorCallback = tcpSocketConnectorCallback;
			return this;
		}
		
		public Builder setTcpConnectorConfig(TcpSocketConnectorConfig tcpSocketConnectorConfig){
			this.mTcpSocketConnectorConfig = tcpSocketConnectorConfig;
			return this;
		}
		
		public Builder setTcpSocketCallback(TcpSocketCallback tcpSocketCallback){
			this.mTcpSocketCallback = tcpSocketCallback;
			return this;
		}
		
		public Builder setProtocol(Protocol protocol){
			this.mProtocol = protocol;
			return this;
		}
		
		
		public TcpSocketConnector createTcpSocketConnector(){
			if(mSelector == null || mTcpSocketConnectorCallback == null || mTcpSocketConnectorConfig == null ||mProtocol == null){
				throw new RuntimeException("selector or tcpsocketconnectorcallback or protocol null point exception ");
			}
			TcpSocketConnector tcpSocketConnector = new TcpSocketConnectorImpl(mTcpSocketConnectorConfig,mProtocol,mSelector, mTcpSocketConnectorCallback,mTcpSocketCallback);
			return tcpSocketConnector;
		}
	}

}
