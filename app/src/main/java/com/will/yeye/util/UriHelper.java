package com.will.yeye.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;

/**
 *  Uri 辅助类
 */
public class UriHelper {



    public static Uri obtainUri(String str){
        if(str == null){
            throw  new RuntimeException("");
        }
        if(str.startsWith("http") || str.startsWith("https")){
            return fromUrl(str);
        }
        else{
            return fromFile(str);
        }
    }


    //from filepath
    public static Uri fromFile(String filePath){
        return Uri.fromFile(new File(filePath));
    }

    //from url
    public static Uri fromUrl(String url){
        return Uri.parse(url);
    }

    //from 资源文件
    public static Uri fromResouse(Context context,int resId){
        return Uri.parse("res://"+context.getPackageName()+"/"+ resId);
    }


}
