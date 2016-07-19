package com.angelatech.yeyelive.model;

/**
 * Created by jjfly on 15-11-2.
 */
public class RechargeModel {

    public int iconRid;//图片id
    public long totalValue;//总价值
    public String diamonds;
    public String amount;
    public String sku;
    public String unit;//货币单位
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
