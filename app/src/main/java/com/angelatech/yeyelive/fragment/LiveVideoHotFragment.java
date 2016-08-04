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
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.LoginActivity;
import com.angelatech.yeyelive.activity.MainActivity;
import com.angelatech.yeyelive.activity.PlayActivity;
import com.angelatech.yeyelive.activity.WebActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BannerModel;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.CommonVideoModel;
import com.angelatech.yeyelive.model.LiveModel;
import com.angelatech.yeyelive.model.LiveVideoModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.model.UserInfoModel;
import com.angelatech.yeyelive.model.VideoModel;
import com.angelatech.yeyelive.model.WebTransportModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.UriHelper;
import com.angelatech.yeyelive.view.CommDialog;
import com.angelatech.yeyelive.view.banner.Banner;
import com.angelatech.yeyelive.view.banner.BannerOnPageChangeListener;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.Logger;
import com.will.common.string.json.JsonUtil;
import com.will.common.tool.network.NetWorkUtil;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;

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
    private final int MSG_SHOW_BANNER = 4;
    private final int MSG_ERROR = 6;
    private final int MSG_NO_MORE = 9;

    private View view;
    private ListView listView;
    private CommonAdapter<LiveVideoModel> adapter;
    private List<LiveVideoModel> datas = new ArrayList<>();
    private long datesort;
    private int pageindex = 1;
    private int pagesize = 5;
    private String liveUrl;
    private volatile boolean IS_REFRESH = false;  //是否需要刷新
    private SwipyRefreshLayout swipyRefreshLayout;

    private BasicUserInfoDBModel userInfo;
    private MainEnter mainEnter;
    private RelativeLayout noDataLayout;
    private int result_type = 0;
    private final Object lock = new Object();

    @SuppressLint("ValidFragment")
    public LiveVideoHotFragment(String url) {
        liveUrl = url;
    }

    public LiveVideoHotFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frame_live_video_hot, container, false);
        initView();
        setView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        freshLoad();
        // adapter.notifyDataSetChanged();
        listView.setSelection(0);
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
        userInfo = CacheDataManager.getInstance().loadUser();
        if (userInfo == null) {
            StartActivityHelper.jumpActivityDefault(getActivity(), LoginActivity.class);
            return;
        }
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.pullToRefreshView);
        listView = (ListView) view.findViewById(R.id.live_video_hot_list);

        noDataLayout = (RelativeLayout) view.findViewById(R.id.no_data_layout);
        adapter = new CommonAdapter<LiveVideoModel>(getActivity(), datas, R.layout.item_live_list) {
            @Override
            public void convert(ViewHolder helper, final LiveVideoModel item, int position) {
                if (item.type == 1) {
                    LiveModel liveModel = (LiveModel) item;
                    helper.setImageResource(R.id.iv_line, R.drawable.icon_home_live_ing);
                    helper.setImageViewByImageLoader(R.id.user_face, liveModel.headurl);
                    helper.setImageViewByImageLoader(R.id.live_cover, liveModel.barcoverurl);
                    helper.setText(R.id.live_hot_num, getLimitNum(liveModel.onlinenum));
                    helper.setText(R.id.user_nick, liveModel.nickname);
                    helper.setText(R.id.tv_line_desc, getString(R.string.text_line_desc_now));
                    if (liveModel.area == null || "".equals(liveModel.area)) {
                        helper.setText(R.id.area, getString(R.string.live_hot_default_area));
                    } else {
                        helper.setText(R.id.area, liveModel.area);
                    }
                    if (liveModel.introduce == null || "".equals(liveModel.introduce)) {
                        helper.hideView(R.id.live_introduce);
                    } else {
                        helper.showView(R.id.live_introduce);
                        helper.setText(R.id.live_introduce, liveModel.introduce);
                    }
                } else {
                    VideoModel videoModel = (VideoModel) item;
                    helper.setImageResource(R.id.iv_line, R.drawable.icon_home_play_back);
                    helper.setImageViewByImageLoader(R.id.user_face, videoModel.headurl);
                    helper.setImageViewByImageLoader(R.id.live_cover, videoModel.barcoverurl);
                    helper.setText(R.id.live_hot_num, getLimitNum(videoModel.playnum));
                    helper.setText(R.id.user_nick, item.nickname);
                    helper.setText(R.id.tv_line_desc, getString(R.string.text_line_desc_already));
                    if (item.area == null || "".equals(item.area)) {
                        helper.setText(R.id.area, getString(R.string.live_hot_default_area));
                    } else {
                        helper.setText(R.id.area, item.area);
                    }
                    if (videoModel.introduce == null || "".equals(videoModel.introduce)) {
                        helper.hideView(R.id.live_introduce);
                    } else {
                        helper.showView(R.id.live_introduce);
                        helper.setText(R.id.live_introduce, videoModel.introduce);
                    }
                }
                //加V标识
                if (item.isv.equals("1")) {
                    helper.showView(R.id.iv_vip);
                } else {
                    helper.hideView(R.id.iv_vip);
                }

                helper.setOnClick(R.id.layout_bar, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jumpUserInfo(item);
                    }
                });
            }
        };
        mainEnter = ((MainActivity) getActivity()).getMainEnter();
    }

    private void jumpUserInfo(LiveVideoModel item) {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.userid = item.userid;
        userInfoModel.headurl = item.headurl;
        userInfoModel.nickname = item.nickname;
        UserInfoDialogFragment userInfoDialogFragment = new UserInfoDialogFragment();
        userInfoDialogFragment.setUserInfoModel(userInfoModel);
        userInfoDialogFragment.show(getActivity().getSupportFragmentManager(), "");
    }

    private void setView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final LiveVideoModel item = (LiveVideoModel) parent.getItemAtPosition(position);

                if (NetWorkUtil.getActiveNetWorkType(getActivity()) == NetWorkUtil.TYPE_MOBILE) {
                    CommDialog commDialog = new CommDialog();
                    CommDialog.Callback callback = new CommDialog.Callback() {
                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onOK() {
                            startLive(item);
                        }
                    };
                    commDialog.CommDialog(getActivity(), getString(R.string.traffic_alert), true, callback);
                } else {
                    startLive(item);
                }

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

    private void startLive(LiveVideoModel item) {
        if (item.type == 1) {
            LiveModel liveModel = (LiveModel) item;
            RoomModel roomModel = new RoomModel();
            roomModel.setId(Integer.parseInt(liveModel.roomid));
            roomModel.setName(liveModel.introduce);
            roomModel.setIp(liveModel.roomserverip.split(":")[0]);
            roomModel.setPort(Integer.parseInt(liveModel.roomserverip.split(":")[1]));
            roomModel.setRtmpip(liveModel.rtmpserverip);
            roomModel.setRoomType(App.LIVE_WATCH);
            roomModel.setIdx(liveModel.roomidx);
            BasicUserInfoDBModel user = new BasicUserInfoDBModel();
            user.userid = liveModel.userid;
            user.headurl = liveModel.headurl;
            user.nickname = liveModel.nickname;
            user.isv = liveModel.isv;
            roomModel.setUserInfoDBModel(user);

            ChatRoom.enterChatRoom(getActivity(), roomModel);
        } else {
            //回放视频
            StartActivityHelper.jumpActivity(getActivity(), PlayActivity.class, (VideoModel) item);
        }
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
                adapter.setData(datas);
                adapter.notifyDataSetChanged();
                noDataLayout.setVisibility(View.GONE);
                break;
            case MSG_ERROR:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                break;
            case MSG_NO_DATA:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
                noDataLayout.setVisibility(View.VISIBLE);
                break;
            case MSG_NO_MORE:
                swipyRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });

                break;
            case MSG_SHOW_BANNER:
                List<SimpleDraweeView> simpleDraweeViews = (List<SimpleDraweeView>) msg.obj;
                List<String> descriptions = new ArrayList<>();
                int size = simpleDraweeViews.size();
                View banner = LayoutInflater.from(getActivity()).inflate(R.layout.banner_item, null);

                ViewPager viewPager = (ViewPager) banner.findViewById(R.id.viewpager);
                LinearLayout pointGroup = (LinearLayout) banner.findViewById(R.id.point_group);
                TextView desciption = (TextView) banner.findViewById(R.id.image_desc);

                for (int i = 0; i < size; i++) {
                    view = new View(getActivity());
                    view.setBackgroundResource(R.drawable.point_background);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
                    params.leftMargin = 5;
                    view.setEnabled(false);
                    view.setLayoutParams(params);
                    pointGroup.addView(view); // 向线性布局中添加“点”
                    descriptions.add("");
                }

                // 初始化viewpager的默认position.MAX_value的一半
                BannerOnPageChangeListener bannerOnPageChangeListener =
                        new BannerOnPageChangeListener(viewPager, descriptions, desciption, pointGroup);
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
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    freshLoad();
                } else {
                    moreLoad();
                }
            }
        });
    }

    //加载更多
    private void moreLoad() {
        IS_REFRESH = false;
        if (pageindex <= 1) {
            pageindex = 2;
        }
        load();
    }

    //刷新
    private void freshLoad() {
        IS_REFRESH = true;
        datesort = 0;
        pageindex = 1;
        result_type = 0;
        load();
    }

    private void load() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                fragmentHandler.obtainMessage(MSG_ERROR).sendToTarget();
            }

            @Override
            public void onSuccess(String response) {
                synchronized (lock) {
                    CommonVideoModel<LiveModel, VideoModel> result = JsonUtil.fromJson(response, new TypeToken<CommonVideoModel<LiveModel, VideoModel>>() {
                    }.getType());
                    if (result != null) {
                        if (HttpFunction.isSuc(result.code)) {

                            if (!result.livedata.isEmpty() || !result.videodata.isEmpty()) {
                                datesort = result.time;
                                pageindex = result.index + 1;
                                result_type = result.type;
                                if (IS_REFRESH) {
                                    datas.clear();
                                }
                                datas.addAll(result.livedata);
                                datas.addAll(result.videodata);
                                fragmentHandler.obtainMessage(MSG_ADAPTER_NOTIFY, result).sendToTarget();
                            } else {
                                fragmentHandler.sendEmptyMessage(MSG_NO_MORE);
                            }
                        } else {
                            onBusinessFaild(result.code, response);
                        }
                    }
                    if (datas.isEmpty()) {
                        fragmentHandler.obtainMessage(MSG_NO_DATA).sendToTarget();
                    }
                    IS_REFRESH = false;
                }
            }
        };
        MainEnter mainEnter = ((MainActivity) getActivity()).getMainEnter();
        mainEnter.loadRoomList(liveUrl, userInfo, pageindex, pagesize, datesort, result_type, callback);
    }

    /**
     * banner 模块 暂时隐藏
     */
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
}
