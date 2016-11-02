package com.angelatech.yeyelive.pay.MimoPay;

/**
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * <p>
 * <p>
 * 作者: Created by: xujian on Date: 2016/11/1.
 * 邮箱: xj626361950@163.com
 * com.org.mimopaydemo 数据模型
 */

public class MimopayModel {
    public String UserId;           //用户ID
    public int paymentid;           //支付类型
    public String productName;      //商品ID
    public String transactionId;    //订单
    public String currency = "IDR"; //币种
    public String coins = "0";      //单价

    @Override
    public String toString() {
        return "MimopayModel{" +
                "UserId='" + UserId + '\'' +
                ", paymentid=" + paymentid +
                ", productName='" + productName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", currency='" + currency + '\'' +
                ", coins='" + coins + '\'' +
                '}';
    }
}
