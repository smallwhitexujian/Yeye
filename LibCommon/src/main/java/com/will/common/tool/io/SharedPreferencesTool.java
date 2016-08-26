package com.will.common.tool.io;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesTool {
    public static final String STRINGTYPE = "String";
    public static final String INTTYPE = "Integer";
    public static final String LONGTYPE = "Long";
    public static final String BOOLEANTYPE = "Boolean";

    public static void clearPreferences(Context ctx, String profileName) {
        SharedPreferences properties = ctx.getSharedPreferences(profileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = properties.edit();
        editor.clear();
        editor.apply();
    }

    public static void putValue(Context ctx, String prefName, String key, Object value) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = properties.edit();

        String typeName = value.getClass().getSimpleName();
        if (INTTYPE.equals(typeName)) {
            editor.putInt(key, (Integer) value);
        } else if (BOOLEANTYPE.equals(typeName)) {
            editor.putBoolean(key, (Boolean) value);
        } else if (STRINGTYPE.equals(typeName)) {
            editor.putString(key, (String) value);
        } else if (LONGTYPE.equals(typeName)) {
            editor.putLong(key, (Long) value);
        } else {
            throw new RuntimeException();
        }
        editor.apply();
    }

    public static int getIntValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getInt(key, -1);
    }

    public static String getStringValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getString(key, null);
    }

    public static boolean getBooleanValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getBoolean(key, true);
    }

    public static boolean getBooleanValue(Context ctx, String prefName, String key, boolean defVal) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getBoolean(key, defVal);
    }

    public static long getLongValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getLong(key, -1);
    }
}