package com.framework.socket.model;

public class WillProtocolModel<T> {

    //包长度
    private int len;
    //操作玛
    private int typeValue;
    //数据
    private T data;

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "WillProtocolModel{" +
                "len=" + len +
                ", typeValue=" + typeValue +
                ", data=" + data +
                '}';
    }
}
