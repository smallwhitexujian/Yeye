package com.will.yeye.socket.im.dispatch;

import android.content.Context;

import com.framework.socket.factory.SocketModuleManager;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.string.json.JsonUtil;
import com.will.yeye.application.App;
import com.will.yeye.model.CommonParseModel;
import com.will.yeye.socket.WillProtocol;
import com.will.yeye.socket.im.ImHeartbeat;


/**
 * 登陆外部服务器
 *
 */
public class LoginDispatch extends Dispatchable {

    private Context mContext;
    private SocketModuleManager mSocketModuleManager;

    public LoginDispatch(SocketModuleManager socketModuleManager){
        this.mSocketModuleManager = socketModuleManager;
    }


    @Override
    public void dispatch(int type,byte[] datas) {
        String dataStr = new String(datas).trim();
        CommonParseModel<String> model = JsonUtil.fromJson(dataStr, new TypeToken<CommonParseModel<String>>() {}.getType());
        if(WillProtocol.CODE_SUCC_STR.equals(model.code)){
            DebugLogs.e("jjfly login sucess ");
            App.isLogin = true;//登陆成功

            byte[] heartbeatParcel = WillProtocol.getParcel(WillProtocol.BEATHEART_TYPE_VALYE,"");
            ImHeartbeat imHeartbeat = new ImHeartbeat(mSocketModuleManager,heartbeatParcel);
            imHeartbeat.doHeartbeat();
            mSocketModuleManager.takeCareHeartbeat(imHeartbeat);
        }else{//登陆失败
            App.isLogin = false;
        }
    }

    @Override
    public boolean validateParcel(byte[] parcel) {
        return true;
    }
}
