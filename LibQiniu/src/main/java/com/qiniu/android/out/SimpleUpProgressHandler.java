package com.qiniu.android.out;

import com.qiniu.android.storage.UpProgressHandler;

/**
 * Author: jjfly
 * Since: 2016年05月10日 15:16
 * Desc: 上传过程回调
 * FIXME:
 */
public class SimpleUpProgressHandler implements UpProgressHandler {

    /**
     * 进度
     *
     * @param key     上传文件的保存文件名
     * @param percent 上传进度，取值范围[0, 1.0]
     */
    @Override
    public void progress(String key, double percent) {

    }
}
