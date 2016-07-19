package com.framework.socket.model;


/**
 *
 *
 * @param <T>
 * 
 * 解析出协议包中的类型+协议中数据data为指定泛型对象
 * 
 * 
 */

public class ProtocolWrap<T> {
	
	private int type;
	private T data;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	
	@Override
	public String toString() {
		return "ParseResultWrap [type=" + type + ", data=" + data + "]";
	}
	
	

}
