package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.FocusModel;
import com.google.gson.reflect.TypeToken;
import com.will.common.string.json.JsonUtil;
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.WithBroadCastHeaderActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.activity.function.FocusFans;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.fragment.UserInfoDialogFragment;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.SearchItemModel;
import com.angelatech.yeyelive.model.UserInfoModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.ErrorHelper;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive .R;
import com.will.view.ToastUtils;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;
import com.angelatech.yeyelive.web.HttpFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/4/6
 * Time: 16:06
 * 我的关注
 */
public class FocusOnActivity extends WithBroadCastHeaderActivity implements SwipyRefreshLayout.OnRefreshListener {

    private ListView list_view_focus;
    private CommonAdapter<FocusModel> adapter;
    private BasicUserInfoDBModel model;
    private List<FocusModel> data = new ArrayList<>();
    private int pageIndex = 1;
    private int pageSize = 10;
    private int type = 1;
    private long dateSort = 0;
    private boolean IS_REFRESH = false;  //是否需要刷新
    private FocusFans focusFans;
    private ChatRoom chatRoom;
    private final int MSG_ADAPTER_NOTIFY = 1;
    private final int MSG_SET_FOLLOW = 2;
    private final int MSG_NO_DATA = 3;
    private final int MSG_ERROR = 4;

    private SwipyRefreshLayout swipyRefreshLayout;

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

        focusFans = new FocusFans(FocusOnActivity.this);
        chatRoom = new ChatRoom(FocusOnActivity.this);
        adapter = new CommonAdapter<FocusModel>(FocusOnActivity.this, data, R.layout.item_focus) {
            @Override
            public void convert(ViewHolder helper, final FocusModel item, final int position) {
                helper.setImageViewByImageLoader(R.id.user_head_photo, item.headurl);
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
                helper.setOnClick(R.id.iv_user_follow_state, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoadingDialog.showLoadingDialog(FocusOnActivity.this);
                        doFocus(data.get(position), position);
                    }
                });
            }

        };
    }

    private void setView() {
        list_view_focus.setAdapter(adapter);
        swipyRefreshLayout.setOnRefreshListener(this);
        headerLayout.showTitle(getString(R.string.user_focus));
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
                UserInfoModel userInfoModel = new UserInfoModel();
                userInfoModel.userid = focusModel.userid;
                userInfoModel.isfollow = focusModel.isfollow;
                userInfoModel.headurl = focusModel.headurl;
                userInfoModel.nickname = focusModel.nickname;
                UserInfoDialogFragment userInfoDialogFragment = new UserInfoDialogFragment();
                userInfoDialogFragment.setUserInfoModel(userInfoModel);
                userInfoDialogFragment.show(getSupportFragmentManager(), "");

            }
        });
        loadData();
        noDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    /**
     * 获取登录用户
     */
    private void getLoginUser() {
        if (model == null || model.userid == null || model.token == null) {
            CacheDataManager cacheDataManager = CacheDataManager.getInstance();
            model = cacheDataManager.loadUser();
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_ADAPTER_NOTIFY:
                noDataLayout.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                break;
            case MSG_SET_FOLLOW:
                LoadingDialog.cancelLoadingDialog();
                adapter.notifyDataSetChanged();
                ToastUtils.showToast(FocusOnActivity.this, getString(R.string.success));
                break;
            case MSG_NO_DATA:
                showNodataLayout();
                break;
            case MSG_ERROR:
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(FocusOnActivity.this, ErrorHelper.getErrorHint(FocusOnActivity.this, msg.obj.toString()));
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

    private void loadData() {
        getLoginUser();
        LoadingDialog.showLoadingDialog(FocusOnActivity.this);
        HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                //uiHandler.obtainMessage(MSG_ERROR).sendToTarget();
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
                            adapter.setData(data);
                            uiHandler.obtainMessage(MSG_ADAPTER_NOTIFY).sendToTarget();
                        }
                    }
                    else{
                        onBusinessFaild(result.code,response);
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

    private void doFocus(FocusModel userModel, final int position) {

        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                //uiHandler.obtainMessage(MSG_ERROR).sendToTarget();
            }

            @Override
            public void onSuccess(String response) {
                CommonModel results = JsonUtil.fromJson(response, CommonModel.class);

                if (results != null && HttpFunction.isSuc(results.code)) {

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
        };

        chatRoom.UserFollow(CommonUrlConfig.UserFollow, model.token, model.userid,
                userModel.userid, Integer.valueOf(userModel.isfollow), callback);
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
        loadData();
    }

    //刷新
    private void freshLoad() {
        IS_REFRESH = true;
        loadData();
    }

    private void showNodataLayout() {
        noDataLayout.setVisibility(View.VISIBLE);
        noDataLayout.findViewById(R.id.hint_textview1).setVisibility(View.VISIBLE);
        ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(getString(R.string.no_data_no_follow));
        noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);
    }
}
