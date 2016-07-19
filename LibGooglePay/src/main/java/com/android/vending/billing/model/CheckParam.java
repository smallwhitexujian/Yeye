package com.android.vending.billing.model;




import android.os.Parcel;
import android.os.Parcelable;

import com.android.vending.billing.util.Purchase;

public class CheckParam implements Parcelable {

    private PurchaseParam purchaseParam;
    private Purchase purchase;


    public CheckParam(PurchaseParam purchaseParam,Purchase purchase){
        this.purchaseParam = purchaseParam;
        this.purchase = purchase;
    }

    protected CheckParam(Parcel in) {
        purchaseParam = in.readParcelable(PurchaseParam.class.getClassLoader());
        purchase = in.readParcelable(Purchase.class.getClassLoader());
    }

    public static final Creator<CheckParam> CREATOR = new Creator<CheckParam>() {
        @Override
        public CheckParam createFromParcel(Parcel in) {
            return new CheckParam(in);
        }

        @Override
        public CheckParam[] newArray(int size) {
            return new CheckParam[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(purchaseParam, flags);
        dest.writeParcelable(purchase, flags);
    }

    public PurchaseParam getPurchaseParam() {
        return purchaseParam;
    }

    public void setPurchaseParam(PurchaseParam purchaseParam) {
        this.purchaseParam = purchaseParam;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }
}
