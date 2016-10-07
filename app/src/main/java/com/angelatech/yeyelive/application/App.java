package com.angelatech.yeyelive.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.angelatech.yeyelive.Contants;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.db.DBConfig;
import com.angelatech.yeyelive.db.DatabaseHelper;
import com.angelatech.yeyelive.model.ChatLineModel;
import com.angelatech.yeyelive.model.GiftModel;
import com.angelatech.yeyelive.service.IService;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.duanqu.qupai.auth.AuthService;
import com.duanqu.qupai.auth.QupaiAuthListener;
import com.duanqu.qupai.httpfinal.QupaiHttpFinal;
import com.duanqu.qupai.jni.ApplicationGlue;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.will.common.log.DebugLogs;

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
    private static List<Activity> activityList = new ArrayList<>();
    private AppInterface mAppInterface = new AppInterfaceImpl();

    //常量区
    public static boolean isDebug = true;
    public static boolean isLogin = false;// 判断用户是否登录
    public static boolean isQiNiu = true; // 是否使用七牛服务器
    public static boolean isqupai = false;// 推流选择,是否是趣拍

    private static String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String FILEPATH_ROOT = SDCARD_ROOT + File.separator + AppConfig.FILEPATH_ROOT_NAME;
    public static final String FILEPATH_CACHE = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_CACHE_NAME;
    public static final String FILEPATH_VOICE = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_VOICE_NAME;
    public static final String FILEPATH_UPAPK = "yalla" + File.separator + AppConfig.FILEPATH_UPAPK_NAME;
    public static final String FILEPATH_CAMERA = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_CAMERA_NAME;
    public static final String FILEPATH_VOICE_RECORD = FILEPATH_VOICE + File.separator + AppConfig.FILEPATH_VOICE_RECORD_NAME;

    public static final String SERVICE_ACTION = AppConfig.SERVICE_ACTION;

    public static final ExecutorService pool = Executors.newFixedThreadPool(5);

    public static ChatRoomActivity chatRoomApplication = null;                       // 保持ChatRoom存在
    public static ArrayList<ChatLineModel> mChatlines = new ArrayList<>();          // 房间数据存储
    public static List<GiftModel> giftdatas = new ArrayList<>();                    // 礼物数据存储

    public static boolean isLiveNotify = true; // 直播提醒开关

    public static String topActivity = "";

    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static DatabaseHelper sDatabaseHelper;

    private static App instance = null;

    public static final String LIVE_WATCH = "WATCH"; // 观看者
    public static final String LIVE_HOST = "LIVE";   // 直播者
    public static final String LIVE_PREVIEW = "PREVIEW"; //预览

    public static String loginPhone = null;
    public static String price = "";
    public static DisplayMetrics screenDpx;

    //test
    //
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
        instance = this;
        mAppInterface.initDir(dirs);
        mAppInterface.initDB(this, DBConfig.DB_NAME, 1);
        mAppInterface.initService(this, IService.class, SERVICE_ACTION);
        mAppInterface.initThirdPlugin(this);
        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = screenWidth * 16 / 9;
        screenDpx = getResources().getDisplayMetrics(); // 取屏幕分辨率
        StreamingEnv.init(this);
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(AppConfig.PACKAGE_NAME, PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                DebugLogs.d("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //趣拍
        if (isqupai){
            System.loadLibrary("gnustl_shared");
            System.loadLibrary("qupai-media-thirdparty");
            System.loadLibrary("qupai-media-jni");
            ApplicationGlue.initialize(this);
            QupaiHttpFinal.getInstance().initOkHttpFinal();
            initAuth(getApplicationContext(), Contants.appkey, Contants.appsecret, Contants.space);
        }
    }

    /**
     * 鉴权 建议只调用一次,在Application调用。在demo里面为了测试调用了多次 得到accessToken
     *
     * @param context
     * @param appKey    appkey
     * @param appsecret appsecret
     * @param space     space
     */
    private void initAuth(Context context, String appKey, String appsecret, String space) {
        AuthService service = AuthService.getInstance();
        service.setQupaiAuthListener(new QupaiAuthListener() {
            @Override
            public void onAuthError(int errorCode, String message) {
                DebugLogs.d("onAuthComplte" + errorCode + "message" + message);
            }

            @Override
            public void onAuthComplte(int responseCode, String responseMessage) {
                DebugLogs.d("onAuthComplte" + responseCode + "message" + responseMessage);
                Contants.accessToken = responseMessage;
            }
        });
        service.startAuth(context, appKey, appsecret, space);
    }

    public synchronized static void register(Activity activity) {
        activityList.add(activity);
    }

    /**
     * Activity被销毁时，从Activities中移除
     */
    public synchronized static void unregister(Activity activity) {
        if (activityList != null && activityList.size() != 0) {
            activityList.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    // 单例模式获取唯一的MyApplication实例
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mAppInterface.destroy();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}