package com.angelatech.yeyelive.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.angelatech.yeyelive.CommonResultCode;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.RoomInfo;
import com.angelatech.yeyelive.receiver.IServiceReceiver;
import com.angelatech.yeyelive.receiver.NetworkReceiver;
import com.angelatech.yeyelive.socket.WillProtocol;
import com.angelatech.yeyelive.socket.room.RoomConnectManager;
import com.angelatech.yeyelive.util.BroadCastHelper;
import com.angelatech.yeyelive.util.JsonUtil;
import com.framework.socket.model.SocketConfig;
import com.will.common.tool.network.NetWorkUtil;
import com.will.web.HttpPostFile;
import com.will.web.callback.HttpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 后台服务类
 * 注：里面的功能通过IServiceInterface来定义，IServiceInterfaceImpl实现
 * 这样作为了更好的混淆代码，以及以后独立成进程
 */
public class IService extends Service {
    private NetworkReceiver mNetworkReceiver;
    private IServiceReceiver mIServiceReceiver;
    private IServiceInterface mIServiceInterface;//service业务逻辑接口
    private Handler mRoomHandler = null;
    private RoomConnectManager roomConnectManager;
    private MyBinder myBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        {
            mIServiceInterface = new IServiceInterfaceImpl(IService.this);
            mIServiceReceiver = new IServiceReceiver(mIServiceInterface);
            mNetworkReceiver = new NetworkReceiver(new NetworkReceiver.NetWorkHandler() {
                @Override
                public void onActive(int networkType) {
                    mIServiceInterface.handleNetworkActivie(networkType);
                    if (mRoomHandler != null) {
                        mRoomHandler.obtainMessage(IServiceValues.NETWORK_SUCCESS, networkType).sendToTarget();
                    }
                }

                @Override
                public void onInactive() {
                    mIServiceInterface.handleNetworkInactive();
                    if (mRoomHandler != null) {
                        mRoomHandler.obtainMessage(IServiceValues.NETWORK_FAILD).sendToTarget();
                    }
                }
            });
        }

        //命令action
        List<String> iServerActions = new ArrayList<>();
        {
            iServerActions.add(IServiceValues.ACTION_CMD_WAY);
            iServerActions.add(IServiceValues.ACTION_CMD_TEST);

        }
        List<String> netWorkAction = new ArrayList<>();
        {
            netWorkAction.add(NetWorkUtil.ACTION_NETWORK);
        }
        BroadCastHelper.registerBroadCast(IService.this, iServerActions, mIServiceReceiver);
        BroadCastHelper.registerBroadCast(IService.this, netWorkAction, mNetworkReceiver);
    }

    public class MyBinder extends Binder {
        public IService getIService() {
            return IService.this;
        }
    }

    public void setRoomHander(Handler hander) {
        this.mRoomHandler = hander;
    }

    /**
     * 登录房间
     */
    public void startRoomConnection(SocketConfig socketconfig, int BarId, int UserId, String token) {
        roomConnectManager = new RoomConnectManager(mRoomHandler);
        RoomInfo roomInfo = new RoomInfo();
        roomInfo.barid = BarId;
        roomInfo.userid = UserId;
        roomInfo.token = token;
        String jsonString = JsonUtil.toJson(roomInfo);
        byte[] bytes = WillProtocol.sendMessage(WillProtocol.ENTER_VOICEROOM_TYPE_VALUE, jsonString);
        roomConnectManager.performConnect(socketconfig, bytes);
    }

    /**
     * 公用拼包
     *
     * @param typeValue 包的操作码
     * @param jsonStr   发送的内容 json字符窜
     */
    public void sendMessage(int typeValue, String jsonStr) {
        byte[] bytes = WillProtocol.sendMessage(typeValue, jsonStr);
        roomConnectManager.sendMessage(bytes);
    }


    /**
     * 停掉bar代理
     */
    public void quitRoom() {
        roomConnectManager.stop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadCastHelper.unregisterBroadCast(IService.this, mNetworkReceiver);
        BroadCastHelper.unregisterBroadCast(IService.this, mIServiceReceiver);
    }

    /**
     * 图片上传
     *
     * @param imgPath
     * @param userId
     * @param token
     * @param webUrl
     * @param type
     * @param id
     */
    public void UpPicture(final String imgPath, final String userId, final String token, final String webUrl,
                          final String type, final String id, final HttpCallback callback) {
        new Thread() {
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("userid", userId);
                params.put("token", token);
                params.put("type", type);
                params.put("id", id);
                HashMap<String, String> fileparams = new HashMap<>();
                fileparams.put("imageurl", imgPath);//本地路径
                try {
                    String str = HttpPostFile.uploadFile(webUrl, params, fileparams);
                    CommonModel results = JsonUtil.fromJson(str, CommonModel.class);
                    if (results != null && results.code.equals(CommonResultCode.INTERFACE_RETURN_CODE)) {
                        callback.onSuccess(results.code);
                        //通知栏 功能 未完成
                        //int requestCode = NotificationUtil.MSG_REQUEST_CODE;
                        //NotificationUtil.launchNotifyDefault(getApplicationContext(), requestCode, getString(R.string.notification_new_message), getString(R.string.notification_new_message), getString(R.string.upload_fail), MainActivity.class);
                    } else {
                        callback.onFailure(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
