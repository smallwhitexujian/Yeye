package com.will.yeye.activity.function;

import android.app.Activity;
import android.content.Intent;

import com.will.common.tool.DeviceTool;
import com.will.yeye.TransactionValues;
import com.will.yeye.activity.LoginActivity;
import com.will.yeye.activity.MainActivity;
import com.will.yeye.activity.ProfileActivity;
import com.will.yeye.db.model.BasicUserInfoDBModel;
import com.will.yeye.handler.CommonHandler;
import com.will.yeye.model.AccountTModel;
import com.will.yeye.model.LoginServerModel;
import com.will.yeye.service.IServiceHelper;
import com.will.yeye.service.IServiceValues;
import com.will.yeye.util.BroadCastHelper;
import com.will.yeye.util.CacheDataManager;
import com.will.yeye.util.StartActivityHelper;


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
            StartActivityHelper.jumpActivityDefault(mContext, MainActivity.class);
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
