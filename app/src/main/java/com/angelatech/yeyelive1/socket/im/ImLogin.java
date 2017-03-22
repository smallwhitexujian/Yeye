package com.angelatech.yeyelive1.socket.im;


import android.content.Context;

import com.angelatech.yeyelive1.model.CommonParseModel;
import com.angelatech.yeyelive1.model.LoginServerModel;
import com.angelatech.yeyelive1.model.SockRecvLoginServerModel;
import com.angelatech.yeyelive1.socket.WillProtocol;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.framework.socket.SocketRequest;
import com.framework.socket.factory.SocketModuleManager;
import com.framework.socket.model.SocketConfig;
import com.framework.socket.out.Selector;
import com.google.gson.reflect.TypeToken;
import com.will.socket.SingleSocketConfigSelector;
import com.will.socket.SocketBusinessHandle;
import com.will.socket.SocketConnectHandle;


/**
 * im登陆
 */
public class ImLogin {
    private SocketConfig mLoginSocketInfo;
    private Context mContext;
    private WillProtocol willoutProtocol = new WillProtocol();

    private static boolean isWorking = false;
    private SocketModuleManager mSocketModuleManager;


    public ImLogin(Context context, SocketConfig loginSocketInfo, SocketModuleManager socketModuleManager) {
        this.mContext = context;
        this.mLoginSocketInfo = loginSocketInfo;
        this.mSocketModuleManager = socketModuleManager;
    }

    //连接登录服务器
    private SockRecvLoginServerModel connLoginServer(LoginServerModel param) {
        SocketRequest sockRequest = new SocketRequest();
        byte[] requestParcel = WillProtocol.getParcel(WillProtocol.LOGIN_TYPE_VALUE, JsonUtil.toJson(param));
        byte[] result = sockRequest.requestRead(willoutProtocol, mLoginSocketInfo, requestParcel);
        if (result == null) {
            return null;
        }
        int type = willoutProtocol.getType(result);
        byte[] datas = willoutProtocol.getData(result);
        String dataStr = new String(datas).trim();
        if (type != WillProtocol.LOGIN_TYPE_VALUE || datas == null || "".equals(dataStr)) {

            return null;
        }
        CommonParseModel<SockRecvLoginServerModel> parseModel = JsonUtil.fromJson(dataStr, new TypeToken<CommonParseModel<SockRecvLoginServerModel>>() {
        }.getType());
        if (WillProtocol.CODE_SIGN_ERROR_STR.equals(parseModel.code)) {
            return null;
        }
        if (WillProtocol.CODE_NO_MORE_SERVER_STR.equals(parseModel.code)) {
            return null;
        }
        SockRecvLoginServerModel loginInfo = parseModel.data;
        return loginInfo;
    }

    private void goImServer(SocketConnectHandle socketConnectHandle, SocketBusinessHandle socketBusinessHandle, Selector selector) {
        mSocketModuleManager.startSocket(socketConnectHandle, socketBusinessHandle, selector);
    }

    public final void performLogin(LoginServerModel param) {
        isWorking = true;
        SockRecvLoginServerModel loginInfo = connLoginServer(param);
        if (loginInfo == null) {
            //提示
            //登陆边服务器失败
            isWorking = false;
            return;
        }
        String[] ip0Port = loginInfo.ip == null ? null : loginInfo.ip.split(":");
        if (ip0Port == null) {
            //错误提示
            isWorking = false;
            return;
        }
        param.setSign(loginInfo.sign);
        byte[] loginParcel = WillProtocol.getParcel(WillProtocol.LOGIN_TYPE_VALUE, JsonUtil.toJson(param));

        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setHost(ip0Port[0]);
        socketConfig.setPort(Integer.parseInt(ip0Port[1]));
        socketConfig.setTimeout(60000);

        SingleSocketConfigSelector selector = new SingleSocketConfigSelector(socketConfig);
        SocketConnectHandle connectHandle = new ImConnectHandle(mContext, loginParcel);
        SocketBusinessHandle socketBusinessHandle = new ImBusinessHandle(mContext, mSocketModuleManager);
        goImServer(connectHandle, socketBusinessHandle, selector);
        isWorking = false;
    }
}
