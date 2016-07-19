/*****
 * 心跳接口（用于操作心跳）
 */
package com.framework.socket.heartbeat;


public interface Heartbeat {
	void doHeartbeat();// 发送心跳包
	void doneHeartbeat();// 关闭心跳包
	int obtainPeriod();// 获取心跳周期
	byte[] obtainHeartbeatParcel();//获取心跳包

}
