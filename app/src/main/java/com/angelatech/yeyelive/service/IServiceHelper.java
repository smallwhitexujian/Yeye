package com.angelatech.yeyelive.service;

import android.content.Intent;
import android.os.Parcelable;

import com.will.common.intent.IntentBuilder;

import java.io.Serializable;

/***
 *
 *  iservice辅助类，便于混淆
 *
 *
 *
 *
 */
public class IServiceHelper{


    public static Intent getSerializableIntent(String action,int cmd,String key,Serializable serializable){
        IntentBuilder builder = getIntentBuilder(action,cmd);
        builder.extra(key,serializable);
        return builder.build();
    }


    public static Intent getParcelableIntent(String action,int cmd,String key,Parcelable parcelable){
        IntentBuilder builder = getIntentBuilder(action,cmd);
        builder.extra(key,parcelable);
        return builder.build();
    }


    public static Intent SerializablesIntent(String action,int cmd,String key, Serializable[] values){
        IntentBuilder builder = getIntentBuilder(action,cmd);
        builder.extra(key,values);
        return builder.build();
    }


    public static Intent getParcelablesIntent(String action,int cmd,String key,Parcelable[] parcelables){
        IntentBuilder builder = getIntentBuilder(action,cmd);
        builder.extra(key,parcelables);
        return builder.build();
    }

    /**没有数据参数**/
    public static Intent getBroadcastIntent(String action,int cmd){
        IntentBuilder builder = getIntentBuilder(action,cmd);
        return builder.build();
    }


    public static IntentBuilder getIntentBuilder(String action,int cmd){
        IntentBuilder builder = new IntentBuilder();
        builder
                .action(action)
                .extra(IServiceValues.KEY_BROADCAST_CMD_VALUE,cmd)
                .extra(IServiceValues.KEY_BROADCAST_SIGN,obtainSign());

        return builder;
    }


    /**
     * 获取签名
     */
    public static String obtainSign(){

        return "";
    }


    /**
     * 校验签名
     */
    public static boolean checkSign(String sign){

        return true;
    }



}
