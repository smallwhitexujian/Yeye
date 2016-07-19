package com.will.yeye.activity.function;

import android.content.Context;

import com.will.yeye.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;

/**
 * User: cbl
 * Date: 2016/4/15
 * Time: 17:41
 */
public class Feedback extends HttpFunction {

    public Feedback(Context context) {
        super(context);
    }

    @Override
    public void httpGet(String url, Map<String, String> params, HttpBusinessCallback callback) {
        super.httpGet(url, params, callback);
    }

    @Override
    public void httpPost(String url, Map<String, String> params, HttpBusinessCallback callback) {
        super.httpPost(url, params, callback);
    }
}
