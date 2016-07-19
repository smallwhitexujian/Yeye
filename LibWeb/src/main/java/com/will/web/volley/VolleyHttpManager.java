package com.will.web.volley;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.will.web.HttpManager;
import com.will.web.callback.HttpCallback;

import java.util.Map;


/**
 * Created by jjfly on 16-3-2.
 */
public class VolleyHttpManager extends HttpManager {
    private RequestQueue mRequestQueue;
    private Context mContext;
    private final String TAG = getClass().getSimpleName();
    private RequestFactory mRequestFactory;


    public VolleyHttpManager(Context context) {
        this.mContext = context;
        this.mRequestFactory = new RequestFactory();
        init();
    }


    @Override
    public String getRequest(String url, Map<String, String> params) {
        StringRequest stringRequest = mRequestFactory.createStringRequest(Request.Method.GET,url,new HttpCallback(){

            @Override
            public void onFailure(Map<String, ?> errorMap) {

            }

            @Override
            public void onSuccess(String response) {

            }
        });
        mRequestQueue.add(stringRequest);
        return null;
    }

    @Override
    public String postRequest(String url, Map<String, String> params) {
        return null;
    }

    @Override
    public void Request(int method, String url, Map<String, String> params, HttpCallback callback) {
        StringRequest stringRequest = mRequestFactory.createStringRequest(Request.Method.GET,url,callback);
        mRequestQueue.add(stringRequest);
    }


    public void init() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        mRequestQueue.add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        mRequestQueue.add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
