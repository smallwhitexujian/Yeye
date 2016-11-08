package com.angelatech.yeyelive.activity.function;

import android.content.Context;

import com.android.vending.billing.security.SecurityablImpl;
import com.android.vending.billing.security.Securityable;
import com.android.vending.billing.util.Purchase;
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: jjfly
 * Since: 2016年05月11日 10:15
 * Desc: google 支付
 * FIXME:
 */
public class GooglePay extends HttpFunction{

    private Securityable securityManager = new SecurityablImpl();

    public GooglePay(Context context) {
        super(context);
    }


    public void loadMenu(int type,HttpBusinessCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put("sources", SOURCES_ANDROID+"");//android or ios
        params.put("type",type+"");
        httpGet(CommonUrlConfig.RechargeConfigList, params, callback);
    }


    public void addItem(String userId, String token, Purchase purchase,HttpBusinessCallback callback){
        Map<String, String> params = new HashMap<>();
        String orderId = purchase.getDeveloperPayload();
        String extraStr = "@CQ+?>K&*N~";
        String sign = getCheckSign(userId + orderId, extraStr, "");
        String productid = purchase.getSku();
        String purchaseToken = purchase.getToken();

        params.put("userid", userId);
        params.put("orderid", orderId);
        params.put("token", token);
        params.put("productid",productid);
        params.put("purchasetoken",purchaseToken);
        params.put("sign", sign);
        httpGet(CommonUrlConfig.RechargeDiamondAdd, params, callback);
    }
    //订单生成
    public void order(String userid, String token,String key,String sku, HttpBusinessCallback callback){
        Map<String, String> params = new HashMap<>();
        String amount = "1";
        String srcStr = userid + amount;
        String sign = getOrderSign(srcStr, key);

        params.put("userid", userid);
        params.put("amount", amount);//金币
        params.put("token", token);
        params.put("iden", key);//订单
        params.put("sku", sku); //商品
        params.put("sign", sign);//前面
        httpGet(CommonUrlConfig.RechargeOrder, params, callback);
    }


    private String getOrderSign(String srcStr, String key) {
        return securityManager.getOrderSign(srcStr, key);
    }

    private String getCheckSign(String srcStr, String extraStr, String key) {
        return securityManager.getCheckSign(srcStr, extraStr, key);
    }

    /**
     * 查询用户 钻石 余额
     *
     * @param params   接口参数
     * @param callback 回调
     */
    public void getUserDiamond(Map<String, String> params, HttpBusinessCallback callback) {
        httpGet(CommonUrlConfig.GetUserDiamond, params, callback);
    }


    public void RechargeDisplay(Map<String, String> params, HttpBusinessCallback callback){
        httpGet(CommonUrlConfig.RechargeDisplay, params, callback);
    }


}
