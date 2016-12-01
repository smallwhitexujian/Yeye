package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.ProductModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * 作者: Created by: xujian on Date: 2016/12/1.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */

public class HostGoldHousActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout swipyRefreshLayout;
    private List<ProductModel> productModels = new ArrayList<>();
    private CommonAdapter<ProductModel> adapter;
    private int screenWidth;
    private BasicUserInfoModel liveInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_notification);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            liveInfo = (BasicUserInfoModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        }
        initView();
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getString(R.string.live_host));
        screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：px）
        ListView list = (ListView) findViewById(R.id.message_notice_list);
        list.setDividerHeight(ScreenUtils.dip2px(HostGoldHousActivity.this, 10));
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        BasicUserInfoDBModel userInfo = CacheDataManager.getInstance().loadUser();
        MainEnter mainEnter = new MainEnter(HostGoldHousActivity.this);
        adapter = new CommonAdapter<ProductModel>(this, productModels, R.layout.item_host_gold) {
            @Override
            public void convert(ViewHolder helper, final ProductModel item, int position) {
                ViewGroup.LayoutParams para;
                para = helper.getView(R.id.pic).getLayoutParams();
                para.height = screenWidth;
                para.width = screenWidth;
                helper.getView(R.id.pic).setLayoutParams(para);
                helper.setImageURI(R.id.pic, item.tradeurl);
                helper.setText(R.id.commodity_price,item.voucher);
                helper.setText(R.id.textView14,item.describe);
            }
        };
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        mainEnter.LiveUserMallList(CommonUrlConfig.LiveUserMallList, userInfo.userid, userInfo.token, liveInfo.Userid, "1", "1000", callback);
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    swipyRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            LoadingDialog.cancelLoadingDialog();
        }

        @Override
        public void onSuccess(final String response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonListResult<ProductModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<ProductModel>>() {
                    }.getType());
                    if (datas == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(datas.code)) {
                        productModels.clear();
                        productModels.addAll(datas.data);
                        adapter.notifyDataSetChanged();
                    } else {
                        onBusinessFaild(datas.code);
                    }
                }
            });
        }
    };
}
