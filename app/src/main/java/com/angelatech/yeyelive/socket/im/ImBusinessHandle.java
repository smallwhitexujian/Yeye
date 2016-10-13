package com.angelatech.yeyelive.socket.im;

import android.content.Context;
import android.content.Intent;

import com.angelatech.yeyelive.service.IServiceHelper;
import com.angelatech.yeyelive.service.IServiceValues;
import com.angelatech.yeyelive.util.BroadCastHelper;
import com.framework.socket.factory.SocketModuleManager;
import com.framework.socket.protocol.Protocol;
import com.framework.socket.util.ByteUtil;
import com.will.common.log.DebugLogs;
import com.will.common.log.Logger;
import com.angelatech.yeyelive.socket.WillProtocol;
import com.angelatech.yeyelive.socket.im.dispatch.BroadCastDispatch;
import com.angelatech.yeyelive.socket.im.dispatch.LoginDispatch;
import com.will.socket.SocketBusinessHandle;


public class ImBusinessHandle extends SocketBusinessHandle {

    private SocketModuleManager mSocketModuleManager;

    private Protocol mProtocol = new WillProtocol();
    private Context mContext;

    public ImBusinessHandle(Context context, SocketModuleManager socketModuleManager) {
        this.mContext = context;
        this.mSocketModuleManager = socketModuleManager;
    }

    @Override
    public void onReceiveParcel(byte[] bytes) {
        Logger.e("====" + ByteUtil.bytes2Hex(bytes));
        int type = mProtocol.getType(bytes);
        byte[] datas = mProtocol.getData(bytes);
        DebugLogs.e("========" + type);
        switch (type) {
            case WillProtocol.BROADCAST_TYPE_VALUE:
                new BroadCastDispatch(mContext).dispatch(type, datas);
                break;
            case WillProtocol.LOGIN_TYPE_VALUE:
                new LoginDispatch(mSocketModuleManager).dispatch(type, datas);
                break;
            case WillProtocol.KICK_OUT_TYPE_VALUE:
                Intent exitIntent = IServiceHelper.getBroadcastIntent(IServiceValues.ACTION_CMD_WAY, IServiceValues.CMD_KICK_OUT);
                BroadCastHelper.sendBroadcast(mContext, exitIntent);
                break;
        }
    }

    @Override
    public void onLostConnect() {
        DebugLogs.d("------断开连接----_>");
    }

    @Override
    public void onReadTaskFinish() {

    }
}
