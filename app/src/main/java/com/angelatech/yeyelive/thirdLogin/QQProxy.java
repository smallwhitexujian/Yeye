package com.angelatech.yeyelive.thirdLogin;

import android.app.Activity;
import android.os.Handler;

import com.tencent.tauth.Tencent;
import com.angelatech.yeyelive.qqapi.BaseUiListenerImpl;

/**
 * User: cbl
 * Date: 2016/1/5
 * Time: 13:59
 */
public class QQProxy {

    public Tencent tencent;
    public static Activity activity;
    private Handler handler;
    public static final String SCOPE = "get_simple_userinfo";
    //QQ APP ID
    public static final String QQ_APP_ID = "1105057529";
    //QQ APP KEY
    public static final String QQ_APP_KEY = "95PCawJCfjiC9eUo";

    public QQProxy(Activity activity, Handler handler) {
        QQProxy.activity = activity;
        this.handler = handler;
    }

    public void login() {
        tencent = Tencent.createInstance(QQ_APP_ID, activity);
        BaseUiListenerImpl qqListener = new BaseUiListenerImpl(handler,BaseUiListenerImpl.TYPE_LOGIN);
        tencent.login(activity, SCOPE, qqListener);
    }

    public void bind() {
        tencent = Tencent.createInstance(QQ_APP_ID, activity);
        BaseUiListenerImpl qqListener = new BaseUiListenerImpl(handler,BaseUiListenerImpl.TYPE_BIND);
        tencent.login(activity, SCOPE, qqListener);
    }


}
