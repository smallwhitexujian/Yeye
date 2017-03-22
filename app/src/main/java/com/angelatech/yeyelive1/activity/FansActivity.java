package com.angelatech.yeyelive1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.Constant;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.base.WithBroadCastHeaderActivity;
import com.angelatech.yeyelive1.activity.function.ChatRoom;
import com.angelatech.yeyelive1.activity.function.FocusFans;
import com.angelatech.yeyelive1.adapter.CommonAdapter;
import com.angelatech.yeyelive1.adapter.ViewHolder;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.BasicUserInfoModel;
import com.angelatech.yeyelive1.model.CommonModel;
import com.angelatech.yeyelive1.model.CommonParseListModel;
import com.angelatech.yeyelive1.model.FocusModel;
import com.angelatech.yeyelive1.model.SearchItemModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.ErrorHelper;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/4/6
 * Time: 18:26
 * 我的粉丝 界面
 */
public class FansActivity extends WithBroadCastHeaderActivity implements SwipyRefreshLayout.OnRefreshListener {
    private boolean IS_REFRESH = false;  //是否需要刷新
    private ListView list_view_focus;
    private CommonAdapter<FocusModel> adapter;
    private BasicUserInfoDBModel model;
    private List<FocusModel> data = new ArrayList<>();
    private int pageIndex = 1;
    private int pageSize = 20;
    private int type = 2;
    private long dateSort = 0;
    private final int MSG_ADAPTER_NOTIFY = 1;
    private final int MSG_SET_FOLLOW = 2;
    private final int MSG_NO_DATA = 3;
    private final int MSG_ERROR = 4;
    private final int MSG_NO_DATA_MORE = 5;
    private SwipyRefreshLayout swipyRefreshLayout;
    private FocusFans focusFans;
    private ChatRoom chatRoom;
    private RelativeLayout noDataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        initView();
        setView();
    }

    private void initView() {
        list_view_focus = (ListView) findViewById(R.id.list_view_focus);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        noDataLayout = (RelativeLayout) findViewById(R.id.no_data_layout);
        CacheDataManager cacheDataManager = CacheDataManager.getInstance();
        model = cacheDataManager.loadUser();
        chatRoom = new ChatRoom(FansActivity.this);
        adapter = new CommonAdapter<FocusModel>(FansActivity.this, data, R.layout.item_focus) {
            @Override
            public void convert(ViewHolder helper, final FocusModel item, final int position) {
                helper.setImageUrl(R.id.user_head_photo, item.headurl);
                helper.setText(R.id.tv_name, item.nickname);
                if (item.sex.equals(Constant.SEX_MALE)) {
                    helper.setImageResource(R.id.iv_user_sex, R.drawable.icon_information_boy);
                } else {
                    helper.setImageResource(R.id.iv_user_sex, R.drawable.icon_information_girl);
                }
                if (item.isfollow.equals("0")) {
                    helper.setImageResource(R.id.iv_user_follow_state, R.drawable.btn_focus);
                } else {
                    helper.setImageResource(R.id.iv_user_follow_state, R.drawable.btn_focus_cancel);
                }
                //0 无 1 v 2 金v 9官
                switch (item.isv){
                    case "1":
                        helper.setImageResource(R.id.iv_vip,R.drawable.icon_identity_vip_white);
                        helper.showView(R.id.iv_vip);
                        break;
                    case "2":
                        helper.setImageResource(R.id.iv_vip,R.drawable.icon_identity_vip_gold);
                        helper.showView(R.id.iv_vip);
                        break;
                    case "9":
                        helper.setImageResource(R.id.iv_vip,R.drawable.icon_identity_official);
                        helper.showView(R.id.iv_vip);
                        break;
                    default:
                        helper.hideView(R.id.iv_vip);
                        break;
                }

                helper.setOnClick(R.id.iv_user_follow_state, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doFocus(data.get(position), position);
                    }
                });
            }

        };
    }

    private void setView() {
        focusFans = new FocusFans(this);
        list_view_focus.setAdapter(adapter);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        headerLayout.showTitle(getString(R.string.user_fans));
        headerLayout.showLeftBackButton(R.id.backBtn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list_view_focus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FocusModel focusModel = data.get(position);
                BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                userInfoModel.Userid = focusModel.userid;
                userInfoModel.nickname = focusModel.nickname;
                StartActivityHelper.jumpActivity(FansActivity.this, FriendUserInfoActivity.class, userInfoModel);
            }
        });
        loadData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private void doFocus(FocusModel userModel, final int position) {

        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                DebugLogs.e("===============失败了");
            }

            @Override
            public void onSuccess(String response) {
                CommonModel results = JsonUtil.fromJson(response, CommonModel.class);
                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        if (data.get(position).isfollow.equals("1")) {
                            data.get(position).isfollow = "0";
                        } else {
                            data.get(position).isfollow = "1";
                        }
                        uiHandler.obtainMessage(MSG_SET_FOLLOW).sendToTarget();
                    } else {
                        uiHandler.obtainMessage(MSG_ERROR, results.code).sendToTarget();
                    }
                }
            }
        };

        chatRoom.UserFollow(CommonUrlConfig.UserFollow, model.token, model.userid,
                userModel.userid, Integer.valueOf(userModel.isfollow), callback);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_ADAPTER_NOTIFY:
                adapter.notifyDataSetChanged();
                break;
            case MSG_NO_DATA:
                showNodataLayout();
                break;
            case MSG_ERROR:
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(this, ErrorHelper.getErrorHint(this, msg.obj.toString()));
                break;
            case MSG_SET_FOLLOW:
                LoadingDialog.cancelLoadingDialog();
                adapter.notifyDataSetChanged();
                ToastUtils.showToast(this, getString(R.string.success));
            case MSG_NO_DATA_MORE:
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(this, getString(R.string.no_data_more));
                break;
        }
    }

    @Override
    protected void doReceive(String action, Intent intent) {
        SearchItemModel searchItemModel = (SearchItemModel) intent.getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        if (searchItemModel != null) {
            for (FocusModel d : data) {
                if (d.userid.equals(searchItemModel.userid)) {
                    d.isfollow = searchItemModel.isfollow;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    freshLoad();
                } else {
                    moreLoad();
                }
                swipyRefreshLayout.setRefreshing(false);
            }
        });
    }

    //加载更多
    private void moreLoad() {
        IS_REFRESH = false;
        pageIndex++;
        loadData();
    }

    //刷新
    private void freshLoad() {
        IS_REFRESH = true;
        pageIndex = 1;
        loadData();
    }

    private void loadData() {
        LoadingDialog.showLoadingDialog(this,null);
        HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                CommonParseListModel<FocusModel> result = JsonUtil.fromJson(response, new TypeToken<CommonParseListModel<FocusModel>>() {
                }.getType());
                if (result != null) {
                    if (HttpFunction.isSuc(result.code)) {
                        if (!result.data.isEmpty()) {
                            dateSort = result.time;
                            int index = result.index;
                            if (IS_REFRESH) {
                                data.clear();
                                index = 0;
                            }
                            pageIndex = index + 1;
                            data.addAll(result.data);
                            uiHandler.obtainMessage(MSG_ADAPTER_NOTIFY).sendToTarget();
                        } else {
                            if (!IS_REFRESH) {
                                uiHandler.sendEmptyMessage(MSG_NO_DATA_MORE);
                            }
                        }
                    } else {
                        onBusinessFaild(result.code);
                    }

                }
                if (data.isEmpty()) {
                    uiHandler.obtainMessage(MSG_NO_DATA).sendToTarget();
                }
                IS_REFRESH = false;
                LoadingDialog.cancelLoadingDialog();
            }
        };

        HashMap<String, String> map = new HashMap<>();
        map.put("token", model.token);
        map.put("userid", model.userid);
        map.put("type", String.valueOf(type));
        if (dateSort > 0) {
            map.put("datesort", String.valueOf(dateSort));
        }
        map.put("pageindex", String.valueOf(pageIndex));
        map.put("pagesize", String.valueOf(pageSize));
        focusFans.httpGet(CommonUrlConfig.FriendMyList, map, httpCallback);
    }


    private void showNodataLayout() {
        noDataLayout.setVisibility(View.VISIBLE);
        noDataLayout.findViewById(R.id.hint_textview1).setVisibility(View.VISIBLE);
        ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(getString(R.string.no_data_no_fans));
        noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);
    }
}
