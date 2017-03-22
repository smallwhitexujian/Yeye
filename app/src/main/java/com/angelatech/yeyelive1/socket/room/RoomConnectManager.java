package com.angelatech.yeyelive1.socket.room;


import android.content.Context;
import android.os.Handler;

import com.angelatech.yeyelive1.socket.WillProtocol;
import com.framework.socket.factory.SocketModuleManager;
import com.framework.socket.factory.SocketModuleManagerImpl;
import com.framework.socket.model.SocketConfig;
import com.framework.socket.model.TcpSocketConnectorConfig;
import com.framework.socket.protocol.Protocol;
import com.will.socket.SingleSocketConfigSelector;
import com.will.socket.SocketBusinessHandle;
import com.will.socket.SocketConnectHandle;


public class RoomConnectManager {

    private final int DELAY = 100;
    private final int RETRYTIME = 5;
    private final int PERIOD = 10000;
    private Handler mRoomHandler;

    private Context mContext;

    private SocketModuleManager mSocketModuleManager;

    public RoomConnectManager(Handler hander) {
        this.mRoomHandler = hander;
    }


    //登录房间
    public final void performConnect(SocketConfig socketConfig, byte[] parcel) {
        //
        Protocol protocol = new WillProtocol();
        TcpSocketConnectorConfig connectorConfig = new TcpSocketConnectorConfig();
        connectorConfig.setLaucherDelay(DELAY);
        connectorConfig.setPeriod(PERIOD);
        connectorConfig.setMaxRetrayTime(RETRYTIME);
        mSocketModuleManager = new SocketModuleManagerImpl(connectorConfig, protocol);

        SingleSocketConfigSelector selector = new SingleSocketConfigSelector(socketConfig);
        SocketConnectHandle connectHandle = new RoomConnectHandle(mContext, parcel, mRoomHandler);
        SocketBusinessHandle socketBusinessHandle = new RoomBusinessHandle(mSocketModuleManager, mRoomHandler);
        mSocketModuleManager.startSocket(connectHandle, socketBusinessHandle, selector);
    }


    public final void sendMessage(byte[] parcel) {
        mSocketModuleManager.send(parcel);
    }

    public void Send(int type, String jsonStr) {
        byte[] parcel = WillProtocol.sendMessage(type, jsonStr);
        mSocketModuleManager.send(parcel);
    }

    public final void stop() {
        if (mSocketModuleManager != null) {
            mSocketModuleManager.stopSocket();
        }
    }
}
