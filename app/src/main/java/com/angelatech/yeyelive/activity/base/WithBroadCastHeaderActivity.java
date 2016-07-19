package com.angelatech.yeyelive.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.angelatech.yeyelive.util.BroadCastHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 带广播接收的activity
 *
 *
 */
public class WithBroadCastHeaderActivity extends HeaderBaseActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcast();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    protected void doReceive(String action,Intent intent){

    }

    private void registerBroadcast(){
        List<String> actions = new ArrayList<>();
        actions.add(WithBroadCastActivity.ACTION_WITH_BROADCAST_ACTIVITY);
        BroadCastHelper.registerBroadCast(WithBroadCastHeaderActivity.this,actions,receiver);
    }

    private void unRegisterBroadcast(){
        BroadCastHelper.unregisterBroadCast(WithBroadCastHeaderActivity.this,receiver);
    }




    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            doReceive(intent.getAction(),intent);
        }
    };



}
