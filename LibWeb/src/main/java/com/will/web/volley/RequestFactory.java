package com.will.web.volley;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.will.web.ErrorMapHelper;
import com.will.web.callback.HttpCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjfly on 16-3-2.
 */
public class RequestFactory {

    public StringRequest createStringRequest(int method, String url, final HttpCallback callback){
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(callback != null){
                    callback.onSuccess(response);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(callback != null){
                    Map errorMap = new HashMap();
                    errorMap.put(ErrorMapHelper.KEY_VOLLEYERROR,error);
                    callback.onFailure(errorMap);
                }
            }
        };
        return new StringRequest(method,url,listener,errorListener);
    }



}
