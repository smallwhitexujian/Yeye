package com.angelatech.yeyelive.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.ProductModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoDrawee;

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
 * 作者: Created by: xujian on Date: 2016/11/21.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.fragment
 */

public class GoodsListFragment extends BaseFragment {
    private View view;
    private FrescoDrawee commodity;
    private RelativeLayout details;
    private TextView title, commodity_price, numText, Coupons;
    private ListView googs_list;
    private List<ProductModel> productModels = new ArrayList<>();
    private BasicUserInfoDBModel liveUserInfo;
    private MainEnter mainEnter;
    private BasicUserInfoDBModel userInfo;
    private int mPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_googs_list, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {
        googs_list = (ListView) view.findViewById(R.id.googs_list);
        details = (RelativeLayout) view.findViewById(R.id.details);
        commodity = (FrescoDrawee) view.findViewById(R.id.commodity);
        title = (TextView) view.findViewById(R.id.title);
        numText = (TextView) view.findViewById(R.id.numText);
        Coupons = (TextView) view.findViewById(R.id.Coupons);
        ImageView goodsnum_more = (ImageView) view.findViewById(R.id.goodsnum_more);
        ImageView goodsnum_less = (ImageView) view.findViewById(R.id.goodsnum_less);
        Button purchase = (Button) view.findViewById(R.id.purchase);
        commodity_price = (TextView) view.findViewById(R.id.commodity_price);
        goodsnum_more.setOnClickListener(this);
        goodsnum_less.setOnClickListener(this);
        purchase.setOnClickListener(this);
    }

    private void initData() {
        liveUserInfo = ChatRoomActivity.roomModel.getUserInfoDBModel();
        userInfo = CacheDataManager.getInstance().loadUser();
        mainEnter = new MainEnter(getActivity());

        CommonAdapter<ProductModel> adapter = new CommonAdapter<ProductModel>(getActivity(), productModels, R.layout.item_goods_list) {
            @Override
            public void convert(ViewHolder helper, final ProductModel item, int position) {
                helper.setImageURI(R.id.commodity, item.tradeurl);
            }
        };
        googs_list.setAdapter(adapter);
        googs_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                details.setVisibility(View.VISIBLE);
                setDetails(productModels.get(position));
                mPosition = position;
            }
        });
        mainEnter.LiveUserMallList(CommonUrlConfig.LiveUserMallList, userInfo.userid, userInfo.token, liveUserInfo.userid, "1", "1000", callback);
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            LoadingDialog.cancelLoadingDialog();
        }

        @Override
        public void onSuccess(final String response) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonListResult<ProductModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<ProductModel>>() {
                    }.getType());
                    if (datas == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(datas.code)) {
                        productModels.clear();
                        productModels.addAll(datas.data);
                    } else {
                        onBusinessFaild(datas.code);
                    }
                }
            });
        }
    };

    private void setDetails(ProductModel model) {
        numText.setText("1");
        commodity.setImageURI(model.tradeurl);
        title.setText(model.tradename);
        commodity_price.setText(model.voucher + getString(R.string.product_voucher));
//        Coupons.setText(getString(R.string.goods_coupons) + 1231231);//TODO 我自己的卷
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.goodsnum_more:
                numText.setText(String.valueOf(Integer.valueOf(numText.getText().toString()) + 1));
                break;
            case R.id.goodsnum_less:
                if (Integer.valueOf(numText.getText().toString()) > 1) {
                    numText.setText(String.valueOf(Integer.valueOf(numText.getText().toString()) - 1));
                }
                break;
            case R.id.purchase:
//                CommDialog dialog = new CommDialog();
//                CommDialog.Callback callback = new CommDialog.Callback() {
//                    @Override
//                    public void onCancel() {
//
//                    }
//
//                    @Override
//                    public void onOK() {
//
//                    }
//                };
//                dialog.CommDialog(getActivity(), getString(R.string.pwd_desc), true, callback, getString(R.string.now_set), getString(R.string.not_set));
                String num = numText.getText().toString();
                VoucherMallExg(productModels.get(mPosition).mallid, num);
                break;
        }
    }

    private void VoucherMallExg(String mallid, String num) {
        mainEnter.VoucherMallExg(CommonUrlConfig.VoucherMallExg, userInfo.userid, userInfo.token, mallid, num, callback2);
    }

    private HttpBusinessCallback callback2 = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {

        }

        @Override
        public void onSuccess(final String response) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonModel commonModel = JsonUtil.fromJson(response, CommonModel.class);
                    if (commonModel == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(commonModel.code)) {//下单成功
                        ToastUtils.showToast(getActivity(), getString(R.string.product_order));
                        details.setVisibility(View.GONE);
                    } else {
                        onBusinessFaild(commonModel.code);
                    }
                }
            });
        }
    };
}
