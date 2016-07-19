package com.angelatech.yeyelive.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.activity.WebActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.LiveListItemModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.model.UserInfoModel;
import com.angelatech.yeyelive.model.WebTransportModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.view.banner.Banner;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.Logger;
import com.will.common.string.json.JsonUtil;
import com.will.view.ToastUtils;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;
import com.angelatech.yeyelive .R;
import com.angelatech.yeyelive.activity.MainActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BannerModel;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.UriHelper;
import com.angelatech.yeyelive.view.banner.BannerOnPageChangeListener;
import com.angelatech.yeyelive.view.banner.BannerPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 热门
 */
public class LiveVideoHotFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    private final int DIVIDE = 999;
    private final int MSG_ADAPTER_NOTIFY = 1;
    private final int MSG_NO_DATA = 2;
    private final int MSG_HAVE_DATA = 3;
    private final int MSG_SHOW_BANNER = 4;
    private final int MSG_SET_ADAPTER = 5;
    private final int MSG_NO_MORE = 9;
    private View view;
    private ListView listView;
    private CommonAdapter<LiveListItemModel> adapter;
    private List<LiveListItemModel> dataList = new ArrayList<>();
    private long dateSort;
    private int pageIndex = 1;
    private int pageSize = 5;
    private volatile boolean IS_REFRESH = false;  //是否需要刷新
    private SwipyRefreshLayout swipyRefreshLayout;
    private String liveUrl = CommonUrlConfig.LiveVideoHot;
    private BasicUserInfoDBModel userInfo = CacheDataManager.getInstance().loadUser();
    private MainEnter mainEnter;
    private RelativeLayout noDataLayout;

    private final Object lock = new Object();

    @SuppressLint("ValidFragment")
    public LiveVideoHotFragment(String url) {
        this.liveUrl = url;
    }

    public LiveVideoHotFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frame_live_video_hot, container, false);
        initView();
        setView();
        freshLoad();
        if (CommonUrlConfig.LiveVideoHot.equals(liveUrl)) {
            loadBanner();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initView() {
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.pullToRefreshView);
        listView = (ListView) view.findViewById(R.id.live_video_hot_list);
        noDataLayout = (RelativeLayout) view.findViewById(R.id.no_data_layout);

        adapter = new CommonAdapter<LiveListItemModel>(getActivity(), dataList, R.layout.item_live_list) {
            @Override
            public void convert(ViewHolder helper, final LiveListItemModel item, int position) {
                helper.setImageViewByImageLoader(R.id.user_face, item.headurl);
                helper.setImageViewByImageLoader(R.id.live_cover, item.barcoverurl);
                helper.setText(R.id.live_hot_num, getLimitNum(item.onlinenum));
                helper.setText(R.id.user_nick, item.nickname);
                if (item.area == null || "".equals(item.area)) {
                    helper.setText(R.id.area, getString(R.string.live_hot_default_area));
                } else {
                    helper.setText(R.id.area, item.area);
                }
                if (item.introduce == null || "".equals(item.introduce)) {
                    helper.hideView(R.id.live_introduce);
                } else {
                    helper.showView(R.id.live_introduce);
                    helper.setText(R.id.live_introduce, item.introduce);
                }
                if (LiveListItemModel.LIVE_NOW.equals(item.livestate)) {
                    helper.showView(R.id.live_anmi_layout);
                    helper.startAnimationList(R.id.live_anim);
                } else {
                    helper.hideView(R.id.live_anmi_layout);
                    helper.stopAnimationList(R.id.live_anim);
                }
                helper.setOnClick(R.id.user_face, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoModel userInfoModel = new UserInfoModel();
                        userInfoModel.userid = item.userid;
                        userInfoModel.headurl = item.headurl;
                        userInfoModel.nickname = item.nickname;
                        UserInfoDialogFragment userInfoDialogFragment = new UserInfoDialogFragment();
                        userInfoDialogFragment.setUserInfoModel(userInfoModel);
                        userInfoDialogFragment.show(getActivity().getSupportFragmentManager(), "");
                    }
                });
            }
        };
        mainEnter = ((MainActivity) getActivity()).getMainEnter();
    }

    private void setView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LiveListItemModel item = (LiveListItemModel) parent.getItemAtPosition(position);
                RoomModel roomModel = new RoomModel();
                roomModel.setId(Integer.parseInt(item.roomid));
                roomModel.setName(item.introduce);

                roomModel.setIp(item.roomserverip.split(":")[0]);
                roomModel.setPort(Integer.parseInt(item.roomserverip.split(":")[1]));
                roomModel.setRtmpip(item.rtmpserverip);

                roomModel.setRoomType("watch");
                roomModel.setIdx(item.roomidx);
                BasicUserInfoDBModel user = new BasicUserInfoDBModel();
                user.userid = item.userid;
                user.headurl = item.headurl;
                user.nickname = item.nickname;
                roomModel.setUserInfoDBModel(user);
                ChatRoom.enterChatRoom(getActivity(), roomModel);

            }
        });
        listView.setAdapter(adapter);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(true);
            }
        });

        noDataLayout.findViewById(R.id.no_data_icon).setOnClickListener(this);
    }


    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_ADAPTER_NOTIFY:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                adapter.setData(dataList);
                adapter.notifyDataSetChanged();
                noDataLayout.setVisibility(View.GONE);
                break;
            case MSG_NO_DATA:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                dataList.clear();
                adapter.setData(dataList);
                adapter.notifyDataSetChanged();
                showNodataLayout();
                break;
            case MSG_NO_MORE:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                if (!IS_REFRESH) {
                    ToastUtils.showToast(getActivity(), R.string.no_data_no_info);
                }
                break;
            case MSG_SHOW_BANNER:
                List<SimpleDraweeView> simpleDraweeViews = (List<SimpleDraweeView>) msg.obj;
                List<String> descriptions = new ArrayList<>();
                int size = simpleDraweeViews.size();
                View banner = LayoutInflater.from(getActivity()).inflate(R.layout.banner_item, null);

                ViewPager viewPager = (ViewPager) banner.findViewById(R.id.viewpager);
                LinearLayout pointGroup = (LinearLayout) banner.findViewById(R.id.point_group);
                TextView tv_desc = (TextView) banner.findViewById(R.id.image_desc);

                new BannerPoint(getActivity()).AddPoint(pointGroup, size);
                // 初始化viewpager的默认position.MAX_value的一半
                BannerOnPageChangeListener bannerOnPageChangeListener =
                        new BannerOnPageChangeListener(viewPager, descriptions, tv_desc, pointGroup);
                viewPager.addOnPageChangeListener(bannerOnPageChangeListener);
                int index = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % size);
                viewPager.setCurrentItem(index);
                listView.setAdapter(null);
                listView.addHeaderView(banner);
                listView.setAdapter(adapter);
                new Banner(viewPager, simpleDraweeViews).showBanner();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_data_icon:
                if (CommonUrlConfig.LiveVideoHot.equals(liveUrl)) {
                    ((MainActivity) getActivity()).selectTab(1);
                } else {
                    ((MainActivity) getActivity()).selectTab(0);
                }
                break;
        }
    }


    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            freshLoad();
        } else {
            moreLoad();
        }
        swipyRefreshLayout.setRefreshing(false);
    }

    //加载更多
    private void moreLoad() {
        IS_REFRESH = false;
        if (pageIndex <= 1) {
            pageIndex = 2;
        }
        load();
    }

    //刷新
    private void freshLoad() {
        IS_REFRESH = true;
        dateSort = 0;
        pageIndex = 1;
        dataList.clear();
        load();
    }

    private void load() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                LoadingDialog.cancelLoadingDialog();
            }

            @Override
            public void onSuccess(String response) {
                synchronized (lock) {
                    CommonParseListModel<LiveListItemModel> result = JsonUtil.fromJson(response, new TypeToken<CommonParseListModel<LiveListItemModel>>() {
                    }.getType());
                    if (result != null) {
                        if (HttpFunction.isSuc(result.code)) {
                            if (!result.data.isEmpty() && result.data.size() > 0) {
                                dateSort = result.time;
                                pageIndex = result.index + 1;
                                if (IS_REFRESH) {
                                    dataList.clear();
                                }
                                dataList.addAll(result.data);
                                fragmentHandler.obtainMessage(MSG_ADAPTER_NOTIFY, result).sendToTarget();
                            }
                        } else {
                            onBusinessFaild(result.code, response);
                        }
                    }
                }

                if (dataList.isEmpty() && IS_REFRESH) {
                    fragmentHandler.obtainMessage(MSG_NO_DATA).sendToTarget();
                }
                IS_REFRESH = false;
            }
        };
        MainEnter mainEnter = ((MainActivity) getActivity()).getMainEnter();
        mainEnter.loadRoomList(liveUrl, userInfo, pageIndex, pageSize, dateSort, callback);
    }

    private void loadBanner() {
        final List<SimpleDraweeView> simpleDraweeViews = new ArrayList<>();
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                CommonParseListModel<BannerModel<String>> results = JsonUtil.fromJson(response, new TypeToken<CommonParseListModel<BannerModel<String>>>() {
                }.getType());

                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        for (final BannerModel data : results.data) {
                            Logger.e("===" + data.toString());
                            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(getActivity());
                            simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.banner_android);
                            simpleDraweeView.setImageURI(UriHelper.obtainUri(data.imageurl));
                            int width = ViewGroup.LayoutParams.MATCH_PARENT;
                            int height = ViewGroup.LayoutParams.MATCH_PARENT;
                            ViewPager.LayoutParams params = new ViewPager.LayoutParams();
                            params.width = width;
                            params.height = height;
                            simpleDraweeView.setLayoutParams(params);
                            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (BannerModel.TYPE_WEB.equals(data.extype)) {
                                        WebTransportModel webTransportModel = new WebTransportModel();
                                        webTransportModel.url = data.url;
                                        webTransportModel.title = getString(R.string.banner_title);
                                        StartActivityHelper.jumpActivity(getActivity(), WebActivity.class, webTransportModel);
                                    }
                                }
                            });
                            simpleDraweeViews.add(simpleDraweeView);
                        }
                        if (!simpleDraweeViews.isEmpty()) {
                            fragmentHandler.obtainMessage(MSG_SHOW_BANNER, simpleDraweeViews).sendToTarget();
                        }
                    }

                }

            }
        };
        BasicUserInfoDBModel user = CacheDataManager.getInstance().loadUser();
        if (user == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userid", user.userid);
        params.put("token", user.token);
        mainEnter.httpGet(CommonUrlConfig.ExtensionList, params, callback);
    }

    private String getLimitNum(String hotNumStr) {
        if (hotNumStr != null) {
            try {
                int hotNum = Integer.valueOf(hotNumStr);
                if (hotNum > DIVIDE) {
                    return DIVIDE + "+";
                }
                return hotNumStr;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void showNodataLayout() {
        noDataLayout.setVisibility(View.VISIBLE);
        if (CommonUrlConfig.LiveVideoHot.equals(liveUrl)) {
            ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(R.string.no_data_no_live_hot);
            noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);
        } else {
            ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(R.string.no_data_no_live);
            ((TextView) noDataLayout.findViewById(R.id.hint_textview2)).setText(R.string.no_data_watch_follow);
            noDataLayout.findViewById(R.id.hint_textview2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).selectTab(0);
                }
            });
        }
    }

    private Runnable loadTask = new Runnable() {
        @Override
        public void run() {
            load();
        }
    };
}
