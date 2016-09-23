package com.angelatech.yeyelive.util;

import android.content.Context;

import com.angelatech.yeyelive.application.App;
import com.will.common.tool.io.FileTool;
import com.xj.frescolib.Config.FrescoHelper;

import java.io.File;

/**
 * Author: jjfly
 * Date: 2016-06-30 11:14
 * FIXME:
 * DESC:
 */
public class ClearCacheUtil {


    public static void clearFile(){

        /**
         *  1、 清除安装包
         *  2、清除裁剪图片
         *  3、清除相册图片
         *  4、清除语音文件
         *
         */
        FileTool.clearSdcardDir(new File(App.FILEPATH_UPAPK));
        FileTool.clearSdcardDir(new File(App.FILEPATH_CACHE));
        FileTool.clearSdcardDir(new File(App.FILEPATH_CAMERA));
        FileTool.clearSdcardDir(new File(App.FILEPATH_VOICE));
        FileTool.clearSdcardDir(new File(App.FILEPATH_VOICE_RECORD));


    }

    public static void clearImageCache(){
        /**
         * 清除fresco 缓存
         */
        FrescoHelper.clean();
    }

    public static void clearDB(Context context){

    }

    public static void clearSharedPreferences(Context context){

    }






}
