/*****
 * 提供socket 服务实现
 * 
 * 
 */

package com.framework.socket;

import com.framework.socket.heartbeat.Heartbeat;
import com.framework.socket.out.Selector;
import com.framework.socket.out.TcpSocketCallback;
import com.framework.socket.protocol.Protocol;


public class TcpSocketServicempl implements TcpSocketService{
	
	private TcpSocket mTcpSocket;
	private Selector mSelector;
	private boolean init = true;
	
	public TcpSocketServicempl(final Selector selector,final Protocol protocol,final TcpSocketCallback tcpSocketCallback) {
		if(selector == null){
			throw new RuntimeException("socket config null point exception ");
		}
		if(protocol == null){
			throw new RuntimeException("protocol null point exception ");
		}
		mSelector = selector;
		mTcpSocket = new TcpSocketImpl(mSelector.getSocketInfo(),protocol,tcpSocketCallback);
		init = true;
	}
	
	public TcpSocketServicempl(Selector selector) {
		if(selector == null){
			throw new RuntimeException("selector null point exception ");
		}
		this.mSelector = selector;
		init = true;
	}

	@Override
	public boolean connect() {
		if(init){
			return mTcpSocket.connect();
		}
		return false;
	}

	@Override
	public void recv() {
		if(init){
			mTcpSocket.recv();
		}
	}

	public boolean send(byte[] src, int start, int len) {
		if(init){
			return mTcpSocket.send(src, start, len);
		}
		return false;
	}

	@Override
	public boolean send(byte[] parcel) {
		if(init){
			return send(parcel, 0, parcel.length);
		}
		return false;
	}
	
	
	@Override
	public void takeCareHeartbeat(Heartbeat heartbeat) {
		if(init){
			mTcpSocket.takeCareHeartbeat(heartbeat);
		}
	}

	@Override
	public int getRunStatus() {
		if(init){
			return mTcpSocket.getRunStatus();
		}
		return 0;
	}

	@Override
	public void disconnect() {
		if(init){
			mTcpSocket.disconnect();
		}
		init = false;
	}
}
