package com.angelatech.yeyelive.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * IM登录登录服务器模型
 */
public class LoginServerModel implements Parcelable{

    private long id;
    private String sign;

    public LoginServerModel(long id,String sign){
        this.id = id;
        this.sign = sign;
    }

    protected LoginServerModel(Parcel in) {
        id = in.readLong();
        sign = in.readString();
    }

    public static final Creator<LoginServerModel> CREATOR = new Creator<LoginServerModel>() {
        @Override
        public LoginServerModel createFromParcel(Parcel in) {
            return new LoginServerModel(in);
        }

        @Override
        public LoginServerModel[] newArray(int size) {
            return new LoginServerModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(sign);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


    @Override
    public String toString() {
        return "LoginServerModel{" +
                "id=" + id +
                ", sign='" + sign + '\'' +
                '}';
    }
}
