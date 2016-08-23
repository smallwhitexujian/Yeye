package com.angelatech.yeyelive.util;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.will.common.log.Logger;
import com.will.common.tool.DeviceTool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 错误code辅助类
 */
public class ErrorHelper {
    private static Map<String, Map<String, String>> errorMap = new HashMap<>();
    private static final String filename = "messages.json";
    private static final String KEY_CODE = "code";
    private static final String KEY_MESSAGES = "messages";
    private static final String KEY_MESSAGE_EN = "messagesen";

    //设置errormap,从文件中读取
    private static void load(Context context) {
        try {
            InputStream in = context.getAssets().open(filename);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int len;
            while ((len = in.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            String json = new String(bos.toByteArray());
            parseJson(json);
            Logger.e("===" + json + "===" + Locale.getDefault().getLanguage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getErrorHint(Context context, String code) {
        if (errorMap.isEmpty()) {
            load(context);
        }
        Map<String, String> map = errorMap.get(code);
        if (map == null) {
            return "";
        }
        return getLocaleMessage(context, map);
    }

    public static String getErrorHint(Context context, int code) {
        return getErrorHint(context, code + "");
    }


    private static void parseJson(String jsonStr) {
        try {
            System.out.println("====" + jsonStr);
            List<Map<String, String>> datas = JsonUtil.fromJson(jsonStr, new TypeToken<List<Map>>() {
            }.getType());
            for (Map<String, String> data : datas) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if ("code".equals(key)) {
                        errorMap.put(value, data);
                        break;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldLogout(String code) {
        if ("1003".equals(code)) {
            return true;
        }
        return false;
    }


    private static String getLocaleMessage(Context context, Map<String, String> errorMap) {
        String language = DeviceTool.getCurrentLauguageUseResources(context);

        if (language != null && errorMap != null) {
            if (language.startsWith("zh")) {
                return errorMap.get(KEY_MESSAGES);
            } else {
                return errorMap.get(KEY_MESSAGE_EN);
            }
        }
        return "";
    }

}
