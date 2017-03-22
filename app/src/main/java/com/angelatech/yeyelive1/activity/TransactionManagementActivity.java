package com.angelatech.yeyelive1.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.activity.function.MainEnter;
import com.angelatech.yeyelive1.adapter.CommonAdapter;
import com.angelatech.yeyelive1.adapter.ViewHolder;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.CommonListResult;
import com.angelatech.yeyelive1.model.CommonModel;
import com.angelatech.yeyelive1.model.ProductModel;
import com.angelatech.yeyelive1.model.TMModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.ScreenUtils;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
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
 * com.angelatech.yeyelive1.activity
 */

public class TransactionManagementActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout swipyRefreshLayout;
    private CommonAdapter<ProductModel> adapter;
    private BasicUserInfoDBModel userInfo;
    private ArrayList<TMModel> tmModels = new ArrayList<>();
    private MainEnter mainEnter;
    private EditText express_name, express_num;
    private int pageindex = 1;
    private int pageSize = 20;
    private AlertDialog dialog = null;
    private CommonAdapter<TMModel> commonAdapter;
    private int mPosition;
    private boolean isfull;

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
        pageindex = 1;
        mainEnter.LiveUesrMallOrderList(CommonUrlConfig.LiveUesrMallOrderList, userInfo.userid, userInfo.token, pageindex, pageSize, callback);
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getString(R.string.transaction_title));
        ListView list = (ListView) findViewById(R.id.message_notice_list);
        list.setDividerHeight(ScreenUtils.dip2px(TransactionManagementActivity.this, 10));
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        commonAdapter = new CommonAdapter<TMModel>(this, tmModels, R.layout.item_tmm) {
            @Override
            public void convert(ViewHolder helper, final TMModel item, final int position) {
                helper.setImageUrl(R.id.hander_pic, item.headurl);
                helper.setText(R.id.name, item.nickname);
                helper.setText(R.id.idx, "ID:" + item.idx);
                helper.setText(R.id.order_str, "订单号:" + item.orderid);
                helper.setText(R.id.order_time, item.createtime);
                helper.setText(R.id.product_name, item.shopname);
                helper.setText(R.id.state, item.state);//状态码
                helper.setText(R.id.num, item.num);
                helper.setText(R.id.commodityPrice, item.voucher);
                helper.setText(R.id.toName, item.username);
                helper.setText(R.id.phone, item.phone);
                helper.setText(R.id.address_str, item.useraddress);
                switch (item.state) {
                    case "0"://订单生成（未下单）
                        helper.setText(R.id.state, getString(R.string.order_state));
                        helper.setText(R.id.btn_confirm, getString(R.string.comfirm_order));
                        helper.setTextColor(R.id.btn_confirm, ContextCompat.getColor(TransactionManagementActivity.this, R.color.color_999999));
                        break;
                    case "10"://下单成功（未发货）
                        helper.setText(R.id.state, getString(R.string.order_state_1));
                        helper.setTextColor(R.id.btn_confirm, ContextCompat.getColor(TransactionManagementActivity.this, R.color.color_999999));
                        helper.setText(R.id.btn_confirm, getString(R.string.ttm_send_product));
                        helper.setOnClick(R.id.btn_confirm, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPosition = position;
                                if (item.kuaidino == null || item.kuaidino.isEmpty()) {
                                    CommDialog(TransactionManagementActivity.this);
                                }
                            }
                        });
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
                            swipyRefreshLayout.setRefreshing(false);
                            if (!isfull) {
                                tmModels.clear();
                            }
                            tmModels.addAll(listResult.data);
                            commonAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    };

    public void CommDialog(final Context context) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
            dialog.show();
            Window window = dialog.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setGravity(Gravity.CENTER);
            window.setContentView(R.layout.dialog_express_bill);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            express_name = (EditText) window.findViewById(R.id.express_name);
            express_num = (EditText) window.findViewById(R.id.express_num);
            Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
            Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String expressName = express_name.getText().toString();
                    String expressNum = express_num.getText().toString();
                    String str = expressName + " " + expressNum;
                    mainEnter.liveUserOrderEidt(CommonUrlConfig.liveUserOrderEidt, userInfo.userid, userInfo.token, str, tmModels.get(mPosition).id, callback2);
                }
            });
        }
    }

    private HttpBusinessCallback callback2 = new HttpBusinessCallback() {
        @Override
        public void onSuccess(final String response) {
            super.onSuccess(response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonModel commonModel = JsonUtil.fromJson(response, CommonModel.class);
                    if (commonModel != null) {
                        if (commonModel.code.equals("1000")) {
                            dialog.dismiss();
                            mainEnter.liveUserOrderEidt(CommonUrlConfig.liveUserOrderEidt, userInfo.userid, userInfo.token, tmModels.get(mPosition).id, new HttpBusinessCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    super.onSuccess(response);
                                    CommonModel re = JsonUtil.fromJson(response, CommonModel.class);
                                    if (re != null) {
                                        if (re.code.equals("1000")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mainEnter.LiveUesrMallOrderList(CommonUrlConfig.LiveUesrMallOrderList, userInfo.userid, userInfo.token, pageindex, pageSize, callback);
                                                    ToastUtils.showToast(TransactionManagementActivity.this, getString(R.string.tips_success));
                                                }
                                            });
                                        } else {
                                            onBusinessFaild(re.code);
                                        }
                                    }
                                }
                            });
                            ToastUtils.showToast(TransactionManagementActivity.this, getString(R.string.tips_tijiao));
                        } else {
                            onBusinessFaild(commonModel.code);
                        }
                    }
                }
            });
        }
    };

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            isfull = false;
            pageindex = 1;
            mainEnter.LiveUesrMallOrderList(CommonUrlConfig.LiveUesrMallOrderList, userInfo.userid, userInfo.token, pageindex, pageSize, callback);
        } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
            isfull = true;
            pageindex = pageindex + 1;
            mainEnter.LiveUesrMallOrderList(CommonUrlConfig.LiveUesrMallOrderList, userInfo.userid, userInfo.token, pageindex, pageSize, callback);
        }

    }
}
