package com.angelatech.yeyelive1.web.jsonhandle;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.angelatech.yeyelive1.service.IServiceHelper;
import com.angelatech.yeyelive1.service.IServiceValues;
import com.angelatech.yeyelive1.util.BroadCastHelper;
import com.angelatech.yeyelive1.util.ErrorHelper;
import com.will.view.ToastUtils;

/**
 * Author: jjfly
 * Since: 2016年05月04日 18:13
 * Desc: 业务错误统一处理类
 * FIXME:
 */
public class JsonBusinessErrorHandle implements JsonHandleable {

    private Context mContext;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ToastUtils.showToast(mContext, ErrorHelper.getErrorHint(mContext,(String)msg.obj));
        }
    };

    public JsonBusinessErrorHandle(Context context){
        this.mContext = context;
    }

    @Override
    public void handle(Object... args) {
        if(args.length < 1){
            return;
        }
        String code = (String)args[0];
        if(mContext == null || code == null){
            return;
        }
        if(ErrorHelper.shouldLogout(code)){
            Intent exitIntent = IServiceHelper.getBroadcastIntent(IServiceValues.ACTION_CMD_WAY,IServiceValues.CMD_EXIT_LOGIN);
            BroadCastHelper.sendBroadcast(mContext, exitIntent);
        }
        else{
            mHandler.obtainMessage(0,code).sendToTarget();
        }
    }
}
