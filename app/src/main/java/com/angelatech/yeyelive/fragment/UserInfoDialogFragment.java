package com.angelatech.yeyelive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.PicViewActivity;
import com.angelatech.yeyelive.activity.base.WithBroadCastActivity;
import com.angelatech.yeyelive.activity.function.FocusFans;
import com.angelatech.yeyelive.activity.function.UserControl;
import com.angelatech.yeyelive.activity.function.UserInfoDialog;
import com.angelatech.yeyelive.adapter.MyFragmentPagerAdapter;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.handler.CommonDoHandler;
import com.angelatech.yeyelive.handler.CommonHandler;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.PicViewModel;
import com.angelatech.yeyelive.model.SearchItemModel;
import com.angelatech.yeyelive.util.BroadCastHelper;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.UriHelper;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.view.ActionSheetDialog;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserInfoDialogFragment extends DialogFragment implements View.OnClickListener, CommonDoHandler {
    public static final int MSG_DISMISS = 0xff;
    private static final int MSG_LOAD_SUC = 1;
    private final int MSG_SET_FOLLOW = 5;
    private final int MSG_LOAD_STATUS = 6;
    private final int MSG_NOTICE = 7;
    private final int MSG_PULL_BLACKLIST_SUC = 8;
    private final int MSG_REPORT_SUC = 10;

    private SimpleDraweeView userface;
    private TextView usernick, intimacy, usersign, fansNum, fouceNum, btn_outUser, liveBtn, user_id;
    private ImageView closeImageView, userSex, attentionsBtn, ringBtn, leftIcon,
            rightIcon, giftBtn, btnUserControl, iv_vip;
    private LinearLayout fansLayout, fouceLayout, fansAndFouceLayout, userinfoLayout;
    private RelativeLayout noDataLayout, bottomLayout;
    private BasicUserInfoModel baseInfo;
    private UserInfoDialog userInfoDialog;
    private boolean isOpen = false;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ViewPager mviewPager;
    private View dividerView;
    private BasicUserInfoDBModel searchUserInfo;//通过userid从接口搜索到的用户信息
    private String isFollowCode;
    private String isNoticeCode;

    private View view;
    private CommonHandler<UserInfoDialogFragment> uiHandler;
    private ICallBack callBack;
    private BasicUserInfoDBModel loginUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        view = inflater.inflate(R.layout.dialog_userinfo, container, false);
        LoadingDialog.showSysLoadingDialog(getActivity(), "");
        initView();
        setView();
        uiHandler = new CommonHandler<>(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        loginUser = CacheDataManager.getInstance().loadUser();
        userface = (SimpleDraweeView) view.findViewById(R.id.user_face);
        usernick = (TextView) view.findViewById(R.id.user_nick);
        intimacy = (TextView) view.findViewById(R.id.user_intimacy);
        usersign = (TextView) view.findViewById(R.id.user_sign);
        fansNum = (TextView) view.findViewById(R.id.user_fans_num);
        fouceNum = (TextView) view.findViewById(R.id.user_fouse_num);
        user_id = (TextView) view.findViewById(R.id.user_id);
        btn_outUser = (TextView) view.findViewById(R.id.btn_outUser);
        btnUserControl = (ImageView) view.findViewById(R.id.btn_user_control);
        closeImageView = (ImageView) view.findViewById(R.id.btn_close);
        userSex = (ImageView) view.findViewById(R.id.user_sex);
        attentionsBtn = (ImageView) view.findViewById(R.id.attentions_btn);
        liveBtn = (TextView) view.findViewById(R.id.live_btn);
        ringBtn = (ImageView) view.findViewById(R.id.ring_btn);
        fansLayout = (LinearLayout) view.findViewById(R.id.fans_layout);
        fouceLayout = (LinearLayout) view.findViewById(R.id.attention_layout);
        mviewPager = (ViewPager) view.findViewById(R.id.data_layout);
        leftIcon = (ImageView) view.findViewById(R.id.left_icon);
        rightIcon = (ImageView) view.findViewById(R.id.right_icon);
        giftBtn = (ImageView) view.findViewById(R.id.gift_btn);
        iv_vip = (ImageView) view.findViewById(R.id.iv_vip);
        dividerView = view.findViewById(R.id.divider_bg);
        noDataLayout = (RelativeLayout) view.findViewById(R.id.no_data_layout);
        bottomLayout = (RelativeLayout) view.findViewById(R.id.bottom_layout);
        fansAndFouceLayout = (LinearLayout) view.findViewById(R.id.fans_and_follows_layout);
        userinfoLayout = (LinearLayout) view.findViewById(R.id.base_user_info_layout);

        if (baseInfo != null) {
            Fragment followFragment = new RelationFragment();
            ((RelationFragment) followFragment).setFuserid(baseInfo.Userid);
            ((RelationFragment) followFragment).setType(FocusFans.TYPE_FOCUS);
            Fragment fansFragment = new RelationFragment();
            ((RelationFragment) fansFragment).setFuserid(baseInfo.Userid);
            ((RelationFragment) fansFragment).setType(FocusFans.TYPE_FANS);
            fragments.add(fansFragment);
            fragments.add(followFragment);
        }
    }

    private void setView() {
        closeImageView.setOnClickListener(this);
        userface.setOnClickListener(this);
        fansLayout.setOnClickListener(this);
        fouceLayout.setOnClickListener(this);
        attentionsBtn.setOnClickListener(this);
        liveBtn.setOnClickListener(this);
        ringBtn.setOnClickListener(this);
        giftBtn.setOnClickListener(this);
        btn_outUser.setOnClickListener(this);
        btnUserControl.setOnClickListener(this);
        MyFragmentPagerAdapter simpleFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        mviewPager.setAdapter(simpleFragmentPagerAdapter);
        noDataLayout.findViewById(R.id.no_data_icon).setOnClickListener(this);
        mviewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    leftIcon.setVisibility(View.VISIBLE);
                    rightIcon.setVisibility(View.GONE);
                } else {
                    leftIcon.setVisibility(View.GONE);
                    rightIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
        mviewPager.setCurrentItem(0);
        if (baseInfo != null) {
            load();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attention_layout:
                if (isOpen && mviewPager.getCurrentItem() == 1) {
                    closeDataView();
                } else {
                    openDataView(1);
                }
                break;
            case R.id.fans_layout:
                if (isOpen && mviewPager.getCurrentItem() == 0) {
                    closeDataView();
                } else {
                    openDataView(0);
                }
                break;
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.live_btn://点击直播按钮
                if (App.chatRoomApplication != null) {
                    callBack.closeLive();
                } else {
                    App.roomModel.setId(0);
                    App.roomModel.setRoomType(App.LIVE_PREVIEW);
                    App.roomModel.setUserInfoDBModel(loginUser);
                    StartActivityHelper.jumpActivity(getActivity(), ChatRoomActivity.class, App.roomModel);
                }
                dismiss();
                break;
            case R.id.attentions_btn:
                if (baseInfo != null) {
                    //通知直播页面
                    if (callBack != null) {
                        callBack.follow(isFollowCode, baseInfo.Userid);
                    }
                    doFocus(baseInfo.Userid, isFollowCode);
                }
                break;
            case R.id.ring_btn:
                if (baseInfo != null) {
                    userNoticeEdit(baseInfo.Userid);
                }
                break;
            case R.id.no_data_icon:
                if (baseInfo != null) {
                    load();
                }
                break;
            case R.id.btn_outUser:
                if (callBack != null) {
                    callBack.kickedOut(baseInfo.Userid);
                }
                dismiss();
                break;
            case R.id.btn_user_control:
                ActionSheetDialog dialog = new ActionSheetDialog(getActivity());
                dialog.builder();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.addSheetItem(getString(R.string.userinfo_dialog_do_pull_blacklist), ActionSheetDialog.SheetItemColor.BLACK_222222,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                LoadingDialog.showLoadingDialog(getActivity());
                                HttpBusinessCallback callback = new HttpBusinessCallback() {
                                    @Override
                                    public void onFailure(Map<String, ?> errorMap) {

                                    }

                                    @Override
                                    public void onSuccess(String response) {
                                        Map map = JsonUtil.fromJson(response, Map.class);
                                        if (map != null) {
                                            if (HttpFunction.isSuc((String) map.get("code"))) {
                                                uiHandler.obtainMessage(MSG_PULL_BLACKLIST_SUC).sendToTarget();
                                            } else {
                                                onBusinessFaild((String) map.get("code"));
                                            }
                                        }
                                        LoadingDialog.cancelLoadingDialog();
                                    }
                                };
                                userInfoDialog.ctlBlacklist(loginUser.userid, loginUser.token, baseInfo.Userid, UserControl.PULL_TO_BLACKLIST, callback);

                            }
                        }).addSheetItem(getString(R.string.userinfo_dialog_do_report), ActionSheetDialog.SheetItemColor.BLACK_222222,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                LoadingDialog.showLoadingDialog(getActivity());
                                HttpBusinessCallback callback = new HttpBusinessCallback() {
                                    @Override
                                    public void onFailure(Map<String, ?> errorMap) {

                                    }

                                    @Override
                                    public void onSuccess(String response) {
                                        Map map = JsonUtil.fromJson(response, Map.class);
                                        if (map != null && HttpFunction.isSuc((String) map.get("code"))) {
                                            uiHandler.obtainMessage(MSG_REPORT_SUC).sendToTarget();
                                        }
                                    }
                                };
                                userInfoDialog.report(loginUser.userid, loginUser.token, UserControl.SOURCE_REPORT + "", baseInfo.Userid, "", callback);
                            }
                        });
                dialog.show();
                break;
            case R.id.gift_btn:
                if (callBack != null) {
                    callBack.sendGift(baseInfo);
                }
                dismiss();
                break;
            case R.id.user_face:
                if (baseInfo != null) {
                    PicViewModel picViewModel = new PicViewModel();
                    picViewModel.url = baseInfo.headurl;
                    picViewModel.defaultPic = R.drawable.default_face_icon;
                    StartActivityHelper.jumpActivity(getContext(), PicViewActivity.class, picViewModel);
                }
                break;
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_LOAD_SUC:
                LoadingDialog.cancelLoadingDialog();
                searchUserInfo = (BasicUserInfoDBModel) msg.obj;
                if (isAdded()) {
                    setUI(searchUserInfo);
                }
                loadStatus();
                break;
            case MSG_LOAD_STATUS:
                if (loginUser != null && !loginUser.userid.equals(baseInfo.Userid)) {
                    if (userInfoDialog.isFollow(isFollowCode)) {
                        if (userInfoDialog.isNotice(isNoticeCode)) {
                            ringBtn.setImageResource(R.drawable.btn_information_notification_n);
                        } else {
                            ringBtn.setImageResource(R.drawable.btn_information_notification_s);
                        }
                        ringBtn.setVisibility(View.VISIBLE);
                        attentionsBtn.setImageResource(R.drawable.btn_information_attention_s);
                    } else {
                        ringBtn.setVisibility(View.GONE);
                        attentionsBtn.setImageResource(R.drawable.btn_information_attention_n);
                    }
                    attentionsBtn.setVisibility(View.VISIBLE);
                }
                break;
            case MSG_SET_FOLLOW:
                if (userInfoDialog.isFollow(isFollowCode)) {
                    attentionsBtn.setImageResource(R.drawable.btn_information_attention_s);
                } else {
                    attentionsBtn.setImageResource(R.drawable.btn_information_attention_n);
                }
                break;

            case MSG_NOTICE:
                if (userInfoDialog.isNotice(isNoticeCode)) {
                    ringBtn.setImageResource(R.drawable.btn_information_notification_n);
                } else {
                    ringBtn.setImageResource(R.drawable.btn_information_notification_s);
                }
                break;
            case MSG_DISMISS:
                dismiss();
                break;
            case MSG_PULL_BLACKLIST_SUC:
                ToastUtils.showToast(getActivity(), getString(R.string.userinfo_dialog_pull_blacklist_suc));
                break;
            case MSG_REPORT_SUC:
                ToastUtils.showToast(getActivity(), getString(R.string.userinfo_dialog_report_suc));
                break;
        }
    }

    /**
     * 查询 用户信息
     */
    private void load() {
        if (userInfoDialog == null) {
            userInfoDialog = new UserInfoDialog(getActivity());
        }

        Map<String, String> params = new HashMap<>();
        params.put("userid", loginUser.userid);
        params.put("token", loginUser.token);
        params.put("touserid", baseInfo.Userid);

        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {

            }

            @Override
            public void onSuccess(String response) {
                CommonListResult<BasicUserInfoDBModel> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<BasicUserInfoDBModel>>() {
                }.getType());
                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        if (results.hasData()) {
                            uiHandler.obtainMessage(MSG_LOAD_SUC, results.data.get(0)).sendToTarget();
                        }
                    } else {
                        onBusinessFaild(results.code);
                    }
                }
            }
        };
        userInfoDialog.httpGet(CommonUrlConfig.UserInformation, params, callback);
    }

    /**
     * 获取用户的状态（是否已经被关注）
     */
    private void loadStatus() {
        HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                Map map = JsonUtil.fromJson(response, Map.class);
                if (map != null) {
                    if (HttpFunction.isSuc((String) map.get("code"))) {
                        Map data = (Map) map.get("data");
                        isFollowCode = (String) data.get("isfollow");
                        isNoticeCode = (String) data.get("isnotice");
                        uiHandler.sendEmptyMessage(MSG_LOAD_STATUS);
                    } else {
                        onBusinessFaild((String) map.get("code"));
                    }
                }
            }
        };
        userInfoDialog.UserIsFollow(CommonUrlConfig.UserIsFollow, loginUser.token, loginUser.userid, baseInfo.Userid, httpCallback);
    }

    private void openDataView(int item) {
        mviewPager.setCurrentItem(item);
        if (item == 1) {
            leftIcon.setVisibility(View.GONE);
            rightIcon.setVisibility(View.VISIBLE);
        } else {
            leftIcon.setVisibility(View.VISIBLE);
            rightIcon.setVisibility(View.GONE);
        }
        bottomLayout.setVisibility(View.GONE);
        intimacy.setVisibility(View.GONE);
        usersign.setVisibility(View.GONE);
        btnUserControl.setVisibility(View.GONE);
        dividerView.setVisibility(View.VISIBLE);
        mviewPager.setVisibility(View.VISIBLE);
        isOpen = true;
    }

    private void closeDataView() {
        bottomLayout.setVisibility(View.VISIBLE);
        intimacy.setVisibility(View.VISIBLE);
        usersign.setVisibility(View.VISIBLE);
        btnUserControl.setVisibility(View.GONE);
        leftIcon.setVisibility(View.GONE);
        rightIcon.setVisibility(View.GONE);
        dividerView.setVisibility(View.GONE);
        mviewPager.setVisibility(View.GONE);
        isOpen = false;
    }

    /**
     * 1、如果是自己：
     * a、没有踢人，拉黑举报功能，没有关注开启通知功能
     * ba、如果在房间：可以退出房间
     * bb、如果不在房间：可以开播
     * <p/>
     * 2、不是自己：
     * a、如果是房主：有踢人功能，没有举报拉黑，可以关注
     * ab、如果不是房主：举报拉黑，可以关注
     */
    private void setUI(BasicUserInfoDBModel user) {
        if (user == null) {
            lockLayout();
            return;
        }
        unlockLayout();
        if (user.nickname != null) {
            usernick.setText(user.nickname);
        }
        userface.setImageURI(UriHelper.obtainUri(VerificationUtil.getImageUrl100(user.headurl)));
        if (user.Intimacy != null) {
            intimacy.setText(String.format("%s%s", getActivity().getString(R.string.intimacy), user.Intimacy));
        }
        if (user.sign == null || "".equals(user.sign)) {
            usersign.setText(getString(R.string.default_sign));
        } else {
            usersign.setText(user.sign);
        }
        if (Constant.SEX_MALE.equals(user.sex)) {
            userSex.setImageResource(R.drawable.icon_information_boy);
        } else {
            userSex.setImageResource(R.drawable.icon_information_girl);
        }
        user_id.setText(MessageFormat.format("{0}{1}", getString(R.string.ID), user.idx));
        fansNum.setText(user.fansNum);
        fouceNum.setText(user.followNum);

        if (user.isv.equals("1")) {
            iv_vip.setVisibility(View.VISIBLE);
        } else {
            iv_vip.setVisibility(View.GONE);
        }

        if (loginUser.userid.equals(user.userid)) {
            btn_outUser.setVisibility(View.GONE);
            btnUserControl.setVisibility(View.GONE);
            giftBtn.setVisibility(View.GONE);
            attentionsBtn.setVisibility(View.GONE);
            ringBtn.setVisibility(View.GONE);
            liveBtn.setVisibility(View.VISIBLE);
            if (App.chatRoomApplication != null) {
                liveBtn.setText(getString(R.string.userinfo_dialog_close_live));
            } else {
                liveBtn.setText(getString(R.string.userinfo_dialog_live));
            }
        } else {
            if (App.chatRoomApplication != null) {
                giftBtn.setVisibility(View.VISIBLE);
            } else {
                giftBtn.setVisibility(View.GONE);
            }
            if (baseInfo.isout || loginUser.role == 1) {//直播者 打开 踢人显示
                btnUserControl.setVisibility(View.GONE);
                btn_outUser.setVisibility(View.VISIBLE);
            } else {
                btn_outUser.setVisibility(View.GONE);
                btnUserControl.setVisibility(View.VISIBLE);
            }
        }
    }

    private void doFocus(final String fuserid, final String isfollow) {

        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                DebugLogs.e("===============失败了");
            }

            @Override
            public void onSuccess(String response) {
                CommonModel results = JsonUtil.fromJson(response, CommonModel.class);
                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        isFollowCode = getOppositeFollow(isfollow);
                        uiHandler.obtainMessage(MSG_SET_FOLLOW).sendToTarget();

                        Intent intent = new Intent();
                        intent.setAction(WithBroadCastActivity.ACTION_WITH_BROADCAST_ACTIVITY);
                        SearchItemModel searchItemModel = new SearchItemModel();
                        searchItemModel.isfollow = getOppositeFollow(isfollow);
                        searchItemModel.userid = fuserid;
                        intent.putExtra(TransactionValues.UI_2_UI_KEY_OBJECT, searchItemModel);
                        BroadCastHelper.sendBroadcast(getActivity(), intent);
                    }
                }

            }
        };
        try {
            int isfollwValue = Integer.parseInt(isfollow);
            userInfoDialog.UserFollow(CommonUrlConfig.UserFollow, loginUser.token, loginUser.userid, fuserid, isfollwValue, callback);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void userNoticeEdit(String touserid) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                DebugLogs.e("===============失败了");
            }

            @Override
            public void onSuccess(String response) {
                Map map = JsonUtil.fromJson(response, Map.class);
                if (map != null) {
                    if (HttpFunction.isSuc((String) map.get("code"))) {
                        isNoticeCode = getOppositeNotice(isNoticeCode);
                        uiHandler.obtainMessage(MSG_NOTICE).sendToTarget();
                    } else {
                        onBusinessFaild((String) map.get("code"));
                    }
                }
            }
        };
        userInfoDialog.userNoticeEdit(CommonUrlConfig.UserNoticeEdit, loginUser.token, loginUser.userid, touserid, callback);

    }


    private String getOppositeFollow(String src) {
        if (UserInfoDialog.HAVE_FOLLOW.equals(src)) {
            return UserInfoDialog.HAVE_NO_FOLLOW;
        }
        return UserInfoDialog.HAVE_FOLLOW;
    }

    private String getOppositeNotice(String src) {
        if (UserInfoDialog.HAVE_NO_NOTICE.equals(src)) {
            return UserInfoDialog.HAVE_NOTICE;
        }
        return UserInfoDialog.HAVE_NO_NOTICE;
    }

    //隐藏
    private void lockLayout() {
        noDataLayout.setVisibility(View.VISIBLE);
        fansAndFouceLayout.setVisibility(View.GONE);
        userinfoLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        userface.setVisibility(View.GONE);
        btn_outUser.setVisibility(View.GONE);
        btnUserControl.setVisibility(View.GONE);

        ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(R.string.no_data_no_info);
        noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);

    }

    private void unlockLayout() {
        noDataLayout.setVisibility(View.GONE);
        fansAndFouceLayout.setVisibility(View.VISIBLE);
        userinfoLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        userface.setVisibility(View.VISIBLE);
        btn_outUser.setVisibility(View.VISIBLE);
        btnUserControl.setVisibility(View.VISIBLE);
    }

    public void setUserInfoModel(BasicUserInfoModel userInfoModel) {
        this.baseInfo = userInfoModel;
    }

    public void setCallBack(ICallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 回调接口
     */
    public interface ICallBack {
        void sendGift(BasicUserInfoModel userInfoDBModel);

        void follow(String val, String userId);//关注操作

        void kickedOut(String userId);

        void closeLive();
    }
}