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
    public static String NOTICE_TO_ALL = "52";//系统通知
    public static String NOTICE_SHOW_PERSON_MSG = "53";//个人通知
    public static String NOTICE_LIVE_FEEDBACK = "51";//意见返回
    public static String NOTICE_FANS_MSG = "54";//个人通知
    public static String NOTICE_RED_MSG = "55";//个人通知
    private SystemMessage systemMessage = null;
    private List<SystemMessageDBModel> systemMsg = null;

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
            systemMessage = SystemMessage.getInstance();
            systemMsg = new ArrayList<>();
            if (userInfo == null) {
                return;
            }
            systemMsg = systemMessage.load(NOTICE_TO_ALL, userInfo.userid, 0, 1);
            if (systemMsg == null) {
                systemMsg = systemMessage.load(NOTICE_SHOW_PERSON_MSG, userInfo.userid, 0, 1);
            }
            if (systemMsg == null){
                systemMsg = systemMessage.load(NOTICE_LIVE_FEEDBACK, userInfo.userid, 0, 1);
            }
            List<SystemMessageDBModel> fensMsg = systemMessage.load(NOTICE_FANS_MSG, userInfo.userid, 0, 1);
            List<SystemMessageDBModel> redMsg = systemMessage.load(NOTICE_RED_MSG, userInfo.userid, 0, 1);
            if (systemMsg != null) {
                models.addAll(systemMsg);//系统消息数据
            }
            if (fensMsg != null) {
                models.addAll(fensMsg);//新增粉丝数据
            }
            if (redMsg != null) {
                models.addAll(redMsg);//新增粉丝数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(R.string.system_msg);
        headerLayout.showRightTextButton(R.color.color_999999, getString(R.string.system_clear), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                systemMessage.update(BaseKey.NOTIFICATION_ISREAD, "1", userInfo.userid);//修改所有未读改成已读
                systemMessage.clearUnReadTag(MessageNotificationActivity.this);
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
                if (item.type_code == 52 || item.type_code == 51 || item.type_code == 53) {
                    List<SystemMessageDBModel> dbModels = systemMessage.getQuerypot(BaseKey.NOTIFICATION_ISREAD, userInfo.userid, NOTICE_TO_ALL);
                    if (dbModels.size() > 0) {
                        helper.setText(R.id.pot, String.valueOf(dbModels.size()));
                    } else if (dbModels.size() > 99) {
                        helper.setText(R.id.pot, "…");
                    } else {
                        helper.hideView(R.id.pot);
                    }
                    helper.setText(R.id.title, getString(R.string.system_gf));
                    helper.setImageResource(R.id.pic, R.drawable.icon_notice_official);
                } else if (item.type_code == 54) {
                    List<SystemMessageDBModel> dbModels = systemMessage.getQuerypot(BaseKey.NOTIFICATION_ISREAD, userInfo.userid, NOTICE_FANS_MSG);
                    if (dbModels.size() > 0) {
                        helper.setText(R.id.pot, String.valueOf(dbModels.size()));
                    } else if (dbModels.size() > 99) {
                        helper.setText(R.id.pot, "…");
                    } else {
                        helper.hideView(R.id.pot);
                    }
                    helper.setText(R.id.title, getString(R.string.system_fans_msg));
                    helper.setImageResource(R.id.pic, R.drawable.icon_notice_newfans);
                } else if (item.type_code == 55) {//红包消息
                    List<SystemMessageDBModel> dbModels = systemMessage.getQuerypot(BaseKey.NOTIFICATION_ISREAD, userInfo.userid, NOTICE_RED_MSG);
                    if (dbModels.size() > 0) {
                        helper.setText(R.id.pot, String.valueOf(dbModels.size()));
                    } else if (dbModels.size() > 99) {
                        helper.setText(R.id.pot, "…");
                    } else {
                        helper.hideView(R.id.pot);
                    }
                    helper.setText(R.id.title, getString(R.string.system_red_msg));
                    helper.setImageResource(R.id.pic, R.drawable.icon_notice_receivemoney);
                }
                String result = DateFormat.formatData("MM/dd HH:mm", Long.valueOf(item.datetime));
                helper.setText(R.id.time, result);
                helper.setText(R.id.context, item.content);
            }
        };
        message_notice_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        message_notice_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (models.get(position).type_code == 52 || models.get(position).type_code == 51 || models.get(position).type_code == 53) {//官方通知
                    StartActivityHelper.jumpActivityDefault(MessageNotificationActivity.this, MessageOfficialActivity.class);
                } else if (models.get(position).type_code == 54) {//粉丝消息活动
                    StartActivityHelper.jumpActivityDefault(MessageNotificationActivity.this, MessageFansActivity.class);
                } else if (models.get(position).type_code == 55) {//红包消息活动
                    StartActivityHelper.jumpActivityDefault(MessageNotificationActivity.this, MessageRedActivity.class);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (systemMessage.getQueryAllpot(BaseKey.NOTIFICATION_ISREAD, userInfo.userid).size() == 0) {
            systemMessage.clearUnReadTag(MessageNotificationActivity.this);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            initData();
        }
        swipyRefreshLayout.setRefreshing(false);
    }
}
