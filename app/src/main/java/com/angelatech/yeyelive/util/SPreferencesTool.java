package com.angelatech.yeyelive.util;

import android.content.Context;
import com.will.common.tool.io.SharedPreferencesTool;

/**
 * User: cbl
 * Date: 2016/3/28
 * Time: 17:40
 */
public class SPreferencesTool {
    private static SPreferencesTool mInstance;
    private final String profile_name = "userinfo";
    public final static String room_guide_key = "room_guide";
    public final static String home_guide_key = "home_guide";
    public final static String VIDEO_FILTER = "video_filter"; //美颜
    public final static String LIVENOTIFY = "live_notify";//直播提醒
    private static final String PREFERENCES_UP_APK_INFO_isUp = "isUp";
    private static final String PREFERENCES_UP_APK_INFO_VERSION = "version";
    private static final String PREFERENCES_UP_APK_INFO_MESSAGE = "message";
    private static final String PREFERENCES_UP_APK_INFO_URL = "upLoadApkUrl";

    public synchronized static SPreferencesTool getInstance() {
        if (mInstance == null) {
            mInstance = new SPreferencesTool();
        }
        return mInstance;
    }

    public SPreferencesTool() {
    }

    public void clearPreferences(Context ctx, String profileName) {
        SharedPreferencesTool.clearPreferences(ctx, profileName);
    }

    public void putValue(Context ctx, String key, Object value) {
        SharedPreferencesTool.putValue(ctx, profile_name, key, value);
    }

    public int getIntValue(Context ctx, String key) {
        return SharedPreferencesTool.getIntValue(ctx, profile_name, key);
    }

    public String getStringValue(Context ctx, String key) {
        return SharedPreferencesTool.getStringValue(ctx, profile_name, key);
    }

    public boolean getBooleanValue(Context ctx, String key) {
        return SharedPreferencesTool.getBooleanValue(ctx, profile_name, key);
    }

    public boolean getBooleanValue(Context ctx, String key, boolean defVal) {
        return SharedPreferencesTool.getBooleanValue(ctx, profile_name, key, defVal);
    }

    public long getLongValue(Context ctx, String key) {
        return SharedPreferencesTool.getLongValue(ctx, profile_name, key);
    }


    public void saveUpLoadApk(Context context, Boolean isUp, String VersionCode, String message, String URl) {
        SPreferencesTool.getInstance().putValue(context, PREFERENCES_UP_APK_INFO_isUp, isUp);
        SPreferencesTool.getInstance().putValue(context, PREFERENCES_UP_APK_INFO_VERSION, VersionCode);
        SPreferencesTool.getInstance().putValue(context, PREFERENCES_UP_APK_INFO_MESSAGE, message);
        SPreferencesTool.getInstance().putValue(context, PREFERENCES_UP_APK_INFO_URL, URl);
    }
}
