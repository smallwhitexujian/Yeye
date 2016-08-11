package com.angelatech.yeyelive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;

import com.will.common.log.DebugLogs;
import com.will.common.tool.network.NetWorkUtil;

/***
 * 网络广播
 */
public class NetworkReceiver extends BroadcastReceiver {

    public static int HISTORY_TYPE = NetWorkUtil.TYPE_NOT_CONNECTED;

    private NetWorkHandler mNetWorkHandler;

    public NetworkReceiver(NetWorkHandler netWorkHandler) {
        this.mNetWorkHandler = netWorkHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (NetWorkUtil.ACTION_NETWORK.equals(action)) {
                NetworkInfo info = NetWorkUtil.getNetworkInfo(context);
                if (info != null) {
                    if (HISTORY_TYPE == info.getType()) {
                        return;
                    }
                    HISTORY_TYPE = info.getType();
                    String name = info.getTypeName();
                    if (mNetWorkHandler != null) {
                        mNetWorkHandler.onActive(HISTORY_TYPE);
                    }
                    DebugLogs.e("当前网络名称：" + name);
                    //doSomething()
                } else {
                    HISTORY_TYPE = NetWorkUtil.TYPE_NOT_CONNECTED;
                    DebugLogs.e("没有可用网络");
                    if (mNetWorkHandler != null) {
                        mNetWorkHandler.onInactive();
                    }
                }
            }
        }
    }

    public interface NetWorkHandler {
        void onActive(int networkType);//网络可用

        void onInactive();//网络不可用
    }
}
