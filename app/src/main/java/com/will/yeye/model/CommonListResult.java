package com.will.yeye.model;

import java.util.List;

/**
 * Created by xujian on 15/9/8.
 */
public class CommonListResult<T> extends CommonModel {
    public List<T> data;
    public List<T> users;

    public boolean hasData() {
        return data != null && data.size() > 0;
    }
}
