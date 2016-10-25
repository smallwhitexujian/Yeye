package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.UserInfoDialog;
import com.angelatech.yeyelive.adapter.MyFragmentPagerAdapter;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.fragment.UserVideoFragment;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoRoundView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 他人用户信息页面
 */
public class FriendUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btn_back;
    private FrescoRoundView user_face;
    private ImageView iv_vip;
    private TextView user_nick;
    private ImageView user_sex;
    private TextView user_id;
    private TextView user_sign;         //个性签名
    private LinearLayout ly_follow, ly_fans, ly_like;
    private TextView txt_follow, txt_fans, txt_like;

    private UserInfoDialog userInfoDialog;
    private BasicUserInfoDBModel loginUser;
    private BasicUserInfoModel baseInfo;
    private static final int MSG_LOAD_SUC = 1;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private ViewPager mviewPager;
    private UserVideoFragment userVideoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_user_info);
        initView();
        setView();
        initData();
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        user_face = (FrescoRoundView) findViewById(R.id.user_face);
        iv_vip = (ImageView) findViewById(R.id.iv_vip);
        user_nick = (TextView) findViewById(R.id.user_nick);
        user_sex = (ImageView) findViewById(R.id.user_sex);
        user_id = (TextView) findViewById(R.id.user_id);
        user_sign = (TextView) findViewById(R.id.user_sign);
        ly_follow = (LinearLayout) findViewById(R.id.ly_follow);
        ly_fans = (LinearLayout) findViewById(R.id.ly_fans);
        ly_like = (LinearLayout) findViewById(R.id.ly_like);
        txt_follow = (TextView) findViewById(R.id.txt_follow);
        txt_fans = (TextView) findViewById(R.id.txt_fans);
        txt_like = (TextView) findViewById(R.id.txt_like);
        mviewPager = (ViewPager) findViewById(R.id.data_layout);
    }

    private void setView() {
        btn_back.setOnClickListener(this);
    }

    private void initData() {
        loginUser = CacheDataManager.getInstance().loadUser();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            baseInfo = (BasicUserInfoModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            userVideoFragment = new UserVideoFragment();
            userVideoFragment.setFuserid(baseInfo.Userid);

            fragments.add(userVideoFragment);
        }
        MyFragmentPagerAdapter simpleFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mviewPager.setAdapter(simpleFragmentPagerAdapter);

        load();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.ly_follow:
                break;
            case R.id.ly_fans:
                break;
            case R.id.ly_like:
                break;
        }
    }

    /**
     * 查询 用户信息
     */
    private void load() {
        if (userInfoDialog == null) {
            userInfoDialog = new UserInfoDialog(this);
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

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_LOAD_SUC:
                LoadingDialog.cancelLoadingDialog();
                BasicUserInfoDBModel searchUserInfo = (BasicUserInfoDBModel) msg.obj;
                setUI(searchUserInfo);
                break;
        }
    }

    private void setUI(BasicUserInfoDBModel user) {
        if (user == null) {
            return;
        }

        if (user.nickname != null) {
            user_nick.setText(user.nickname);
        }
        user_face.setImageURI(VerificationUtil.getImageUrl150(user.headurl));

        if (user.sign == null || "".equals(user.sign)) {
            user_sign.setText(getString(R.string.default_sign));
        } else {
            user_sign.setText(user.sign);
        }
        if (Constant.SEX_MALE.equals(user.sex)) {
            user_sex.setImageResource(R.drawable.icon_information_boy);
        } else {
            user_sex.setImageResource(R.drawable.icon_information_girl);
        }
        user_id.setText(MessageFormat.format("{0}{1}", getString(R.string.ID), user.idx));
        txt_fans.setText(user.fansNum);
        txt_follow.setText(user.followNum);
        txt_like.setText(user.Intimacy);

        if (user.isv.equals("1")) {
            iv_vip.setVisibility(View.VISIBLE);
        } else {
            iv_vip.setVisibility(View.GONE);
        }
    }
}
