package com.angelatech.yeyelive.model;

/**
 * Created by jjfly on 15-11-2.
 */
public class RechargeModel {

    public int iconRid;     //图片id
    public long totalValue; //总价值
    public String diamonds; //金币
    public String amount;   //商品价格
    public String sku;      //商品标识
    public String unit;     //货币单位
    public int isCheck = 0;

    @Override
    public String toString() {
        return "RechargeModel{" +
                "iconRid=" + iconRid +
                ", totalValue=" + totalValue +
                ", diamonds=" + diamonds +
                ", amount=" + amount +
                ", sku='" + sku + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
