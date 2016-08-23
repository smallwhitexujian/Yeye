package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.angelatech.yeyelive.pay.PayType;
import com.angelatech.yeyelive.pay.google.PayActivity;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StringHelper;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * google 支付
 * 充值
 */
public class RechargeActivity extends PayActivity implements View.OnClickListener {
    private final int MSG_LOAD_PAY_MENU = 1;
    private final int MSG_ADD_ITEM = 2;


    private boolean isTest = false;
    private final int ORDER_FAILD = 0;//下单失败
    private ListView mRechargeListView;
    private CommonAdapter<RechargeModel> mCommonAdapter;
    private List<RechargeModel> mDatas;

    private TextView mBalanceTextView;

    private boolean isAvaliable = true;
    private GooglePay mGooglePay;
    private BasicUserInfoDBModel user = CacheDataManager.getInstance().loadUser();


    private TextView mRechargeTextView;
    private RechargeModel mRechargeModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_);
        mGooglePay = new GooglePay(this);
        initView();
        setView();
    }

    private void initView() {
        mRechargeListView = (ListView) findViewById(R.id.recharge_listview);
        mBalanceTextView = (TextView) findViewById(R.id.recharge_balance_coin);
        mRechargeTextView = (TextView) findViewById(R.id.btn_submit_pay);
    }

    private void setView() {
        headerLayout.showTitle(getString(R.string.recharge_title));
        headerLayout.showLeftBackButton();
        mRechargeTextView.setOnClickListener(this);
        mDatas = new ArrayList<>();
        loadMenu();
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
//                RechargeModel model = mDatas.get(position);
//                order(model);
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
        //
        if (user != null && user.diamonds != null) {
            mBalanceTextView.setText(StringHelper.getThousandFormat(user.diamonds));
        } else {
            mBalanceTextView.setText("0");
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_pay:
                if (mRechargeModel != null && isAvaliable) {
                    order(mRechargeModel);
                }
                break;
        }
    }

    private void loadMenu() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
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
        mGooglePay.loadMenu(PayType.TYPE_GOOGLE, callback);
    }

    private void addItem(Purchase purchase) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                DebugLogs.e("=====" + response);
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


    private void order(final RechargeModel model) {
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
                            pay(model.sku, requestCode, results.data);
                        } else {
                            uiHandler.obtainMessage(ORDER_FAILD).sendToTarget();
                        }
                    } else {
                        onBusinessFaild(results.code);
                    }
                }
            }
        };
        mGooglePay.order(user.userid, user.token, key, model.sku, callback);
    }

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
            case PayManager.ADD_ITEM:
//                ToastUtils.showToast(this, getString(R.string.purchase_succ), Toast.LENGTH_SHORT);
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
                break;
            case MSG_ADD_ITEM:
                ToastUtils.showToast(this, getString(R.string.purchase_succ), Toast.LENGTH_SHORT);
                refreshCoin();
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

    private void refreshCoin() {
        user = CacheDataManager.getInstance().loadUser();
        if (user != null && user.diamonds != null) {
            mBalanceTextView.setText(StringHelper.getThousandFormat(user.diamonds));
        } else {
            mBalanceTextView.setText("0");
        }
    }
}
