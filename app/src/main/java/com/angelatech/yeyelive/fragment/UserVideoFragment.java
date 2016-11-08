package com.angelatech.yeyelive.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.PlayActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.activity.function.PlayRecord;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.mediaplayer.util.PlayerUtil;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonVideoModel;
import com.angelatech.yeyelive.model.LiveModel;
import com.angelatech.yeyelive.model.LiveVideoModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.model.VideoModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 视频
 */
public class UserVideoFragment extends BaseLazyFragment implements SwipyRefreshLayout.OnRefreshListener {
    private final int DIVIDE = 999;
    private final int MSG_ADAPTER_NOTIFY = 1;
    private final int MSG_NO_DATA = 2;
    private final int MSG_HAVE_DATA = 3;
    private final int MSG_SETADPTER = 4;


    private View view;
    private GridView mGridView;
    private CommonAdapter<LiveVideoModel> adapter;
    private List<LiveVideoModel> datas = new ArrayList<>();
    private long datesort;
    private int pageindex = 1;
    private int pagesize = 8;
    private volatile boolean IS_REFRESH = false;  //是否需要刷新
    private SwipyRefreshLayout swipyRefreshLayout;
    private BasicUserInfoDBModel userInfo = CacheDataManager.getInstance().loadUser();
    private RelativeLayout noDataLayout;
    private String fuserid;

    private PlayRecord playRecord;

    private boolean isHost = false;
    private boolean isSelf = false;
    private boolean isLiveHost = false;

    private final Object lock = new Object();
    private int result_type = 0;

    public UserVideoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_video, container, false);
        initView();
        setView();
        return view;
    }

    private void initView() {
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.pullToRefreshView);
        mGridView = (GridView) view.findViewById(R.id.user_video_gridview);
        noDataLayout = (RelativeLayout) view.findViewById(R.id.no_data_layout);

        adapter = new CommonAdapter<LiveVideoModel>(getActivity(), datas, R.layout.item_user_video) {
            @Override
            public void convert(ViewHolder helper, final LiveVideoModel item, int position) {
                if (item.type == 1) {
                    LiveModel liveModel = (LiveModel) item;
                    helper.hideView(R.id.user_video_duration);
                    helper.setImageViewByImageLoader(R.id.user_video_cover, liveModel.headurl);
                    helper.setTextBackground(R.id.iv_line, ContextCompat.getDrawable(getActivity(), R.drawable.icon_home_live_ing));
                    helper.setText(R.id.iv_line, "LIVE");
                    helper.setText(R.id.user_video_duration, liveModel.onlinenum + getString(R.string.text_line_desc_now));
                } else {
                    VideoModel videoModel = (VideoModel) item;
                    helper.setTextBackground(R.id.iv_line, ContextCompat.getDrawable(getActivity(), R.drawable.icon_home_play_back));
                    helper.setText(R.id.iv_line, "REC");
                    helper.showView(R.id.user_video_duration);
                    try {
                        helper.setText(R.id.user_video_duration, videoModel.durations == null ? PlayerUtil.showTime(0) : PlayerUtil.showTime3(Integer.valueOf(videoModel.durations)));

                    } catch (NumberFormatException e) {
                        helper.setText(R.id.user_video_duration, PlayerUtil.showTime3(0));
                    }
                    helper.setImageViewByImageLoader(R.id.user_video_cover, videoModel.barcoverurl);
                    helper.setText(R.id.user_video_duration, videoModel.playnum + getString(R.string.text_line_desc_already));
                }
            }
        };
        playRecord = new PlayRecord(getActivity());
    }

    private void setView() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isHost) {
                    // ToastUtils.showToast(getActivity(), R.string.forbid_host_watch_record);
                    return;
                }
                LiveVideoModel item = (LiveVideoModel) parent.getItemAtPosition(position);
                if (item.type == 1) {
                    if (!isLiveHost) {
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
                        roomModel.setUserInfoDBModel(user);
                        BasicUserInfoModel loginUser = new BasicUserInfoModel();//登录信息
                        loginUser.Userid = userInfo.userid;
                        loginUser.Token = userInfo.token;
                        roomModel.setLoginUser(loginUser);
                        if (liveModel.ispwdroom.equals("1") && !liveModel.password.isEmpty()) {
                            ChatRoom.enterPWDChatRoom(getActivity(), roomModel, liveModel.password);
                        } else {
                            ChatRoom.enterChatRoom(getActivity(), roomModel);
                        }
                    }
                    getActivity().finish();
                } else {
                    if (App.chatRoomApplication != null) {
                        App.chatRoomApplication.closeLive();
                    }
                    VideoModel videoModel = (VideoModel) item;
                    //回放视频
                    StartActivityHelper.jumpActivity(getActivity(), PlayActivity.class, videoModel);
                }

            }
        });
        mGridView.setAdapter(adapter);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        noDataLayout.setVisibility(View.GONE);
        noDataLayout.findViewById(R.id.no_data_icon).setOnClickListener(this);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_ADAPTER_NOTIFY:
                adapter.setData(datas);
                adapter.notifyDataSetChanged();
                noDataLayout.setVisibility(View.GONE);
                break;
            case MSG_NO_DATA:
                datas.clear();
//                adapter.setData(datas);
//                adapter.notifyDataSetChanged();
                showNodataLayout();
                break;
            case MSG_HAVE_DATA:
                noDataLayout.setVisibility(View.GONE);
                break;
            case MSG_SETADPTER:
                mGridView.setAdapter(adapter);
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

    @Override
    public void onFirstUserVisible() {
        freshLoad();
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
        datas.clear();
        load();
    }

    private void load() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                synchronized (lock) {
                    try {
                        CommonVideoModel<LiveModel, VideoModel> result = JsonUtil.fromJson(response, new TypeToken<CommonVideoModel<LiveModel, VideoModel>>() {
                        }.getType());
                        if (result != null) {
                            if (HttpFunction.isSuc(result.code)) {
                                datesort = result.time;
                                pageindex = result.index + 1;
                                result_type = result.type;
                                if (result.livedata != null && !result.livedata.isEmpty()) {
                                    for (LiveModel liveModel : result.livedata) {
                                        if (liveModel.livestate == null || PlayRecord.HAVE_NO_LIVE.equals(liveModel.livestate)) {
                                            continue;
                                        } else {
                                            datas.add(liveModel);
                                        }
                                    }
                                }
                                if (result.videodata != null && !result.videodata.isEmpty()) {
                                    datas.addAll(result.videodata);
                                }

                                fragmentHandler.obtainMessage(MSG_ADAPTER_NOTIFY, result).sendToTarget();
                            } else {
                                onBusinessFaild(result.code, response);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (datas == null || datas.isEmpty()) {
                        fragmentHandler.obtainMessage(MSG_NO_DATA).sendToTarget();
                    }
                    IS_REFRESH = false;
                }
            }

        };
        playRecord.getUserRecord(userInfo.userid, userInfo.token, fuserid, pagesize, pageindex, callback);
//        mMainEnter.loadRoomList(liveUrl, userInfo, pageindex, pagesize, datesort,result_type,callback);
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
        if (isSelf) {
            // ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(R.string.no_data_no_video_self);
        } else {
            // ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(R.string.no_data_no_video_other);
        }
        noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);
    }

    public void setFuserid(String fuserid) {
        this.fuserid = fuserid;
    }

}
