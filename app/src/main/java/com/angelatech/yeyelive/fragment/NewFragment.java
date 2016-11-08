package com.angelatech.yeyelive.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.PlayActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.CommonVideoModel;
import com.angelatech.yeyelive.model.LiveModel;
import com.angelatech.yeyelive.model.LiveVideoModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.model.VideoModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.tool.network.NetWorkUtil;
import com.will.view.ToastUtils;
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
 * 作者: Created by: xujian on Date: 2016/10/19.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.fragment
 */

public class NewFragment extends BaseFragment implements SwipyRefreshLayout.OnRefreshListener {
    private final int DIVIDE = 999999999;
    private final int MSG_ADAPTER_NOTIFY = 1;
    private final int MSG_NO_DATA = 2;
    private final int MSG_SHOW_BANNER = 4;
    private final int MSG_ERROR = 6;
    private final int MSG_NO_MORE = 9;

    private View view;
    private GridView listView;
    private CommonAdapter<LiveVideoModel> adapter;
    private List<LiveVideoModel> datas = new ArrayList<>();
    private long datesort = 0;
    private int pageindex = 1;
    private int pagesize = 100;
    private String liveUrl;
    private volatile boolean IS_REFRESH = true;  //是否需要刷新
    private SwipyRefreshLayout swipyRefreshLayout;
    private TimeCount timeCount;
    private BasicUserInfoDBModel userInfo;
    private MainEnter mainEnter;
    private RelativeLayout noDataLayout;
    private int result_type = 0;
    private final Object lock = new Object();
    private static final String ARG_POSITION = "position";
    private int fromType = 0;
    private int screenWidth;

    public static NewFragment newInstance(int position) {
        NewFragment f = new NewFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new, container, false);
        initView();
        setView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StartTimeCount();
        freshLoad();
    }

    @Override
    public void onPause() {
        super.onPause();
        StopTimeCount();
        LoadingDialog.cancelLoadingDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        StopTimeCount();
    }

    private void initView() {
        screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：px）
        userInfo = CacheDataManager.getInstance().loadUser();
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.pullToRefreshView);
        listView = (GridView) view.findViewById(R.id.live_video_hot_list);

        noDataLayout = (RelativeLayout) view.findViewById(R.id.no_data_layout);
        adapter = new CommonAdapter<LiveVideoModel>(getActivity(), datas, R.layout.item_new_list) {
            @Override
            public void convert(ViewHolder helper, final LiveVideoModel item, int position) {
                if (isAdded()) {
                    if (item.type == 1) {
                        LiveModel liveModel = (LiveModel) item;
                        ViewGroup.LayoutParams para;
                        para = helper.getView(R.id.live_cover).getLayoutParams();
                        para.height = screenWidth / 2;
                        para.width = screenWidth / 2;
                        helper.getView(R.id.live_cover).setLayoutParams(para);
                        helper.setTextBackground(R.id.iv_line, ContextCompat.getDrawable(getActivity(), R.drawable.icon_home_live_ing));
                        helper.setText(R.id.iv_line, "LIVE");
                        helper.setImageURI(R.id.live_cover, liveModel.headurl);
                        if (liveModel.area == null || "".equals(liveModel.area)) {
                            helper.setText(R.id.area, getString(R.string.live_hot_default_area));
                        } else {
                            helper.setText(R.id.area, liveModel.area);
                        }
                        if ( liveModel.ticketprice != null && Integer.parseInt(liveModel.ticketprice) > 0) {
                            helper.showView(R.id.ticket);
                            helper.setImageResource(R.id.ticket, R.drawable.icon_tickets_golds_big);
                        } else if ( liveModel.password != null && liveModel.password.length() == 4) {
                            helper.showView(R.id.ticket);
                            helper.setImageResource(R.id.ticket, R.drawable.btn_home_passroom_s);
                        } else {
                            helper.hideView(R.id.ticket);
                        }
                    } else {
                        VideoModel videoModel = (VideoModel) item;
                        ViewGroup.LayoutParams para;
                        para = helper.getView(R.id.live_cover).getLayoutParams();
                        para.height = screenWidth / 2;
                        para.width = screenWidth / 2;
                        helper.getView(R.id.live_cover).setLayoutParams(para);
                        helper.setTextBackground(R.id.iv_line, ContextCompat.getDrawable(getActivity(), R.drawable.icon_home_play_back));
                        helper.setText(R.id.iv_line, "REC");
                        helper.setImageURI(R.id.live_cover, videoModel.headurl);
                        if (item.area == null || "".equals(item.area)) {
                            helper.setText(R.id.area, getString(R.string.live_hot_default_area));
                        } else {
                            helper.setText(R.id.area, item.area);
                        }

                        if (videoModel.ticketprice != null &&  !videoModel.ticketprice.isEmpty() && Integer.parseInt(videoModel.ticketprice) > 0) {
                            helper.setImageResource(R.id.ticket, R.drawable.icon_tickets_golds_big);
                            helper.showView(R.id.ticket);
                        } else if (videoModel.password != null && videoModel.password.length() == 4) {
                            helper.setImageResource(R.id.ticket, R.drawable.btn_home_passroom_s);
                            helper.showView(R.id.ticket);
                        } else {
                            helper.hideView(R.id.ticket);
                        }
                    }
                }
            }
        };
        mainEnter = new MainEnter(getActivity());
    }

    private void setView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoadingDialog.showLoadingDialog(getActivity(), null);
                final LiveVideoModel item = (LiveVideoModel) parent.getItemAtPosition(position);
                if (NetWorkUtil.getActiveNetWorkType(getActivity()) == NetWorkUtil.TYPE_MOBILE) {
                    ToastUtils.showToast(getActivity(), getString(R.string.continue_to_watch));
                }
                startLive(item);
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
            roomModel.setRtmpwatchaddress(liveModel.rtmpserverip);
            roomModel.setRoomType(App.LIVE_WATCH);
            roomModel.setIdx(liveModel.roomidx);
            roomModel.setBarcoverurl(liveModel.barcoverurl);
            BasicUserInfoDBModel user = new BasicUserInfoDBModel(); //直播者信息
            user.userid = liveModel.userid;
            user.headurl = liveModel.headurl;
            user.nickname = liveModel.nickname;
            user.isv = liveModel.isv;
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
        } else {
            //回放视频
            StartActivityHelper.jumpActivity(getActivity(), PlayActivity.class, item);
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
                if (isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            noDataLayout.setVisibility(View.GONE);
                        }
                    });
                }
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
                ToastUtils.showToast(getActivity(), getString(R.string.no_data_more));
                break;
        }
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        if (isAdded()) {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(false);
            }
        });
    }

    //加载更多
    private void moreLoad() {
        IS_REFRESH = false;
        pageindex = pageindex + 1;
        load(fromType);
        StopTimeCount();
    }

    //刷新
    private void freshLoad() {
        IS_REFRESH = true;
        datesort = 0;
        result_type = 0;
        pageindex = 1;
        load(fromType);
        StartTimeCount();
    }

    //重新开始计时
    private void StartTimeCount() {
        StopTimeCount();
        timeCount = new TimeCount(25000, 25000);
        timeCount.start();
    }

    //结束计时
    private void StopTimeCount() {
        if (timeCount != null) {
            timeCount.cancel();
            timeCount = null;
        }
    }

    private void load(int type) {
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
                    if (result != null && isAdded()) {
                        if (HttpFunction.isSuc(result.code)) {
                            if (!result.livedata.isEmpty() || !result.videodata.isEmpty()) {
                                datesort = result.time;
                                result_type = result.type;
                                if (IS_REFRESH) {//刷新
                                    datas.clear();
                                }
                                datas.addAll(result.livedata);
                                datas.addAll(result.videodata);
                                fragmentHandler.obtainMessage(MSG_ADAPTER_NOTIFY, result).sendToTarget();
                            } else {
                                if (IS_REFRESH) {
                                    fragmentHandler.sendEmptyMessage(MSG_NO_DATA);
                                } else {
                                    fragmentHandler.sendEmptyMessage(MSG_NO_MORE);
                                }
                            }
                        } else {
                            onBusinessFaild(result.code, response);
                        }
                    }
                    IS_REFRESH = false;
                }
            }
        };
        try {
            if (mainEnter != null) {
                mainEnter.loadRoomList(CommonUrlConfig.LiveVideoNewM, userInfo, pageindex, pagesize, datesort, result_type, callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            StartTimeCount();
            freshLoad();
        }
    }
}
