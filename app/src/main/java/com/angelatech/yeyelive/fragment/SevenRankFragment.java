package com.angelatech.yeyelive.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.handler.CommonDoHandler;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.RankModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.VerificationUtil;
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
 * 作者: Created by: xujian on Date: 16/9/22.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.fragment
 */

public class SevenRankFragment extends BaseFragment implements
        SwipyRefreshLayout.OnRefreshListener, CommonDoHandler {
    private SwipyRefreshLayout swipyRefreshLayout;
    private static final String ARG_POSITION = "position";
    private BasicUserInfoDBModel userInfo;
    private CommonAdapter<RankModel> adapter;
    private List<RankModel> rankModels = new ArrayList<>();
    private MainEnter mainEnter;
    private TextView rank_coin;

    public static SevenRankFragment newInstance(int position) {
        SevenRankFragment f = new SevenRankFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mainEnter = new MainEnter(getActivity());
        LoadingDialog.showLoadingDialog(getActivity());
        userInfo = CacheDataManager.getInstance().loadUser();
        LinearLayout bottom_layout = (LinearLayout) view.findViewById(R.id.bottom_layout);
        FrescoRoundView rank_my_pic = (FrescoRoundView) view.findViewById(R.id.rank_my_pic);
        rank_coin = (TextView) view.findViewById(R.id.rank_mycoin);
        if (ChatRoomActivity.roomModel.getUserInfoDBModel().userid.equals(userInfo.userid)) {
            bottom_layout.setVisibility(View.GONE);
        }
        rank_my_pic.setImageURI(VerificationUtil.getImageUrl(userInfo.headurl));
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(true);
            }
        });
        ListView liveView = (ListView) view.findViewById(R.id.liveView);
        RelativeLayout noDataLayout = (RelativeLayout) view.findViewById(R.id.no_data_layout);
        noDataLayout.findViewById(R.id.no_data_icon).setOnClickListener(this);
        adapter = new CommonAdapter<RankModel>(getActivity(), rankModels, R.layout.item_rank) {
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
        load();
    }

    private void load() {
        String roomid = String.valueOf(ChatRoomActivity.roomModel.getId());
        if (userInfo != null && !roomid.isEmpty()) {
            mainEnter.loadSevenRank(CommonUrlConfig.RankListByRoom, userInfo.userid, userInfo.token, roomid, callback);
        }
    }

    private void jumpUserInfo(RankModel item) {
        BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
        userInfoModel.Userid = item.id;
        userInfoModel.headurl = item.imageurl;
        userInfoModel.nickname = item.name;
        userInfoModel.sex = item.sex;
        userInfoModel.isv = item.isv;
        UserInfoDialogFragment userInfoDialogFragment = new UserInfoDialogFragment();
        userInfoDialogFragment.setUserInfoModel(userInfoModel);
        if (isAdded()) {
            userInfoDialogFragment.show(getActivity().getSupportFragmentManager(), "");
        }
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            LoadingDialog.cancelLoadingDialog();
        }

        @Override
        public void onSuccess(final String response) {
            getActivity().runOnUiThread(new Runnable() {
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
                        CharSequence str = Html.fromHtml("<font color='" + ContextCompat.getColor(getActivity(), R.color.color_999999) + "'>" + getString(R.string.dedicate) + "</font>" +
                                "<font color='" + ContextCompat.getColor(getActivity(), R.color.color_eecc1b) + "'>" + datas.pernumber + "</font>"
                                + "<font color='" + ContextCompat.getColor(getActivity(), R.color.color_999999) + "'>" + getString(R.string.rank_coin) + "</font>");
                        rank_coin.setText(str);
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
        getActivity().runOnUiThread(new Runnable() {
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
