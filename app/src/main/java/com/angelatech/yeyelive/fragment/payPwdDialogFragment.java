package com.angelatech.yeyelive.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.android.vending.billing.util.Md5;
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.SetPayPwdActivity;
import com.angelatech.yeyelive.activity.TransferAccountsActivity;
import com.angelatech.yeyelive.activity.TransferActivity;
import com.angelatech.yeyelive.activity.TransferCompleteActivity;
import com.angelatech.yeyelive.activity.function.UserInfoDialog;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.view.CommDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * User: cbl
 * Date: 2016/8/4
 * Time: 18:11
 * 支付密码 dialog
 */
public class payPwdDialogFragment extends DialogFragment implements View.OnClickListener {

    private View view;
    private EditText pay_password;
    private TextView dialog_btn_cancel, dialog_btn_enter, txt_p1, txt_p2, txt_p3, txt_p4, txt_p5, txt_p6;
    private BasicUserInfoDBModel loginUserInfo;
    private final int MSG_ENTER_ROOM = 22;
    private Context context;
    private String remark = "";
    private float money;  // 转账金额
    private UserInfoDialog userInfoDialog;
    private String touserid;

    public payPwdDialogFragment(Context mcontext, Callback callback, String mtouserid, String mremark, float mmoney) {
        this.context = mcontext;
        this.mCallback = callback;
        this.remark = mremark;
        this.money = mmoney;
        this.touserid = mtouserid;
    }

    public interface Callback {
        void onCancel(String code);

        void onEnter(String pwd);
    }

    private Callback mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        view = inflater.inflate(R.layout.dialog_pay_pwd, container, false);
        initView();
        setView();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        loginUserInfo = CacheDataManager.getInstance().loadUser();
    }

    private void initView() {
        Utility.openKeybord(pay_password, getActivity());
        pay_password = (EditText) view.findViewById(R.id.pay_password);
        dialog_btn_cancel = (TextView) view.findViewById(R.id.dialog_btn_cancel);
        dialog_btn_enter = (TextView) view.findViewById(R.id.dialog_btn_enter);
        txt_p1 = (TextView) view.findViewById(R.id.txt_p1);
        txt_p2 = (TextView) view.findViewById(R.id.txt_p2);
        txt_p3 = (TextView) view.findViewById(R.id.txt_p3);
        txt_p4 = (TextView) view.findViewById(R.id.txt_p4);
        txt_p5 = (TextView) view.findViewById(R.id.txt_p5);
        txt_p6 = (TextView) view.findViewById(R.id.txt_p6);
    }

    private void clearPwd() {

        txt_p1.setText("");
        txt_p2.setText("");
        txt_p3.setText("");
        txt_p4.setText("");
        txt_p5.setText("");
        txt_p6.setText("");
    }

    private void setView() {
        loginUserInfo = CacheDataManager.getInstance().loadUser();
        dialog_btn_cancel.setOnClickListener(this);
        dialog_btn_enter.setOnClickListener(this);
        pay_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DebugLogs.e("CharSequence:" + s + "count" + count);
                dialog_btn_enter.setBackgroundResource(R.drawable.bg_dialog_btn_enter_n);
                dialog_btn_enter.setEnabled(false);
                clearPwd();
                switch (s.length()) {
                    case 6:
                        txt_p6.setText("*");
                        dialog_btn_enter.setBackgroundResource(R.drawable.bg_dialog_btn_enter);
                        dialog_btn_enter.setEnabled(true);
                    case 5:
                        txt_p5.setText("*");
                    case 4:
                        txt_p4.setText("*");
                    case 3:
                        txt_p3.setText("*");
                    case 2:
                        txt_p2.setText("*");
                    case 1:
                        txt_p1.setText("*");
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mCallback.onCancel("");
                    dismiss();
                    return true; // pretend we've processed it
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_btn_cancel:
                Utility.closeKeybord(pay_password, context);
                mCallback.onCancel("");
                dismiss();
                break;
            case R.id.dialog_btn_enter:

                transfer(touserid, Md5.md5(pay_password.getText().toString()), String.valueOf(money), remark);

                break;
        }
    }


    /**
     * 转账
     */
    private void transfer(String touserid, String payPassword, String money, String remark) {
        if (userInfoDialog == null) {
            userInfoDialog = new UserInfoDialog(context);
        }
        Map<String, String> params = new HashMap<>();
        params.put("userId", loginUserInfo.userid);
        params.put("token", loginUserInfo.token);
        params.put("toUserId", touserid);
        params.put("payPassword", payPassword);
        params.put("money", money);
        params.put("remark", remark);

        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {

                Map result = JsonUtil.fromJson(response, Map.class);
                if (result != null) {
                    if (result.get("code").toString().equals("6003")) {
                        Looper.prepare();
                        ToastUtils.showToast(getActivity(), R.string.pay_pwd_err);
                        Looper.loop();
                    }

                    else {
                        mCallback.onEnter(response);
                        dismiss();
                        Utility.closeKeybord(pay_password, context);
                    }
                }
            }
        };
        userInfoDialog.httpGet(CommonUrlConfig.transfer, params, callback);
    }
}
