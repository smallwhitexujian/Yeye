package com.angelatech.yeyelive.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;

import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.model.ChatLineModel;
import com.angelatech.yeyelive.model.GiftModel;
import com.angelatech.yeyelive.service.IService;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.facebook.FacebookSdk;
import com.angelatech.yeyelive.db.DatabaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 初始化一些全局的控间，及全局变量
 * 例如初始化：
 * 1、activeandroid数据库
 * 2、有盟统计
 */
public class App extends Application {

    private AppInterface mAppInterface = new AppInterfaceImpl();


    //常量区
    public static boolean isDebug = false;
    public static boolean isLogin = false;//判断用户是否登录

    public static String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String FILEPATH_ROOT = SDCARD_ROOT + File.separator + AppConfig.FILEPATH_ROOT_NAME;
    public static final String FILEPATH_CACHE = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_CACHE_NAME;
    public static final String FILEPATH_VOICE = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_VOICE_NAME;
    public static final String FILEPATH_UPAPK = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_UPAPK_NAME;
    public static final String FILEPATH_CAMERA = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_CAMERA_NAME;
    public static final String FILEPATH_VOICE_RECORD = FILEPATH_VOICE + File.separator + AppConfig.FILEPATH_VOICE_RECORD_NAME;

    public static final String SERVICE_ACTION = AppConfig.SERVICE_ACTION;

    public static final ExecutorService pool = Executors.newFixedThreadPool(5);

    public static ChatRoomActivity chatroomApplication = null;                      // 保持ChatRoom存在
    public static ArrayList<ChatLineModel> mChatlines = new ArrayList<>();          // 房间数据存储
    public static List<GiftModel> giftdatas = new ArrayList<>();                    // 礼物数据存储

    public static boolean isLiveNotify = true;

    public static String topActivity = "";

    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static DatabaseHelper sDatabaseHelper;


    public static final String LIVE_WATCH = "WATCH"; //观看者
    public static final String LIVE_HOST = "LIVE"; //直播者
    public static final String LIVE_PREVIEW = "PREVIEW"; //预览

    //facebook 分享
    public static String shareTitle = "Tempat Nongkrong Favorit Gue";
    public static String shareContent = "Gue punya Room di nih guys, mampir yuk ke Room gue. Kita bisa nongkrong dan ngobrol di. Download di : goo.gl/ExzX9I";
    public static String shareURL = "http://sharetest.vvago.com/Share/Download";

    @Override
    public void onCreate() {
        super.onCreate();
        List<String> dirs = new ArrayList<>();
        {
            dirs.add(FILEPATH_CACHE);
            dirs.add(FILEPATH_VOICE);
            dirs.add(FILEPATH_UPAPK);
            dirs.add(FILEPATH_CAMERA);
            dirs.add(FILEPATH_VOICE_RECORD);
        }
        mAppInterface.initThirdPlugin(this);
        mAppInterface.initDir(dirs);
        mAppInterface.initDB(this, "yeye.db", 1);
        mAppInterface.initService(this, IService.class, SERVICE_ACTION);

        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = screenWidth * 16 / 9;
        // AppEventsLogger.activateApp(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mAppInterface.destory();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}