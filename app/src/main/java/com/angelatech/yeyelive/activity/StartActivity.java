package com.angelatech.yeyelive.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.will.common.tool.network.NetWorkUtil;
import com.angelatech.yeyelive.activity.function.Start;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.NomalAlertDialog;
import com.angelatech.yeyelive .R;

/**
 * 启动界面
 */
public class StartActivity extends BaseActivity {
    private Start mStart;
    public static final int MSG_GOTO_LOGIN = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_start);
        init();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.isLogin) {
            //外部服务器登陆成功，保证外部服务器成功直接进入首页
            StartActivityHelper.jumpActivityDefault(StartActivity.this, MainActivity.class);
            finish();
        } else {
            if (!NetWorkUtil.isNetworkConnected(StartActivity.this)) {
                NomalAlertDialog.alwaysShow2(StartActivity.this, getString(R.string.seting_newwork),
                        getString(R.string.not_newwork), getString(R.string.ok),
                        new NomalAlertDialog.HandlerDialog() {
                            @Override
                            public void handleOk() {
                                setNetwork();
                            }

                            @Override
                            public void handleCancel() {
                            }
                        }
                );
            } else {
                backgroundHandler.postDelayed(mStart, 100);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_GOTO_LOGIN:
                StartActivityHelper.jumpActivityDefault(StartActivity.this, LoginActivity.class);
                finish();
                break;
            default:
                break;
        }
    }

    private void init() {
        mStart = new Start(StartActivity.this, backgroundHandler);
    }

    private void setNetwork() {
        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
    }

}