package com.angelatech.yeyelive1.activity.function;

import android.content.Context;

import com.angelatech.yeyelive1.model.WxOrder;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.angelatech.yeyelive1.wxapi.WXInterface;
import com.angelatech.yeyelive1.CommonUrlConfig;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;


/**
 * User: cbl
 * Date: 2016/4/8
 * Time: 13:46
 */
public class WxPay extends HttpFunction {

    public WxPay(Context context) {
        super(context);
    }
    //public static int ORDER_SUCCESS = 1;
    //public static int ORDER_ERROR = 2;

    /**
     * 生成订单
     *
     * @param params   参数
     * @param callback httpCallback
     */
    public void createOrder(Map<String, String> params, HttpBusinessCallback callback) {
        httpGet(CommonUrlConfig.RechargeWeixinOrder, params, callback);
    }

    public void goWxClient(Context context, WxOrder WxOrder) {
        new WXInterface(context).payWxClient(WxOrder);
    }

    /***
     * 充值列表
     *
     * @param callback httpCallback
     */
    public void getConfigList(Map<String, String> map, HttpBusinessCallback callback) {

        httpGet(CommonUrlConfig.RechargeConfigList, map, callback);
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
}
