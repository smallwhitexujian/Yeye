package com.angelatech.yeyelive.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.VoucherModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.google.gson.reflect.TypeToken;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @BindView(R.id.info)
    LinearLayout info;
    @BindView(R.id.weichat)
    TextView weichat;
    @BindView(R.id.payssion)
    TextView payssion;
    @BindView(R.id.tab_menu)
    LinearLayout tabMenu;
    @BindView(R.id.menu)
    GridView menu;
    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.headerLayout)
    RelativeLayout headerLayout;

    private Drawable drawable, drawable2;
    private List<VoucherModel> voucherModels;
    private CommonAdapter<VoucherModel> adapter;
    private BasicUserInfoDBModel userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        voucherModels = new ArrayList<>();
        MainEnter mainEnter = new MainEnter(this);
        userInfo = CacheDataManager.getInstance().loadUser();
        drawable = ContextCompat.getDrawable(this, R.drawable.btn_navigation_bar_hot_n);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawable2 = ContextCompat.getDrawable(this, R.color.transparent);
        drawable2.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        strCoins.setText(userInfo.diamonds);
        strVourcher.setText(userInfo.voucher);
        weichat.setOnClickListener(this);
        payssion.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        mainEnter.money2ticket(CommonUrlConfig.money2ticket, callback);
        adapter = new CommonAdapter<VoucherModel>(getApplication(), voucherModels, R.layout.item_voucher) {
            @Override
            public void convert(ViewHolder helper, VoucherModel item, int position) {
                helper.setText(R.id.Price, voucherModels.get(position).key + getString(R.string.tips_voucher));
                helper.setText(R.id.rmb, getString(R.string.selling_price) + voucherModels.get(position).key + getString(R.string.rmb));
            }
        };
        menu.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        clearTabColor();
        switch (v.getId()) {
            case R.id.weichat:
                weichat.setCompoundDrawables(null, null, null, drawable);
                payssion.setCompoundDrawables(null, null, null, drawable2);
                weichat.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));
                payssion.setTextColor(ContextCompat.getColor(this, R.color.color_787878));
                break;
            case R.id.payssion:
                payssion.setCompoundDrawables(null, null, null, drawable);
                weichat.setCompoundDrawables(null, null, null, drawable2);
                payssion.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));
                weichat.setTextColor(ContextCompat.getColor(this, R.color.color_787878));
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }

    private void clearTabColor() {
        weichat.setCompoundDrawables(null, null, null, null);
        payssion.setCompoundDrawables(null, null, null, null);
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            LoadingDialog.cancelLoadingDialog();
        }

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
}
