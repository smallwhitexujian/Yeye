package com.angelatech.yeyelive1.activity.function;

import android.app.Activity;
import android.content.Intent;

import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.LoginActivity;
import com.angelatech.yeyelive1.activity.ProfileActivity;
import com.angelatech.yeyelive1.activity.TabMenuActivity;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.handler.CommonHandler;
import com.angelatech.yeyelive1.model.AccountTModel;
import com.angelatech.yeyelive1.model.LoginServerModel;
import com.angelatech.yeyelive1.service.IServiceHelper;
import com.angelatech.yeyelive1.service.IServiceValues;
import com.angelatech.yeyelive1.util.BroadCastHelper;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.will.common.tool.DeviceTool;


/**
 *  start业务逻辑
 *  见xmind 设计
 *
 *
 */
public class Start implements Runnable{
    private CommonHandler mBackgroundHandler;
    private Activity mContext;

    public Start(Activity context, CommonHandler backgroundHandler){
        this.mBackgroundHandler = backgroundHandler;
        this.mContext = context;
    }


    //1、获取用户信息
    private BasicUserInfoDBModel loadUser(){
        BasicUserInfoDBModel user = CacheDataManager.getInstance().loadUser();
        if (user == null || user.userid == null || user.token == null) {
            return null;
        }
        return user;
    }

    //2、统计
    private void acount(AccountTModel accountTModel){
        Intent intent = IServiceHelper.getSerializableIntent(
                IServiceValues.ACTION_CMD_WAY,
                IServiceValues.CMD_ACCOUNT,
                TransactionValues.UI_2_SERVICE_KEY1,accountTModel);
        BroadCastHelper.sendBroadcast(mContext,intent);
    }

    //3、更新
    private void update(){


    }

    private void afterLogin(BasicUserInfoDBModel user){
        if(Login.checkUserInfo(user.userid)){
            StartActivityHelper.jumpActivityDefault(mContext, TabMenuActivity.class);
        }
        else{
            StartActivityHelper.jumpActivityDefault(mContext, ProfileActivity.class);
        }
        mContext.finish();
    }

    @Override
    public void run() {
        BasicUserInfoDBModel user = loadUser();
        if (user == null) {
            //跳转到登录界面
            mBackgroundHandler.obtainMessage(LoginActivity.MSG_GOTO_LOGIN).sendToTarget();
        }
        else {
            String userId = user.userid;
            String token = user.token;
//            发送登录广播
            LoginServerModel param = new LoginServerModel(Long.valueOf(userId),token);
            Intent i = IServiceHelper.getParcelableIntent(
                    IServiceValues.ACTION_CMD_WAY,
                    IServiceValues.CMD_LOGIN,
                    TransactionValues.UI_2_SERVICE_KEY1,param);
            BroadCastHelper.sendBroadcast(mContext,i);
            //统计
            AccountTModel accountTModel = new AccountTModel();
            accountTModel.device = DeviceTool.getUniqueID(mContext);
            accountTModel.userid = userId;
            acount(accountTModel);
            afterLogin(user);
        }
        //发送更新广播
        update();
    }


}
