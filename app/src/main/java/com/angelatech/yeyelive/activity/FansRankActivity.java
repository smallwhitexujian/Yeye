package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.handler.CommonDoHandler;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.RankModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//粉丝贡献榜详情
public class FansRankActivity extends HeaderBaseActivity implements
        SwipyRefreshLayout.OnRefreshListener, CommonDoHandler {
    private SwipyRefreshLayout swipyRefreshLayout;
    private CommonAdapter<RankModel> adapter;
    private BasicUserInfoDBModel userInfo;
    private MainEnter mainEnter;
    private List<RankModel> rankModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_rank);

        initView();
    }

    private void initView() {
        headerLayout.showTitle(R.string.fans_rank);
        headerLayout.showLeftBackButton();
        mainEnter = new MainEnter(this);
        userInfo = CacheDataManager.getInstance().loadUser();
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(true);
            }
        });
        ListView liveView = (ListView) findViewById(R.id.liveView);
        RelativeLayout noDataLayout = (RelativeLayout) findViewById(R.id.no_data_layout);
        noDataLayout.findViewById(R.id.no_data_icon).setOnClickListener(this);
        adapter = new CommonAdapter<RankModel>(FansRankActivity.this, rankModels, R.layout.item_rank) {
            @Override
            public void convert(ViewHolder helper, final RankModel item, int position) {
                String top = item.num;
                if (top.equals("1")) {
                    helper.hideView(R.id.rank_top);
                    helper.showView(R.id.rank_top_img);
                    helper.setImageResource(R.id.rank_top_img, R.drawable.icon_contribution_list_one);
                } else if (top.equals("2")) {
                    helper.hideView(R.id.rank_top);
                    helper.showView(R.id.rank_top_img);
                    helper.setImageResource(R.id.rank_top_img, R.drawable.icon_contribution_list_two);
                } else if (top.equals("3")) {
                    helper.hideView(R.id.rank_top);
                    helper.showView(R.id.rank_top_img);
                    helper.setImageResource(R.id.rank_top_img, R.drawable.icon_contribution_list_three);
                } else {
                    helper.showView(R.id.rank_top);
                    helper.hideView(R.id.rank_top_img);
                }
                helper.setText(R.id.rank_top, top);
                helper.setImageUrl(R.id.rank_handler, item.imageurl);
                helper.setText(R.id.rank_nickName, item.name);
                CharSequence str = Html.fromHtml("<font color='" + ContextCompat.getColor(mContext, R.color.color_999999) + "'>" + getString(R.string.dedicate) + "</font>" +
                        "<font color='" + ContextCompat.getColor(mContext, R.color.color_eecc1b) + "'>" + item.number + "</font>"
                        + "<font color='" + ContextCompat.getColor(mContext, R.color.color_999999) + "'>" + getString(R.string.rank_coin) + "</font>");
                helper.setText(R.id.txt_coin, str);
                if (item.sex.equals(Constant.SEX_MALE)) {
                    helper.setImageResource(R.id.icon_sex, R.drawable.icon_information_boy);
                } else {
                    helper.setImageResource(R.id.icon_sex, R.drawable.icon_information_girl);
                }
                //0 无 1 v 2 金v 9官
                switch (item.isv){
                    case "1":
                        helper.setImageResource(R.id.iv_vip,R.drawable.icon_identity_vip_white);
                        helper.showView(R.id.iv_vip);
                        break;
                    case "2":
                        helper.setImageResource(R.id.iv_vip,R.drawable.icon_identity_vip_gold);
                        helper.showView(R.id.iv_vip);
                        break;
                    case "9":
                        helper.setImageResource(R.id.iv_vip,R.drawable.icon_identity_official);
                        helper.showView(R.id.iv_vip);
                        break;
                    default:
                        helper.hideView(R.id.iv_vip);
                        break;
                }
                helper.setOnClick(R.id.rank_handler, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userInfo.userid.equals(item.id)){
                            return;
                        }
                        //jumpUserInfo(item);
                    }
                });
            }
        };
        liveView.setAdapter(adapter);
    }
    private void load() {
        String roomid = String.valueOf(ChatRoomActivity.roomModel.getId());
        if (userInfo != null && !roomid.isEmpty()) {
            mainEnter.loadSevenRank(CommonUrlConfig.RankListByRoom, userInfo.userid, userInfo.token, roomid, callback);
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
                        CharSequence str = Html.fromHtml("<font color='" + ContextCompat.getColor(FansRankActivity.this, R.color.color_999999) + "'>" + getString(R.string.dedicate) + "</font>" +
                                "<font color='" + ContextCompat.getColor(FansRankActivity.this, R.color.color_eecc1b) + "'>" + datas.pernumber + "</font>"
                                + "<font color='" + ContextCompat.getColor(FansRankActivity.this, R.color.color_999999) + "'>" + getString(R.string.rank_coin) + "</font>");

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
