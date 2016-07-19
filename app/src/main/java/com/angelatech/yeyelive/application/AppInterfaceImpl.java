package com.angelatech.yeyelive.application;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.appsflyer.AppsFlyerLib;
import com.will.imageloader.FrescoHelper;
import com.angelatech.yeyelive.db.DatabaseHelper;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 *
 *
 */
public class AppInterfaceImpl implements AppInterface {

    private ExecutorService pool = Executors.newFixedThreadPool(1);

    @Override
    public void initThirdPlugin(final Context context) {
       AppsFlyerLib.getInstance().startTracking((Application)context, "70567ec6-da70-48e3-ab54-29a1451b9e91");
        pool.execute(new Runnable() {
            @Override
            public void run() {
                FrescoHelper.frescoInitDefault(context);
            }
        });
    }

    @Override
    public void initDir(List<String> dirs) {
        for(String dir:dirs){
            File dirFile = new File(dir);
            if(!dirFile.exists()){
                dirFile.mkdirs();
            }
        }
    }

    @Override
    public void destory() {
        App.pool.shutdownNow();//关闭线程池里面所有任务
        App.sDatabaseHelper.close();
    }

    public void initService(Context context, Class<? extends Service> service,String action){
        Intent serviceIntent = new Intent(context, service);
        serviceIntent.setAction(action);
        context.startService(serviceIntent);
    }

    @Override
    public void initDB(Context context,String DBName, int DBVersion) {
        App.sDatabaseHelper = DatabaseHelper.open(context);
    }


}
