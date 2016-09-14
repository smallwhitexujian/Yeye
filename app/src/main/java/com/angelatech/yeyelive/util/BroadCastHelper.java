package com.angelatech.yeyelive.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;


public class BroadCastHelper {

    /***
     * 发送广播
     ***/
    public static void sendBroadcast(Context context, Intent intent) {
        if (context!=null){
            context.sendBroadcast(intent);
        }
    }

    public static void sendLocalBroadcast(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /****
     * 注册(取消注册)广播
     **/
    public static void registerLocalBroadCast(Context context, List<String> actions, BroadcastReceiver receiver) {
        if (receiver == null || actions == null || actions.size() == 0) {
//            throw new RuntimeException("");
            return;
        }
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
    }

    public static void unregisterLocalBroadCast(Context context, BroadcastReceiver receiver) {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        }
    }

    //注册广播
    public static void registerBroadCast(Context context, List<String> actions, BroadcastReceiver receiver) {
        if (receiver == null || actions == null || actions.size() == 0) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        context.registerReceiver(receiver, filter);
    }

    public static void registerBroadCast(Context context, String[] actions, BroadcastReceiver receiver) {
        if (receiver == null || actions == null || actions.length == 0) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        context.registerReceiver(receiver, filter);
    }


    //取消注册广播
    public static void unregisterBroadCast(Context context, BroadcastReceiver receiver) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }

}
