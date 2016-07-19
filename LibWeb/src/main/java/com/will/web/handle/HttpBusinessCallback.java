package com.will.web.handle;

import com.will.web.callback.HttpCallback;

import java.util.List;
import java.util.Map;

/**
 *  统一封装web接口中间层
 **/
public class HttpBusinessCallback implements HttpCallback{

    private WebBusinessHandler mWebHandler;

    public HttpBusinessCallback(){

    }

    @Override
    public void onFailure(Map<String, ?> errorMap) {
        if(mWebHandler != null){
            mWebHandler.handleClientNetError();
        }
    }

    @Override
    public void onSuccess(String response) {
        if(response == null){
            if(mWebHandler != null){
                mWebHandler.handleServerReturnError(response);
            }
            return;
        }
    }

    public <T> void onBusinessSuc(T data) {
        if (mWebHandler != null) {
            mWebHandler.handleBusinessSuc(data);
        }
    }

    public <T> void onBusinessSuc(List<T> datas){
        if (mWebHandler != null) {
            mWebHandler.handleBusinessSuc(datas);
        }
    }

    public void onBusinessFaild(Object ...args) {
        if (mWebHandler != null) {
            mWebHandler.handleBusinessFaild(args);
        }
    }

    public void setWebHandler(WebBusinessHandler webHandler) {
        mWebHandler = webHandler;
    }
}
