package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.util.SPreferencesTool;

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
 * 作者: Created by: xujian on Date: 2016/10/9.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive
 * 消息通知设置
 */

public class SettingNoticeActivity extends HeaderBaseActivity implements View.OnClickListener {
    private ImageView live_notify_turn, official_notify_turn, red_notify_turn, fans_notify_turn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notice);
        initView();
    }

    private void initView() {
        live_notify_turn = (ImageView) findViewById(R.id.live_notify_turn);
        official_notify_turn = (ImageView) findViewById(R.id.official_notify_turn);
        red_notify_turn = (ImageView) findViewById(R.id.red_notify_turn);
        fans_notify_turn = (ImageView) findViewById(R.id.fans_notify_turn);
        if (!App.isLiveNotify) {
            live_notify_turn.setImageResource(R.drawable.btn_me_switch_s);
        } else {
            live_notify_turn.setImageResource(R.drawable.btn_me_switch_n);
        }
        if (!App.isofficialNotify) {
            official_notify_turn.setImageResource(R.drawable.btn_me_switch_s);
        } else {
            official_notify_turn.setImageResource(R.drawable.btn_me_switch_n);
        }
        if (!App.isredNotify) {
            red_notify_turn.setImageResource(R.drawable.btn_me_switch_s);
        } else {
            red_notify_turn.setImageResource(R.drawable.btn_me_switch_n);
        }
        if (!App.isfansNotify) {
            fans_notify_turn.setImageResource(R.drawable.btn_me_switch_s);
        } else {
            fans_notify_turn.setImageResource(R.drawable.btn_me_switch_n);
        }
        live_notify_turn.setOnClickListener(this);
        official_notify_turn.setOnClickListener(this);
        red_notify_turn.setOnClickListener(this);
        fans_notify_turn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.live_notify_turn:
                setLiveNotify(App.isLiveNotify, live_notify_turn);
                break;
            case R.id.official_notify_turn:
                setLiveNotify(App.isofficialNotify, official_notify_turn);
                break;
            case R.id.red_notify_turn:
                setLiveNotify(App.isredNotify, red_notify_turn);
                break;
            case R.id.fans_notify_turn:
                setLiveNotify(App.isfansNotify, fans_notify_turn);
                break;
        }
    }

    private void setLiveNotify(boolean isNotify, ImageView view) {
        if (isNotify) {
            isNotify = false;
            view.setImageResource(R.drawable.btn_me_switch_s);
        } else {
            isNotify = true;
            view.setImageResource(R.drawable.btn_me_switch_n);
        }
        if (view.getId() == R.id.live_notify_turn) {
            App.isLiveNotify = isNotify;
            SPreferencesTool.getInstance().putValue(this, SPreferencesTool.LIVENOTIFY, isNotify);
        } else if (view.getId() == R.id.official_notify_turn) {
            App.isofficialNotify = isNotify;
            SPreferencesTool.getInstance().putValue(this, SPreferencesTool.OFFICIALNOTIFY, isNotify);
        } else if (view.getId() == R.id.red_notify_turn) {
            App.isredNotify = isNotify;
            SPreferencesTool.getInstance().putValue(this, SPreferencesTool.COINSNOTIFY, isNotify);
        } else if (view.getId() == R.id.fans_notify_turn) {
            App.isfansNotify = isNotify;
            SPreferencesTool.getInstance().putValue(this, SPreferencesTool.FANSNOTIFY, isNotify);
        }
    }
}
