package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.base.WithBroadCastActivity;
import com.angelatech.yeyelive.activity.function.FocusFans;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.activity.function.UserControl;
import com.angelatech.yeyelive.activity.function.UserInfoDialog;
import com.angelatech.yeyelive.adapter.HorizontalUserRankListViewAdapter;
import com.angelatech.yeyelive.adapter.MyFragmentPagerAdapter;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.fragment.UserVideoFragment;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.RankModel;
import com.angelatech.yeyelive.model.SearchItemModel;
import com.angelatech.yeyelive.util.BroadCastHelper;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.view.ActionSheetDialog;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoRoundView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 他人用户信息页面
 */
public class FriendUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btn_back, btn_more;
    private FrescoRoundView user_face;
    private ImageView iv_vip;
    private TextView user_nick;
    private ImageView user_sex;
    private TextView user_id, txt_title;
    private TextView user_sign;         //个性签名
    private LinearLayout ly_follow, ly_fans, ly_like;
    private TextView txt_follow, txt_fans, txt_like;
    private GridView grid_online;
    private Button attentionsBtn;

    private UserInfoDialog userInfoDialog;
    private BasicUserInfoDBModel loginUser;
    private BasicUserInfoDBModel loadUser;
    private BasicUserInfoModel baseInfo;
    private static final int MSG_LOAD_SUC = 1, RANK_LOAD_SUC = 11;
    private final int MSG_SET_FOLLOW = 5;
    private final int MSG_LOAD_STATUS = 6;
    private final int MSG_PULL_BLACKLIST_SUC = 8;
    private final int MSG_REPORT_SUC = 10;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private HorizontalUserRankListViewAdapter horizontalListViewAdapter;
    private List<RankModel> showList;

    private ViewPager mviewPager;
    private UserVideoFragment userVideoFragment;
    private MainEnter mainEnter;
    private String isFollowCode;

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
        grid_online = (GridView) findViewById(R.id.grid_online);
        attentionsBtn = (Button) findViewById(R.id.attentions_btn);
        btn_more = (ImageView) findViewById(R.id.btn_more);
        txt_title = (TextView) findViewById(R.id.txt_title);
    }

    private void setView() {
        btn_back.setOnClickListener(this);
        ly_follow.setOnClickListener(this);
        ly_fans.setOnClickListener(this);
        ly_like.setOnClickListener(this);
        attentionsBtn.setOnClickListener(this);
        btn_more.setOnClickListener(this);
    }

    private void initData() {
        loginUser = CacheDataManager.getInstance().loadUser();
        mainEnter = new MainEnter(this);
        showList = new ArrayList<>();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            baseInfo = (BasicUserInfoModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            userVideoFragment = new UserVideoFragment();
            userVideoFragment.setFuserid(baseInfo.Userid);
            fragments.add(userVideoFragment);
        }
        if (baseInfo.Userid.equals(loginUser.userid)) {
            txt_title.setText(R.string.me_home);
            btn_more.setVisibility(View.GONE);
        } else {
            txt_title.setText(R.string.ta_home);
            btn_more.setVisibility(View.VISIBLE);
        }
        grid_online.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent tabactivity = new Intent(FriendUserInfoActivity.this, TabActivity.class);
                tabactivity.putExtra("USERID", baseInfo.Userid);
                startActivity(tabactivity);
            }
        });
        MyFragmentPagerAdapter simpleFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mviewPager.setAdapter(simpleFragmentPagerAdapter);
        load();
        loadStatus();
        getRank();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.ly_follow:
                Intent focusactivity = new Intent(FriendUserInfoActivity.this, RelationActivity.class);
                focusactivity.putExtra("fuserid", baseInfo.Userid);
                focusactivity.putExtra("type", FocusFans.TYPE_FOCUS);
                startActivity(focusactivity);
                break;
            case R.id.ly_fans:
                Intent fansactivity = new Intent(FriendUserInfoActivity.this, RelationActivity.class);
                fansactivity.putExtra("fuserid", baseInfo.Userid);
                fansactivity.putExtra("type", FocusFans.TYPE_FANS);
                startActivity(fansactivity);
                break;
            case R.id.ly_like:
                StartActivityHelper.jumpActivity(FriendUserInfoActivity.this, PopularityActivity.class, loadUser);
                break;
            case R.id.attentions_btn:
                if (baseInfo != null) {
                    doFocus(baseInfo.Userid, isFollowCode);
                }
                break;
            case R.id.btn_more:
                ActionSheetDialog dialog = new ActionSheetDialog(FriendUserInfoActivity.this);
                dialog.builder();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.addSheetItem(getString(R.string.userinfo_dialog_do_pull_blacklist), ActionSheetDialog.SheetItemColor.BLACK_222222,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                LoadingDialog.showLoadingDialog(FriendUserInfoActivity.this);
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
                                userInfoDialog.ctlBlacklist(loginUser.userid, loginUser.token, loadUser.userid, UserControl.PULL_TO_BLACKLIST, callback);
                            }
                        }).addSheetItem(getString(R.string.userinfo_dialog_do_report), ActionSheetDialog.SheetItemColor.BLACK_222222,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                LoadingDialog.showLoadingDialog(FriendUserInfoActivity.this);
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
                                userInfoDialog.report(loginUser.userid, loginUser.token, UserControl.SOURCE_REPORT + "", loadUser.userid, "", callback);
                            }
                        });
                dialog.show();
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

    //获取粉丝贡献排行榜
    private void getRank() {
        final String touserid = String.valueOf(baseInfo.Userid);
        if (baseInfo != null && !touserid.isEmpty()) {
            HttpBusinessCallback callback = new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    LoadingDialog.cancelLoadingDialog();
                }

                @Override
                public void onSuccess(final String response) {
                    CommonListResult<RankModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<RankModel>>() {
                    }.getType());
                    if (datas == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(datas.code) && datas.hasData()) {

                        showList.clear();
                        if (datas.data.size() > 3) {
                            for (int i = 0; i < 3; i++) {
                                showList.add(datas.data.get(i));
                            }
                        } else {
                            showList.addAll(datas.data);
                        }
                        uiHandler.obtainMessage(RANK_LOAD_SUC).sendToTarget();

                    } else {
                        // onBusinessFaild(datas.code);
                    }
                }
            };
            mainEnter.loadSevenUserRank(CommonUrlConfig.RankListByRoom, loginUser.userid, loginUser.token, touserid, callback);
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_LOAD_SUC:
                LoadingDialog.cancelLoadingDialog();
                loadUser = (BasicUserInfoDBModel) msg.obj;
                setUI(loadUser);
                break;
            case RANK_LOAD_SUC:
                int length = 30;
                DisplayMetrics density = ScreenUtils.getScreen(FriendUserInfoActivity.this);
                if (density != null) {
                    int gridViewWidth = (int) (showList.size() * (length + 4) * density.density);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                    grid_online.setLayoutParams(params);
                }
                int itemWidth = (int) (length * density.density);
                grid_online.setNumColumns(showList.size());
                grid_online.setColumnWidth(itemWidth);
                grid_online.setStretchMode(GridView.NO_STRETCH);
                horizontalListViewAdapter = new HorizontalUserRankListViewAdapter(FriendUserInfoActivity.this, showList);
                grid_online.setAdapter(horizontalListViewAdapter);
                horizontalListViewAdapter.notifyDataSetChanged();
                break;
            case MSG_LOAD_STATUS:
                if (loginUser != null && !loginUser.userid.equals(baseInfo.Userid)) {
                    uiHandler.obtainMessage(MSG_SET_FOLLOW).sendToTarget();
                    attentionsBtn.setVisibility(View.VISIBLE);
                }
                break;
            case MSG_SET_FOLLOW:
                if (userInfoDialog.isFollow(isFollowCode)) {
                    attentionsBtn.setBackgroundResource(R.drawable.btn_bg_d9);
                    attentionsBtn.setText(R.string.follows);
                } else {
                    attentionsBtn.setBackgroundResource(R.drawable.btn_bg_red);
                    attentionsBtn.setText(R.string.live_follow);
                }
                break;
            case MSG_PULL_BLACKLIST_SUC:
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(FriendUserInfoActivity.this, getString(R.string.userinfo_dialog_pull_blacklist_suc));
                break;
            case MSG_REPORT_SUC:
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(FriendUserInfoActivity.this, getString(R.string.userinfo_dialog_report_suc));
                break;
        }
    }

    private String getOppositeFollow(String src) {
        if (UserInfoDialog.HAVE_FOLLOW.equals(src)) {
            return UserInfoDialog.HAVE_NO_FOLLOW;
        }
        return UserInfoDialog.HAVE_FOLLOW;
    }

    private void doFocus(final String fuserid, final String isfollow) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
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
                        BroadCastHelper.sendBroadcast(FriendUserInfoActivity.this, intent);
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

                        uiHandler.sendEmptyMessage(MSG_LOAD_STATUS);
                    } else {
                        onBusinessFaild((String) map.get("code"));
                    }
                }
            }
        };
        userInfoDialog.UserIsFollow(CommonUrlConfig.UserIsFollow, loginUser.token, loginUser.userid, baseInfo.Userid, httpCallback);
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
        //0 无 1 v 2 金v 9官
        switch (user.isv) {
            case "0":
                iv_vip.setVisibility(View.GONE);
                break;
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
        }
    }
}
