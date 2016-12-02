package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.ProductModel;
import com.angelatech.yeyelive.model.TMModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;

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

public class TransactionManagementActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout swipyRefreshLayout;
    private CommonAdapter<ProductModel> adapter;
    private BasicUserInfoDBModel userInfo;
    private ArrayList<TMModel> tmModels = new ArrayList<>();
    private MainEnter mainEnter;
    private String pageindex = "1";
    private String pageSize = "20";
    private CommonAdapter<TMModel> commonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_notification);
        userInfo = CacheDataManager.getInstance().loadUser();
        mainEnter = new MainEnter(TransactionManagementActivity.this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainEnter.LiveUesrMallOrderList(CommonUrlConfig.LiveUesrMallOrderList, userInfo.userid, userInfo.token, pageindex, pageSize, callback);
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getString(R.string.transaction_title));
        ListView list = (ListView) findViewById(R.id.message_notice_list);
        list.setDividerHeight(ScreenUtils.dip2px(TransactionManagementActivity.this, 10));
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        commonAdapter = new CommonAdapter<TMModel>(this, tmModels, R.layout.item_tmm) {
            @Override
            public void convert(ViewHolder helper, TMModel item, int position) {
                helper.setImageUrl(R.id.hander_pic,item.headurl);
                helper.setText(R.id.name,item.nickname);
                helper.setText(R.id.idx,"ID:"+item.idx);
                helper.setText(R.id.order_str,"订单号:"+item.orderid);
                helper.setText(R.id.order_time,item.createtime);
                helper.setText(R.id.product_name,item.shopname);
                helper.setText(R.id.state,item.state);//状态码
                helper.setText(R.id.num,item.num);
                helper.setText(R.id.commodityPrice,item.voucher);
                helper.setText(R.id.toName,item.username);
                helper.setText(R.id.phone,item.phone);
                helper.setText(R.id.address_str,item.useraddress);
                switch (item.state) {
                    case "0"://订单生成（未下单）
                        helper.setText(R.id.state, getString(R.string.order_state));
                        helper.setText(R.id.btn_confirm, getString(R.string.comfirm_order));
                        helper.setTextColor(R.id.btn_confirm, ContextCompat.getColor(TransactionManagementActivity.this,R.color.color_999999));
                        break;
                    case "10"://下单成功（未发货）
                        helper.setText(R.id.state, getString(R.string.order_state_1));
                        helper.setTextColor(R.id.btn_confirm, ContextCompat.getColor(TransactionManagementActivity.this, R.color.color_d9d9d9));
                        helper.setText(R.id.btn_confirm, getString(R.string.ttm_send_product));
                        break;
                    case "20"://已发货
                        helper.setText(R.id.state, getString(R.string.order_state_2));
                        helper.setTextColor(R.id.btn_confirm, ContextCompat.getColor(TransactionManagementActivity.this, R.color.color_d9d9d9));
                        helper.setText(R.id.btn_confirm, getString(R.string.order_state_2));
                        break;
                    case "30"://已签收
                        helper.setText(R.id.state, getString(R.string.order_state_3));
                        helper.setTextColor(R.id.btn_confirm, ContextCompat.getColor(TransactionManagementActivity.this, R.color.color_d9d9d9));
                        helper.setText(R.id.btn_confirm, getString(R.string.order_state_3));
                        break;
                }
            }
        };
        list.setAdapter(commonAdapter);
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onSuccess(final String response) {
            super.onSuccess(response);
            DebugLogs.e("-------->" + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonListResult<TMModel> listResult = JsonUtil.fromJson(response, new TypeToken<CommonListResult<TMModel>>() {
                    }.getType());
                    if (listResult != null) {
                        if (listResult.code.equals("1000")) {
                            tmModels.addAll(listResult.data);
                            commonAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    };

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }
}
