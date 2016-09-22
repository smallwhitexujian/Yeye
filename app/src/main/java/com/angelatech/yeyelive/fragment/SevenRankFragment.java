package com.angelatech.yeyelive.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
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
    private ListView liveView;
    private RelativeLayout noDataLayout;
    private static final String ARG_POSITION = "position";
    private BasicUserInfoDBModel userInfo;
    private CommonAdapter<RankModel> adapter;
    private List<RankModel> rankModels = new ArrayList<>();
    private MainEnter mainEnter;

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
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.pullToRefreshView);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(true);
            }
        });
        noDataLayout.findViewById(R.id.no_data_icon).setOnClickListener(this);
        liveView = (ListView) view.findViewById(R.id.liveView);
        noDataLayout = (RelativeLayout) view.findViewById(R.id.no_data_layout);
        adapter = new CommonAdapter<RankModel>(getActivity(), rankModels, R.layout.item_rank) {
            @Override
            public void convert(ViewHolder helper, RankModel item, int position) {

            }
        };
        liveView.setAdapter(adapter);
    }

    private void load() {
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
                    fragmentHandler.obtainMessage(1, basicUserInfoDBModel).sendToTarget();
                }
            }
        };
        if (userInfo != null) {
            mainEnter.loadSevenRank(CommonUrlConfig.UserInformation, userInfo.userid, userInfo.userid, userInfo.token, callback);
        }
    }

    @Override
    public void doHandler(Message msg) {
        super.doHandler(msg);
        switch (msg.what){
            case 1:
                break;
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }
}
