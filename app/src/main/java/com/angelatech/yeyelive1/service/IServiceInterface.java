package com.angelatech.yeyelive1.service;

import android.content.Intent;

/**
 * Iserver
 */
public interface IServiceInterface {

    void handleAction(String action, Intent intent);
//    public void handleActionObj(String action);
//    public void handleActionString(String action);

    void handleNetworkInactive();

    void handleNetworkActivie(int networkType);

    //im部分接口
}
