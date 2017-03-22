package com.angelatech.yeyelive1.model;

/**
 * Created by jjfly on 15-10-21.
 */
public class CommonParseModel<T> extends CommonModel {
    public T data;

    @Override
    public String toString() {
        return "CommonParseModel{" +
                "data=" + data +
                '}';
    }
}
