package com.android.vending.billing.pay;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.android.vending.billing.log.DebugLogs;
import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;

import java.util.ArrayList;
import java.util.List;


public class PayManager {

    public static final int PREFIX = 0xEE;
    public static final int FAILED_QUERY_INVENTORY = PREFIX + 1;//
    public static final int FAILED_SETTING_UP_IAB = PREFIX + 2;//
    public static final int FAILED_PURCHASE = PREFIX + 3;//
    public static final int SUCC_PURCHASE = PREFIX + 4;//
    public static final int NOT_ALLOW_BUY = PREFIX + 5;//
    public static final int QUERY_INVENTORY_FINISH = PREFIX + 6;//
    public static final int ILLEGAL_SKU = PREFIX + 7;//
    public static final int ADD_ITEM = PREFIX + 8;//  加币过程

    //Google Play 后台 RSA key
    public static final String RSA_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgl4YIZL9iip7VLjO4zsXJjlveSEoOAzAleGgXlB0J8r88pOc8rCVAeD6Ig3bQpltHfHSU8QScGe6v93eRIoxuKQxgXwj8HVxZ7B7rLGOIRmrmAP6zeFpIMzQd0HnYLuH28QiicVoFAhsK/zo32lFdEu+M4SeE7XpbIXedcSgNdoswi2cmwwF4gEXN08b/ecJ0+DdZnkQcyk9KQw1l5M4vFEjII/RQYmBiBjV3gS/++BBOJo4I80J5Uk/JGCYZPsSRl++2m5dNhrK5IxfE4AK88o5DHoLVay9KC9dGFEPA4gvecjltFVevjJwv+XrRE1GObVcS9GKKkijd8k4dU9CBQIDAQAB";

    protected final String GOOGLE_PLAY_PACK_NAME = "com.android.vending";
    protected final String GOOGLE_PLAY_SERVICE_PACK_NAME = "com.google.android.gms";

    private volatile IabHelper mHelper;
    private boolean isInit = false;
    private Handler mHandler;
    private PayManagerCallback mPayManagerCallback;

    public PayManager(Handler handler, PayManagerCallback payManagerCallback) {
        this.mHandler = handler;
        this.mPayManagerCallback = payManagerCallback;
    }


    /**************************************************
     * 回调接口
     ************************************************/
    //如果查询成功,查询结果存储在一个库存对象传递到侦听器。下面的代码显示了如何检索项价格从结果集。
    private IabHelper.QueryInventoryFinishedListener gotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (mHelper == null) {
                return;
            }
            // Is it a failure?
            if (result.isFailure()) {
                mHandler.obtainMessage(FAILED_QUERY_INVENTORY).sendToTarget();
                return;
            }
            //消耗
            if (mPayManagerCallback != null) {
                List<Purchase> purchases = inventory.getAllPurchases();
                for (Purchase purchase : purchases) {
                    if (purchase != null && mPayManagerCallback.verifyDeveloperPayload(purchase)) {
                        mHelper.consumeAsync(purchase, consumeFinishedListener);
                    }
                }
            }
            //更新主ui
            mHandler.obtainMessage(QUERY_INVENTORY_FINISH).sendToTarget();
        }
    };

    // Callback for when a purchase is finished
    private IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (mHelper == null) {
                return;
            }
            if (result.isFailure()) {
                //购买失败
                int responseCode = result.getResponse();
                mHandler.obtainMessage(FAILED_PURCHASE, responseCode, 0).sendToTarget();
                return;
            }
            if (!mPayManagerCallback.verifyDeveloperPayload(purchase)) {
                mHandler.obtainMessage(ILLEGAL_SKU).sendToTarget();
                return;
            }

            //消耗
            if (mPayManagerCallback != null) {
                mHelper.consumeAsync(purchase, consumeFinishedListener);
            }
        }
    };

    //消耗完成调用 接口
    private IabHelper.OnConsumeFinishedListener consumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            DebugLogs.e("jjfly", "Consumption finished. Purchase: " + purchase + ", result: " + result);
            if (mHelper == null) {
                return;
            }
            if (result.isSuccess()) {
                //加币
                DebugLogs.e("jjfly 加币");
                mHandler.obtainMessage(ADD_ITEM, 0, 0, purchase).sendToTarget();
            } else {
                DebugLogs.e("jjfly Error while consuming: " + result);
            }
            //更新ui
        }
    };


    /*******************************************
     * 功能区域
     *********************************************/
    //支付
    public void pay(Activity context, String sku, int requestCode, String extraData) {
        // 限制购买
        if (!isInit) {
            mHandler.obtainMessage(NOT_ALLOW_BUY).sendToTarget();
            return;
        }
        if (mHelper != null) {
            mHelper.launchPurchaseFlow(context, sku, requestCode, purchaseFinishedListener, extraData);
        }
    }


    /**************************
     * activity 相关
     ****************************/
    public void onCreate(Activity context) {
        if (isHaveGooglePlay(context, GOOGLE_PLAY_PACK_NAME) && isHaveGooglePlay(context, GOOGLE_PLAY_SERVICE_PACK_NAME)) {
            mHelper = new IabHelper(context, RSA_KEY);
            mHelper.enableDebugLogging(true, "jjfly");

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        // handler 发送提示
                        mHandler.obtainMessage(FAILED_SETTING_UP_IAB).sendToTarget();
                        return;
                    }
                    isInit = true;
                    mHelper.queryInventoryAsync(gotInventoryListener);// 查询订单并补单
                }
            });
        } else {
            mHandler.obtainMessage(FAILED_SETTING_UP_IAB).sendToTarget();
        }
    }

    //Activity中进行onActivityResult调用
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null || !isInit) {
            return false;
        }
        // Pass on the activity result to the helper for handling
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    public void onDestroy() {
        if (mHelper != null) {
            mHelper.dispose();
        }
        mHelper = null;
    }


    /***********************************************************************
     * 辅助方法 判断是否安装有Google服务
     ***********************************************************************/
    //Check Google Play
    protected boolean isHaveGooglePlay(Context context, String packageName) {
        //Get PackageManager
        final PackageManager packageManager = context.getPackageManager();
        //Get The All Install App Package Name
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        //Create Name List
        List<String> pName = new ArrayList<String>();

        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                pName.add(pn);
            }
        }
        //Check
        return pName.contains(packageName);
    }


    public interface PayManagerCallback {
        String getPublicKey();

        boolean verifyDeveloperPayload(Purchase p);
    }


}
