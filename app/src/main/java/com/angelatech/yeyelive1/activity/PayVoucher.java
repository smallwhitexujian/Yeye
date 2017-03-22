package com.angelatech.yeyelive1.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.activity.base.BaseActivity;
import com.angelatech.yeyelive1.activity.function.MainEnter;
import com.angelatech.yeyelive1.adapter.CommonAdapter;
import com.angelatech.yeyelive1.adapter.ViewHolder;
import com.angelatech.yeyelive1.application.App;
import com.angelatech.yeyelive1.db.BaseKey;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.CommonListResult;
import com.angelatech.yeyelive1.model.CommonParseModel;
import com.angelatech.yeyelive1.model.SelecTypeModel;
import com.angelatech.yeyelive1.model.VoucherModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.google.gson.reflect.TypeToken;
import com.payssion.android.sdk.PayssionActivity;
import com.payssion.android.sdk.PayssionConfig;
import com.payssion.android.sdk.model.PayRequest;
import com.payssion.android.sdk.model.PayResponse;
import com.will.common.tool.DeviceTool;
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
    private RelativeLayout rootView;
    private TextView title;
    private List<SelecTypeModel> listDatas = new ArrayList<>();
    private CommonAdapter<SelecTypeModel> commonAdapter;
    private int mPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        kindPay = 2;
        ButterKnife.bind(this);
        voucherModels = new ArrayList<>();
        mainEnter = new MainEnter(this);
        userInfo = CacheDataManager.getInstance().loadUser();
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        ListView listView = (ListView) findViewById(R.id.listView);
        ImageView btnBack = (ImageView) findViewById(R.id.btnBack);
        title = (TextView)findViewById(R.id.title);
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
        btnBack.setOnClickListener(this);
        PayssionConfig.enablePM("fpx_my|hlb_my|maybank2u_my|cimb_my|affinepg_my|amb_my|rhb_my|molpay|webcash_my|unionpay_cn|tenpay_cn|alipay_cn");
        PayssionConfig.setLanguage("ms");
        int WeiChatNooff = (int) Double.parseDouble(App.configOnOff.get(2).value);
        int PayssionNooff = (int) Double.parseDouble(App.configOnOff.get(3).value);
        if (PayssionNooff == 1) {
            payssion.setVisibility(View.VISIBLE);
        } else {
            payssion.setVisibility(View.GONE);
        }
        if (WeiChatNooff == 1) {
            weichat.setVisibility(View.VISIBLE);
        } else {
            weichat.setVisibility(View.GONE);
        }
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
                        mPosition = i;
                        rootView.setVisibility(View.VISIBLE);
                        title.setText(String.format("%sMYR", voucherModels.get(mPosition).key));
                        break;
                    default:
                        break;
                }
            }
        });

        commonAdapter = new CommonAdapter<SelecTypeModel>(this, listDatas, R.layout.item_pay_select_type) {
            @Override
            public void convert(ViewHolder helper, SelecTypeModel item, int position) {
                helper.setText(R.id.str, listDatas.get(position).getKey());
            }
        };
        listView.setAdapter(commonAdapter);
        commonAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String PMID = listDatas.get(i).getValue();
                rootView.setVisibility(View.GONE);
                doOrder(PMID);
            }
        });
    }

    private void initData() {
        SelecTypeModel selec1 = new SelecTypeModel("Myclear FPX", "fpx_my");
        SelecTypeModel selec2 = new SelecTypeModel("Hong Leong", "hlb_my");
        SelecTypeModel selec3 = new SelecTypeModel("Maybank2u", "maybank2u_my");
        SelecTypeModel selec4 = new SelecTypeModel("CIMB Clicks", "cimb_my");
        SelecTypeModel selec5 = new SelecTypeModel("Affin Bank", "affinepg_my");
        SelecTypeModel selec6 = new SelecTypeModel("Am online", "amb_my");
        SelecTypeModel selec7 = new SelecTypeModel("RHB Now", "rhb_my");
        SelecTypeModel selec8 = new SelecTypeModel("MOLPay", "molpay");
        SelecTypeModel selec9 = new SelecTypeModel("Webcash", "webcash_my");
        SelecTypeModel selec10 = new SelecTypeModel("支付宝（Alipay）", "alipay_cn");
        SelecTypeModel selec11 = new SelecTypeModel("财付通/微信支付（Tenpay）", "tenpay_cn");
        SelecTypeModel selec12 = new SelecTypeModel("银联（UnionPay）", "unionpay_cn");
        listDatas.add(selec1);
        listDatas.add(selec2);
        listDatas.add(selec3);
        listDatas.add(selec4);
        listDatas.add(selec5);
        listDatas.add(selec6);
        listDatas.add(selec7);
        listDatas.add(selec8);
        listDatas.add(selec9);
        listDatas.add(selec10);
        listDatas.add(selec11);
        listDatas.add(selec12);
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
            case R.id.btnBack:
                rootView.setVisibility(View.GONE);
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
                        mainEnter.userMoney(CommonUrlConfig.userMoney, userInfo.userid, new HttpBusinessCallback() {
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

    public void doOrder(final String pmid) {
        mainEnter.doorder(CommonUrlConfig.doorder, userInfo.userid, voucherModels.get(mPosition).key, voucherModels.get(mPosition).value, DeviceTool.getUniqueID(getApplication()), new HttpBusinessCallback() {
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
                                payssionPay(Double.valueOf(voucherModels.get(mPosition).key), orderid, pmid, userInfo.nickname, userInfo.userid);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * payssion支付订单
     */
    public void payssionPay(Double amount, String TrackId, String pmid, String payEmail, String pauName) {
        Intent intent = new Intent(PayVoucher.this, PayssionActivity.class);
        intent.putExtra(PayssionActivity.ACTION_REQUEST,
                new PayRequest()
                        .setLiveMode(true) //false if you are using sandbox environment
                        .setAPIKey(key_api) //Payssion帐户API Key
                        .setAmount(amount) //订单金额
                        .setCurrency("MYR") //货币USD
                        .setPMId(pmid) //支付方式id
                        .setPayerRef("yeye") //支付方式的其他参数
//                        .setLanguage(PLanguage.ZH_SIMPLIFIED)
                        .setOrderId(TrackId) // your order id
                        .setDescription("yeye payssion") //订单说明
                        .setSecretKey(Secret_Key) //Secret Key
                        .setPayerEmail(payEmail) //付款y用户昵称
                        .setPayerName(pauName)); //付款用户id
        PayVoucher.this.startActivityForResult(intent, 0);
    }

}
