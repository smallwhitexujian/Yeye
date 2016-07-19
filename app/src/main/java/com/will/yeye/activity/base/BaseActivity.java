package com.will.yeye.activity.base;

import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.will.common.log.Logger;
import com.will.yeye.application.App;
import com.will.yeye.handler.CommonDoHandler;
import com.will.yeye.handler.CommonHandler;

public class BaseActivity extends FragmentActivity implements View.OnClickListener,CommonDoHandler {
    protected String TAG = BaseActivity.class.getName();

    static {
        Logger.init("av");
    }

    protected CommonHandler<BaseActivity> uiHandler;
    protected CommonHandler<BaseActivity> backgroundHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mark();
//        if (context.toString().indexOf("ChatRoomActivity")!=23){
//            // API >= 4.4 or API < 5.0 全透明状态栏
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            }
//            // API >=5.0 全透明实现
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Window window = getWindow();
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
////            window.setStatusBarColor(Color.parseColor("#B0B0B0"));
//            }
//        }
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
    protected void onDestroy() {
        super.onDestroy();
        unmark();
    }


    public void doHandler(Message msg) {
        uiHandler.handleMessage(msg);
    }


    //私有方法区域
    private void init() {
        uiHandler = new CommonHandler(BaseActivity.this);
        HandlerThread handlerThread = new HandlerThread(getClass().getName());
        handlerThread.start();
        backgroundHandler = new CommonHandler(BaseActivity.this,handlerThread.getLooper());
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

    private void mark(){
        App.topActivity = getClass().getSimpleName();
    }

    private void unmark(){
        App.topActivity = "";
    }

}
