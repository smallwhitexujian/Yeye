package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.RankModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoRoundView;

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
 * 作者: Created by: xujian on Date: 2016/10/20.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */

public class RankActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener ,View.OnClickListener{
    private BasicUserInfoDBModel userInfo;
    private MainEnter mainEnter;
    private CommonAdapter<RankModel> adapter;
    private SwipyRefreshLayout swipyRefreshLayout;
    private List<RankModel> rankModels = new ArrayList<>();
    private List<RankModel> rankLists = new ArrayList<>();
    private FrescoRoundView rankPic, rankPic2, rankPic3;
    private ImageView iv_vip,iv_vip2,iv_vip3;
    private TextView rankName, rankName2, rankName3, rankCoins, rankCoins2, rankCoins3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        initView();
        initData();
        load();
    }

    private void initData() {
        ListView liveView = (ListView) findViewById(R.id.liveView);
        adapter = new CommonAdapter<RankModel>(this, rankModels, R.layout.item_rank2) {
            @Override
            public void convert(ViewHolder helper, final RankModel item, int position) {
                String top = item.num;
                helper.setText(R.id.rank_top, top);
                helper.setImageUrl(R.id.rank_handler, item.imageurl);
                helper.setText(R.id.rank_nickName, item.name);
                CharSequence str = Html.fromHtml("<font color='" + ContextCompat.getColor(mContext, R.color.color_eecc1b) + "'>" + item.number + "</font>"
                        + " <font color='" + ContextCompat.getColor(mContext, R.color.color_999999) + "'>" + getString(R.string.rank_coin) + "</font>");
                helper.setText(R.id.txt_coin, str);
                if (item.sex.equals(Constant.SEX_MALE)) {
                    helper.setImageResource(R.id.icon_sex, R.drawable.icon_information_boy);
                } else {
                    helper.setImageResource(R.id.icon_sex, R.drawable.icon_information_girl);
                }
                helper.setOnClick(R.id.rank_handler, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jumpUserInfo(item);
                    }
                });
                if (item.isv.equals("1")) {
                    helper.showView(R.id.iv_vip);
                } else {
                    helper.hideView(R.id.iv_vip);
                }
            }
        };
        liveView.setAdapter(adapter);
    }

    private void jumpUserInfo(RankModel item) {
        BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
        userInfoModel.Userid = item.id;
        StartActivityHelper.jumpActivity(RankActivity.this, FriendUserInfoActivity.class, userInfoModel);
    }

    private void initView() {
        mainEnter = new MainEnter(this);
        LoadingDialog.showLoadingDialog(this, null);
        userInfo = CacheDataManager.getInstance().loadUser();
        rankPic = (FrescoRoundView) findViewById(R.id.rank_handler);
        rankPic2 = (FrescoRoundView) findViewById(R.id.rank_handler2);
        rankPic3 = (FrescoRoundView) findViewById(R.id.rank_handler3);
        rankName = (TextView) findViewById(R.id.rank_name);
        rankName2 = (TextView) findViewById(R.id.rank_name2);
        rankName3 = (TextView) findViewById(R.id.rank_name3);
        rankCoins = (TextView) findViewById(R.id.coins_str);
        rankCoins2 = (TextView) findViewById(R.id.coins_str2);
        rankCoins3 = (TextView) findViewById(R.id.coins_str3);
        iv_vip = (ImageView) findViewById(R.id.iv_vip);
        iv_vip2 = (ImageView) findViewById(R.id.iv_vip2);
        iv_vip3 = (ImageView) findViewById(R.id.iv_vip3);
        ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(true);
            }
        });
        rankPic.setOnClickListener(this);
        rankPic2.setOnClickListener(this);
        rankPic3.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rank_handler:
                jumpUserInfo(rankLists.get(0));
                break;
            case R.id.rank_handler2:
                jumpUserInfo(rankLists.get(1));
                break;
            case R.id.rank_handler3:
                jumpUserInfo(rankLists.get(2));
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }

    private void load() {
        if (userInfo != null) {
            mainEnter.loadRank(CommonUrlConfig.RankingList, userInfo.userid, userInfo.token, callback);
        }
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
                    LoadingDialog.cancelLoadingDialog();
                    CommonListResult<RankModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<RankModel>>() {
                    }.getType());
                    if (datas == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(datas.code)) {
                        rankModels.clear();
                        rankModels.addAll(datas.data);
                        adapter.setData(rankModels);
                        rankPic.setImageURI(rankModels.get(0).imageurl);
                        rankName.setText(rankModels.get(0).name);
                        if (!rankModels.get(0).isv.equals("1")) {
                            iv_vip.setVisibility(View.GONE);
                        }
                        CharSequence str = Html.fromHtml("<font color='" + ContextCompat.getColor(RankActivity.this, R.color.color_eecc1b) + "'>" + rankModels.get(0).number + "</font>"
                                + " <font color='" + ContextCompat.getColor(RankActivity.this, R.color.color_999999) + "'>" + getString(R.string.rank_coin) + "</font>");
                        rankCoins.setText(str);
                        rankPic2.setImageURI(rankModels.get(1).imageurl);
                        rankName2.setText(rankModels.get(1).name);
                        if (!rankModels.get(1).isv.equals("1")) {
                            iv_vip2.setVisibility(View.GONE);
                        }
                        CharSequence str2 = Html.fromHtml("<font color='" + ContextCompat.getColor(RankActivity.this, R.color.color_eecc1b) + "'>" + rankModels.get(1).number + "</font>"
                                + " <font color='" + ContextCompat.getColor(RankActivity.this, R.color.color_999999) + "'>" + getString(R.string.rank_coin) + "</font>");
                        rankCoins2.setText(str2);
                        rankPic3.setImageURI(rankModels.get(2).imageurl);
                        rankName3.setText(rankModels.get(2).name);
                        if (!rankModels.get(2).isv.equals("1")) {
                            iv_vip3.setVisibility(View.GONE);
                        }
                        CharSequence str3 = Html.fromHtml("<font color='" + ContextCompat.getColor(RankActivity.this, R.color.color_eecc1b) + "'>" + rankModels.get(2).number + "</font>"
                                + " <font color='" + ContextCompat.getColor(RankActivity.this, R.color.color_999999) + "'>" + getString(R.string.rank_coin) + "</font>");
                        rankCoins3.setText(str3);
                        rankLists.add(rankModels.get(0));
                        rankLists.add(rankModels.get(1));
                        rankLists.add(rankModels.get(2));
                        rankModels.remove(0);
                        rankModels.remove(0);
                        rankModels.remove(0);
                    } else {
                        onBusinessFaild(datas.code);
                    }
                    swipyRefreshLayout.setRefreshing(false);
                }
            });
        }
    };

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    load();
                }
                swipyRefreshLayout.setRefreshing(false);
            }
        });
    }
}
