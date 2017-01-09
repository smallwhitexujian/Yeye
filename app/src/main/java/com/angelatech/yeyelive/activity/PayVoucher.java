package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonParseModel;
import com.angelatech.yeyelive.model.VoucherModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.google.gson.reflect.TypeToken;
import com.payssion.android.sdk.PayssionActivity;
import com.payssion.android.sdk.model.PayRequest;
import com.payssion.android.sdk.model.PayResponse;
import com.will.common.log.DebugLogs;
import com.will.common.tool.DeviceTool;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoRoundView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xujian on 17-1-3.
 * 充值券的界面
 */

public class PayVoucher extends BaseActivity {
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.str_vourcher)
    TextView strVourcher;
    @BindView(R.id.str_coins)
    TextView strCoins;
    @BindView(R.id.weichat)
    TextView weichat;
    @BindView(R.id.payssion)
    TextView payssion;
    @BindView(R.id.menu)
    GridView menu;
    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.headerLayout)
    RelativeLayout headerLayout;
    @BindView(R.id.hander_pic)
    FrescoRoundView handerPic;
    @BindView(R.id.btn_changer)
    TextView btnChanger;

    private Drawable drawable, drawable2;
    private List<VoucherModel> voucherModels;
    private CommonAdapter<VoucherModel> adapter;
    private BasicUserInfoDBModel userInfo;
    private static String key_api = "36d13a0e54f8ffa9";
    private static String Secret_Key = "20a9631e6ab7c064d9c62a85a9ee8ec9";
    private static int kindPay = 1;//默认微信支付
    private MainEnter mainEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        kindPay = 2;
        ButterKnife.bind(this);
        voucherModels = new ArrayList<>();
        mainEnter = new MainEnter(this);
        userInfo = CacheDataManager.getInstance().loadUser();
        drawable = ContextCompat.getDrawable(this, R.drawable.btn_navigation_bar_hot_n);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawable2 = ContextCompat.getDrawable(this, R.color.transparent);
        drawable2.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        strCoins.setText(userInfo.diamonds);
        strVourcher.setText(userInfo.voucher);
        handerPic.setImageURI(userInfo.headurl);
        weichat.setOnClickListener(this);
        payssion.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        btnChanger.setOnClickListener(this);

        mainEnter.money2ticket(CommonUrlConfig.money2ticket, callback);
        adapter = new CommonAdapter<VoucherModel>(getApplication(), voucherModels, R.layout.item_voucher) {
            @Override
            public void convert(ViewHolder helper, VoucherModel item, int position) {
                helper.setText(R.id.Price, voucherModels.get(position).key + getString(R.string.tips_voucher));
                helper.setText(R.id.rmb, getString(R.string.selling_price) + voucherModels.get(position).key + "MYR");
            }
        };
        menu.setAdapter(adapter);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                switch (kindPay) {
                    case 1://weichat支付

                        break;
                    case 2://paySsion支付
                        mainEnter.doorder(CommonUrlConfig.doorder, userInfo.userid, voucherModels.get(i).key,voucherModels.get(i).value, DeviceTool.getUniqueID(getApplication()), new HttpBusinessCallback() {
                            @Override
                            public void onSuccess(final String response) {
                                super.onSuccess(response);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonParseModel commonModel = JsonUtil.fromJson(response, CommonParseModel.class);
                                        if (commonModel != null && commonModel.code.equals("1000")) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(commonModel.data.toString());
                                                String orderid = jsonObject.getString("orderid");
                                                payssionPay(Double.valueOf(voucherModels.get(i).key), orderid, userInfo.nickname, userInfo.userid);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.weichat:
                clearTabColor();
                kindPay = 1;
                weichat.setCompoundDrawables(null, null, null, drawable);
                payssion.setCompoundDrawables(null, null, null, drawable2);
                weichat.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));
                payssion.setTextColor(ContextCompat.getColor(this, R.color.color_787878));
                break;
            case R.id.payssion:
                clearTabColor();
                kindPay = 2;
                payssion.setCompoundDrawables(null, null, null, drawable);
                weichat.setCompoundDrawables(null, null, null, drawable2);
                payssion.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));
                weichat.setTextColor(ContextCompat.getColor(this, R.color.color_787878));
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.btn_changer:
                StartActivityHelper.jumpActivityDefault(PayVoucher.this, MoneyChangerActivity.class);
                break;
        }
    }

    private void clearTabColor() {
        weichat.setCompoundDrawables(null, null, null, null);
        payssion.setCompoundDrawables(null, null, null, null);
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onSuccess(final String response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonListResult<VoucherModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<VoucherModel>>() {
                    }.getType());
                    if (datas != null) {
                        if (datas.code.equals("1000")) {
                            voucherModels.addAll(datas.data);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case PayssionActivity.RESULT_OK:
                if (null != data) {
                    PayResponse response = (PayResponse) data.getSerializableExtra(PayssionActivity.RESULT_DATA);
                    if (null != response) {
                        //去服务端查询该笔订单状态，注意订单状态以服务端为准
                        String transId = response.getTransactionId(); //获取Payssion交易Id
                        String orderId = response.getOrderId(); //获取您的订单Id
                        String num = response.getAmount();
                        //you will have to query the payment state with the transId or orderId from your server
                        //as we will notify you server whenever there is a payment state change
                        mainEnter.userMoney(CommonUrlConfig.userMoney,userInfo.userid,new HttpBusinessCallback(){
                            @Override
                            public void onSuccess(final String response) {
                                super.onSuccess(response);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonParseModel commonModel = JsonUtil.fromJson(response, CommonParseModel.class);
                                        if (commonModel != null) {
                                            if (commonModel.code.equals("1000")) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(commonModel.data.toString());
                                                    String diamonds = jsonObject.getString("diamonds");
                                                    String voucher = jsonObject.getString("voucher");
                                                    strCoins.setText(diamonds);
                                                    strVourcher.setText(voucher);
                                                    CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, diamonds, userInfo.userid);
                                                    CacheDataManager.getInstance().update(BaseKey.USER_VOUCHER, voucher, userInfo.userid);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
                break;
            case PayssionActivity.RESULT_CANCELED:
                //the transation has been cancelled, for example, the users doesn't pay but get back
                break;
            case PayssionActivity.RESULT_ERROR:
                //there is some error
                if (null != data) {
                    String err_des = data.getStringExtra(PayssionActivity.RESULT_DESCRIPTION);
                    Log.v(this.getClass().getSimpleName(), "RESULT_ERROR" + err_des);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * payssion支付订单
     */
    private void payssionPay(Double amount, String TrackId, String payEmail, String pauName) {
        Intent intent = new Intent(PayVoucher.this, PayssionActivity.class);
        intent.putExtra(PayssionActivity.ACTION_REQUEST,
                new PayRequest()
                        .setLiveMode(true) //false if you are using sandbox environment
                        .setAPIKey(key_api) //Payssion帐户API Key
                        .setAmount(amount) //订单金额
                        .setCurrency("MYR") //货币USD
//                        .setPMId("CashU") //支付方式id
                        .setPayerRef("yeye") //支付方式的其他参数
//                        .setLanguage(language)
                        .setOrderId(TrackId) // your order id
                        .setDescription("yeye payssion") //订单说明
                        .setSecretKey(Secret_Key) //Secret Key
                        .setPayerEmail(payEmail) //付款y用户昵称
                        .setPayerName(pauName)); //付款用户id
        PayVoucher.this.startActivityForResult(intent, 0);
    }

}
