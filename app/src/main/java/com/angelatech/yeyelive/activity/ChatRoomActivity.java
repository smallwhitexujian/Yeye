package com.angelatech.yeyelive.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.GlobalDef;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.ChatManager;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.adapter.MyFragmentPagerAdapter;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.fragment.CallFragment;
import com.angelatech.yeyelive.fragment.LiveFinishFragment;
import com.angelatech.yeyelive.fragment.ReadyLiveFragment;
import com.angelatech.yeyelive.model.BarInfoModel;
import com.angelatech.yeyelive.model.ChatLineModel;
import com.angelatech.yeyelive.model.Cocos2dxGiftModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.GiftAnimationModel;
import com.angelatech.yeyelive.model.GiftModel;
import com.angelatech.yeyelive.model.OnlineListModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.socket.room.ServiceManager;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.SPreferencesTool;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.view.CommChooseDialog;
import com.angelatech.yeyelive.view.CommDialog;
import com.angelatech.yeyelive.view.FrescoBitmapUtils;
import com.angelatech.yeyelive.view.GaussAmbiguity;
import com.angelatech.yeyelive.view.LoadingDialogNew;
import com.framework.socket.model.SocketConfig;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.string.json.JsonUtil;
import com.will.common.tool.network.NetWorkUtil;
import com.will.common.tool.time.DateTimeTool;
import com.will.libmedia.MediaCenter;
import com.will.libmedia.MediaNative;
import com.will.libmedia.OnLiveListener;
import com.will.libmedia.OnPlayListener;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import org.cocos2dx.lib.util.Cocos2dxGift;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 视频直播主界面
 */
public class ChatRoomActivity extends BaseActivity implements CallFragment.OnCallEvents, ReadyLiveFragment.OnCallEvents, OnLiveListener, OnPlayListener {
    private Boolean boolCloseRoom = false;
    private CallFragment callFragment;//房间操作
    private ReadyLiveFragment readyLiveFragment = null;//准备播放页面
    private ImageView button_call_disconnect, face, room_guide;
    public RelativeLayout viewPanel;
    private ViewPager mAbSlidingTabView;
    private ServiceManager serviceManager;
    private RoomModel roomModel;                                  //房间信息，其中包括房主信息

    private ChatManager chatManager;
    private ArrayList<Fragment> fragmentList;
    private static List<OnlineListModel> onlineListDatas = new ArrayList<>();         // 房间在线人数列表
    private MyFragmentPagerAdapter fragmentPagerAdapter;
    private int beginTime = 0;          //房间直播开始时间，用来计算房间直播时长

    private BasicUserInfoDBModel userModel;  //登录用户信息
    private BasicUserInfoDBModel liveUserModel; //直播用户信息

    //重连的次数
    private int connectionServiceNumber = 0;
    private LoadingDialogNew LoadingDialog;
    //房间是否初始化
    private boolean isInit = false;
    private boolean isSysMsg = false;
    private static boolean isCloseLiveDialog = false;
    private LiveFinishFragment liveFinishFragment;
    private TimeCount timeCount;
    private long BigData = 0;
    private boolean isbigData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //保持屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        initView();
        findView();
        App.chatRoomApplication = this;
    }

    private void initView() {
        LoadingDialog = new LoadingDialogNew();
        viewPanel = (RelativeLayout) findViewById(R.id.view);
        button_call_disconnect = (ImageView) findViewById(R.id.button_call_disconnect);
        face = (ImageView) findViewById(R.id.face);
        room_guide = (ImageView) findViewById(R.id.room_guide);
        room_guide.setOnClickListener(this);
        mAbSlidingTabView = (ViewPager) findViewById(R.id.mAbSlidingTabView);
        userModel = CacheDataManager.getInstance().loadUser();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.room_guide:
                room_guide.setVisibility(View.GONE);
                SPreferencesTool.getInstance().putValue(this, SPreferencesTool.room_guide_key, false);
                break;
            case R.id.button_call_disconnect:
                CloseLiveDialog();
                break;
        }
    }

    private void findView() {
        if (App.roomModel.getUserInfoDBModel() != null) {
            roomModel = App.roomModel;
            liveUserModel = roomModel.getUserInfoDBModel();
        } else {
            finish();
            return;
        }
        connectionServiceNumber = 0;
        isInit = false;
        isCloseLiveDialog = false;
        chatManager = new ChatManager(this);
        fragmentList = new ArrayList<>();
        callFragment = new CallFragment();
        if (userModel.userid.equals(liveUserModel.userid)) {
            readyLiveFragment = new ReadyLiveFragment();
            fragmentList.add(readyLiveFragment);
        }
        fragmentPagerAdapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager(), fragmentList);
        mAbSlidingTabView.setAdapter(fragmentPagerAdapter);
        mAbSlidingTabView.setCurrentItem(0);
        //清空聊天记录
        App.mChatlines.clear();
        FrescoBitmapUtils.getImageBitmap(ChatRoomActivity.this, VerificationUtil.getImageUrl100(liveUserModel.headurl), new FrescoBitmapUtils.BitCallBack() {
            @Override
            public void onNewResultImpl(Bitmap bitmap) {
                final Drawable drawable = GaussAmbiguity.BlurImages(bitmap, ChatRoomActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        face.setImageDrawable(drawable);
                        face.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                });
            }
        });

        //如果是观众，直接启动房间
        if (roomModel.getRoomType().equals(App.LIVE_WATCH)) {
            fragmentList.add(callFragment);
            fragmentPagerAdapter.notifyDataSetChanged();
            MediaCenter.initPlay(this);
            roomStart();
        }
        //如果是预览，进入预览流程
        if (roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
            face.setVisibility(View.GONE);
            MediaCenter.initLive(ChatRoomActivity.this);

            //美颜开启此属性
            MediaNative.VIDEO_FILTER = false;
            MediaCenter.startRecording(viewPanel, App.screenWidth, App.screenHeight);
        }
        button_call_disconnect.setOnClickListener(this);
    }

    /**
     * 关闭房间
     */
    private void CloseLiveDialog() {
        CommChooseDialog dialog = new CommChooseDialog();
        CommChooseDialog.Callback callback = new CommChooseDialog.Callback() {
            @Override
            public void onCancel() {
                isCloseLiveDialog = false;
            }

            @Override
            public void onOK(boolean choose) {
                //如果是直播，发送下麦通知
                if (roomModel.getRoomType().equals(App.LIVE_HOST) && serviceManager != null) {
                    serviceManager.downMic();
                    App.roomModel.setLivetime(DateTimeTool.DateFormathms(((int) (DateTimeTool.GetDateTimeNowlong() / 1000) - beginTime)));
                    StartActivityHelper.jumpActivity(ChatRoomActivity.this, LiveFinishActivity.class, roomModel);
                    if (choose && (DateTimeTool.GetDateTimeNowlong() / 1000) - beginTime > 60) {
                        LiveQiSaveVideo();
                    }
                } else if (roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
                    //收起键盘
                    if (readyLiveFragment != null) {
                        readyLiveFragment.closekeybord();
                    }
                }
                exitRoom();
            }
        };
        if (!isCloseLiveDialog) {
            isCloseLiveDialog = true;
            if (!liveUserModel.userid.equals(userModel.userid)) {
                dialog.dialog(this, getString(R.string.quit_room), true, false, callback);
            } else {
                dialog.dialog(this, getString(R.string.finish_room), true, true, callback);
            }
        }
    }

    /**
     * 结束直播 对话框
     *
     * @param resId 字符串
     */
    private void endLive(String resId) {
        CommDialog commDialog = new CommDialog();
        CommDialog.Callback callback = new CommDialog.Callback() {
            @Override
            public void onCancel() {
                //结束直播
                if (roomModel.getRoomType().equals(App.LIVE_HOST) && serviceManager != null) {
                    serviceManager.downMic();
                    roomModel.setLivetime(DateTimeTool.DateFormathms(((int) (DateTimeTool.GetDateTimeNowlong() / 1000) - beginTime)));
                    StartActivityHelper.jumpActivity(ChatRoomActivity.this, LiveFinishActivity.class, roomModel);
                } else if (roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
                    //收起键盘
                    if (readyLiveFragment != null) {
                        readyLiveFragment.closekeybord();
                    }
                }
                exitRoom();
            }

            @Override
            public void onOK() {
                restartConnection();
            }
        };
        commDialog.CommDialog(ChatRoomActivity.this, resId, true, callback);
    }

    /**
     * 保存直播视频
     */
    private void LiveQiSaveVideo() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                DebugLogs.e("=========response=====保存录像失败");
            }

            @Override
            public void onSuccess(String response) {
                DebugLogs.e("=========response=====保存录像" + response);
            }
        };
        ChatRoom chatRoom = new ChatRoom(ChatRoomActivity.this);
        chatRoom.LiveQiSaveVideo(CommonUrlConfig.LiveQiSaveVideo, CacheDataManager.getInstance().loadUser(), roomModel.getLiveid(), callback);
    }


    private void roomStart() {
        LoadingDialog.showSysLoadingDialog(this, getString(R.string.room_conne));
        //房间引导页展示
        boolean boolGuide = SPreferencesTool.getInstance().getBooleanValue(this, SPreferencesTool.room_guide_key);
        if (boolGuide) {
            room_guide.setVisibility(View.VISIBLE);
        } else {
            room_guide.setVisibility(View.GONE);
        }
        if (serviceManager == null && roomModel.getIp() != null && roomModel.getPort() != 0) {
            SocketConfig socketConfig = new SocketConfig();
            socketConfig.setHost(roomModel.getIp());
            socketConfig.setPort(roomModel.getPort());
            serviceManager = new ServiceManager(this, socketConfig, roomModel.getId(), uiHandler, userModel);
        } else {
            ToastUtils.showToast(this, getString(R.string.login_room_fail));
        }
    }

    private void restartConnection() {
        connectionServiceNumber++;
        if (connectionServiceNumber < 5) {
            if (serviceManager != null) {
                serviceManager.connectionService();
            }
        } else {
            //五次还是连不上就退出房间
            peerdisConnection(getString(R.string.room_net_toast_error));
        }
    }

    @Override
    public void doHandler(final Message msg) {
        switch (msg.what) {
            case GlobalDef.WM_ROOM_LOGIN_OUT://退出房间
                exitRoom();
                break;
            case GlobalDef.SERVICE_STATUS_FAILD://连接失败
                DebugLogs.e("network test---------faild");
                //如果首次连接失败，给出提示并退出房间
                if (connectionServiceNumber < 1) {
                    ToastUtils.showToast(this, getString(R.string.the_server_connect_fail));
                    exitRoom();
                }
                break;
            case GlobalDef.SERVICE_STATUS_CONNETN:
                DebugLogs.e("network test---------SERVICE_STATUS_CONNETN");
                //失去了连接，重连5次
                if (NetWorkUtil.getActiveNetWorkType(this) == NetWorkUtil.TYPE_MOBILE) {
                    endLive(getString(R.string.traffic_alert));
                }
                break;
            case GlobalDef.SERVICE_STATUS_SUCCESS://房间服务器连接成功
                connectionServiceNumber = 1;
                //房间信息没有初始化才进行下一步，防止断线重连后重复初始化房间信息
                if (!isInit && roomModel != null) {
                    callFragment.setRoomInfo();
                    //检查关注状态
                }
                break;
            case GlobalDef.WM_ROOM_LOGIN://房间登录成功，身份验证通过
                //先判断是不是重连的状态，是重连的登录成功，跳过此步骤
                if (fragmentList.size() > 1) {
                    mAbSlidingTabView.setCurrentItem(1);
                }
                try {
                    BarInfoModel loginMessage = JsonUtil.fromJson(msg.obj.toString(), BarInfoModel.class);
                    if (loginMessage != null && loginMessage.code.equals("0")) {
                        LoadingDialog.cancelLoadingDialog();
                        long coin = loginMessage.coin;
                        //更新金币
                        userModel.diamonds = String.valueOf(coin);
                        CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, userModel.diamonds, userModel.userid);
                        callFragment.setDiamonds(userModel.diamonds);
                        App.roomModel.setLikenum(Integer.parseInt(loginMessage.hot));
                        callFragment.setLikeNum(App.roomModel.getLikenum());
                        serviceManager.getOnlineListUser();
                        if (!isSysMsg) {
                            isSysMsg = true;
                            ChatLineModel chatlinemodel = new ChatLineModel();
                            ChatLineModel.from from = new ChatLineModel.from();
                            from.name = userModel.nickname;
                            from.uid = userModel.userid;
                            from.headphoto = userModel.headurl;
                            from.level = "0";
                            chatlinemodel.from = from;
                            chatlinemodel.type = 9;
                            chatlinemodel.isFirst = true;
                            chatlinemodel.message = String.format(getString(R.string.room_system_message), userModel.nickname);
                            chatManager.AddChatMessage(chatlinemodel);
                            callFragment.notifyData();
                        }

                        if (roomModel.getRoomType().equals(App.LIVE_HOST)) {
                            //如果状态是直播，发送直播上麦
                            serviceManager.sendRTMP_WM_SDP(roomModel.getRtmpip(), "");
                        } else if (roomModel.getRoomType().equals(App.LIVE_WATCH)) {
                            //如果是观看流程，首先检查是否正在直播
                            //上麦
                            if (loginMessage.live == 1) {
                                App.roomModel.setRtmpwatchaddress(loginMessage.live_uri);
                                MediaCenter.startPlay(viewPanel, App.screenWidth, App.screenHeight, roomModel.getRtmpwatchaddress(), ChatRoomActivity.this);
                            } else {
                                //房间未直播
                                liveFinish();
                            }
                        }
                    } else {
                        ToastUtils.showToast(this, getString(R.string.room_login_failed));
                        exitRoom();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case GlobalDef.WM_SDP:
                //判断房间信息是否加载成功，如果没有加载，设置房间信息
                if (!isInit) {
                    callFragment.setLikeNum(App.roomModel.getLikenum());
                    serviceManager.getOnlineListUser();
                    isInit = true;
                }
                break;
            case GlobalDef.WM_ROOM_MESSAGE://接受服务器消息
                CommonModel commonModel_chat = JsonUtil.fromJson(msg.obj.toString(), CommonModel.class);
                if (commonModel_chat != null && commonModel_chat.code.equals("0")) {
                    chatManager.receivedChatMessage(msg.obj, callFragment);
                    callFragment.notifyData();
                    if (timeCount == null) {
                        timeCount = new TimeCount(1000, 100);
                        timeCount.start();
                        BigData = 0;
                        isbigData = false;
                    }
                    BigData++;
                }
                break;
            case GlobalDef.WM_LIVE_SHOWMIC: //主播上麦了，重新观看
                JSONObject jsobj;
                try {
                    jsobj = new JSONObject(msg.obj.toString());
                    if (jsobj.getInt("live") == 0 && roomModel.getRoomType().equals(App.LIVE_WATCH)) {
                        //主播停止直播了
                        liveFinish();
                    } else if (jsobj.getInt("live") == 1 && roomModel.getRoomType().equals(App.LIVE_WATCH)) {
                        if (liveFinishFragment != null) {
                            //恢复播放
                            liveFinishFragment.dismiss();
                            if (roomModel != null) {
                                MediaCenter.startPlay(viewPanel, App.screenWidth, App.screenHeight, roomModel.getRtmpwatchaddress(), ChatRoomActivity.this);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GlobalDef.WM_ROOM_EXIT_ENTRY: //进出房间通知
                OnlineListModel.OnlineNotice onlineNotice = JsonUtil.fromJson(msg.obj.toString(), OnlineListModel.OnlineNotice.class);
                if (onlineNotice != null) {
                    if (onlineNotice.kind == 0) {//上线
                        onlineListDatas.add(onlineNotice.user);
                        roomModel.addlivenum();
                        ChatLineModel chatlinemodel = new ChatLineModel();
                        ChatLineModel.from from = new ChatLineModel.from();
                        from.name = onlineNotice.user.name;
                        from.uid = String.valueOf(onlineNotice.user.uid);
                        from.headphoto = onlineNotice.user.headphoto;
                        from.level = String.valueOf(onlineNotice.user.level);
                        chatlinemodel.from = from;
                        chatlinemodel.type = 0;
                        chatlinemodel.isFirst = true;
                        chatlinemodel.message = getString(R.string.me_online);
                        chatManager.AddChatMessage(chatlinemodel);
                        callFragment.notifyData();
                    } else {
                        for (int i = 0; i < onlineListDatas.size(); i++) {
                            if (onlineListDatas.get(i).uid == onlineNotice.user.uid) {
                                onlineListDatas.remove(i);
                            }
                        }
                    }
                    //更新界面
                    callFragment.initPeopleView(onlineListDatas);
                }
                break;
            case GlobalDef.WM_ROOM_LIKENUM: //有人点赞
                JSONObject jsonlikenum;
                try {
                    jsonlikenum = new JSONObject((String) msg.obj);
                    if (jsonlikenum.getInt("data") > roomModel.getLikenum()) {
                        int count = jsonlikenum.getInt("data") - roomModel.getLikenum();
                        if (!isbigData) {
                            callFragment.runAddLove(count);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GlobalDef.WM_ROOM_RECEIVE_PEOPLE: //收到在线列表
                OnlineData(msg);
                break;
            case GlobalDef.WM_ROOM_SENDGIFT:
                GiftModel.AcceptGift giftModel = JsonUtil.fromJson(msg.obj.toString(), GiftModel.AcceptGift.class);
                if (giftModel == null) {
                    break;
                }
                int gift_Num = giftModel.number;
                String msgstr = "%/%";
                if (giftModel.code.equals("0")) {
                    if (giftModel.type == 6) {//收礼物消息，礼物接受者
                        //加币
                        CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, String.valueOf(giftModel.coin), userModel.userid);
                        callFragment.setDiamonds(String.valueOf(giftModel.coin));
                        //记录金币数量
                        roomModel.addLivecoin(callFragment.getGiftCoinToId(giftModel.giftid) * giftModel.number);
                    } else if (giftModel.type == 61) {//发送礼物消息。发送者消息结果，
                        //减币
                        CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, String.valueOf(giftModel.coin), userModel.userid);
                        callFragment.setDiamonds(String.valueOf(giftModel.coin));
                    }

                    if (giftModel.giftid == 2) {
                        Cocos2dxGiftModel cocos2dxGiftModel = new Cocos2dxGiftModel();
                        cocos2dxGiftModel.aniName = "firework_01_4";
                        cocos2dxGiftModel.imagePath = "firework_01_40.png";
                        cocos2dxGiftModel.plistPath = "firework_01_40.plist";
                        cocos2dxGiftModel.exportJsonPath = "firework_01_4.ExportJson";
                        int x = getResources().getDisplayMetrics().widthPixels / 2;
                        int y = getResources().getDisplayMetrics().heightPixels /2 ;
                        callFragment.play(cocos2dxGiftModel,x,y);
                    }

                    GiftModel giftmodelInfo = callFragment.getGifPath(giftModel.giftid);
                    //礼物特效
                    GiftAnimationModel giftaModel = new GiftAnimationModel();
                    if (giftModel.from != null) {
                        giftaModel.userheadpoto = giftModel.from.headphoto;
                        giftaModel.from_uname = giftModel.from.name;
                    }
                    giftaModel.giftmodel = giftmodelInfo;
                    giftaModel.giftnum = gift_Num;
                    callFragment.addGifAnimation(giftaModel);
                    ChatLineModel chatLineModel = new ChatLineModel();
                    chatLineModel.giftmodel = giftModel;
                    chatLineModel.type = 0;
                    chatLineModel.message = msgstr;
                    chatManager.AddChatMessage(chatLineModel);
                    callFragment.notifyData();
                } else if (giftModel.code.equals(String.valueOf(GlobalDef.NOT_SUFFICIENT_COIN_1012))) {
                    ToastUtils.showToast(ChatRoomActivity.this, getString(R.string.gift_code1012));
                }
                break;
            case GlobalDef.WM_ROOM_Kicking:
                JSONObject jsonkicking;
                try {
                    jsonkicking = new JSONObject((String) msg.obj);
                    if (jsonkicking.getInt("code") == 0 && jsonkicking.getJSONObject("from") != null) {
                        ToastUtils.showToast(ChatRoomActivity.this, getString(R.string.you_are_invited_out_of_the_room));
                        finish();

                    } else if (jsonkicking.getInt("code") == GlobalDef.NO_PERMISSION_OPE_1009) {
                        ToastUtils.showToast(ChatRoomActivity.this, getString(R.string.not_font));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 在线列表数据处理
     */
    private void OnlineData(Message msg) {
        CommonListResult<OnlineListModel> results = JsonUtil.fromJson(msg.obj.toString(), new TypeToken<CommonListResult<OnlineListModel>>() {
        }.getType());
        if (results == null) {
            return;
        }
        if (results.code.equals("0")) {
            onlineListDatas = results.users;//在线列表
            callFragment.initPeopleView(onlineListDatas);
        }
    }

    //主播结束了直播
    private void liveFinish() {
        face.setVisibility(View.GONE);
        liveFinishFragment = new LiveFinishFragment();
        liveFinishFragment.setRoomModel(roomModel);
        liveFinishFragment.show(getSupportFragmentManager(), "");
    }

    //切换摄像头
    @Override
    public void onCameraSwitch() {
        MediaCenter.switchCamera();
    }

    //发送私人消息
    @Override
    public void onSendMessage(String msg) {
        if (serviceManager != null) {
            serviceManager.sendRoomMessage(msg);
            ChatLineModel chatLineModel = new ChatLineModel();
            ChatLineModel.from from = new ChatLineModel.from();
            from.name = userModel.nickname;
            from.uid = String.valueOf(userModel.userid);
            chatLineModel.from = from;
            chatLineModel.type = 0;
            chatLineModel.message = msg;
            chatManager.AddChatMessage(chatLineModel);
            callFragment.notifyData();
        }
    }

    @Override
    public void onSendGift(int toid, int giftId, int nNum) {
        if (serviceManager != null) {
            serviceManager.sendGift(toid, giftId, nNum);
        }
    }

    @Override
    public void sendLove(int num) {
        if (serviceManager != null) {
            serviceManager.sendLove(num);
        }
    }

    @Override
    public void kickedOut(String userId) {
        if (serviceManager != null) {
            serviceManager.kickedOut(userId);
        }
    }

    @Override
    public void closeLive() {
        CloseLiveDialog();
    }

    /**
     * 点击物理返回按钮**
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isCloseLiveDialog = false;
            if (!roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
                if (callFragment.getBackState()) {
                    callFragment.keyBack();
                    return true;
                } else {
                    CloseLiveDialog();
                }
            } else {
                CloseLiveDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        if (roomModel != null && roomModel.getRoomType().equals(App.LIVE_HOST)) {
            MediaCenter.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (roomModel != null && roomModel.getRoomType().equals(App.LIVE_HOST)) {
            MediaCenter.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (!boolCloseRoom) {
            exitRoom();
        }
        super.onDestroy();
        System.gc();
    }

    /**
     * 退房间操作
     */
    private void roomFinish() {
        uiHandler.removeCallbacksAndMessages(null);
        if (serviceManager != null) {
            serviceManager.quitRoom();
            serviceManager = null;
        }
        if (App.roomModel != null && App.roomModel.getRoomType().equals(App.LIVE_WATCH)) {
            MediaCenter.destoryPlay();
        } else {
            MediaCenter.destoryLive();
        }
        App.mChatlines.clear();
        App.roomModel = new RoomModel();
        userModel = null;
        liveUserModel = null;
        boolCloseRoom = true;
        App.chatRoomApplication = null;
    }

    private void peerdisConnection(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommDialog commDialog = new CommDialog();
                CommDialog.Callback callback = new CommDialog.Callback() {
                    @Override
                    public void onCancel() {
                        isCloseLiveDialog = false;
                        if (roomModel.getRoomType().equals(App.LIVE_HOST)) {
                            StartActivityHelper.jumpActivity(ChatRoomActivity.this, LiveFinishActivity.class, roomModel);
                        }
                        exitRoom();
                    }

                    @Override
                    public void onOK() {
                        //如果是直播，发送下麦通知
                        if (roomModel.getRoomType().equals(App.LIVE_HOST)) {
                            StartActivityHelper.jumpActivity(ChatRoomActivity.this, LiveFinishActivity.class, roomModel);
                        } else if (roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
                            if (readyLiveFragment != null) {
                                readyLiveFragment.closekeybord();
                            }
                        }
                        exitRoom();
                    }
                };
                if (!isCloseLiveDialog) {
                    if (roomModel.getRoomType().equals(App.LIVE_HOST) && serviceManager != null) {
                        serviceManager.downMic();
                        roomModel.setLivetime(DateTimeTool.DateFormathms(((int) (DateTimeTool.GetDateTimeNowlong() / 1000) - beginTime)));
                    }
                    isCloseLiveDialog = true;
                    commDialog.CommDialog(ChatRoomActivity.this, s, false, callback);
                }
            }
        });
    }

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            isbigData = BigData > 100;
        }

        @Override
        public void onFinish() {
            BigData = 0;
            timeCount.cancel();
            timeCount = null;
        }
    }

    /***
     * 开播
     */
    @Override
    public void onBeginLive() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaCenter.startLive(roomModel.getRtmpip(), ChatRoomActivity.this);
                beginTime = (int) (DateTimeTool.GetDateTimeNowlong() / 1000);
                if (callFragment != null) {
                    fragmentList.add(callFragment);
                    fragmentPagerAdapter.notifyDataSetChanged();
                    //fragmentPagerAdapter.setFragmentsList(callFragment);
                    if (fragmentList.size() > 1) {
                        mAbSlidingTabView.setCurrentItem(1);
                    }
                }
                roomModel.setRoomType(App.LIVE_HOST);
                roomStart();
            }
        });
    }

    /**
     * 直播者回调函数
     *
     * @param rtmpUrl url
     * @param event   event
     */
    @Override
    public void onLiveCallback(String rtmpUrl, int event) {
        DebugLogs.e("rtmp event" + event);
        switch (event) {
            case MediaNative.RTMP_LIVE_RECONNECTING:
                //ToastUtils.showToast(ChatRoomActivity.this,"网络开小差了");
                //断线重连中
                break;
            case MediaNative.RTMP_LIVE_RECONNECT:
                face.setVisibility(View.GONE);
                DebugLogs.e("-------RTMP_LIVE_RECONNECT---------");
                //重连成功
                break;
            case MediaNative.RTMP_LIVE_CONNECT_ERROR:
                // ToastUtils.showToast(ChatRoomActivity.this,"主播正在化妆");
                //直播错误
                peerdisConnection(getString(R.string.room_net_toast_error));
                break;
            case MediaNative.RTMP_LIVE_CONNECT:
                break;
            case MediaNative.RTMP_LIVE_STOP:
                // ToastUtils.showToast(ChatRoomActivity.this,"网络去哪了？");
                // roomFinish();
                break;
        }
    }

    /**
     * 观看的人 回调
     */
    @Override
    public void onPlayCallback(String rtmpUrl, int event) {

        DebugLogs.e("rtmp event" + event);
        switch (event) {
            case MediaNative.RTMP_PLAY_RECONNECTING://流媒体重连
                //ToastUtils.showToast(ChatRoomActivity.this,"网络开小差了");
                //重连
                break;
            case MediaNative.RTMP_PLAY_RECONNECT://
                //重连成功
                break;
            case MediaNative.RTMP_PLAY_CONNECT:
                face.setVisibility(View.GONE);
                break;
            case MediaNative.RTMP_PLAY_STOP:
                break;
            case MediaNative.RTMP_PLAY_CONNECT_ERROR:
                peerdisConnection(getString(R.string.room_net_toast_error));
                break;
        }
    }


    /**
     * 退出房间
     */
    public void exitRoom() {
        if (!boolCloseRoom) {
            roomFinish();
        }
        finish();
    }
}
