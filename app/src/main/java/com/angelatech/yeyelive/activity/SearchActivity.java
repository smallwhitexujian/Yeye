package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.WithBroadCastActivity;
import com.angelatech.yeyelive.activity.function.SearchUser;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.SearchItemModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.string.Encryption;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends WithBroadCastActivity {

    private final int MSG_UPDATE_SEARCH_RESULT = 1;
    private final int MSG_NO_DATA = 2;
    private final int MSG_ADAPTER_NOTIFY = 3;
    private final int MSG_SET_FOLLOW = 4;
    private final int MSG_CLEAR_DATA = 5;

    private ListView searchListView;
    private EditText searchEditText;
    private CommonAdapter<SearchItemModel> adapter;
    private volatile List<SearchItemModel> datas = new ArrayList<>();

    private TextView searchCancel;

    private SearchUser searchUser;
    private BasicUserInfoDBModel model;

    private volatile String searchKey;

    private RelativeLayout noDataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search);
        initView();
        setView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.closeKeybord(searchEditText, this);
    }

    private void initView() {
        model = CacheDataManager.getInstance().loadUser();
        searchUser = new SearchUser(this);
        searchEditText = (EditText) findViewById(R.id.search_input);
        searchListView = (ListView) findViewById(R.id.search_list);
        searchCancel = (TextView) findViewById(R.id.search_cancel);
        noDataLayout = (RelativeLayout)findViewById(R.id.no_data_layout);

        adapter = new CommonAdapter<SearchItemModel>(this, datas, R.layout.item_search) {
            @Override
            public void convert(ViewHolder helper, SearchItemModel item, final int position) {
                helper.setText(R.id.user_nick, item.nickname);
                helper.setImageViewByImageLoader(R.id.user_face, item.headurl);

                if (Constant.SEX_MALE.equals(item.sex)) {
                    helper.setImageResource(R.id.user_sex, R.drawable.icon_information_boy);
                } else {
                    helper.setImageResource(R.id.user_sex, R.drawable.icon_information_girl);
                }
                if (SearchItemModel.HAVE_FOLLOW.equals(item.isfollow)) {
                    helper.setImageResource(R.id.attention_btn, R.drawable.btn_focus_cancel);
                } else {
                    helper.setImageResource(R.id.attention_btn, R.drawable.btn_focus);
                }
                //0 无 1 v 2 金v 9官
                if (item.isv!=null){
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
                }
                helper.setOnClick(R.id.attention_btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doFocus(datas.get(position), position);
                    }
                });

                if(model.userid.equals(item.userid)){
                    helper.hideView(R.id.attention_btn);
                }else{
                    helper.showView(R.id.attention_btn);
                }
            }
        };
    }

    private void setView() {
        searchListView.setAdapter(adapter);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchTask != null) {
                    uiHandler.removeCallbacks(searchTask);
                    noDataLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    searchKey = s.toString();
                    uiHandler.postDelayed(searchTask, 1000);
                } else {
                    uiHandler.obtainMessage(MSG_CLEAR_DATA, 0, 0).sendToTarget();
                }
            }
        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //关闭搜素输入框
                Utility.closeKeybord(searchEditText,SearchActivity.this);
                SearchItemModel searchItemModel = datas.get(position);
                BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                userInfoModel.Userid = searchItemModel.userid;
                StartActivityHelper.jumpActivity(SearchActivity.this, FriendUserInfoActivity.class, userInfoModel);
            }
        });

        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.closeKeybord(searchEditText,SearchActivity.this);
                finish();
            }
        });

        Utility.openKeybord(searchEditText, this);
        noDataLayout.setVisibility(View.GONE);
    }

    public Runnable searchTask = new Runnable() {
        @Override
        public void run() {
            ResponseRelationSearch();
        }
    };

    private void ResponseRelationSearch() {
        final HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {}

            @Override
            public void onSuccess(String response) {
                CommonListResult<SearchItemModel> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<SearchItemModel>>() {
                }.getType());
                if (results != null && HttpFunction.isSuc(results.code)) {
                    if (results.hasData()) {
                        uiHandler.obtainMessage(MSG_UPDATE_SEARCH_RESULT, 0, 0, results.data).sendToTarget();
                    } else {
                        uiHandler.obtainMessage(MSG_NO_DATA, 0, 0).sendToTarget();
                    }
                }
            }
        };
        if (searchKey == null || "".equals(searchKey)) {
            return;
        }
        searchUser.searchUser(model.userid, model.token, Encryption.utf8ToUnicode(searchKey), callback);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_SEARCH_RESULT:
                if (searchKey == null || "".equals(searchKey)) {
                    return;
                }
                datas = (List<SearchItemModel>) msg.obj;
                adapter.setData(datas);
                adapter.notifyDataSetChanged();
                break;
            case MSG_NO_DATA:
                showNoDataLayout();
                datas = new ArrayList<>();
                adapter.setData(datas);
                adapter.notifyDataSetChanged();
                break;
            case MSG_CLEAR_DATA:
                noDataLayout.setVisibility(View.GONE);
                datas = new ArrayList<>();
                adapter.setData(datas);
                adapter.notifyDataSetChanged();
                break;
            case MSG_ADAPTER_NOTIFY:
                adapter.notifyDataSetChanged();
                break;
            case MSG_SET_FOLLOW:
                adapter.notifyDataSetChanged();
//                ToastUtils.showToast(SearchActivity.this, getString(R.string.success));
                break;
        }
    }

    @Override
    protected void doReceive(String action, Intent intent) {
        if(WithBroadCastActivity.ACTION_WITH_BROADCAST_ACTIVITY.equals(action)){
            SearchItemModel searchItemModel = (SearchItemModel) intent.getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            if(searchItemModel != null){
                for(SearchItemModel data:datas){
                    if(data.userid.equals(searchItemModel.userid)){
                        data.isfollow = searchItemModel.isfollow;
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void doFocus(SearchItemModel data, final int position) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                DebugLogs.e("===============失败了");
            }
            @Override
            public void onSuccess(String response) {
                CommonModel results = JsonUtil.fromJson(response, CommonModel.class);
                if (results != null) {
                    if(HttpFunction.isSuc(results.code)){
                        if (datas.get(position).isfollow.equals("1")) {
                            datas.get(position).isfollow = "0";
                        } else {
                            datas.get(position).isfollow = "1";
                        }
                        uiHandler.obtainMessage(MSG_SET_FOLLOW).sendToTarget();
                    }
                    else{
                        onBusinessFaild(results.code);
                    }
                }

            }
        };
        BasicUserInfoDBModel model = CacheDataManager.getInstance().loadUser();
        searchUser.UserFollow(CommonUrlConfig.UserFollow, model.token, model.userid, data.userid, Integer.valueOf(data.isfollow), callback);
    }

    private void showNoDataLayout(){
        noDataLayout.setVisibility(View.VISIBLE);
        noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);
        ((TextView)noDataLayout.findViewById(R.id.hint_textview1)).setText(getString(R.string.no_data_not_match_people));
    }
}