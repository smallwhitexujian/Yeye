package com.angelatech.yeyelive.socket.im;

import android.content.Context;

import com.framework.socket.factory.SocketModuleManager;
import com.will.socket.SocketConnectHandle;

/**
 * socket 连接器处理类
 */
public class ImConnectHandle extends SocketConnectHandle{

    public byte[] mLoginParcel;
    private Context mContext;

    public ImConnectHandle(Context context,byte[] loginParcel){
        this.mContext = context;
        this.mLoginParcel = loginParcel;
    }

    @Override
    public void retryOverlimit(int i) {

    }

    @Override
    public void connectFaild(int i) {

    }

    @Override
    public void connectSuc(SocketModuleManager socketModuleManager, int i) {
        socketModuleManager.send(mLoginParcel);
    }
}
