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
import com.angelatech.yeyelive.activity.base.WithBroadCastActivity;
import com.angelatech.yeyelive.activity.function.FocusFans;
import com.angelatech.yeyelive.activity.function.UserControl;
import com.angelatech.yeyelive.activity.function.UserInfoDialog;
import com.angelatech.yeyelive.adapter.MyFragmentPagerAdapter;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.handler.CommonDoHandler;
import com.angelatech.yeyelive.handler.CommonHandler;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.model.SearchItemModel;
import com.angelatech.yeyelive.model.UserInfoModel;
import com.angelatech.yeyelive.util.BroadCastHelper;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.UriHelper;
import com.angelatech.yeyelive.view.ActionSheetDialog;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.string.json.JsonUtil;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

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
    private TextView usernick, intimacy, usersign, fansNum, fouceNum, btn_outUser, liveBtn;
    private ImageView closeImageView, userSex, attentionsBtn, ringBtn, leftIcon,
            rightIcon, giftBtn, btnUserControl, iv_vip;
    private LinearLayout fansLayout, fouceLayout, fansAndFouceLayout, userinfoLayout;
    private RelativeLayout noDataLayout, bottomLayout;
    private UserInfoModel info;
    private UserInfoDialog userInfoDialog;
    private boolean isOpen = false;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ViewPager mviewPager;
    private View dividerView;
    private BasicUserInfoDBModel searchUserInfo;//通过userid从接口搜索到的用户信息
    private String isFollowCode;
    private String isNoticeCode;
    private BasicUserInfoDBModel userInfo = CacheDataManager.getInstance().loadUser();
    private View view;
    private CommonHandler<UserInfoDialogFragment> uiHandler;

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
        userface = (SimpleDraweeView) view.findViewById(R.id.user_face);
        usernick = (TextView) view.findViewById(R.id.user_nick);
        intimacy = (TextView) view.findViewById(R.id.user_intimacy);
        usersign = (TextView) view.findViewById(R.id.user_sign);
        fansNum = (TextView) view.findViewById(R.id.user_fans_num);
        fouceNum = (TextView) view.findViewById(R.id.user_fouse_num);
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

        if (info != null) {
            Fragment followFragment = new RelationFragment();
            ((RelationFragment) followFragment).setFuserid(info.userid);
            ((RelationFragment) followFragment).setType(FocusFans.TYPE_FOCUS);
            Fragment fansFragment = new RelationFragment();
            ((RelationFragment) fansFragment).setFuserid(info.userid);
            ((RelationFragment) fansFragment).setType(FocusFans.TYPE_FANS);
            fragments.add(fansFragment);
            fragments.add(followFragment);
        }
    }

    private void setView() {
        closeImageView.setOnClickListener(this);
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
        if (info != null) {
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
                if (isInChatRoom()) {
                    ((ChatRoomActivity) getActivity()).CloseLiveDialog();
                } else {
                    RoomModel roomModel = new RoomModel();
                    roomModel.setId(0);
                    roomModel.setRoomType(App.LIVE_PREVIEW);
                    roomModel.setUserInfoDBModel(CacheDataManager.getInstance().loadUser());
                    StartActivityHelper.jumpActivity(getActivity(), ChatRoomActivity.class, roomModel);
                }
                dismiss();
                break;
            case R.id.attentions_btn:
                if (info != null) {
                    //通知直播页面
                    if (ChatRoomActivity.roomModel != null && ChatRoomActivity.roomModel.getUserInfoDBModel() != null
                            && ChatRoomActivity.roomModel.getUserInfoDBModel().userid.equals(info.userid)) {

                        if (isFollowCode.equals("0")) {
                            CallFragment.instance.followHandle.sendEmptyMessage(1);
                        } else {
                            CallFragment.instance.followHandle.sendEmptyMessage(0);
                        }
                    }

                    doFocus(info.userid, isFollowCode);
                }
                break;
            case R.id.ring_btn:
                if (info != null) {
                    userNoticeEdit(info.userid);
                }
                break;
            case R.id.no_data_icon:
                if (info != null) {
                    load();
                }
                break;
            case R.id.btn_outUser:
                ChatRoomActivity.serviceManager.kickedOut(info.userid);
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
                                userInfoDialog.ctlBlacklist(userInfo.userid, userInfo.token, info.userid, UserControl.PULL_TO_BLACKLIST, callback);

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
                                userInfoDialog.report(userInfo.userid, userInfo.token, UserControl.SOURCE_REPORT + "", info.userid, "", callback);
                            }
                        });
                dialog.show();
                break;
            case R.id.gift_btn:
                if (isInChatRoom()) {
                    ((ChatRoomActivity) getActivity()).openGiftLayout();
                }
                dismiss();
                break;
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_LOAD_SUC:
                LoadingDialog.cancelLoadingDialog();
                searchUserInfo = (BasicUserInfoDBModel) msg.obj;
                loadStatus();
                break;
            case MSG_SET_FOLLOW:
                if (userInfoDialog.isFollow(isFollowCode)) {
                    attentionsBtn.setImageResource(R.drawable.btn_information_attention_s);
                    ringBtn.setVisibility(View.VISIBLE);

                } else {
                    attentionsBtn.setImageResource(R.drawable.btn_information_attention_n);
                    ringBtn.setVisibility(View.GONE);
                }
                break;
            case MSG_LOAD_STATUS:
                if (isAdded()) {
                    setUI(searchUserInfo);
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

    private void load() {
        if (userInfoDialog == null) {
            userInfoDialog = new UserInfoDialog(getActivity());
        }

        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        params.put("touserid", info.userid);

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
                        onBusinessFaild(results.code, response);
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
        if (searchUserInfo == null) {
            return;
        }
        HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
//                Logger.e("======" + response);
                Map map = JsonUtil.fromJson(response, Map.class);
                if (map != null) {
                    if (HttpFunction.isSuc((String) map.get("code"))) {
                        Map data = (Map) map.get("data");
                        isFollowCode = (String) data.get("isfollow");
                        isNoticeCode = (String) data.get("isnotice");
                        uiHandler.obtainMessage(MSG_LOAD_STATUS).sendToTarget();
                    } else {
                        onBusinessFaild((String) map.get("code"));
                    }
                }
            }
        };
        userInfoDialog.UserIsFollow(CommonUrlConfig.UserIsFollow, userInfo.token, userInfo.userid, searchUserInfo.userid, httpCallback);
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
        userface.setVisibility(View.GONE);
        iv_vip.setVisibility(View.GONE);
        intimacy.setVisibility(View.GONE);
        usersign.setVisibility(View.GONE);
        btnUserControl.setVisibility(View.GONE);

        dividerView.setVisibility(View.VISIBLE);
        mviewPager.setVisibility(View.VISIBLE);
        isOpen = true;
    }

    private void closeDataView() {
        userface.setVisibility(View.VISIBLE);
        iv_vip.setVisibility(View.VISIBLE);
        intimacy.setVisibility(View.VISIBLE);
        usersign.setVisibility(View.VISIBLE);
        if (isHost()) {
            btnUserControl.setVisibility(View.GONE);
        } else {
            btnUserControl.setVisibility(View.VISIBLE);
        }
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
    private void setUI(BasicUserInfoDBModel userInfo) {
        if (userInfo == null) {
            lockLayout();
            return;
        }
        unlockLayout();
        BasicUserInfoDBModel userInfoDBModel = CacheDataManager.getInstance().loadUser();
        if (userInfo.nickname != null) {
            usernick.setText(userInfo.nickname);
        }
        userface.setImageURI(UriHelper.obtainUri(userInfo.headurl));
        if (userInfo.Intimacy != null) {
            intimacy.setText(String.format("%s%s", getActivity().getString(R.string.intimacy), userInfo.Intimacy));
        }
        if (userInfo.sign == null || "".equals(userInfo.sign)) {
            usersign.setText(getString(R.string.default_sign));
        } else {
            usersign.setText(userInfo.sign);
        }
        if (Constant.SEX_MALE.equals(userInfo.sex)) {
            userSex.setImageResource(R.drawable.icon_information_boy);
        } else {
            userSex.setImageResource(R.drawable.icon_information_girl);
        }
        fansNum.setText(userInfo.fansNum);
        fouceNum.setText(userInfo.followNum);
        if (userInfoDBModel.userid.equals(info.userid)) {
            attentionsBtn.setVisibility(View.GONE);
            ringBtn.setVisibility(View.GONE);
            liveBtn.setVisibility(View.VISIBLE);
        } else {
            attentionsBtn.setVisibility(View.VISIBLE);
            ringBtn.setVisibility(View.VISIBLE);
            liveBtn.setVisibility(View.GONE);
        }
        if (userInfo.isv.equals("1")) {
            iv_vip.setVisibility(View.VISIBLE);
        } else {
            iv_vip.setVisibility(View.GONE);
        }

        if (userInfoDialog.isNotice(isNoticeCode)) {
            ringBtn.setImageResource(R.drawable.btn_information_notification_n);
        } else {
            ringBtn.setImageResource(R.drawable.btn_information_notification_s);
        }
        if (userInfoDialog.isFollow(isFollowCode)) {
            attentionsBtn.setImageResource(R.drawable.btn_information_attention_s);
            ringBtn.setVisibility(View.VISIBLE);
        } else {
            attentionsBtn.setImageResource(R.drawable.btn_information_attention_n);
            ringBtn.setVisibility(View.GONE);
        }


        if (isSelf()) {
            btn_outUser.setVisibility(View.GONE);
            btnUserControl.setVisibility(View.GONE);
            giftBtn.setVisibility(View.GONE);
            attentionsBtn.setVisibility(View.GONE);
            ringBtn.setVisibility(View.GONE);

            liveBtn.setVisibility(View.VISIBLE);
            if (isInChatRoom()) {
                liveBtn.setText(getString(R.string.userinfo_dialog_close_live));
            } else {
                liveBtn.setText(getString(R.string.userinfo_dialog_live));
            }
        } else if (isHost()) {
            giftBtn.setVisibility(View.VISIBLE);
            btn_outUser.setVisibility(View.VISIBLE);
            btnUserControl.setVisibility(View.GONE);
        } else if (isInChatRoom()) {
            giftBtn.setVisibility(View.VISIBLE);
            btn_outUser.setVisibility(View.GONE);
            btnUserControl.setVisibility(View.VISIBLE);
        } else {
            giftBtn.setVisibility(View.GONE);
            btn_outUser.setVisibility(View.GONE);
            btnUserControl.setVisibility(View.VISIBLE);
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
            userInfoDialog.UserFollow(CommonUrlConfig.UserFollow, userInfo.token, userInfo.userid, fuserid, isfollwValue, callback);
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
        userInfoDialog.userNoticeEdit(CommonUrlConfig.UserNoticeEdit, userInfo.token, userInfo.userid, touserid, callback);

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


    private boolean isHost() {
        return info.isout && !info.userid.equals(ChatRoomActivity.userModel.userid);
    }

    //是否是自己
    private boolean isSelf() {
        return info.userid.equals(userInfo.userid);
    }

    //是否在房间
    private boolean isInChatRoom() {
        String simpleClassName = getActivity().getClass().getSimpleName();
        return simpleClassName.equals(ChatRoomActivity.class.getSimpleName());
    }

    public void setUserInfoModel(UserInfoModel userInfoModel) {
        this.info = userInfoModel;
    }
}