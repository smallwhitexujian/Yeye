package com.qiniu.android.out;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONObject;

/**
 * Author: jjfly
 * Since: 2016年05月10日 15:14
 * Desc: 上传成功回调
 * FIXME:
 */
public class SimpleUpCompletionHandler implements UpCompletionHandler {


    /**
     * 上传 结束回调
     *
     * @param key      文件上传保存名称
     * @param info     上传完成返回日志信息
     * @param response 上传完成的回复内容
     */
    @Override
    public void complete(String key, ResponseInfo info, JSONObject response) {

    }
}
