package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.db.model.SystemMessageDBModel;
import com.angelatech.yeyelive.model.SystemMessage;
import com.angelatech.yeyelive.model.WebTransportModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.will.common.tool.time.DateFormat;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;

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
 * 作者: Created by: xujian on Date: 2016/10/8.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 * 官方消息详情
 */

public class MessageOfficialActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
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
            systemMsg = SystemMessage.getInstance().load(MessageNotificationActivity.NOTICE_TO_ALL, 0, 1000);
            SystemMessage.getInstance().updateIsread(BaseKey.NOTIFICATION_ISREAD, "1", userInfo.userid, MessageNotificationActivity.NOTICE_TO_ALL);//修改所有未读改成已读
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(R.string.system_gf);
        ListView message_notice_list = (ListView) findViewById(R.id.message_notice_list);
        message_notice_list.setDividerHeight(0);
        message_notice_list.setDivider(null);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        CommonAdapter<SystemMessageDBModel> adapter = new CommonAdapter<SystemMessageDBModel>(MessageOfficialActivity.this, systemMsg, R.layout.item_official) {
            @Override
            public void convert(ViewHolder helper, final SystemMessageDBModel item, final int position) {
                if (item._data != null) {
                    helper.showView(R.id.line1);
                    helper.showView(R.id.str);
                } else {
                    helper.hideView(R.id.line1);
                    helper.hideView(R.id.str);
                }
                String result = DateFormat.formatData("yyyy-MM-dd HH:mm", Long.valueOf(item.datetime));
                helper.setText(R.id.time, result);
                helper.setText(R.id.context, item.content);
            }
        };
        message_notice_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        message_notice_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (systemMsg.get(position)._data!=null){
                    WebTransportModel webTransportModel = new WebTransportModel();
                    webTransportModel.url = systemMsg.get(position)._data;
                    webTransportModel.title = getString(R.string.system_gf);
                    if (!webTransportModel.url.isEmpty()) {
                        StartActivityHelper.jumpActivity(MessageOfficialActivity.this, WebActivity.class, webTransportModel);
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            initData();
        }
        swipyRefreshLayout.setRefreshing(false);
    }
}
