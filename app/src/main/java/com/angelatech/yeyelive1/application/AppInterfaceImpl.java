package com.angelatech.yeyelive1.application;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.angelatech.yeyelive1.db.DatabaseHelper;
import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.xj.frescolib.Config.FrescoHelper;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * app 初始化 接口实现
 */
public class AppInterfaceImpl implements AppInterface {

    private ExecutorService pool = Executors.newFixedThreadPool(1);

    @Override
    public void initThirdPlugin(final Context context) {
        AppsFlyerLib.getInstance().startTracking((Application) context, "9v5atDfx84wq3afTjwjsub");
        pool.execute(new Runnable() {
            @Override
            public void run() {
                FrescoHelper.frescoInit(context);
                FacebookSdk.sdkInitialize(context);
            }
        });
    }

    @Override
    public void initDir(List<String> dirs) {
        for (String dir : dirs) {
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        }
    }

    @Override
    public void destroy() {
        App.pool.shutdownNow();//关闭线程池里面所有任务
        App.sDatabaseHelper.close();
    }

    public void initService(Context context, Class<? extends Service> service, String action) {
        Intent serviceIntent = new Intent(context, service);
        serviceIntent.setAction(action);
        context.startService(serviceIntent);
    }

    @Override
    public void initDB(Context context, String DBName, int DBVersion) {
        App.sDatabaseHelper = DatabaseHelper.open(context);
    }

}
