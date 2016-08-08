package com.angelatech.yeyelive.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.angelatech.yeyelive.activity.LoginActivity;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.handler.CommonDoHandler;
import com.angelatech.yeyelive.handler.CommonHandler;
import com.umeng.analytics.MobclickAgent;
import com.will.common.log.Logger;

public class BaseActivity extends FragmentActivity implements View.OnClickListener, CommonDoHandler {
    protected String TAG = BaseActivity.class.getName();

    static {
        Logger.init("av");
    }

    protected CommonHandler<BaseActivity> uiHandler;
    protected CommonHandler<BaseActivity> backgroundHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.register(this);// 将房间Activity加入activityList
        mark();
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //sharedUtil.getInstance(mContext).putBoolean(sharedUtil.PREFERENCES_RESTART, true);
        startActivity(new Intent(this, LoginActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.unregister(this);
        unmark();
    }

    public void doHandler(Message msg) {
        uiHandler.handleMessage(msg);
    }

    //私有方法区域
    private void init() {
        uiHandler = new CommonHandler(this);
        HandlerThread handlerThread = new HandlerThread(getClass().getName());
        handlerThread.start();
        backgroundHandler = new CommonHandler(this, handlerThread.getLooper());
    }

    public void onErrorCode(String codeStr) {
        try {
            int code = Integer.parseInt(codeStr);
            onErrorCode(code);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    //统一错误的code处理
    public void onErrorCode(int code) {
    }

    private void mark() {
        App.topActivity = getClass().getSimpleName();
    }

    private void unmark() {
        App.topActivity = "";
    }

}
