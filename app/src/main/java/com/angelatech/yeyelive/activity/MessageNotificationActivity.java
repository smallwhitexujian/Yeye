package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.db.model.SystemMessageDBModel;
import com.angelatech.yeyelive.model.SystemMessage;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.will.common.log.DebugLogs;
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
 * 作者: Created by: xujian on Date: 2016/9/30.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */

public class MessageNotificationActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout swipyRefreshLayout;
    private ListView message_notice_list;
    private CommonAdapter<SystemMessageDBModel> adapter;
    private List<SystemMessageDBModel> models = new ArrayList<>();
    private BasicUserInfoDBModel userInfo;
    private static String NOTICE_TO_ALL = "52";
    private static String NOTICE_SHOW_PERSON_MSG = "53";
    private long startRow = 0;
    private long maxRows = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_notification);
        initView();
        initData();
    }

    private void initData() {
        try {
            userInfo = CacheDataManager.getInstance().loadUser();
            if (userInfo ==null){
                return;
            }
            List<SystemMessageDBModel> systemMsg = SystemMessage.getInstance().load(NOTICE_TO_ALL, 0, 1);
            List<SystemMessageDBModel> fensMsg = SystemMessage.getInstance().load(NOTICE_SHOW_PERSON_MSG, 0, 1);
            if (systemMsg != null) {
                models.addAll(systemMsg);//系统消息数据
            }
            if (fensMsg != null) {
                models.addAll(fensMsg);//新增粉丝数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("消息通知");
        headerLayout.showRightTextButton(R.color.color_999999, "忽略未读", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemMessage.getInstance().update(BaseKey.NOTIFICATION_ISREAD, "1", userInfo.userid);//修改所有未读改成已读
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
        message_notice_list = (ListView) findViewById(R.id.message_notice_list);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        adapter = new CommonAdapter<SystemMessageDBModel>(MessageNotificationActivity.this, models, R.layout.item_msg_notification) {
            @Override
            public void convert(ViewHolder helper, final SystemMessageDBModel item, final int position) {
                if (item.type_code == 52) {
                    List<SystemMessageDBModel> dbModels = SystemMessage.getInstance().getQuerypot(BaseKey.NOTIFICATION_ISREAD, userInfo.userid, NOTICE_TO_ALL);
                    if (dbModels.size() > 0) {
                        helper.setText(R.id.pot, String.valueOf(dbModels.size()));
                    } else if (dbModels.size() > 99) {
                        helper.setText(R.id.pot, "…");
                    } else {
                        helper.hideView(R.id.pot);
                    }
                    helper.setText(R.id.title, "官方消息");
                    helper.setImageResource(R.id.pic, R.drawable.icon_notice_official);
                } else if (item.type_code == 53) {
                    List<SystemMessageDBModel> dbModels = SystemMessage.getInstance().getQuerypot(BaseKey.NOTIFICATION_ISREAD, userInfo.userid, NOTICE_SHOW_PERSON_MSG);
                    if (dbModels.size() > 0) {
                        helper.setText(R.id.pot, String.valueOf(dbModels.size()));
                    } else if (dbModels.size() > 99) {
                        helper.setText(R.id.pot, "…");
                    } else {
                        helper.hideView(R.id.pot);
                    }
                    helper.setText(R.id.title, "个人消息");
                    helper.setImageResource(R.id.pic, R.drawable.icon_notice_newfans);
                }
                String result = DateFormat.formatData("MM:dd", Long.valueOf(item.datetime));
                helper.setText(R.id.time, result);
                helper.setText(R.id.context, item.content);
                DebugLogs.d("===>" + result);
            }
        };
        message_notice_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }
}
