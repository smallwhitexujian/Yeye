package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.gson.reflect.TypeToken;
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
 * Created by donghao on 17-1-6.
 *
 */

public class MoneyChangerActivity extends BaseActivity {
    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.headerLayout)
    RelativeLayout headerLayout;
    @BindView(R.id.hander_pic)
    FrescoRoundView handerPic;
    @BindView(R.id.str_vourcher)
    TextView strVourcher;
    @BindView(R.id.str_coins)
    TextView strCoins;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    private BasicUserInfoDBModel userInfo;
    private CommonAdapter<VoucherModel> adapter;
    private MainEnter mainEnter;
    private List<VoucherModel> voucherModels;
    private int mPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_change);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        voucherModels = new ArrayList<>();
        mainEnter = new MainEnter(getApplication());
        userInfo = CacheDataManager.getInstance().loadUser();
        backBtn.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        handerPic.setImageURI(userInfo.headurl);
        strVourcher.setText(userInfo.voucher);
        strCoins.setText(userInfo.diamonds);
        adapter = new CommonAdapter<VoucherModel>(getApplication(), voucherModels, R.layout.item_changer_money) {
            @Override
            public void convert(ViewHolder helper, VoucherModel item, int position) {
                helper.setText(R.id.voucher, item.key + "MYR");
                helper.setText(R.id.coins, item.value + getString(R.string.tips_voucher));
                if (item.isCheck == 0) {
                    helper.hideView(R.id.selected);
                } else {
                    helper.showView(R.id.selected);
                }
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mPosition = position;
                for (int i = 0; i < voucherModels.size(); i++) {
                    VoucherModel voucherModel = voucherModels.get(i);
                    if (i != position) {
                        voucherModel.isCheck = 0;
                    } else {
                        voucherModel.isCheck = 1;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        mainEnter.gold2ticket(CommonUrlConfig.gold2ticket, new HttpBusinessCallback() {
            @Override
            public void onSuccess(final String response) {
                super.onSuccess(response);
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
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.btn_submit:
                String voucher = voucherModels.get(mPosition).key;
                String str = voucher.substring(0, voucher.length() - 3);
                mainEnter.voucher2gold(CommonUrlConfig.voucher2gold, userInfo.userid, str, new HttpBusinessCallback() {
                    @Override
                    public void onSuccess(final String response) {
                        super.onSuccess(response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonParseModel model = JsonUtil.fromJson(response, CommonParseModel.class);
                                        if (model != null && model.code.equals("10000")) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(model.data.toString());
                                                String diamonds = jsonObject.getString("diamonds");
                                                String voucher = jsonObject.getString("voucher");
                                                strCoins.setText(diamonds);
                                                strVourcher.setText(voucher);
                                                CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, diamonds, userInfo.userid);
                                                CacheDataManager.getInstance().update(BaseKey.USER_VOUCHER, voucher, userInfo.userid);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ToastUtils.showToast(MoneyChangerActivity.this, getString(R.string.Money_changer_success));
                                        } else {
                                            ToastUtils.showToast(MoneyChangerActivity.this, getString(R.string.Money_changer_faile));
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
                break;
        }
    }
}
