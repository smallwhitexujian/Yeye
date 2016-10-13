package com.angelatech.yeyelive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.angelatech.yeyelive.service.IServiceInterface;

/**
 * Created by jjfly on 16-3-3.
 *
 */
public class IServiceReceiver extends BroadcastReceiver {

    private IServiceInterface mIServiceInterface;

    public IServiceReceiver(IServiceInterface iServiceInterface){
        this.mIServiceInterface = iServiceInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null){
            return;
        }
        String action = intent.getAction();
        mIServiceInterface.handleAction(action,intent);
    }
}
