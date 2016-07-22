package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.view.CommDialog;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.string.json.JsonUtil;
import com.angelatech.yeyelive.activity.function.UserControl;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive .R;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;
import com.angelatech.yeyelive.web.HttpFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 黑名单
 */
public class BlacklistActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {

    private final int MSG_ADAPTER_NOTIFY = 1;
    private final int MSG_NO_DATA = 2;
    private final int MSG_DELETE_BLACKLIST = 3;

    private boolean IS_REFRESH = false;  //是否需要刷新
    private ListView list_blacklist;
    private CommonAdapter<BasicUserInfoDBModel> adapter;
    private BasicUserInfoDBModel model;
    private List<BasicUserInfoDBModel> data = new ArrayList<>();
    private int pageIndex = 1;
    private int pageSize = 10;
    private long dateSort = 0;

    private SwipyRefreshLayout swipyRefreshLayout;

    private RelativeLayout noDataLayout;
    private UserControl mUserControl = new UserControl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        initView();
        setView();
    }

    private void initView() {
        list_blacklist = (ListView) findViewById(R.id.list_blacklist);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        noDataLayout = (RelativeLayout) findViewById(R.id.no_data_layout);
        CacheDataManager cacheDataManager = CacheDataManager.getInstance();
        model = cacheDataManager.loadUser();
        adapter = new CommonAdapter<BasicUserInfoDBModel>(BlacklistActivity.this, data, R.layout.item_blacklist) {
            @Override
            public void convert(ViewHolder helper, final BasicUserInfoDBModel item, final int position) {
                helper.setImageViewByImageLoader(R.id.user_head_photo, item.headurl);
                helper.setText(R.id.tv_name, item.nickname);
                if (item.sex.equals(Constant.SEX_MALE)) {
                    helper.setImageResource(R.id.iv_user_sex, R.drawable.icon_information_boy);
                } else {
                    helper.setImageResource(R.id.iv_user_sex, R.drawable.icon_information_girl);
                }
            }

        };
    }

    private void setView() {
        list_blacklist.setAdapter(adapter);
        swipyRefreshLayout.setOnRefreshListener(this);
        headerLayout.showTitle(getString(R.string.blacklist_title));
        headerLayout.showLeftBackButton();
        list_blacklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BasicUserInfoDBModel item = data.get(position);
                CommDialog commDialog = new CommDialog();
                CommDialog.Callback callback = new CommDialog.Callback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOK() {
                        LoadingDialog.showLoadingDialog(BlacklistActivity.this);
                        HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
                            @Override
                            public void onFailure(Map<String, ?> errorMap) {
                            }

                            @Override
                            public void onSuccess(String response) {
                                DebugLogs.e("=====" + response);
                                Map map = JsonUtil.fromJson(response, Map.class);
                                if (HttpFunction.isSuc((String) map.get("code"))) {
                                    uiHandler.obtainMessage(MSG_DELETE_BLACKLIST, item.userid).sendToTarget();
                                } else {
                                    onBusinessFaild((String) map.get("code"));
                                }
                                LoadingDialog.cancelLoadingDialog();
                            }
                        };
                        BasicUserInfoDBModel basicUserInfoDBModel = CacheDataManager.getInstance().loadUser();
                        mUserControl.ctlBlacklist(basicUserInfoDBModel.userid, basicUserInfoDBModel.token, item.userid, UserControl.REMOVE_FROM_BLACKLIST, httpCallback);
                    }
                };
                commDialog.CommDialog(BlacklistActivity.this, getString(R.string.blacklist_delete), true, callback);
            }
        });

        loadData();
        noDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_ADAPTER_NOTIFY:
                noDataLayout.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                break;
            case MSG_NO_DATA:
                showNoDataLayout();
                break;
            case MSG_DELETE_BLACKLIST:
                flashList((String) msg.obj);
                break;
        }
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            freshLoad();
        } else {
            moreLoad();
        }
        swipyRefreshLayout.setRefreshing(false);
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

    private void loadData() {
        LoadingDialog.showLoadingDialog(BlacklistActivity.this);
        HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {

            }

            @Override
            public void onSuccess(String response) {
                CommonParseListModel<BasicUserInfoDBModel> result = JsonUtil.fromJson(response, new TypeToken<CommonParseListModel<BasicUserInfoDBModel>>() {
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
                    } else {
                        onBusinessFaild(result.code, response);
                    }
                }
                if (data.isEmpty()) {
                    uiHandler.obtainMessage(MSG_NO_DATA).sendToTarget();
                }
                IS_REFRESH = false;
                LoadingDialog.cancelLoadingDialog();
            }
        };
        mUserControl.loadBlacklist(model.userid, model.token, dateSort, pageIndex, pageSize, httpCallback);
    }


    private void showNoDataLayout() {
        noDataLayout.setVisibility(View.VISIBLE);
        noDataLayout.findViewById(R.id.hint_textview1).setVisibility(View.VISIBLE);
        ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(getString(R.string.no_data_no_blacklist));
        noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);
    }


    private void flashList(String userId) {
        if (userId == null) {
            return;
        }
        boolean isRemove = false;
        for (BasicUserInfoDBModel item : data) {
            if (item.userid.equals(userId)) {
                data.remove(item);
                isRemove = true;
                break;
            }
        }
        if (isRemove) {
            adapter.setData(data);
            adapter.notifyDataSetChanged();
        }
    }
}
