package com.angelatech.yeyelive.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.UserInfoDialog;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.UesrVoucherBillModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.web.HttpFunction;
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
 * 交易记录
 */
public class UesrVoucherBillListActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private ImageView btn_back;
    private BasicUserInfoDBModel loginUser = null;
    private final int pageSize = 10;
    private int pageIndex = 1;
    private String otherId = null;
    private UserInfoDialog userInfoDialog;
    private SwipyRefreshLayout swipyRefreshLayout;
    private volatile boolean IS_REFRESH = true;  //是否需要刷新
    private List<UesrVoucherBillModel> list = new ArrayList<>();
    private ListView list_view;
    private CommonAdapter<UesrVoucherBillModel> adapter;

    private final int MSG_LIST_SUCCESS = 1;
    private final int MSG_NO_MORE = 2;
    private final int MSG_LIST_NODATA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uesr_voucher_bill_list);
        initView();
        setView();
        initData();
    }

    private void initData() {
        loginUser = CacheDataManager.getInstance().loadUser();
        swipyRefreshLayout.setRefreshing(true);
        if (loginUser != null) {
            getBillList();
        }
    }

    private void setView() {
        btn_back.setOnClickListener(this);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(true);
            }
        });
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        adapter = new CommonAdapter<UesrVoucherBillModel>(this, list, R.layout.item_voucherbill) {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void convert(ViewHolder helper, UesrVoucherBillModel item, int position) {
                helper.setText(R.id.txt_orderid, getString(R.string.txt_orderid) + item.orderid);
                helper.setText(R.id.txt_targetuseridx, getString(R.string.txt_targetuseridx) + item.targetuseridx);
                helper.setText(R.id.txt_descript, getString(R.string.txt_pay_descript) + item.descript);
                helper.setText(R.id.txt_tradedate, getString(R.string.txt_tradedate) + item.tradedate);
                switch (item.type) {
                    case 1:
                        helper.setText(R.id.txt_type,R.string.txt_sales_goods);
                        helper.setText(R.id.txt_voucherchange, "+" + item.voucherchange);
                        helper.setTextColor(R.id.txt_voucherchange, Color.parseColor("#FF43D835"));
                        break;
                    case 2:
                        helper.setText(R.id.txt_type, R.string.txt_recharge);
                        helper.setText(R.id.txt_voucherchange, "+" + item.voucherchange);
                        helper.setTextColor(R.id.txt_voucherchange, Color.parseColor("#FF43D835"));
                        break;
                    case 3:
                        helper.setText(R.id.txt_type, R.string.transfer);
                        helper.setText(R.id.txt_voucherchange, "+" + item.voucherchange);
                        helper.setTextColor(R.id.txt_voucherchange, Color.parseColor("#FF43D835"));
                        break;
                    case 51:
                        helper.setText(R.id.txt_type, R.string.txt_buy_goods);
                        helper.setText(R.id.txt_voucherchange, item.voucherchange);
                        helper.setTextColor(R.id.txt_voucherchange, Color.parseColor("#FFD80C18"));
                        break;
                    case 52:
                        helper.setText(R.id.txt_type,R.string.transfer);
                        helper.setText(R.id.txt_voucherchange, item.voucherchange);
                        helper.setTextColor(R.id.txt_voucherchange, Color.parseColor("#FFD80C18"));
                        break;
                }
            }
        };
        list_view.setAdapter(adapter);
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        list_view = (ListView) findViewById(R.id.list_view);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }


    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_LIST_SUCCESS:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                //noDataLayout.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                break;
            case MSG_LIST_NODATA:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                // showNodataLayout();
                //adapter.notifyDataSetChanged();
                break;
            case MSG_NO_MORE:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                ToastUtils.showToast(this, getString(R.string.no_data_more));
                break;

        }
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            uiHandler.sendEmptyMessage(MSG_LIST_NODATA);
        }

        @Override
        public void onSuccess(String response) {
            if (response != null) {
                DebugLogs.e("response:" + response);
                CommonParseListModel<UesrVoucherBillModel> result = JsonUtil.fromJson(response, new TypeToken<CommonParseListModel<UesrVoucherBillModel>>() {
                }.getType());
                if (result != null) {
                    if (HttpFunction.isSuc(result.code)) {
                        if (result.data.size() > 0) {
                            if (IS_REFRESH) {
                                list.clear();
                            }
                            list.addAll(result.data);
                            uiHandler.obtainMessage(MSG_LIST_SUCCESS).sendToTarget();
                        } else {
                            if (!IS_REFRESH) {
                                uiHandler.obtainMessage(MSG_NO_MORE).sendToTarget();
                            }
                        }
                    }
                }
                if (list.isEmpty()) {
                    uiHandler.sendEmptyMessage(MSG_LIST_NODATA);
                }
            }
        }
    };

    /**
     * 转账
     */
    private void getBillList() {
        if (userInfoDialog == null) {
            userInfoDialog = new UserInfoDialog(this);
        }
        userInfoDialog.getBillList(CommonUrlConfig.UesrVoucherBillList, loginUser.token, loginUser.userid, String.valueOf(pageSize), String.valueOf(pageIndex), callback);
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    IS_REFRESH = true;
                    pageIndex = 0;
                } else {
                    IS_REFRESH = false;
                }
                pageIndex++;
                initData();
            }
        });
    }
}
