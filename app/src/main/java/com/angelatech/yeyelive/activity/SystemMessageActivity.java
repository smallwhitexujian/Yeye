package com.angelatech.yeyelive.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.util.BroadCastHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * <p>
 * <p>
 * 作者: Created by: xujian on Date: 16/9/21.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */
public class SystemMessageActivity extends HeaderBaseActivity {
    private BroadcastReceiver receiver;

    private void setBroadcast() {
        List<String> actions = new ArrayList<>();
        actions.add(Constant.REFRESH_SYSTEM_MESSAGE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //TODO 处理系统消息
            }
        };
        BroadCastHelper.registerBroadCast(SystemMessageActivity.this, actions, receiver);
    }
}
