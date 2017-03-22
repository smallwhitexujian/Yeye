package com.angelatech.yeyelive1.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.activity.function.MainEnter;
import com.angelatech.yeyelive1.adapter.CommonAdapter;
import com.angelatech.yeyelive1.adapter.ViewHolder;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.BasicUserInfoModel;
import com.angelatech.yeyelive1.model.CommonListResult;
import com.angelatech.yeyelive1.model.CommonModel;
import com.angelatech.yeyelive1.model.ProductModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.ScreenUtils;
import com.angelatech.yeyelive1.util.Utility;
import com.angelatech.yeyelive1.view.DialogInputPwd;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.string.Encryption;
import com.will.view.ToastUtils;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
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
 * 作者: Created by: xujian on Date: 2016/12/1.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive1.activity
 */

public class HostGoldHousActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout swipyRefreshLayout;
    private List<ProductModel> productModels = new ArrayList<>();
    private CommonAdapter<ProductModel> adapter;
    private int screenWidth;
    private int mPosition;
    private BasicUserInfoModel liveInfo;
    private BasicUserInfoDBModel userInfo;
    private MainEnter mainEnter;
    private AlertDialog dialog = null;
    private StringBuilder builder;
    private EditText lock_password;
    private ImageView[] imageViews;
    private TextView title;
    private String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_notification);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            liveInfo = (BasicUserInfoModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        }
        initView();
        load();
    }

    /**
     * 查询 用户信息
     */
    private void load() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                LoadingDialog.cancelLoadingDialog();
            }

            @Override
            public void onSuccess(String response) {
                DebugLogs.e("response" + response);
                LoadingDialog.cancelLoadingDialog();
                CommonListResult<BasicUserInfoDBModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<BasicUserInfoDBModel>>() {
                }.getType());
                if (datas != null && HttpFunction.isSuc(datas.code)) {
                    BasicUserInfoDBModel basicUserInfoDBModel = datas.data.get(0);
                    liveInfo.nickname = basicUserInfoDBModel.nickname;
                }
            }
        };
        if (userInfo != null) {
            mainEnter.loadUserInfo(CommonUrlConfig.UserInformation, userInfo.userid, liveInfo.Userid, userInfo.token, callback);
        }
    }

    private void initView() {
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getString(R.string.live_host));
        screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：px）
        ListView list = (ListView) findViewById(R.id.message_notice_list);
        list.setDividerHeight(ScreenUtils.dip2px(HostGoldHousActivity.this, 10));
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        userInfo = CacheDataManager.getInstance().loadUser();
        mainEnter = new MainEnter(HostGoldHousActivity.this);
        adapter = new CommonAdapter<ProductModel>(this, productModels, R.layout.item_host_gold) {
            @Override
            public void convert(ViewHolder helper, final ProductModel item, final int position) {
                ViewGroup.LayoutParams para;
                para = helper.getView(R.id.pic).getLayoutParams();
                para.height = screenWidth;
                para.width = screenWidth;
                helper.getView(R.id.pic).setLayoutParams(para);
                helper.setImageURI(R.id.pic, item.tradeurl);
                helper.setText(R.id.commodity_price, item.voucher);
                helper.setText(R.id.textView14, item.describe);
                helper.setOnClick(R.id.button2, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPosition = position;
                        CommDialog(HostGoldHousActivity.this);
                    }
                });
            }
        };
        list.setAdapter(adapter);
        mainEnter.LiveUserMallList(CommonUrlConfig.LiveUserMallList, userInfo.userid, userInfo.token, liveInfo.Userid, "1", "1000", callback);
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mainEnter.LiveUserMallList(CommonUrlConfig.LiveUserMallList, userInfo.userid, userInfo.token, liveInfo.Userid, "1", "1000", callback);
                }
            }
        });
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
                    swipyRefreshLayout.setRefreshing(false);
                    CommonListResult<ProductModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<ProductModel>>() {
                    }.getType());
                    if (datas == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(datas.code)) {
                        productModels.clear();
                        productModels.addAll(datas.data);
                        adapter.notifyDataSetChanged();
                    } else {
                        onBusinessFaild(datas.code);
                    }
                }
            });
        }
    };

    public void CommDialog(final Context context) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
            dialog.show();
            Window window = dialog.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setGravity(Gravity.CENTER);
            window.setContentView(R.layout.dialog_pay_card);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            builder = new StringBuilder();
            ImageView tv_p1 = (ImageView) window.findViewById(R.id.tv_p1);
            ImageView tv_p2 = (ImageView) window.findViewById(R.id.tv_p2);
            ImageView tv_p3 = (ImageView) window.findViewById(R.id.tv_p3);
            ImageView tv_p4 = (ImageView) window.findViewById(R.id.tv_p4);
            ImageView tv_p5 = (ImageView) window.findViewById(R.id.tv_p5);
            ImageView tv_p6 = (ImageView) window.findViewById(R.id.tv_p6);
            RelativeLayout input_pwd = (RelativeLayout) window.findViewById(R.id.input_pwd);
            input_pwd.setVisibility(View.VISIBLE);
            lock_password = (EditText) window.findViewById(R.id.lock_password);
            imageViews = new ImageView[]{tv_p1, tv_p2, tv_p3, tv_p4, tv_p5, tv_p6};
            TextView strName = (TextView) window.findViewById(R.id.name);
            title = (TextView) window.findViewById(R.id.title);
            TextView commodityPrice = (TextView) window.findViewById(R.id.commodityPrice);
            Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
            Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
            commodityPrice.setText(productModels.get(mPosition).voucher);
            strName.setText(liveInfo.nickname);
            Utility.openKeybord(lock_password, HostGoldHousActivity.this);
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
                        setTextValue(HostGoldHousActivity.this, builder, imageViews, lock_password);
                    }
                    s.delete(0, s.length());
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    dialog = null;
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VoucherMallExg(productModels.get(mPosition).mallid, "1", Encryption.MD5(str));
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonModel commonModel = JsonUtil.fromJson(response, CommonModel.class);
                    if (commonModel == null) {
                        return;
                    }
                    if (commonModel.code.equals("6003")) {
                        title.setText(getString(R.string.tips_error));
                        title.setTextColor(ContextCompat.getColor(HostGoldHousActivity.this, R.color.color_d80c18));
                    }
                    if (commonModel.code.equals("5000")) {
                        ToastUtils.showToast(HostGoldHousActivity.this, getString(R.string.tips_eyu));
                    }
                    if (HttpFunction.isSuc(commonModel.code)) {//下单成功
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        DialogInputPwd dialogInputPwd = new DialogInputPwd();
                        dialogInputPwd.CommDialog(HostGoldHousActivity.this);
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
