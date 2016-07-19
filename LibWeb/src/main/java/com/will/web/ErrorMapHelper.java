package com.will.web;

import com.android.volley.VolleyError;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by jjfly on 16-3-2.
 */
public class ErrorMapHelper {

    public static final String KEY_CALL = "key_call";
    public static final String KEY_EXCEPTION= "key_exception";
    public static final String KEY_VOLLEYERROR = "key_volleyerror";

    public static Call getCallFromErrorMap(Map<String,?> errorMap){
        if(errorMap == null){
            return null;
        }
        return (Call) errorMap.get(KEY_CALL);
    }

    public static Exception getExceptionFromErrorMap(Map<String,?> errorMap){
        if(errorMap == null){
            return null;
        }
        return (Exception) errorMap.get(KEY_EXCEPTION);
    }

    public static VolleyError getVolleyErrorFromErrorMap(Map<String,?> errorMap){
        if(errorMap == null){
            return  null;
        }
        return (VolleyError) errorMap.get(KEY_VOLLEYERROR);
    }









}
