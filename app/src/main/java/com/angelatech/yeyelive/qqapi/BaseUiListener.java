package com.angelatech.yeyelive.qqapi;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.will.common.log.DebugLogs;

import org.json.JSONObject;

/**
 * Created by Shanli_pc on 2016/1/4.
 */
public class BaseUiListener implements IUiListener {

    protected void doComplete(JSONObject values) {
    }

    @Override
    public void onComplete(Object o) {
        doComplete((JSONObject) o);
    }

    @Override
    public void onError(UiError e) {
        DebugLogs.e("UiError" + e.toString());
    }

    @Override
    public void onCancel() {
        DebugLogs.e("onCancel");
    }

}