package com.angelatech.yeyelive1.thirdShare;

/**
 * User: cbl
 * Date: 2016/5/18
 * Time: 18:06
 * 分享 监听 事件
 */
public interface ShareListener {
    void callBackSuccess(int shareType);

    void callbackError(int shareType);

    void callbackCancel(int shareType);
}
