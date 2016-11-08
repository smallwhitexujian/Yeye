package com.angelatech.yeyelive.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.mediaplayer.handler.CommonDoHandler;
import com.angelatech.yeyelive.mediaplayer.handler.CommonHandler;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.Utility;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;

/**
 * User: cbl
 * Date: 2016/8/4
 * Time: 18:11
 * 密码 dialog
 */
public class LockChooseDialogFragment extends DialogFragment implements View.OnClickListener {

    private View view;
    private EditText lock_password;
    private TextView dialog_btn_cancel, dialog_btn_enter, txt_p1, txt_p2, txt_p3, txt_p4;
    private BasicUserInfoDBModel loginUserInfo;
    private final int MSG_ENTER_ROOM = 22;
    private Context context;
    private String password = "";
    private int type;  //0 设置密码  1 进房间的时候录入密码

    public LockChooseDialogFragment(Context mcontext, Callback callback, String mPwd, int mtype) {
        this.context = mcontext;
        this.mCallback = callback;
        this.password = mPwd;
        this.type = mtype;
    }

    public interface Callback {
        void onCancel();

        void onEnter(String pwd);
    }

    private Callback mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        view = inflater.inflate(R.layout.dialog_lock_choose, container, false);
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
        Utility.openKeybord(lock_password, getActivity());
        lock_password = (EditText) view.findViewById(R.id.lock_password);
        dialog_btn_cancel = (TextView) view.findViewById(R.id.dialog_btn_cancel);
        dialog_btn_enter = (TextView) view.findViewById(R.id.dialog_btn_enter);
        txt_p1 = (TextView) view.findViewById(R.id.txt_p1);
        txt_p2 = (TextView) view.findViewById(R.id.txt_p2);
        txt_p3 = (TextView) view.findViewById(R.id.txt_p3);
        txt_p4 = (TextView) view.findViewById(R.id.txt_p4);
    }

    private void clearPwd() {
        txt_p1.setText("");
        txt_p2.setText("");
        txt_p3.setText("");
        txt_p4.setText("");
    }

    private void setView() {
        dialog_btn_cancel.setOnClickListener(this);
        dialog_btn_enter.setOnClickListener(this);
        lock_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DebugLogs.e("CharSequence:" + s + "count" + count);
                clearPwd();
                dialog_btn_enter.setBackgroundResource(R.drawable.bg_dialog_btn_enter_n);
                dialog_btn_enter.setEnabled(false);
                switch (s.length()) {
                    case 1:
                        txt_p1.setText(s.subSequence(0, 1));
                        break;
                    case 2:
                        txt_p1.setText(s.subSequence(0, 1));
                        txt_p2.setText(s.subSequence(1, 2));
                        break;
                    case 3:
                        txt_p1.setText(s.subSequence(0, 1));
                        txt_p2.setText(s.subSequence(1, 2));
                        txt_p3.setText(s.subSequence(2, 3));
                        break;
                    case 4:
                        txt_p1.setText(s.subSequence(0, 1));
                        txt_p2.setText(s.subSequence(1, 2));
                        txt_p3.setText(s.subSequence(2, 3));
                        txt_p4.setText(s.subSequence(3, 4));
                        dialog_btn_enter.setBackgroundResource(R.drawable.bg_dialog_btn_enter);
                        dialog_btn_enter.setEnabled(true);
                        break;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (type == 0 && password != null && !password.isEmpty()) {
            lock_password.setText(password);
        }

        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mCallback.onCancel();
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
                Utility.closeKeybord(lock_password, getActivity());
                mCallback.onCancel();
                dismiss();
                break;
            case R.id.dialog_btn_enter:

                if (type == 0) {
                    Utility.closeKeybord(lock_password, getActivity());
                    mCallback.onEnter(lock_password.getText().toString());
                    dismiss();
                } else {
                    if (lock_password.getText().toString().equals(password)) {
                        Utility.closeKeybord(lock_password, getActivity());
                        mCallback.onEnter(lock_password.getText().toString());
                        dismiss();
                    } else {
                        ToastUtils.showToast(context, R.string.roompwd_err);
                    }
                }
                break;
        }
    }
}
