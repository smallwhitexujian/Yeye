package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.SearchUser;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.SearchItemModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.string.Encryption;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransferAccountsActivity extends BaseActivity implements OnClickListener {

    private final int MSG_UPDATE_SEARCH_RESULT = 1;
    private final int MSG_NO_DATA = 2;
    private final int MSG_ADAPTER_NOTIFY = 3;
    private final int MSG_SET_FOLLOW = 4;
    private final int MSG_CLEAR_DATA = 5;

    private final int MSG_UPDATE_FRIEND_RESULT = 11;
    private ImageView btn_back;
    private ListView searchListView;
    private EditText searchEditText;
    private TextView txt_title;
    private Button btn_search;

    private RelativeLayout noDataLayout;
    private SearchUser searchUser;
    private BasicUserInfoDBModel model;
    private volatile String searchKey;
    private CommonAdapter<SearchItemModel> adapter;
    private volatile List<SearchItemModel> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_accounts);
        initView();
        setView();
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        model = CacheDataManager.getInstance().loadUser();
        searchUser = new SearchUser(this);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        txt_title = (TextView) findViewById(R.id.txt_title);
        noDataLayout = (RelativeLayout) findViewById(R.id.no_data_layout);
        searchEditText = (EditText) findViewById(R.id.search_input);
        searchListView = (ListView) findViewById(R.id.search_list);
        adapter = new CommonAdapter<SearchItemModel>(this, datas, R.layout.item_transfer) {
            @Override
            public void convert(ViewHolder helper, SearchItemModel item, final int position) {
                helper.setText(R.id.user_nick, item.nickname);
                helper.setText(R.id.userid, item.userid);
                helper.setImageViewByImageLoader(R.id.user_face, item.headurl);

                //0 无 1 v 2 金v 9官
                if (item.isv != null) {
                    switch (item.isv) {
                        case "1":
                            helper.setImageResource(R.id.iv_vip, R.drawable.icon_identity_vip_white);
                            helper.showView(R.id.iv_vip);
                            break;
                        case "2":
                            helper.setImageResource(R.id.iv_vip, R.drawable.icon_identity_vip_gold);
                            helper.showView(R.id.iv_vip);
                            break;
                        case "9":
                            helper.setImageResource(R.id.iv_vip, R.drawable.icon_identity_official);
                            helper.showView(R.id.iv_vip);
                            break;
                        default:
                            helper.hideView(R.id.iv_vip);
                            break;
                    }
                }

            }
        };
    }

//    public Runnable searchTask = new Runnable() {
//        @Override
//        public void run() {
//            ResponseRelationSearch();
//        }
//    };


    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_SEARCH_RESULT:
                txt_title.setVisibility(View.GONE);
                if (searchKey == null || "".equals(searchKey)) {
                    return;
                }
                datas = (List<SearchItemModel>) msg.obj;
                adapter.setData(datas);
                adapter.notifyDataSetChanged();
                break;
            case MSG_UPDATE_FRIEND_RESULT:
                txt_title.setVisibility(View.VISIBLE);
                datas = (List<SearchItemModel>) msg.obj;
                adapter.setData(datas);
                adapter.notifyDataSetChanged();
                break;
            case MSG_NO_DATA:
                txt_title.setVisibility(View.GONE);
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


    private void ResponseRelationSearch() {
        final HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

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

    private void setView() {
        searchListView.setAdapter(adapter);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //关闭搜素输入框
                Utility.closeKeybord(searchEditText, TransferAccountsActivity.this);
                SearchItemModel searchItemModel = datas.get(position);
                BasicUserInfoDBModel userInfoModel = new BasicUserInfoDBModel();
                userInfoModel.userid = searchItemModel.userid;
                userInfoModel.idx = searchItemModel.idx;
                userInfoModel.headurl = searchItemModel.headurl;
                userInfoModel.nickname = searchItemModel.nickname;
                StartActivityHelper.jumpActivity(TransferAccountsActivity.this, TransferActivity.class, userInfoModel);
            }
        });
        noDataLayout.setVisibility(View.GONE);
        Utility.openKeybord(searchEditText, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_search:
                //搜索
                searchKey = searchEditText.getText().toString();
                ResponseRelationSearch();
                break;
        }
    }

    private void showNoDataLayout() {
        noDataLayout.setVisibility(View.VISIBLE);
        noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);
        ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(getString(R.string.no_data_not_match_people));
    }
}
