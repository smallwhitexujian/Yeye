package com.angelatech.yeyelive.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.SetPayPwdActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.ProductModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.view.CommDialog;
import com.angelatech.yeyelive.view.DialogInputPwd;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.string.Encryption;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoDrawee;

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
    private RelativeLayout details, input_pwd;
    private TextView product_title, commodity_price, numText, Coupons;
    private ListView googs_list;
    private MainEnter mainEnter;
    private BasicUserInfoDBModel userInfo;
    private int mPosition;
    private StringBuilder builder;
    private BasicUserInfoDBModel liveInfo;
    private TextView strName, commodityPrice, title;
    private String str;//密码
    private ImageView[] imageViews;
    private EditText lock_password;
    private String num;//个数

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
        input_pwd = (RelativeLayout) view.findViewById(R.id.input_pwd);
        commodity = (FrescoDrawee) view.findViewById(R.id.commodity);
        product_title = (TextView) view.findViewById(R.id.product_title);
        numText = (TextView) view.findViewById(R.id.numText);
        Coupons = (TextView) view.findViewById(R.id.Coupons);
        title = (TextView) view.findViewById(R.id.title);
        ImageView goodsnum_more = (ImageView) view.findViewById(R.id.goodsnum_more);
        ImageView goodsnum_less = (ImageView) view.findViewById(R.id.goodsnum_less);
        Button purchase = (Button) view.findViewById(R.id.purchase);
        commodity_price = (TextView) view.findViewById(R.id.commodity_price);

        builder = new StringBuilder();
        ImageView tv_p1 = (ImageView) view.findViewById(R.id.tv_p1);
        ImageView tv_p2 = (ImageView) view.findViewById(R.id.tv_p2);
        ImageView tv_p3 = (ImageView) view.findViewById(R.id.tv_p3);
        ImageView tv_p4 = (ImageView) view.findViewById(R.id.tv_p4);
        ImageView tv_p5 = (ImageView) view.findViewById(R.id.tv_p5);
        ImageView tv_p6 = (ImageView) view.findViewById(R.id.tv_p6);
        lock_password = (EditText) view.findViewById(R.id.lock_password);
        imageViews = new ImageView[]{tv_p1, tv_p2, tv_p3, tv_p4, tv_p5, tv_p6};
        strName = (TextView) view.findViewById(R.id.name);
        commodityPrice = (TextView) view.findViewById(R.id.commodityPrice);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        goodsnum_more.setOnClickListener(this);
        goodsnum_less.setOnClickListener(this);
        purchase.setOnClickListener(this);
        lock_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    return;
                }
                if (builder.length() < 6) {
                    builder.append(s.toString());
                    setTextValue(getActivity(), builder, imageViews, lock_password);
                }
                s.delete(0, s.length());
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_pwd.setVisibility(View.GONE);
                lock_password.setText("");
                str = "";
                builder.delete(0, builder.length());
                for (int i = 0; i < 6; i++) {
                    imageViews[i].setVisibility(View.INVISIBLE);
                }
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_pwd.setVisibility(View.GONE);
                VoucherMallExg(App.productModels.get(mPosition).mallid, num, Encryption.MD5(str));
                str = "";
                builder.delete(0, builder.length());
                lock_password.setText("");
                for (int i = 0; i < 6; i++) {
                    imageViews[i].setVisibility(View.INVISIBLE);
                }
            }
        });
        lock_password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) {
                    delTextValue(builder, imageViews);
                    return true;
                }
                return false;
            }
        });
    }

    private void initData() {
        liveInfo = ChatRoomActivity.roomModel.getUserInfoDBModel();
        userInfo = CacheDataManager.getInstance().loadUser();
        mainEnter = new MainEnter(getActivity());
        CommonAdapter<ProductModel> adapter = new CommonAdapter<ProductModel>(getActivity(), App.productModels, R.layout.item_goods_list) {
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
                setDetails(App.productModels.get(position));
                mPosition = position;
            }
        });
        mainEnter.LiveUserMallList(CommonUrlConfig.LiveUserMallList, userInfo.userid, userInfo.token, liveInfo.userid, "1", "1000", callback);
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            LoadingDialog.cancelLoadingDialog();
        }

        @Override
        public void onSuccess(final String response) {
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonListResult<ProductModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<ProductModel>>() {
                        }.getType());
                        if (datas == null) {
                            return;
                        }
                        if (HttpFunction.isSuc(datas.code) && App.productModels!=null) {
                            App.productModels.clear();
                            App.productModels.addAll(datas.data);
                            if (App.productModels.size() <= 0) {
                                App.chatRoomApplication.callFragment.open_goods_list.setVisibility(View.GONE);
                            } else {
                                App.chatRoomApplication.callFragment.open_goods_list.setVisibility(View.VISIBLE);
                            }
                        } else {
                            onBusinessFaild(datas.code);
                        }
                    }
                });
            }
        }
    };

    private void setDetails(ProductModel model) {
        numText.setText("1");
        commodity.setImageURI(model.tradeurl);
        product_title.setText(model.describe);
        commodity_price.setText(model.voucher + getString(R.string.product_voucher));
        Coupons.setText(getString(R.string.goods_coupons) + userInfo.voucher);
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
                if (userInfo.ispaypassword == 0) {//未设置密码
                    final CommDialog dialog = new CommDialog();
                    CommDialog.Callback callback = new CommDialog.Callback() {
                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onOK() {
                            StartActivityHelper.jumpActivityDefault(getActivity(), SetPayPwdActivity.class);
                        }
                    };
                    dialog.CommDialog(getActivity(), getString(R.string.pwd_desc), true, callback, getString(R.string.now_set), getString(R.string.not_set));
                }
                if (userInfo.ispaypassword == 1) {
                    lock_password.setFocusable(true);
                    num = numText.getText().toString();
                    String pirce = String.valueOf(Integer.valueOf(num) * Float.valueOf(App.productModels.get(mPosition).voucher));
                    commodityPrice.setText(pirce);
                    strName.setText(getString(R.string.dialog_tips_1) + liveInfo.nickname + getString(R.string.dialog_tips_2));
                    input_pwd.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void VoucherMallExg(String mallid, String num, String paypassword) {
        mainEnter.VoucherMallExg(CommonUrlConfig.VoucherMallExg, userInfo.userid, userInfo.token, mallid, num, paypassword, callback2);
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
                    if (commonModel.code.equals("6003")) {
                        title.setText(getString(R.string.tips_error));
                        title.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_d80c18));
                        input_pwd.setVisibility(View.VISIBLE);
                    }
                    if (commonModel.code.equals("5000")) {
                        ToastUtils.showToast(getActivity(), getString(R.string.tips_eyu));
                    }
                    if (HttpFunction.isSuc(commonModel.code)) {//下单成功
                        details.setVisibility(View.GONE);
                        DialogInputPwd dialogInputPwd = new DialogInputPwd();
                        dialogInputPwd.CommDialog(getActivity());
                    } else {
                        onBusinessFaild(commonModel.code);
                    }
                }
            });
        }
    };

    //设置密码显示
    private void setTextValue(Context context, StringBuilder builder, ImageView[] imageViews, EditText editText) {
        try {
            str = builder.toString();
            int len = str.length();
            DebugLogs.d("--长度-->" + len);
            if (len <= 6 && len > 0) {
                imageViews[len - 1].setVisibility(View.VISIBLE);
            }
            if (len == 6) {//设置密码
                Utility.closeKeybord(editText, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除密码设置
    private void delTextValue(StringBuilder builder, ImageView[] imageViews) {
        str = builder.toString();
        int len = str.length();
        if (len == 0) {
            return;
        }
        if (len > 0 && len <= 6) {
            builder.delete(len - 1, len);
        }
        imageViews[len - 1].setVisibility(View.INVISIBLE);
    }
}
