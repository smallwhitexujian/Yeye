package com.angelatech.yeyelive.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.FansActivity;
import com.angelatech.yeyelive.activity.FocusOnActivity;
import com.angelatech.yeyelive.activity.FriendUserInfoActivity;
import com.angelatech.yeyelive.activity.GoldHousActivity;
import com.angelatech.yeyelive.activity.MessageNotificationActivity;
import com.angelatech.yeyelive.activity.PayActivity;
import com.angelatech.yeyelive.activity.PopularityActivity;
import com.angelatech.yeyelive.activity.ProductActivity;
import com.angelatech.yeyelive.activity.RechargeActivity;
import com.angelatech.yeyelive.activity.RecodeActivity;
import com.angelatech.yeyelive.activity.SettingActivity;
import com.angelatech.yeyelive.activity.TestScanActivity;
import com.angelatech.yeyelive.activity.UserInfoActivity;
import com.angelatech.yeyelive.activity.UserVideoActivity;
import com.angelatech.yeyelive.activity.WebActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.SystemMessage;
import com.angelatech.yeyelive.model.VoucherModel;
import com.angelatech.yeyelive.model.WebTransportModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoRoundView;

import java.text.MessageFormat;
import java.util.Map;

/**
 * 我的界面
 */
public class LeftFragment extends HintFragment {
    private final int MSG_LOAD_SUC = 1;
    private View view;
    private MainEnter mainEnter;
    private TextView id, intimacy, attention, fans, user_nick, user_sign, user_video, message_notice, txt_like, coins;

    private RelativeLayout settingLayout, ly_qcode, gold_hous, my_product,
            layout_diamond, layout_video, layout_systemMsg, layout_gift, diamond;
    private LinearLayout fansLayout, attentionLayout, ly_like;
    private ImageView editImageView, sexImageView, iv_vip, btn_qcode;
    private FrescoRoundView userFace;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userInfo.userid == null) {
            return;
        }
        String str = String.valueOf(SystemMessage.getInstance().getQueryAllpot(BaseKey.NOTIFICATION_ISREAD, userInfo.userid).size());
        if (str.equals("0")) {
            SystemMessage.getInstance().clearUnReadTag(getActivity());
        }
        message_notice.setText(str);
        getConfig();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initView() {
        mainEnter = new MainEnter(getActivity());
        user_nick = (TextView) view.findViewById(R.id.user_nick);
        user_sign = (TextView) view.findViewById(R.id.user_sign);
        user_video = (TextView) view.findViewById(R.id.user_video);
        id = (TextView) view.findViewById(R.id.user_id);//用户id
        fans = (TextView) view.findViewById(R.id.user_fans);//粉丝
        attention = (TextView) view.findViewById(R.id.user_attention);//关注
        intimacy = (TextView) view.findViewById(R.id.user_intimacy);//亲密度
        txt_like = (TextView) view.findViewById(R.id.txt_like);//亲密度
        coins = (TextView) view.findViewById(R.id.coins);//亲密度
        message_notice = (TextView) view.findViewById(R.id.message_notice);
        gold_hous = (RelativeLayout) view.findViewById(R.id.gold_hous);
        layout_systemMsg = (RelativeLayout) view.findViewById(R.id.layout_systemMsg);
        fansLayout = (LinearLayout) view.findViewById(R.id.fans_layout);
        ly_like = (LinearLayout) view.findViewById(R.id.ly_like);
        attentionLayout = (LinearLayout) view.findViewById(R.id.attention_layout);
        settingLayout = (RelativeLayout) view.findViewById(R.id.setting_layout);
        layout_diamond = (RelativeLayout) view.findViewById(R.id.layout_diamond);
        diamond = (RelativeLayout) view.findViewById(R.id.diamond);
        layout_video = (RelativeLayout) view.findViewById(R.id.layout_video);
        layout_gift = (RelativeLayout) view.findViewById(R.id.layout_gift);
        ly_qcode = (RelativeLayout) view.findViewById(R.id.ly_qcode);
        my_product = (RelativeLayout) view.findViewById(R.id.my_product);
        editImageView = (ImageView) view.findViewById(R.id.btn_edit);
        sexImageView = (ImageView) view.findViewById(R.id.user_sex);
        userFace = (FrescoRoundView) view.findViewById(R.id.user_face);
        iv_vip = (ImageView) view.findViewById(R.id.iv_vip);
        btn_qcode = (ImageView) view.findViewById(R.id.btn_qcode);
    }

    private void setView() {
        fansLayout.setOnClickListener(this);
        layout_gift.setOnClickListener(this);
        attentionLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        layout_systemMsg.setOnClickListener(this);
        editImageView.setOnClickListener(this);
        userFace.setOnClickListener(this);
        layout_diamond.setOnClickListener(this);
        diamond.setOnClickListener(this);
        layout_video.setOnClickListener(this);
        btn_qcode.setOnClickListener(this);
        ly_qcode.setOnClickListener(this);
        gold_hous.setOnClickListener(this);
        my_product.setOnClickListener(this);
        ly_like.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_like:
                StartActivityHelper.jumpActivity(getActivity(), PopularityActivity.class, userInfo);
                break;
            case R.id.diamond:
                StartActivityHelper.jumpActivityDefault(getActivity(), RechargeActivity.class);
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
                    BasicUserInfoModel usermodel = new BasicUserInfoModel();
                    usermodel.Userid = userInfo.userid;
                    usermodel.nickname = userInfo.nickname;
                    StartActivityHelper.jumpActivity(getActivity(), FriendUserInfoActivity.class, usermodel);
                }
                break;
            case R.id.layout_diamond:
                StartActivityHelper.jumpActivityDefault(getActivity(), PayActivity.class);
                break;
            case R.id.layout_video:
                StartActivityHelper.jumpActivityDefault(getActivity(), UserVideoActivity.class);
                break;
            case R.id.layout_systemMsg:
                StartActivityHelper.jumpActivityDefault(getActivity(), MessageNotificationActivity.class);
                break;
            case R.id.btn_qcode:
                //打开二维码页面
                StartActivityHelper.jumpActivity(getActivity(), RecodeActivity.class, 0);
                break;
            case R.id.ly_qcode:
                StartActivityHelper.jumpActivityDefault(getActivity(), TestScanActivity.class);
                break;
            case R.id.layout_gift:
//                WebTransportModel webTransportModel = new WebTransportModel();
//                webTransportModel.url = CommonUrlConfig.MallIndex + "?userid=" + userInfo.userid + "&token=" + userInfo.token + "&time=" + System.currentTimeMillis();
//                webTransportModel.title = getString(R.string.gift_center);
//                if (!webTransportModel.url.isEmpty()) {
//                    StartActivityHelper.jumpActivity(getActivity(), WebActivity.class, webTransportModel);
//                }
                WebTransportModel webTransportModel = new WebTransportModel();
                webTransportModel.url = "http://api.iamyeye.com/shop/index";
                webTransportModel.title = getString(R.string.gift_center);
                StartActivityHelper.jumpActivity(getActivity(), WebActivity.class,webTransportModel);
                break;
            case R.id.gold_hous:
                StartActivityHelper.jumpActivityDefault(getActivity(), GoldHousActivity.class);
                break;
            case R.id.my_product:
                StartActivityHelper.jumpActivityDefault(getActivity(), ProductActivity.class);
                break;
        }
    }

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
                    fragmentHandler.obtainMessage(MSG_LOAD_SUC, basicUserInfoDBModel).sendToTarget();
                }
            }
        };
        if (userInfo != null) {
            mainEnter.loadUserInfo(CommonUrlConfig.UserInformation, userInfo.userid, userInfo.userid, userInfo.token, callback);
        }
    }

    private void getConfig() {
        mainEnter.configOnoff(CommonUrlConfig.configOnoff, new HttpBusinessCallback() {
            @Override
            public void onSuccess(final String response) {
                super.onSuccess(response);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonListResult<VoucherModel> voucherM = JsonUtil.fromJson(response, new TypeToken<CommonListResult<VoucherModel>>() {
                            }.getType());
                            if (voucherM != null && voucherM.code.equals("1000")) {
                                App.configOnOff.addAll(voucherM.data);
                                if (App.configOnOff != null) {
                                    int type = (int) Double.parseDouble(App.configOnOff.get(1).value);
                                    if (type == 1) {
                                        layout_diamond.setVisibility(View.VISIBLE);
                                    } else {
                                        layout_diamond.setVisibility(View.GONE);
                                    }
                                    int Coins = (int) Double.parseDouble(App.configOnOff.get(0).value);
                                    if (Coins == 1) {
                                        diamond.setVisibility(View.VISIBLE);
                                    } else {
                                        diamond.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
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
                txt_like.setText(String.format("%s", basicUserInfoDBModel.followNum));
                userFace.setImageURI(VerificationUtil.getImageUrl150(basicUserInfoDBModel.headurl));
                user_video.setText(String.format("%s", basicUserInfoDBModel.videoNum));
                coins.setText(userInfo.diamonds);
                if (Constant.SEX_MALE.equals(basicUserInfoDBModel.sex)) {
                    sexImageView.setImageResource(R.drawable.icon_information_boy);
                } else {
                    sexImageView.setImageResource(R.drawable.icon_information_girl);
                }
                //0 无 1 v 2 金v 9官
                switch (basicUserInfoDBModel.isv) {
                    case "1":
                        iv_vip.setImageResource(R.drawable.icon_identity_vip_white);
                        iv_vip.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        iv_vip.setImageResource(R.drawable.icon_identity_vip_gold);
                        iv_vip.setVisibility(View.VISIBLE);
                        break;
                    case "9":
                        iv_vip.setImageResource(R.drawable.icon_identity_official);
                        iv_vip.setVisibility(View.VISIBLE);
                        break;
                    default:
                        iv_vip.setVisibility(View.GONE);
                        break;
                }
                CacheDataManager.getInstance().update(BaseKey.USER_FANS, basicUserInfoDBModel.fansNum, basicUserInfoDBModel.userid);
                CacheDataManager.getInstance().update(BaseKey.USER_IS_V, basicUserInfoDBModel.isv, basicUserInfoDBModel.userid);
                CacheDataManager.getInstance().update(BaseKey.USER_HEAD_URL, basicUserInfoDBModel.headurl, basicUserInfoDBModel.userid);
                CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, basicUserInfoDBModel.diamonds, basicUserInfoDBModel.userid);
                CacheDataManager.getInstance().update(BaseKey.USER_IS_TICKET, basicUserInfoDBModel.isticket, basicUserInfoDBModel.userid);
                CacheDataManager.getInstance().update(BaseKey.USER_IS_PWDROOM, basicUserInfoDBModel.ispwdroom, basicUserInfoDBModel.userid);
                CacheDataManager.getInstance().update(BaseKey.USER_EMAIL, basicUserInfoDBModel.email, basicUserInfoDBModel.userid);
                if (basicUserInfoDBModel.voucher != null) {
                    CacheDataManager.getInstance().update(BaseKey.USER_VOUCHER, basicUserInfoDBModel.voucher, basicUserInfoDBModel.userid);
                }
                CacheDataManager.getInstance().update(BaseKey.ISPWDpassword, basicUserInfoDBModel.ispaypassword, basicUserInfoDBModel.userid);
        }
    }


    @Override
    protected void lazyLoad() {

    }

    public void setPhoto() {
        userInfo = CacheDataManager.getInstance().loadUser();
        userFace.setImageURI(VerificationUtil.getImageUrl150(userInfo.headurl));
        if (userInfo.userid == null) {
            return;
        }
        String str = String.valueOf(SystemMessage.getInstance().getQueryAllpot(BaseKey.NOTIFICATION_ISREAD, userInfo.userid).size());
        if (str.equals("0")) {
            SystemMessage.getInstance().clearUnReadTag(getActivity());
        }
        message_notice.setText(str);
    }
}
