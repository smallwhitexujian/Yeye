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

    public long getLongValue(Context ctx, String key) {
        return SharedPreferencesTool.getLongValue(ctx, profile_name, key);
    }
}
