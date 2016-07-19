package com.will.web.handle;

import java.util.List;

/**
 * Author: jjfly
 * Since: 2016年05月04日 16:11
 * Desc: web业务处理的接口
 * FIXME:
 */
public abstract class WebBusinessHandler {


    public  void handleClientNetError(){

    }

    public  void handleServerReturnError(String returnStr){

    }

    public void handleBusinessFaild(Object ...args){

    }

    public <T> void handleBusinessSuc(T data){

    }

    public <T> void handleBusinessSuc(List<T> datas){

    }

}
