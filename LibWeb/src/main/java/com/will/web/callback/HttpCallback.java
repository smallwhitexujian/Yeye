package com.will.web.callback;


import java.util.Map;

/**
 * Created by jjfly on 16-3-2.
 */
public interface HttpCallback {

    /****失败处理***/
    void onFailure(Map<String, ?> errorMap);
    /***成功返回***/
    void onSuccess(String response);
    /**请求错误返回 **/
}
