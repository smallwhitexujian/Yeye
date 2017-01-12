package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.pay.PayManager;
import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.Md5;
import com.android.vending.billing.util.Purchase;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.function.GooglePay;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonParseModel;
import com.angelatech.yeyelive.model.RechargeModel;
import com.angelatech.yeyelive.pay.MimoPay.MimoPayLib;
import com.angelatech.yeyelive.pay.MimoPay.MimopayModel;
import com.angelatech.yeyelive.pay.PayType;
import com.angelatech.yeyelive.pay.google.PayActivity;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StringHelper;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * google 支付
 * 充值
 */
public class RechargeActivity extends PayActivity implements View.OnClickListener {
    private final int MSG_LOAD_PAY_MENU = 1;
    public final static int MSG_ADD_ITEM = 2;
    private boolean isTest = false;
    private final int ORDER_FAILD = 0;//下单失败
    private ListView mRechargeListView;
    private CommonAdapter<RechargeModel> mCommonAdapter;
    private List<RechargeModel> mDatas;
    private TextView mBalanceTextView, recharge_tips;
    private boolean isAvaliable = true;
    private GooglePay mGooglePay;
    private MimoPayLib mimoPayLib;
    private BasicUserInfoDBModel user;
    private TextView mRechargeTextView;
    private RechargeModel mRechargeModel;
    private ImageView digi_selected, google_selected,Celcom_selected;
    private boolean isMiMoPay = false;
    private RelativeLayout item_google, item_digi,item_Celcom;
    private LinearLayout recharge_pay_model,recharge_pay_model_1;
    private int payType = MimoPayLib.DPOINT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_);
        mGooglePay = new GooglePay(this);
        mimoPayLib = new MimoPayLib();
        user = CacheDataManager.getInstance().loadUser();
        initView();
        setView();
    }

    private void initView() {
        mRechargeListView = (ListView) findViewById(R.id.recharge_listview);
        mBalanceTextView = (TextView) findViewById(R.id.recharge_balance_coin);
        mRechargeTextView = (TextView) findViewById(R.id.btn_submit_pay);
        recharge_tips = (TextView) findViewById(R.id.recharge_tips);
        digi_selected = (ImageView) findViewById(R.id.digi_selected);
        google_selected = (ImageView) findViewById(R.id.google_selected);
        Celcom_selected = (ImageView) findViewById(R.id.Celcom_selected);
        recharge_pay_model = (LinearLayout) findViewById(R.id.recharge_pay_model);
        recharge_pay_model_1 = (LinearLayout) findViewById(R.id.recharge_pay_model_1);
        google_selected.setVisibility(View.VISIBLE);
        item_Celcom = (RelativeLayout) findViewById(R.id.item_Celcom);
        item_google = (RelativeLayout) findViewById(R.id.item_google);
        item_digi = (RelativeLayout) findViewById(R.id.item_digi);
        item_google.setOnClickListener(this);
        item_digi.setOnClickListener(this);
        item_Celcom.setOnClickListener(this);
        Map<String, String> map = new HashMap<>();
        map.put("userid", user.userid);
        map.put("token", user.token);
        mGooglePay.RechargeDisplay(map, new HttpBusinessCallback() {
            @Override
            public void onSuccess(final String response) {
                super.onSuccess(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String data = jsonObject.getString("data");
                            JSONObject object = new JSONObject(data);
                            String isMimopay = object.getString("mimopay");
                            if (!isMimopay.equals("0")) {//审核
                                recharge_tips.setVisibility(View.VISIBLE);
                                recharge_pay_model.setVisibility(View.VISIBLE);
//                                recharge_pay_model_1.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void setView() {
        headerLayout.showTitle(getString(R.string.recharge_title));
        headerLayout.showLeftBackButton();
        mRechargeTextView.setOnClickListener(this);
        mDatas = new ArrayList<>();
        loadMenu(PayType.TYPE_GOOGLE);
        mCommonAdapter = new CommonAdapter<RechargeModel>(RechargeActivity.this, mDatas, R.layout.item_recharge_config) {
            @Override
            public void convert(ViewHolder helper, RechargeModel item, int position) {
                helper.setText(R.id.tv_diamond, String.valueOf(item.diamonds));
                helper.setText(R.id.tv_money, getString(R.string.recharge_unit) + " " + String.valueOf(item.amount));
                if (item.isCheck == 0) {
                    helper.hideView(R.id.iv_choice_right);
                } else {
                    helper.showView(R.id.iv_choice_right);
                }
            }
        };
        mRechargeListView.setAdapter(mCommonAdapter);
        mRechargeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRechargeModel = mDatas.get(position);
                for (int i = 0; i < mDatas.size(); i++) {
                    RechargeModel rechargeModel = mDatas.get(i);
                    if (i != position) {
                        rechargeModel.isCheck = 0;
                    } else {
                        rechargeModel.isCheck = 1;
                    }
                }
                mCommonAdapter.notifyDataSetChanged();
            }
        });
        if (user != null && user.diamonds != null) {
            mBalanceTextView.setText(StringHelper.getThousandFormat(user.diamonds));
        } else {
            mBalanceTextView.setText("0");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCoin();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_pay:
                if (isMiMoPay) {
                    orderDigi(mRechargeModel,payType);
                } else if (mRechargeModel != null && isAvaliable) {
                    order(mRechargeModel);
                }
                break;
            case R.id.item_digi://digi支付
                sethintSelected();
                loadMenu(PayType.TYPE_MIMOPAY);
                payType = MimoPayLib.DPOINT;
                digi_selected.setVisibility(View.VISIBLE);
                item_digi.setBackground(ContextCompat.getDrawable(RechargeActivity.this, R.drawable.circle_coner_bg_red));
                break;
            case R.id.item_google://Google支付
                sethintSelected();
                loadMenu(PayType.TYPE_GOOGLE);
                google_selected.setVisibility(View.VISIBLE);
                item_google.setBackground(ContextCompat.getDrawable(RechargeActivity.this, R.drawable.circle_coner_bg_red));
                break;
            case R.id.item_Celcom:
                sethintSelected();
                loadMenu(PayType.TYPE_MIMOPAY);
                payType = MimoPayLib.CELCOM;
                Celcom_selected.setVisibility(View.VISIBLE);
                item_Celcom.setBackground(ContextCompat.getDrawable(RechargeActivity.this, R.drawable.circle_coner_bg_red));
                break;
        }
    }

    private void sethintSelected() {
        item_digi.setBackground(ContextCompat.getDrawable(RechargeActivity.this, R.drawable.circle_coner_bg_ffffff));
        item_google.setBackground(ContextCompat.getDrawable(RechargeActivity.this, R.drawable.circle_coner_bg_ffffff));
        item_Celcom.setBackground(ContextCompat.getDrawable(RechargeActivity.this, R.drawable.circle_coner_bg_ffffff));
        digi_selected.setVisibility(View.GONE);
        google_selected.setVisibility(View.GONE);
        Celcom_selected.setVisibility(View.GONE);
    }

    private void loadMenu(int type) {
        LoadingDialog.showLoadingDialog(RechargeActivity.this, null);
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                LoadingDialog.cancelLoadingDialog();
                CommonListResult<RechargeModel> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<RechargeModel>>() {
                }.getType());
                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        if (results.data != null) {
                            uiHandler.obtainMessage(MSG_LOAD_PAY_MENU, results.data).sendToTarget();
                        }
                    } else {
                        onBusinessFaild(results.code);
                    }
                }
            }
        };
        switch (type) {
            case PayType.TYPE_GOOGLE:
                isMiMoPay = false;
                break;
            case PayType.TYPE_MIMOPAY:
                isMiMoPay = true;
                break;
        }
        mGooglePay.loadMenu(type, callback);
    }

    private void addItem(Purchase purchase) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                CommonParseModel<String> results = JsonUtil.fromJson(response, new TypeToken<CommonParseModel<String>>() {
                }.getType());
                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        //更新金币显示
                        if (results.data != null) {
                            DebugLogs.e("===diamonds " + results.data);
                            CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, results.data, user.userid);
                            uiHandler.obtainMessage(MSG_ADD_ITEM).sendToTarget();
                        }
                    } else {
                        onBusinessFaild(results.code);
                    }
                }
            }
        };
        String userId = user.userid;
        String token = user.token;
        mGooglePay.addItem(userId, token, purchase, callback);
    }


    //订单,生成订单
    private void order(final RechargeModel model) {
        if (model == null) return;
        LoadingDialog.showLoadingDialog(RechargeActivity.this,null);
        String key = Md5.md5(UUID.randomUUID().toString());
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                CommonParseModel<String> results = JsonUtil.fromJson(response, new TypeToken<CommonParseModel<String>>() {
                }.getType());
                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        if (results.data != null) {
                            pay(model.sku, requestCode, results.data);//调用支付过程
                        } else {
                            uiHandler.obtainMessage(ORDER_FAILD).sendToTarget();
                        }
                    } else {
                        onBusinessFaild(results.code);
                    }
                }
            }
        };
        if (model.sku != null) {
            mGooglePay.order(user.userid, user.token, key, model.sku, callback);
        }
    }

    //digi订单生成
    private void orderDigi(final RechargeModel model,final int type) {
        if (model == null) return;
        LoadingDialog.showLoadingDialog(RechargeActivity.this, null);
        final String key = Md5.md5(UUID.randomUUID().toString());
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                LoadingDialog.cancelLoadingDialog();
                CommonParseModel<String> results = JsonUtil.fromJson(response, new TypeToken<CommonParseModel<String>>() {
                }.getType());
                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        if (results.data != null) {
                            DebugLogs.d("-------订单生成完成-----》" + results.toString());
                            MimopayModel mimopayModel = new MimopayModel();
                            mimopayModel.UserId = user.userid;
                            mimopayModel.productName = model.diamonds;
                            mimopayModel.currency = getString(R.string.recharge_unit);
                            mimopayModel.coins = model.amount;
                            mimopayModel.transactionId = results.data;//订单
                            mimopayModel.paymentid = type;
                            mimoPayLib.initMimopay(RechargeActivity.this, mimopayModel);
                            mimoPayLib.setcallBack(callBack);
                        } else {
                            uiHandler.obtainMessage(ORDER_FAILD).sendToTarget();
                        }
                    } else {
                        onBusinessFaild(results.code);
                    }
                }
            }
        };
        //服务器下载订单
        if (model.sku != null) {
            mGooglePay.order(user.userid, user.token, key, model.sku, callback);
        }
    }

    private MimoPayLib.CallBack callBack = new MimoPayLib.CallBack() {
        @Override
        public void cuccess() {
            Map<String, String> map = new HashMap<>();
            map.put("userid", user.userid);
            map.put("token", user.token);
            mGooglePay.getUserDiamond(map, httpCallback);
        }

        @Override
        public void error() {

        }

        @Override
        public void fatalerror() {

        }
    };

    HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
        }

        @Override
        public void onSuccess(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String code = jsonObject.getString("code");
                String data = jsonObject.getString("data");
                if (code.equals(String.valueOf(HttpFunction.SUC_OK))) {
                    user.diamonds = data;
                    DebugLogs.d("-----查询金币------>" + user.diamonds);
                    CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, user.diamonds, user.userid);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void doHandler(Message msg) {
        int what = msg.what;
        switch (what) {
            case PayManager.FAILED_PURCHASE:
                if (isTest) {
                    String str = IabHelper.getResponseDesc(msg.arg1);
                    Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                } else {
                    //取消提示
                    if (msg.arg1 == -1005) {
                        return;
                    }
                    Toast.makeText(this, getString(R.string.purchase_faild), Toast.LENGTH_SHORT).show();
                }
                break;
            case PayManager.FAILED_QUERY_INVENTORY:
                if (isTest) {
                    Toast.makeText(this, getString(R.string.inquiry_faild), Toast.LENGTH_SHORT).show();
                }
                break;
            case PayManager.FAILED_SETTING_UP_IAB:
                isAvaliable = false;
                if (isTest) {
                    Toast.makeText(this, "设置IAB失败", Toast.LENGTH_SHORT).show();
                } else {
                    ToastUtils.showToast(RechargeActivity.this, getString(R.string.google_play_useless), Toast.LENGTH_SHORT);
                }
                break;
            case PayManager.NOT_ALLOW_BUY:
                if (isTest) {
                    Toast.makeText(this, getString(R.string.google_play_useless), Toast.LENGTH_SHORT).show();
                }
                break;
            case PayManager.QUERY_INVENTORY_FINISH:
                if (isTest) {
                    Toast.makeText(this, "查询调单库存结束", Toast.LENGTH_SHORT).show();
                }
                break;
            case PayManager.ILLEGAL_SKU:
                ToastUtils.showToast(this, getString(R.string.illegal_sku), Toast.LENGTH_SHORT);
                break;
            case PayManager.ADD_ITEM://加币
                addItem((Purchase) msg.obj);
                break;
            case CANCEL_PURCHASE:
                ToastUtils.showToast(this, getString(R.string.purchase_cancel), Toast.LENGTH_SHORT);
                break;
            //
            case MSG_LOAD_PAY_MENU:
                mDatas = (List<RechargeModel>) msg.obj;
                if (mDatas != null && !mDatas.isEmpty()) {
                    mCommonAdapter.setData(mDatas);
                    mCommonAdapter.notifyDataSetChanged();
                }
                mRechargeModel = mDatas.get(0);
                for (int i = 0; i < mDatas.size(); i++) {
                    RechargeModel rechargeModel = mDatas.get(i);
                    if (i != 0) {
                        rechargeModel.isCheck = 0;
                    } else {
                        rechargeModel.isCheck = 1;
                    }
                }
                mCommonAdapter.notifyDataSetChanged();
                break;
            case MSG_ADD_ITEM:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(getApplicationContext(), getString(R.string.purchase_succ), Toast.LENGTH_SHORT);
                        LoadingDialog.cancelLoadingDialog();
                        refreshCoin();
                    }
                });
                break;
            case ORDER_FAILD:
                ToastUtils.showToast(this, getString(R.string.server_require_faild), Toast.LENGTH_SHORT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //刷新金币
    private void refreshCoin() {
        user = CacheDataManager.getInstance().loadUser();
        if (user != null && user.diamonds != null) {
            mBalanceTextView.setText(user.diamonds);
        } else {
            mBalanceTextView.setText("0");
        }
    }
}
