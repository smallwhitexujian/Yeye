package com.angelatech.yeyelive1.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.adapter.CommonAdapter;
import com.angelatech.yeyelive1.adapter.ViewHolder;
import com.angelatech.yeyelive1.db.BaseKey;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.db.model.SystemMessageDBModel;
import com.angelatech.yeyelive1.model.SystemMessage;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.will.common.tool.time.DateFormat;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
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
 *
 *
 * 作者: Created by: xujian on Date: 2016/10/9.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive1.activity
 * 红包通知展示界面
 */

public class MessageRedActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout swipyRefreshLayout;
    private List<SystemMessageDBModel> systemMsg = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_notification);
        initData();
        initView();
    }

    private void initData() {
        try {
            BasicUserInfoDBModel userInfo = CacheDataManager.getInstance().loadUser();
            if (userInfo == null) {
                return;
            }
            systemMsg = SystemMessage.getInstance().load(MessageNotificationActivity.NOTICE_RED_MSG,userInfo.userid, 0, 1000);
            SystemMessage.getInstance().updateIsread(BaseKey.NOTIFICATION_ISREAD, "1", userInfo.userid, MessageNotificationActivity.NOTICE_RED_MSG);//修改所有未读改成已读
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getString(R.string.system_red_title));
        ListView message_notice_list = (ListView) findViewById(R.id.message_notice_list);
        message_notice_list.setDividerHeight(0);
        message_notice_list.setDivider(null);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        CommonAdapter<SystemMessageDBModel> adapter = new CommonAdapter<SystemMessageDBModel>(MessageRedActivity.this, systemMsg, R.layout.item_red) {
            @Override
            public void convert(ViewHolder helper, final SystemMessageDBModel item, final int position) {
                String result = DateFormat.formatData("yyyy-MM-dd HH:mm", Long.valueOf(item.datetime));
                helper.setText(R.id.time, result);
                helper.setText(R.id.str_context, item.content);
                JSONObject msgJsonObj;
                String nickname,amount;
                try {
                    msgJsonObj = new JSONObject(item.data);
                    nickname = msgJsonObj.getString("nickname");
                    amount = msgJsonObj.getString("amount");
                    helper.setText(R.id.tv_nickName,nickname);
                    helper.setText(R.id.coins_str,amount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        message_notice_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            initData();
        }
        swipyRefreshLayout.setRefreshing(false);
    }
}
