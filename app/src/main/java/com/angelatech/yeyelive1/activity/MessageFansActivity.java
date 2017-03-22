package com.angelatech.yeyelive1.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.adapter.CommonAdapter;
import com.angelatech.yeyelive1.adapter.ViewHolder;
import com.angelatech.yeyelive1.db.BaseKey;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.db.model.SystemMessageDBModel;
import com.angelatech.yeyelive1.model.BasicUserInfoModel;
import com.angelatech.yeyelive1.model.SystemMessage;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.will.common.tool.time.DateFormat;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

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
 * com.angelatech.yeyelive1
 * 粉丝消息
 */

public class MessageFansActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
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
            systemMsg = SystemMessage.getInstance().load(MessageNotificationActivity.NOTICE_FANS_MSG, userInfo.userid, 0, 1000);
            if (systemMsg == null) {
                return;
            }
            SystemMessage.getInstance().updateIsread(BaseKey.NOTIFICATION_ISREAD, "1", userInfo.userid, MessageNotificationActivity.NOTICE_FANS_MSG);//修改所有未读改成已读
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getString(R.string.system_new_fans));
        ListView message_notice_list = (ListView) findViewById(R.id.message_notice_list);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        CommonAdapter<SystemMessageDBModel> adapter = new CommonAdapter<SystemMessageDBModel>(MessageFansActivity.this, systemMsg, R.layout.item_fans_msg) {
            @Override
            public void convert(ViewHolder helper, final SystemMessageDBModel item, final int position) {
                String result = DateFormat.formatData("MM/dd HH:mm", Long.valueOf(item.datetime));
                helper.setText(R.id.time, result);
                helper.setText(R.id.context, item.content);
                JSONObject msgJsonObj;
                final String msgStr, fromUserid, nickname;
                try {
                    msgJsonObj = new JSONObject(item.data);
                    msgStr = msgJsonObj.getString("headurl");
                    fromUserid = msgJsonObj.getString("fromuserid");
                    nickname = msgJsonObj.getString("nickname");
                    helper.setImageUrl(R.id.userPic, msgStr);
                    helper.setOnClick(R.id.userPic, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                            userInfoModel.Userid = fromUserid;
                            userInfoModel.nickname = nickname;
                            StartActivityHelper.jumpActivity(MessageFansActivity.this, FriendUserInfoActivity.class, userInfoModel);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        message_notice_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        message_notice_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject msgJsonObj;
                String fromUserid,nickName;
                try {
                    msgJsonObj = new JSONObject(systemMsg.get(position).data);
                    fromUserid = msgJsonObj.getString("fromuserid");
                    nickName = msgJsonObj.getString("nickname");
                    BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                    userInfoModel.Userid = fromUserid;
                    userInfoModel.nickname = nickName;
                    StartActivityHelper.jumpActivity(MessageFansActivity.this, FriendUserInfoActivity.class, userInfoModel);
                } catch (JSONException e) {
                    e.printStackTrace();
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