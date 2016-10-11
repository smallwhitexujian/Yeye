package com.angelatech.yeyelive.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.WxPay;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.CommonParseModel;
import com.angelatech.yeyelive.model.ConfigPayModel;
import com.angelatech.yeyelive.model.WxOrder;
import com.angelatech.yeyelive.pay.PayType;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.pay.SecurityUtil;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.angelatech.yeyelive.wxapi.WXInterface;
import com.google.gson.reflect.TypeToken;
import com.will.common.string.security.Md5;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * User: cbl
 * Date: 2016/4/8
 * Time: 14:01
 */
public class RechargeWayActivity extends HeaderBaseActivity {

    private LinearLayout layout_aliPay, layout_wxPay;
    private ImageView iv_choice_left, iv_choice_right;
    private ListView list_view;
    private TextView btn_submit_pay, tv_diamond;

    private final int HANDLER_SUCCESS = 6;
    private final int HANDLER_ERROR = 4;

    private CommonAdapter<ConfigPayModel> adapter;
    private List<ConfigPayModel> data = new ArrayList<>();
    private BasicUserInfoDBModel model;

    private int checkPayPosition = 0;
    private WxPay wxPay = new WxPay(RechargeWayActivity.this);
    private WXPayResultReceiver wxPayResultReceiver = new WXPayResultReceiver();
    private int checkWay = 1; //1是支付宝 2 是微信
    private int handler_type = 0; //0 是获取充值配置列表 1是创建微信订单 3是微信充值结果通知 4 是查询钻石余额通知

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        initView();
        setView();
        loadData();
    }

    private void initView() {
        LoadingDialog.showLoadingDialog(this,null);
        headerLayout.showTitle(getString(R.string.button_top_up));
        headerLayout.showLeftBackButton();

        layout_aliPay = (LinearLayout) findViewById(R.id.layout_aliPay);
        layout_wxPay = (LinearLayout) findViewById(R.id.layout_wxPay);
        iv_choice_left = (ImageView) findViewById(R.id.iv_choice_left);
        iv_choice_right = (ImageView) findViewById(R.id.iv_choice_right);
        list_view = (ListView) findViewById(R.id.list_view);
        btn_submit_pay = (TextView) findViewById(R.id.btn_submit_pay);
        tv_diamond = (TextView) findViewById(R.id.tv_diamond);

        model = CacheDataManager.getInstance().loadUser();
    }

    private void setView() {
        layout_aliPay.setOnClickListener(this);
        layout_wxPay.setOnClickListener(this);
        iv_choice_right.setVisibility(View.GONE);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                checkPayPosition = i;
                for (int k = 0; k < data.size(); k++) {
                    data.get(k).isCheck = 0;
                }
                data.get(checkPayPosition).isCheck = 1;
                adapter.notifyDataSetChanged();
            }
        });
        btn_submit_pay.setOnClickListener(this);
        tv_diamond.setText(model.diamonds);
        adapter = new CommonAdapter<ConfigPayModel>(this, data, R.layout.item_recharge_config) {
            @Override
            public void convert(ViewHolder helper, ConfigPayModel item, int position) {
                helper.setText(R.id.tv_diamond, item.diamonds);
                helper.setText(R.id.tv_money, "￥" + item.amount);
                if (item.isCheck == 0) {
                    helper.hideView(R.id.iv_choice_right);
                } else {
                    helper.showView(R.id.iv_choice_right);
                }
            }
        };
        list_view.setAdapter(adapter);

        registerReceiver(wxPayResultReceiver, new IntentFilter(WXInterface.WEIXIN_PAY_RESULT_ACTION));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_aliPay:
                iv_choice_right.setVisibility(View.GONE);
                iv_choice_left.setVisibility(View.VISIBLE);
                checkWay = 1;
                break;
            case R.id.layout_wxPay:
                iv_choice_right.setVisibility(View.VISIBLE);
                iv_choice_left.setVisibility(View.GONE);
                checkWay = 2;
                break;
            case R.id.btn_submit_pay:
                if (checkWay == 2) {
                    LoadingDialog.showLoadingDialog(RechargeWayActivity.this,null);
                    order(data.get(checkPayPosition));
                }
                if (checkWay == 1) {
                    ToastUtils.showToast(RechargeWayActivity.this, R.string.alipay_testing);
                }
                break;
        }
    }

    /**
     * 初始化 充值列表
     */
    private void loadData() {
        handler_type = 0;
        Map<String, String> map = new HashMap<>();
        map.put("sources", HttpFunction.SOURCES_ANDROID + "");
        map.put("type", PayType.TYPE_WEIXIN + ""); //type  2 weixin 3 alipay
        wxPay.getConfigList(map, httpCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case HANDLER_ERROR:
                LoadingDialog.cancelLoadingDialog();
                break;
            case HANDLER_SUCCESS:
                switch (handler_type) {
                    case 0:
                        LoadingDialog.cancelLoadingDialog();
                        CommonParseListModel<ConfigPayModel> result = JsonUtil.fromJson(msg.obj.toString(), new TypeToken<CommonParseListModel<ConfigPayModel>>() {
                        }.getType());
                        if (result != null) {
                            data.addAll(result.data);
                            adapter.setData(data);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case 1:
                        LoadingDialog.cancelLoadingDialog();
                        CommonParseModel<WxOrder> orderResult = JsonUtil.fromJson(msg.obj.toString(), new TypeToken<CommonParseModel<WxOrder>>() {
                        }.getType());
                        if (orderResult != null) {
                            wxPay.goWxClient(RechargeWayActivity.this, orderResult.data);
                        }
                        break;
                    case 3://查询
                        Map<String, String> map = new HashMap<>();
                        map.put("userid", model.userid);
                        map.put("token", model.token);
                        handler_type = 4;
                        wxPay.getUserDiamond(map, httpCallback);
                        break;
                    case 4: //更新钻石信息
                        CommonParseModel<CommonModel> commonModel = JsonUtil.fromJson(msg.obj.toString(), new TypeToken<CommonParseModel<CommonModel>>() {
                        }.getType());
                        if (commonModel != null) {
                            model.diamonds = commonModel.data.toString();
                            CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, model.diamonds, model.userid);
                        }
                        finish();
                        break;
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wxPayResultReceiver != null) {
            unregisterReceiver(wxPayResultReceiver);
            wxPayResultReceiver = null;
        }
    }

    private void order(ConfigPayModel payModel) {

        Map<String, String> params = new HashMap<String, String>();
        String key = Md5.md5(UUID.randomUUID().toString());
        final String userId = model.userid;
        final String token = model.token;
        final String sku = payModel.sku;

        String srcStr = userId + payModel.amount;
        String sign = SecurityUtil.getOrderSign(srcStr, key);

        params.put("userid", userId);
        params.put("amount", payModel.amount);
        params.put("iden", key);
        params.put("sku", sku);
        params.put("sign", sign);
        params.put("token", token);
        WxPay wxPay = new WxPay(RechargeWayActivity.this);
        handler_type = 1;
        wxPay.createOrder(params, httpCallback);
    }

    HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            //uiHandler.obtainMessage(HANDLER_ERROR, errorMap).sendToTarget();
        }

        @Override
        public void onSuccess(String response) {
            uiHandler.obtainMessage(HANDLER_SUCCESS, response).sendToTarget();
        }
    };

    public class WXPayResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WXInterface.WEIXIN_PAY_RESULT_ACTION)) {
                handler_type = 3;
                uiHandler.sendEmptyMessageDelayed(HANDLER_SUCCESS, 1000);
            }
        }
    }
}
