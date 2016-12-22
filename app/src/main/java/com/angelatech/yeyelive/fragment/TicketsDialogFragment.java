package com.angelatech.yeyelive.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.RechargeActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.CommDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;

/**
 * User: cbl
 * Date: 2016/8/4
 * Time: 18:11
 * 门票 dialog
 */
@SuppressLint("ValidFragment")
public class TicketsDialogFragment extends DialogFragment implements View.OnClickListener {
    private View view;
    private TextView tv_cancel, tv_go_pay, tv_pay_coin;
    private BasicUserInfoDBModel loginUserInfo;
    private Context context;
    private Callback mCallback;
    private String ticket;
    private int roomid;
    private ChatRoom chatRoom;
    private int type ; //0 直播 1 录像

    public TicketsDialogFragment(Context mcontext, Callback callback, String mticket, int mroomid, int mtype) {
        this.context = mcontext;
        this.mCallback = callback;
        this.ticket = mticket;
        this.roomid = mroomid;
        this.type = mtype;
    }

    public interface Callback {
        void onCancel();

        void onEnter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        view = inflater.inflate(R.layout.dialog_tickets_pay, container, false);
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
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_go_pay = (TextView) view.findViewById(R.id.tv_go_pay);
        tv_pay_coin = (TextView) view.findViewById(R.id.tv_pay_coin);
    }

    private void setView() {
        chatRoom = new ChatRoom(context);
        tv_pay_coin.setText(ticket);
        tv_cancel.setOnClickListener(this);
        tv_go_pay.setOnClickListener(this);
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
            case R.id.tv_cancel:
                mCallback.onCancel();
                dismiss();
                break;
            case R.id.tv_go_pay:
                if (Integer.parseInt(loginUserInfo.diamonds) > Integer.parseInt(ticket)) {
                    if(type == 0) {
                        chatRoom.payTicketsIsIns(loginUserInfo.userid,
                                loginUserInfo.token,"toroomid", String.valueOf(roomid), callback);
                    }
                    else
                    {
                        chatRoom.payTicketsIsIns(loginUserInfo.userid,
                                loginUserInfo.token, "videoid",String.valueOf(roomid), callback);
                    }
                } else {
                    CommDialog commDialog = new CommDialog();
                    commDialog.CommDialog(getActivity(), getString(R.string.dialog_coin_lack_of_balance), true, new CommDialog.Callback() {
                        @Override
                        public void onCancel() {
                            mCallback.onCancel();
                        }

                        @Override
                        public void onOK() {
                            StartActivityHelper.jumpActivityDefault(context, RechargeActivity.class);
                        }
                    });
                }
                dismiss();
                break;
        }
    }

    /**
     * 支付门票回调
     */
    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onSuccess(String response) {
            Map map = JsonUtil.fromJson(response, Map.class);
            if (map != null) {
                if (HttpFunction.isSuc(map.get("code").toString())) {
                    mCallback.onEnter();

                } else {
                    mCallback.onCancel();
                    onBusinessFaild(map.get("code").toString());
                }
            }
        }

        @Override
        public void onFailure(Map<String, ?> errorMap) {
            super.onFailure(errorMap);
        }
    };
}
