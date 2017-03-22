package com.angelatech.yeyelive1.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.LoginActivity;
import com.angelatech.yeyelive1.activity.function.Login;
import com.angelatech.yeyelive1.application.App;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.AccountTModel;
import com.angelatech.yeyelive1.model.LoginServerModel;
import com.angelatech.yeyelive1.socket.WillProtocol;
import com.angelatech.yeyelive1.socket.im.ImLogin;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.NotificationUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.facebook.AccessToken;
import com.framework.socket.factory.SocketModuleManager;
import com.framework.socket.factory.SocketModuleManagerImpl;
import com.framework.socket.model.SocketConfig;
import com.framework.socket.model.TcpSocketConnectorConfig;
import com.framework.socket.protocol.Protocol;
import com.will.common.log.DebugLogs;
import com.will.common.log.Logger;
import com.will.web.HttpManager;
import com.will.web.okhttp3.OkHttpManager;

import java.util.HashMap;
import java.util.Map;

/**
 * service 业务实现
 */
public class IServiceInterfaceImpl implements IServiceInterface {
    private final int DELAY = 100;
    private final int RETRYTIME = 5;
    private final int PERIOD = 10000;
    private Context mContext;
    private SocketModuleManager mImSocketModuleManager = null;//im socket 管理
    private HttpManager httpManager = new OkHttpManager();
    private boolean IM_Run = false;

    public IServiceInterfaceImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void handleAction(String action, Intent intent) {
        if (IServiceValues.ACTION_CMD_WAY.equals(action)) {
            int cmd = intent.getIntExtra(IServiceValues.KEY_BROADCAST_CMD_VALUE, -1);
            String sign = intent.getStringExtra(IServiceValues.KEY_BROADCAST_SIGN);
            DebugLogs.e("======" + cmd + "----" + sign + "-----");
            if (cmd == -1 || sign == null) {
                return;
            }
            switch (cmd) {
                case IServiceValues.CMD_LOGIN:
                    if (mImSocketModuleManager != null) {
                        mImSocketModuleManager.stopSocket();
                    }
                    final LoginServerModel loginServerModel = intent.getParcelableExtra(TransactionValues.UI_2_SERVICE_KEY1);
                    if (loginServerModel != null) {
                        //连接
                        DebugLogs.e("im----login--start");
                        Protocol protocol = new WillProtocol();
                        TcpSocketConnectorConfig connectorConfig = new TcpSocketConnectorConfig();
                        connectorConfig.setLaucherDelay(DELAY);
                        connectorConfig.setPeriod(PERIOD);
                        connectorConfig.setMaxRetrayTime(RETRYTIME);
                        mImSocketModuleManager = new SocketModuleManagerImpl(connectorConfig, protocol);

                        SocketConfig loginSocketConfig = new SocketConfig();
                        loginSocketConfig.setHost(CommonUrlConfig.OUT_IP);
                        loginSocketConfig.setPort(CommonUrlConfig.OUT_PORT);
                        loginSocketConfig.setTimeout(6000);
                        final ImLogin imLogin = new ImLogin(mContext, loginSocketConfig, mImSocketModuleManager);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                imLogin.performLogin(loginServerModel);
                            }
                        }).start();
                    }
                    break;
                case IServiceValues.CMD_ACCOUNT:
                    final AccountTModel accountTModel = (AccountTModel) intent.getSerializableExtra(TransactionValues.UI_2_SERVICE_KEY1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, String> params = new HashMap<>();
                            params.put("device", accountTModel.device);
                            params.put("userid", accountTModel.userid);
                            httpManager.getRequest(CommonUrlConfig.PlatformIntoLogIns, params);
                            IM_Run = true;
                        }
                    }).start();
                    break;
                case IServiceValues.CMD_EXIT_LOGIN://手动退出
                    if (mImSocketModuleManager != null) {
                        mImSocketModuleManager.stopSocket();
                    }
                    BasicUserInfoDBModel userInfoDBModel = CacheDataManager.getInstance().loadUser();
                    if (userInfoDBModel != null && userInfoDBModel.userid != null) {
                        CacheDataManager.getInstance().deleteAll();
                    }
                    App.isLogin = false;
                    CacheDataManager.loginUser = null;
                    AccessToken.setCurrentAccessToken(null);
                    if (App.chatRoomApplication != null) {
                        App.chatRoomApplication.exitRoom();
                    }
                    StartActivityHelper.jumpActivity(mContext, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK, null, LoginActivity.class, null);
                    Logger.e("退出=========================");
                    break;
                case IServiceValues.CMD_KICK_OUT://重复退出
                    AlertDialog.Builder b = new AlertDialog.Builder(mContext);
                    b.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (mImSocketModuleManager != null) {
                                        mImSocketModuleManager.stopSocket();
                                    }
                                    BasicUserInfoDBModel userInfoDBModel = CacheDataManager.getInstance().loadUser();
                                    if (userInfoDBModel != null && userInfoDBModel.userid != null) {
                                        CacheDataManager.getInstance().deleteAll();
                                        StartActivityHelper.jumpActivity(mContext, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK, null, LoginActivity.class, null);
                                    }
                                    App.isLogin = false;
                                    AccessToken.setCurrentAccessToken(null);
                                    NotificationUtil.clearAllNotify(mContext);//清理所有的通知
                                    CacheDataManager.loginUser = null;
                                }
                            }
                    );
                    b.setMessage(R.string.other_place_login_kick_out);
                    b.setCancelable(false);
                    AlertDialog d = b.create();
                    d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    d.show();
                    break;
            }
        }
        if (IServiceValues.ACTION_CMD_TEST.equals(action)) {

        }
    }

    @Override
    public void handleNetworkInactive() {
        //ToastUtils.showToast(mContext, "没有网络");
        if (mImSocketModuleManager != null) {
            mImSocketModuleManager.stopSocket();
            mImSocketModuleManager = null;
            App.isLogin = false;
        }
    }

    @Override
    public void handleNetworkActivie(int networkType) {
        if(IM_Run){
            BasicUserInfoDBModel userModel = CacheDataManager.getInstance().loadUser();
            LoginServerModel loginServerModel = new LoginServerModel(Long.valueOf(userModel.userid), userModel.token);
            new Login(mContext).attachIM(loginServerModel);
        }
    }
}
