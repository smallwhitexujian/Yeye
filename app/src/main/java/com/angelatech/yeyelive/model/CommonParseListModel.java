package com.angelatech.yeyelive.model;

import java.util.List;

/**
 * Created by jjfly on 15-10-21.
 */
public class CommonParseListModel<T> extends CommonModel {

    public List<T> data;

    @Override
    public String toString() {
        return "CommonParseListModel{" +
                "data=" + data +
                '}';
    }
}
