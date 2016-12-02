package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.ProductModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
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
 * 作者: Created by: xujian on Date: 2016/11/22.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */

public class GoldHousActivity extends BaseActivity {
    private List<ProductModel> productModel = new ArrayList<>();
    private ListView list;
    private CommonAdapter<ProductModel> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gold_hous);
        initView();
        initData();
    }

    private void initData() {
        adapter = new CommonAdapter<ProductModel>(this, productModel, R.layout.item_gold_list) {
            @Override
            public void convert(ViewHolder helper, final ProductModel item, final int position) {
                helper.setImageURI(R.id.gold_cover, item.tradeurl);
                helper.setText(R.id.name, item.tradename);
                helper.setText(R.id.commodity_price, item.voucher);
                switch (item.state) {
                    case "0"://审核中
                        helper.setText(R.id.state, getText(R.string.gold_state_2));
                        helper.hideView(R.id.btn_edit);
                        break;
                    case "1"://已上架
                        helper.showView(R.id.btn_edit);
                        helper.setText(R.id.state, getText(R.string.gold_state_1));
                        break;
                    case "2"://已下架
                        helper.hideView(R.id.btn_edit);
                        helper.setText(R.id.state, getString(R.string.gold_state_3));
                        break;
                    case "3"://审核不通过
                        helper.hideView(R.id.btn_edit);
                        helper.setText(R.id.state, getString(R.string.gold_state_4));
                        break;
                }
                helper.setOnClick(R.id.btn_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StartActivityHelper.jumpActivity(GoldHousActivity.this, UploadProductsActivity.class, productModel.get(position));
                    }
                });
            }
        };
        BasicUserInfoDBModel userInfo = CacheDataManager.getInstance().loadUser();
        MainEnter mainEnter = new MainEnter(this);
        mainEnter.UserMallList(CommonUrlConfig.UserMallList, userInfo.userid, userInfo.token, "1", "1000", callback);
    }

    private void initView() {
        LoadingDialog.showLoadingDialog(this, null);
        list = (ListView) findViewById(R.id.list);
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        ImageView btn_upload_product = (ImageView) findViewById(R.id.btn_upload_product);
        TextView transaction_m = (TextView) findViewById(R.id.Transaction_m);
        btn_back.setOnClickListener(this);
        btn_upload_product.setOnClickListener(this);
        transaction_m.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_upload_product:
                StartActivityHelper.jumpActivityDefault(GoldHousActivity.this, UploadProductsActivity.class);
                break;
            case R.id.Transaction_m:
                StartActivityHelper.jumpActivityDefault(GoldHousActivity.this, TransactionManagementActivity.class);
                break;
        }
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
                    LoadingDialog.cancelLoadingDialog();
                    CommonListResult<ProductModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<ProductModel>>() {
                    }.getType());
                    if (datas == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(datas.code)) {
                        productModel.clear();
                        productModel.addAll(datas.data);
                        list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        onBusinessFaild(datas.code);
                    }
                }
            });
        }
    };
}
