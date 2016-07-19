package com.android.vending.billing.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jjfly on 15-11-7.
 */
public class PurchaseParam implements Parcelable{


    private String userId;//用户id
    private String token;//用户token
    private String sku;//支付sku
    private String orderNum;//自己服务器订单好
    private String extraStr;//加密参数
    
    
    public PurchaseParam(String userId,String token,String sku,String orderNum,String extraStr){
    	this.userId = userId;
    	this.token = token;
    	this.sku = sku;
    	this.orderNum = orderNum;
    	this.extraStr = extraStr;
    }
    
    protected PurchaseParam(Parcel in) {
        userId = in.readString();
        token = in.readString();
        sku = in.readString();
        orderNum = in.readString();
        extraStr = in.readString();
    }

    public static final Creator<PurchaseParam> CREATOR = new Creator<PurchaseParam>() {
        @Override
        public PurchaseParam createFromParcel(Parcel in) {
            return new PurchaseParam(in);
        }

        @Override
        public PurchaseParam[] newArray(int size) {
            return new PurchaseParam[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(token);
        dest.writeString(sku);
        dest.writeString(orderNum);
        dest.writeString(extraStr);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

	public String getExtraStr() {
		return extraStr;
	}

	public void setExtraStr(String extraStr) {
		this.extraStr = extraStr;
	}
    
}