package com.angelatech.yeyelive.socket;

/**
 * 单例
 * fun:socket 连接管理
 */
public class ConnectManager {

    private static ConnectManager instance;
    private ConnectManager(){

    }

    public static ConnectManager getInstance(){
        if(instance == null){
            synchronized (ConnectManager.class){
                if(instance == null){
                    instance = new ConnectManager();
                }
            }
        }
        return instance;
    }


    //添加连接
    public void connect(){
    }












}
