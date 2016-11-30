package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.MyProductModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
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
 * 作者: Created by: xujian on Date: 2016/11/29.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */

public class ProductActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout swipyRefreshLayout;
    private List<MyProductModel> productModelList = new ArrayList<>();
    private ListView list;
    private CommonAdapter<MyProductModel> adapter;
    private MainEnter mainEnter;
    private BasicUserInfoDBModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_notification);
        mainEnter = new MainEnter(this);
        userModel = CacheDataManager.getInstance().loadUser();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainEnter.UesrMallOrderList(CommonUrlConfig.UesrMallOrderList, userModel.userid, userModel.token, callback);
    }

    private void initData() {
        mainEnter.UesrMallOrderList(CommonUrlConfig.UesrMallOrderList, userModel.userid, userModel.token, callback);
        adapter = new CommonAdapter<MyProductModel>(ProductActivity.this, productModelList, R.layout.item_my_product) {
            @Override
            public void convert(ViewHolder helper, final MyProductModel item, final int position) {
                helper.setText(R.id.order_str, getString(R.string.tips_order) + item.orderid);
                helper.setText(R.id.name, item.shopname);
                helper.setText(R.id.commodity_price, item.voucher + getString(R.string.product_voucher));
                helper.setText(R.id.order_time, getString(R.string.order_time) + item.lastupttime);
                helper.setText(R.id.name_phone, item.username + "       " + item.phone);
                helper.setText(R.id.address, item.useraddress);
                if (item.useraddress == null) {
                    helper.invisibleView(R.id.details);
                    helper.showView(R.id.add_address);
                    helper.hideView(R.id.btn_edit);
                } else {
                    helper.showView(R.id.btn_edit);
                    helper.hideView(R.id.add_address);
                    helper.showView(R.id.details);
                }
                switch (item.state) {
                    case "0"://订单生成（未下单）
                        helper.setText(R.id.state, getString(R.string.order_state));
                        helper.setText(R.id.confirm_order, getString(R.string.comfirm_order));
                        break;
                    case "10"://下单成功（未发货）
                        helper.hideView(R.id.btn_edit);
                        helper.setText(R.id.state, getString(R.string.order_state_1));
                        helper.setText(R.id.confirm_order, getString(R.string.product_confirm_order));
                        break;
                    case "20"://已发货
                        helper.hideView(R.id.btn_edit);
                        helper.setText(R.id.state, getString(R.string.order_state_2));
                        helper.setText(R.id.confirm_order, getString(R.string.product_confirm_order));
                        break;
                    case "30"://已签收
                        helper.hideView(R.id.btn_edit);
                        helper.setText(R.id.state, getString(R.string.order_state_3));
                        helper.setText(R.id.confirm_order, getString(R.string.product_confirm_order));
                        break;
                }
                helper.setOnClick(R.id.btn_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StartActivityHelper.jumpActivity(ProductActivity.this, ProductAddressActivity.class, item);
                    }
                });
                helper.setOnClick(R.id.add_address, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StartActivityHelper.jumpActivity(ProductActivity.this, ProductAddressActivity.class, item);
                    }
                });
                helper.setOnClick(R.id.confirm_order, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.useraddress != null && !item.useraddress.isEmpty()) {
                            mainEnter.UserConfirmOrder(CommonUrlConfig.UserOrderEidt, userModel.userid, userModel.token, item.id, callback2);
                        } else {
                            ToastUtils.showToast(ProductActivity.this, getString(R.string.tips_address));
                        }
                    }
                });
            }
        };

    }

    private HttpBusinessCallback callback2 = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
        }

        @Override
        public void onSuccess(String response) {
            DebugLogs.d("---------->" + response);
            final CommonModel commonModel = JsonUtil.fromJson(response, CommonModel.class);
            if (commonModel != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (commonModel.code.equals("1000")) {
                            ToastUtils.showToast(ProductActivity.this, getString(R.string.confirm_order_success));
                            mainEnter.UesrMallOrderList(CommonUrlConfig.UesrMallOrderList, userModel.userid, userModel.token, callback);
                        }
                    }
                });
            }
        }
    };

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
        }

        @Override
        public void onSuccess(final String response) {
            DebugLogs.d("---------->" + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final CommonListResult<MyProductModel> commonListResult = JsonUtil.fromJson(response, new TypeToken<CommonListResult<MyProductModel>>() {
                    }.getType());
                    if (commonListResult != null) {
                        swipyRefreshLayout.setRefreshing(false);
                        productModelList.clear();
                        productModelList.addAll(commonListResult.data);
                        list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getString(R.string.my_product));
        list = (ListView) findViewById(R.id.message_notice_list);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mainEnter.UesrMallOrderList(CommonUrlConfig.UesrMallOrderList, userModel.userid, userModel.token, callback);
                }
            }
        });
    }
}
