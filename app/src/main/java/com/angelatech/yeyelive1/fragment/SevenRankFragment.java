package com.angelatech.yeyelive1.fragment;

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

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.Constant;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.activity.ChatRoomActivity;
import com.angelatech.yeyelive1.activity.FriendUserInfoActivity;
import com.angelatech.yeyelive1.activity.function.MainEnter;
import com.angelatech.yeyelive1.adapter.CommonAdapter;
import com.angelatech.yeyelive1.adapter.ViewHolder;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.handler.CommonDoHandler;
import com.angelatech.yeyelive1.model.BasicUserInfoModel;
import com.angelatech.yeyelive1.model.CommonListResult;
import com.angelatech.yeyelive1.model.RankModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.util.VerificationUtil;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
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
 * com.angelatech.yeyelive1.fragment
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
    private String roomid;
    private int type;
    private RelativeLayout no_data_layout;

    public static SevenRankFragment newInstance(int position) {
        SevenRankFragment f = new SevenRankFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putInt("TYPE", 0);
        f.setArguments(b);
        return f;
    }

    //
    public static SevenRankFragment newInstance(int position, String userid) {
        SevenRankFragment f = new SevenRankFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putString("USERID", userid);
        b.putInt("TYPE", 1);
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
        LoadingDialog.showLoadingDialog(getActivity(), null);
        userInfo = CacheDataManager.getInstance().loadUser();

        LinearLayout bottom_layout = (LinearLayout) view.findViewById(R.id.bottom_layout);
        FrescoRoundView rank_my_pic = (FrescoRoundView) view.findViewById(R.id.rank_my_pic);
        rank_coin = (TextView) view.findViewById(R.id.rank_mycoin);
        no_data_layout = (RelativeLayout) view.findViewById(R.id.no_data_layout);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("TYPE", 0);
            if (type == 1) {
                roomid = bundle.getString("USERID");
            } else {
                roomid = String.valueOf(ChatRoomActivity.roomModel.getId());
                if (ChatRoomActivity.roomModel.getUserInfoDBModel().userid.equals(userInfo.userid)) {
                    bottom_layout.setVisibility(View.GONE);
                }
            }
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
                        if (userInfo.userid.equals(item.id)) {
                            return;
                        }
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
        if (userInfo != null && !roomid.isEmpty()) {
            if (type == 0) {
                mainEnter.loadSevenRank(CommonUrlConfig.RankListByRoom, userInfo.userid, userInfo.token, roomid, callback);
            } else {
                mainEnter.loadSevenUserRank(CommonUrlConfig.RankListByRoom, userInfo.userid, userInfo.token, roomid, callback);
            }
        }
    }

    private void jumpUserInfo(RankModel item) {
        BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
        userInfoModel.Userid = item.id;
        if (isAdded()) {
            StartActivityHelper.jumpActivity(getActivity(), FriendUserInfoActivity.class, userInfoModel);
        }
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            LoadingDialog.cancelLoadingDialog();
            no_data_layout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(final String response) {
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialog.cancelLoadingDialog();
                        CommonListResult<RankModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<RankModel>>() {
                        }.getType());
                        if (datas == null) {
                            no_data_layout.setVisibility(View.VISIBLE);
                            return;
                        }
                        if (HttpFunction.isSuc(datas.code)) {
                            if (datas.hasData() && datas.data.size() > 0) {
                                rankModels.clear();
                                rankModels.addAll(datas.data);
                                adapter.setData(rankModels);
                                CharSequence str = Html.fromHtml("<font color='" + ContextCompat.getColor(getActivity(), R.color.color_999999) + "'>" + getString(R.string.dedicate) + "</font>" +
                                        "<font color='" + ContextCompat.getColor(getActivity(), R.color.color_eecc1b) + "'>" + datas.pernumber + "</font>"
                                        + "<font color='" + ContextCompat.getColor(getActivity(), R.color.color_999999) + "'>" + getString(R.string.rank_coin) + "</font>");
                                rank_coin.setText(str);
                            } else {
                                no_data_layout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            no_data_layout.setVisibility(View.VISIBLE);
                            onBusinessFaild(datas.code);
                        }
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
            }
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
