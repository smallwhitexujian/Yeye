package com.angelatech.yeyelive1.socket.im;

import android.content.Context;

import com.angelatech.yeyelive1.activity.function.Login;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.LoginServerModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.framework.socket.factory.SocketModuleManager;
import com.will.common.log.DebugLogs;
import com.will.socket.SocketConnectHandle;

/**
 * socket 连接器处理类
 */
public class ImConnectHandle extends SocketConnectHandle {
    public byte[] mLoginParcel;
    private Context mContext;
    private BasicUserInfoDBModel userModel;

    public ImConnectHandle(Context context, byte[] loginParcel) {
        this.mContext = context;
        this.mLoginParcel = loginParcel;
    }

    @Override
    public void retryOverlimit(int i) {
        DebugLogs.d("----是否重连---->" + i);
    }

    @Override
    public void connectFaild(int i) {
        DebugLogs.d("---------连接断开------");
        userModel = CacheDataManager.getInstance().loadUser();
        LoginServerModel loginServerModel = new LoginServerModel(Long.valueOf(userModel.userid), userModel.token);
        new Login(mContext).attachIM(loginServerModel);
    }

    @Override
    public void connectSuc(SocketModuleManager socketModuleManager, int i) {
        DebugLogs.d("----connectSuc---->" + i);
        socketModuleManager.send(mLoginParcel);
    }
}
