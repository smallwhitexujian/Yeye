package com.will.yeye.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xujian on 15/9/10.
 * 房间登陆信息
 */
public class RoomInfo implements Parcelable{
    public int barid = 0;
    public long userid = 0;
    public String token = "";


    public RoomInfo(){

    }

    protected RoomInfo(Parcel in) {
        barid = in.readInt();
        userid = in.readLong();
        token = in.readString();
    }

    public static final Creator<RoomInfo> CREATOR = new Creator<RoomInfo>() {
        @Override
        public RoomInfo createFromParcel(Parcel in) {
            return new RoomInfo(in);
        }

        @Override
        public RoomInfo[] newArray(int size) {
            return new RoomInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(barid);
        dest.writeLong(userid);
        dest.writeString(token);
    }
}
