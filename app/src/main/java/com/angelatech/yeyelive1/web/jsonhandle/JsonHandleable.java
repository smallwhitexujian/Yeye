package com.angelatech.yeyelive1.web.jsonhandle;

/**
 * Author: jjfly
 * Since: 2016年05月04日 17:32
 * Desc:
 * FIXME:
 */
public interface JsonHandleable<T> {
    void handle(T... args);
}
