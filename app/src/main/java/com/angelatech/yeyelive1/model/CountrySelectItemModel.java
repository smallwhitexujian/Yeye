package com.angelatech.yeyelive1.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 *
 */
public class CountrySelectItemModel implements Parcelable {

    public String country;
    public String letter;
    public String num;
    public int icon;

    public CountrySelectItemModel() {

    }

    protected CountrySelectItemModel(Parcel in) {
        country = in.readString();
        letter = in.readString();
        num = in.readString();
        icon = in.readInt();
    }

    public static final Creator<CountrySelectItemModel> CREATOR = new Creator<CountrySelectItemModel>() {
        @Override
        public CountrySelectItemModel createFromParcel(Parcel in) {
            return new CountrySelectItemModel(in);
        }

        @Override
        public CountrySelectItemModel[] newArray(int size) {
            return new CountrySelectItemModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(letter);
        dest.writeString(num);
        dest.writeInt(icon);
    }
}
