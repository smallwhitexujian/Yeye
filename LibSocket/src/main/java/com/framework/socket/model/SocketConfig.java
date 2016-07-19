package com.framework.socket.model;

import java.io.Serializable;

public class SocketConfig implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String host;
    private int port;
    private int timeout;

    @Override
    public String toString() {
        return "SocketInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", timeout=" + timeout +
                '}';
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}