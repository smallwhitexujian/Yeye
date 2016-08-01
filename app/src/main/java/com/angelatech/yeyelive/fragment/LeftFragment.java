package com.angelatech.yeyelive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.FansActivity;
import com.angelatech.yeyelive.activity.FocusOnActivity;
import com.angelatech.yeyelive.activity.LoginActivity;
import com.angelatech.yeyelive.activity.MainActivity;
import com.angelatech.yeyelive.activity.PicViewActivity;
import com.angelatech.yeyelive.activity.RechargeActivity;
import com.angelatech.yeyelive.activity.SettingActivity;
import com.angelatech.yeyelive.activity.UserInfoActivity;
import com.angelatech.yeyelive.activity.UserVideoActivity;
import com.angelatech.yeyelive.activity.function.CommDialog;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.PicViewModel;
import com.angelatech.yeyelive.service.IServiceHelper;
import com.angelatech.yeyelive.service.IServiceValues;
import com.angelatech.yeyelive.util.BroadCastHelper;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.UriHelper;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.will.common.string.json.JsonUtil;
import com.will.web.handle.HttpBusinessCallback;

import java.text.MessageFormat;
import java.util.Map;

/**
 * 我的界面
 */
public class LeftFragment extends BaseFragment {
    private final int MSG_LOAD_SUC = 1;
    private View view;
    private MainEnter mainEnter;
    private TextView id, intimacy, attention, fans, diamond, user_nick, user_sign, user_video;
    private RelativeLayout exitLayout, attentionLayout, fansLayout, settingLayout,
            layout_diamond, layout_video;
    private ImageView editImageView, sexImageView, iv_vip;
    private SimpleDraweeView userFace;
    private GestureDetector gestureDetector;

    private BasicUserInfoDBModel userInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_left_menu, container, false);
        initView();
        setView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        userInfo = CacheDataManager.getInstance().loadUser();
        load();
        ((MainActivity) getActivity()).registerFragmentTouch(gestureDetector);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initView() {
        gestureDetector = new GestureDetector(getActivity(), simpleOnGestureListener);
        mainEnter = ((MainActivity) getActivity()).getMainEnter();

        user_nick = (TextView) view.findViewById(R.id.user_nick);
        user_sign = (TextView) view.findViewById(R.id.user_sign);
        user_video = (TextView) view.findViewById(R.id.user_video);
        id = (TextView) view.findViewById(R.id.user_id);//用户id
        fans = (TextView) view.findViewById(R.id.user_fans);//粉丝
        attention = (TextView) view.findViewById(R.id.user_attention);//关注
        intimacy = (TextView) view.findViewById(R.id.user_intimacy);//亲密度
        diamond = (TextView) view.findViewById(R.id.user_diamond);

        exitLayout = (RelativeLayout) view.findViewById(R.id.exit_layout);
        fansLayout = (RelativeLayout) view.findViewById(R.id.fans_layout);
        attentionLayout = (RelativeLayout) view.findViewById(R.id.attention_layout);
        settingLayout = (RelativeLayout) view.findViewById(R.id.setting_layout);
        layout_diamond = (RelativeLayout) view.findViewById(R.id.layout_diamond);
        layout_video = (RelativeLayout) view.findViewById(R.id.layout_video);
        //
        editImageView = (ImageView) view.findViewById(R.id.btn_edit);
        sexImageView = (ImageView) view.findViewById(R.id.user_sex);
        userFace = (SimpleDraweeView) view.findViewById(R.id.user_face);
        iv_vip = (ImageView) view.findViewById(R.id.iv_vip);
    }


    private void setView() {

        exitLayout.setOnClickListener(this);
        fansLayout.setOnClickListener(this);
        attentionLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);

        editImageView.setOnClickListener(this);
        userFace.setOnClickListener(this);
        layout_diamond.setOnClickListener(this);
        layout_video.setOnClickListener(this);
        view.findViewById(R.id.backBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_layout:
                new CommDialog().CommDialog(getActivity(), getString(R.string.setting_exit_dialog), true, new CommDialog.Callback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOK() {
                        Intent exitIntent = IServiceHelper.getBroadcastIntent(IServiceValues.ACTION_CMD_WAY,
                                IServiceValues.CMD_EXIT_LOGIN);
                        BroadCastHelper.sendBroadcast(getActivity(), exitIntent);
                        StartActivityHelper.jumpActivity(getActivity(),
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK,
                                null, LoginActivity.class, null);
                    }
                });
                break;
            case R.id.attention_layout:
                StartActivityHelper.jumpActivityDefault(getActivity(), FocusOnActivity.class);
                break;
            case R.id.fans_layout:
                StartActivityHelper.jumpActivityDefault(getActivity(), FansActivity.class);
                break;
            case R.id.setting_layout:
                StartActivityHelper.jumpActivityDefault(getActivity(), SettingActivity.class);
                break;
            case R.id.btn_edit:
                if (userInfo != null) {
                    StartActivityHelper.jumpActivity(getActivity(), UserInfoActivity.class, userInfo);
                }
                break;
            case R.id.user_face:
                if (userInfo != null) {
                    PicViewModel picViewModel = new PicViewModel();
                    picViewModel.url = userInfo.headurl;
                    picViewModel.defaultPic = R.drawable.default_face_icon;
                    StartActivityHelper.jumpActivity(getContext(), PicViewActivity.class, picViewModel);
                }
                break;
            case R.id.layout_diamond:
                StartActivityHelper.jumpActivityDefault(getActivity(), RechargeActivity.class);
                break;
            case R.id.backBtn:
                ((MainActivity) getActivity()).closeMenu();
                break;
            case R.id.layout_video:
                StartActivityHelper.jumpActivityDefault(getActivity(), UserVideoActivity.class);
                break;
        }
    }

    private void load() {
        // LoadingDialog.showLoadingDialog(getActivity());
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                LoadingDialog.cancelLoadingDialog();
            }

            @Override
            public void onSuccess(String response) {
                LoadingDialog.cancelLoadingDialog();
                CommonListResult<BasicUserInfoDBModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<BasicUserInfoDBModel>>() {
                }.getType());
                if (datas != null && HttpFunction.isSuc(datas.code)) {
                    BasicUserInfoDBModel basicUserInfoDBModel = datas.data.get(0);
                    fragmentHandler.obtainMessage(MSG_LOAD_SUC, basicUserInfoDBModel).sendToTarget();
                }
            }
        };
        if (userInfo != null) {
            mainEnter.loadUserInfo(CommonUrlConfig.UserInformation, userInfo.userid, userInfo.userid, userInfo.token, callback);
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_LOAD_SUC:
                BasicUserInfoDBModel basicUserInfoDBModel = (BasicUserInfoDBModel) msg.obj;
                if (isAdded()) {
                    id.setText(MessageFormat.format("{0}{1}", getString(R.string.ID), basicUserInfoDBModel.idx));
                    intimacy.setText(MessageFormat.format("{0}{1}", getString(R.string.intimacy), basicUserInfoDBModel.Intimacy));

                    if (basicUserInfoDBModel.sign == null || "".equals(basicUserInfoDBModel.sign)) {
                        user_sign.setText(getString(R.string.default_sign));
                    } else {
                        user_sign.setText(basicUserInfoDBModel.sign);
                    }
                }
                if (basicUserInfoDBModel.nickname != null && !"".equals(basicUserInfoDBModel.nickname)) {
                    user_nick.setText(basicUserInfoDBModel.nickname);
                }

                attention.setText(basicUserInfoDBModel.followNum);
                fans.setText(String.format("%s", basicUserInfoDBModel.fansNum));
                diamond.setText(String.format("%s", basicUserInfoDBModel.diamonds));
                userFace.setImageURI(UriHelper.obtainUri(basicUserInfoDBModel.headurl));
                user_video.setText(String.format("%s", basicUserInfoDBModel.videoNum));
                if (Constant.SEX_MALE.equals(basicUserInfoDBModel.sex)) {
                    sexImageView.setImageResource(R.drawable.icon_information_boy);
                } else {
                    sexImageView.setImageResource(R.drawable.icon_information_girl);
                }
                if (basicUserInfoDBModel.isv.equals("1")) {
                    iv_vip.setVisibility(View.VISIBLE);
                } else {
                    iv_vip.setVisibility(View.GONE);
                }
                CacheDataManager.getInstance().update(BaseKey.USER_HEAD_URL, basicUserInfoDBModel.headurl, basicUserInfoDBModel.userid);
                CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, basicUserInfoDBModel.diamonds, basicUserInfoDBModel.userid);
        }
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float distance = e2.getX() - e1.getX();
                if (distance < 0 && Math.abs(distance) > 100) {
                    ((MainActivity) getActivity()).closeMenu();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };


}
