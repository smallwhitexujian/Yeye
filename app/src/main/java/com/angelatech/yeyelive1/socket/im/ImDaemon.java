package com.angelatech.yeyelive1.socket.im;

import com.framework.socket.factory.SocketModuleManager;

/**
 * im 守护
 */
public class ImDaemon {

    private static Object lock = new Object();

    public static void startWatch(SocketModuleManager proxy) {

    }

    public static void stopWatch(SocketModuleManager proxy) {

    }

    /***
     * 连接探测
     */
    private static void connectProbe() {
        synchronized (lock) {
        }
    }

}
