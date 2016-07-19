package com.angelatech.yeyelive.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.thirdShare.WxShare;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.will.common.log.DebugLogs;


/**
 * Created by Shanli_pc on 2015/12/23.
 * 用来接收微信接口返回值
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    public static WxUIListener wxUIListener;
    public static ShareListener shareListener;
    public static String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        DebugLogs.e("IWXAPIEventHandler");
    }

    @Override
    public void onReq(BaseReq baseReq) {
        // ToastUtils.showToast(this, baseReq.openId + "");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        DebugLogs.e("=======BaseResp====" + baseResp.getType());
    }

    private void handleIntent(Intent intent) {
        DebugLogs.e("=======handleIntent====" + intent.getExtras());
        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            //用户同意
            if (resp.code != null) {
                if (WXInterface.WXType.equals("LOGIN")) {
                    if (wxUIListener != null) {
                        wxUIListener.callBackLogin(resp.code);
                    }
                } else {
                    DebugLogs.e("=====wxbind==绑定====");
                    if (wxUIListener != null) {
                        wxUIListener.callbackBind(resp.code);
                    }
                }
            } else {
                //分享回调
                wxUIListener.callbackShare("");
                if (shareListener != null) {
                    shareListener.callBackSuccess(WxShare.SHARE_TYPE_WX);
                }
            }
        }
        finish();
    }
}
