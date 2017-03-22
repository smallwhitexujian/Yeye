package com.angelatech.yeyelive1.web.jsonhandle;

import android.content.Context;

import com.will.web.handle.WebBusinessHandler;

import java.util.List;

/**
 * Author: jjfly
 * Since: 2016年05月04日 16:11
 * Desc: json http 处理类
 * FIXME:
 */
public class JsonWebBusinessHandler extends WebBusinessHandler {

    private JsonHandleable mClientNetErrorHandle;
    private JsonHandleable mServerErrorHandle;
    private JsonBusinessErrorHandle mJsonBusinessErrorHandle;
    private Context mContext;

    public JsonWebBusinessHandler(Context context) {
        this.mContext = context;
        this.mClientNetErrorHandle = new JsonClientNetErrorHandle();
        this.mServerErrorHandle = new JsonServerErrorHandle();
        this.mJsonBusinessErrorHandle = new JsonBusinessErrorHandle(mContext);
    }


    @Override
    public void handleClientNetError() {
        mClientNetErrorHandle.handle();
    }

    @Override
    public void handleServerReturnError(String returnStr) {
        mServerErrorHandle.handle(returnStr);
    }

    @Override
    public void handleBusinessFaild(Object... args) {
        mJsonBusinessErrorHandle.handle(args);
    }

    @Override
    public <T> void handleBusinessSuc(T data) {

    }

    @Override
    public <T> void handleBusinessSuc(List<T> datas) {

    }

}
