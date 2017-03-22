package com.angelatech.yeyelive1.socket.room;

import android.content.Context;
import android.os.Handler;

import com.angelatech.yeyelive1.GlobalDef;
import com.framework.socket.factory.SocketModuleManager;
import com.will.common.log.DebugLogs;
import com.will.socket.SocketConnectHandle;

/**
 * socket 连接器处理类
 */
public class RoomConnectHandle extends SocketConnectHandle {

    public byte[] mParcel;
    private Context mContext;
    private Handler mRoomHandler;

    public RoomConnectHandle(Context context, byte[] parcel) {
        this.mContext = context;
        this.mParcel = parcel;
    }

    public RoomConnectHandle(Context mContext, byte[] parcel, Handler handler) {
        super();

        this.mContext = mContext;
        this.mParcel = parcel;
        this.mRoomHandler = handler;
    }

    @Override
    public void retryOverlimit(int i) {
//        if (i > 5) {
//            mRoomHandler.sendEmptyMessage(GlobalDef.SERVICE_STATUS_FAILD);
//        }
    }

    @Override
    public void connectFaild(int i) {
        DebugLogs.e("======连接失败=====" + i);
        mRoomHandler.obtainMessage(GlobalDef.SERVICE_STATUS_FAILD,i).sendToTarget();
    }

    @Override
    public void connectSuc(SocketModuleManager socketModuleManager, int i) {
        mRoomHandler.sendEmptyMessage(GlobalDef.SERVICE_STATUS_SUCCESS);
        if (mParcel != null) {
            socketModuleManager.send(mParcel);
        }
    }
}
