package com.will.yeye.thirdLogin;

import android.app.Activity;
import android.os.Handler;

import com.facebook.login.widget.LoginButton;

/**
 * User: cbl
 * Date: 2016/1/4
 * Time: 14:36
 */
public class LoginManager {
    private Handler handler;
    //private PlatformActionListener platformActionListener;
    private Activity activity;
    private LoginButton loginButton;

    public LoginManager(Activity activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
    }


    public LoginManager(Activity activity, LoginButton loginButton, Handler handler) {
        this.activity = activity;
        this.loginButton = loginButton;
        this.handler = handler;
    }

    public enum LoginType {
        NORMAL(1, "账号登录"), QQ(2, "QQ登录"), SI_NA(3, "新浪登录"), WEI_XIN(4, "微信登录"),
        FACE_BOOK(5, "FACE_BOOK登录"), VK(6, "VK登录");

        private int type;
        private String label;

        LoginType(int type, String label) {
            this.type = type;
            this.label = label;
        }

        public LoginType getType(int type) {
            if (type == 1)
                return NORMAL;
            else if (type == 2)
                return QQ;
            else if (type == 3)
                return SI_NA;
            else if (type == 4)
                return WEI_XIN;
            else if (type == 5)
                return FACE_BOOK;
            else if (type == 6)
                return VK;
            else
                return NORMAL;
        }
    }

    public void login(LoginType loginType) {
        switch (loginType) {
            case QQ:
                new QQProxy(activity, handler).login();
                break;
            case WEI_XIN:
                new WxProxy(activity, handler).login();
                break;
            case SI_NA:
//                new SinaLogin().login();
                break;
            case FACE_BOOK:
                new FbProxy(loginButton, handler).login();
                break;
            case VK:
//                new VkLogin(activity, handler).login();
                break;
        }
    }

}
