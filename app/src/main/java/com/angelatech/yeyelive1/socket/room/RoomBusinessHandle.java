package com.angelatech.yeyelive1.socket.room;

import android.os.Handler;
import android.os.Message;

import com.angelatech.yeyelive1.GlobalDef;
import com.angelatech.yeyelive1.socket.WillProtocol;
import com.framework.socket.factory.SocketModuleManager;
import com.framework.socket.protocol.Protocol;
import com.will.common.log.DebugLogs;
import com.will.socket.SocketBusinessHandle;


public class RoomBusinessHandle extends SocketBusinessHandle {

    private SocketModuleManager mSocketModuleManager;

    private Protocol mProtocol = new WillProtocol();

    private Handler mRoomHandler;

    public RoomBusinessHandle(SocketModuleManager socketModuleManager) {
        this.mSocketModuleManager = socketModuleManager;
    }

    public RoomBusinessHandle(SocketModuleManager mSocketModuleManager, Handler handler) {
        super();
        this.mSocketModuleManager = mSocketModuleManager;
        this.mRoomHandler = handler;
    }

    //接收服务器回调
    @Override
    public void onReceiveParcel(byte[] bytes) {
        int type = mProtocol.getType(bytes);
        byte[] datas = mProtocol.getData(bytes);
        String response = new String(mProtocol.getData(bytes));
        DebugLogs.e("===onReceiveParcel==type:" + type + "data:" + new String(datas));
        Message msg = new Message();
        msg.what = type;
        switch (type) {
            case GlobalDef.WM_ROOM_LOGIN:
                DebugLogs.e("======房间心跳=======");
                byte[] heartbeatParcel = WillProtocol.getParcel(WillProtocol.BEATHEART_TYPE_VALYE, "");
                RoomHeartbeat roomheartbeat = new RoomHeartbeat(mSocketModuleManager, heartbeatParcel);
                roomheartbeat.doHeartbeat();
                mSocketModuleManager.takeCareHeartbeat(roomheartbeat);
                msg.obj = response;
                mRoomHandler.sendMessage(msg);
                break;
            case GlobalDef.WM_ROOM_MESSAGE: //房间消息
                msg.obj = response;
                mRoomHandler.sendMessage(msg);
                break;
            default:
                msg.obj = response;
                mRoomHandler.sendMessage(msg);
                break;
        }
    }

    @Override
    public void onLostConnect() {
        DebugLogs.e("===onLostConnect==");
        mRoomHandler.obtainMessage(GlobalDef.SERVICE_STATUS_CONNETN).sendToTarget();
    }

    @Override
    public void onReadTaskFinish() {
        DebugLogs.e("===onReadTaskFinish==");
    }
}
