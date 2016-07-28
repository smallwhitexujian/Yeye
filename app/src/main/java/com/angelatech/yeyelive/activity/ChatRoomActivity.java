package com.angelatech.yeyelive.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angelatech.yeyelive.GlobalDef;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.function.ChatManager;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.fragment.ReadyLiveFragment;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.model.GiftModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.view.CommDialog;
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
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.adapter.MyFragmentPagerAdapter;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.fragment.CallFragment;
import com.angelatech.yeyelive.fragment.LiveFinishFragment;
import com.angelatech.yeyelive.model.BarInfoModel;
import com.angelatech.yeyelive.model.ChatLineModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.GiftAnimationModel;
import com.angelatech.yeyelive.model.OnlineListModel;
import com.angelatech.yeyelive.socket.room.ServiceManager;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.SPreferencesTool;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.FrescoBitmapUtils;
import com.angelatech.yeyelive.view.GaussAmbiguity;
import com.angelatech.yeyelive.view.LoadingDialog;

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
    private ReadyLiveFragment readyLiveFragment;//准备播放页面
    private ImageView button_call_disconnect, face, room_guide;
    public RelativeLayout viewPanel;
    private ViewPager mAbSlidingTabView;
    public static ServiceManager serviceManager;
    public static RoomModel roomModel;                                  //房间信息，其中包括房主信息

    private ChatManager chatManager;
    private ArrayList<Fragment> fragmentList;
    private static List<OnlineListModel> onlineListDatas = null;         // 房间在线人数列表
    private MyFragmentPagerAdapter fragmentPagerAdapter;
    private int beginTime = 0;          //房间直播开始时间，用来计算房间直播时长
    public static final int MSG_OPEN_GIFT_LAYOUT = 0xfff;

    public static BasicUserInfoDBModel userModel;  //登录用户信息
    public static BasicUserInfoDBModel liveUserModel; //直播用户信息

    //重连的次数
    private int connectionServiceNumber = 0;

    //房间是否初始化
    private boolean isInit = false;
    private boolean isSysMsg = false;
    private static boolean isCloseLiveDialog = false;
    private LiveFinishFragment liveFinishFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //保持屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
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
        connectionServiceNumber = 0;
        isInit = false;

        isCloseLiveDialog = false;
        chatManager = new ChatManager(ChatRoomActivity.this);
        callFragment = new CallFragment();
        readyLiveFragment = new ReadyLiveFragment();
        onlineListDatas = new ArrayList<>();
        fragmentList = new ArrayList<>();
        fragmentList.add(readyLiveFragment);
        fragmentPagerAdapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager(), fragmentList);
        mAbSlidingTabView.setAdapter(fragmentPagerAdapter);
        mAbSlidingTabView.setCurrentItem(0);
        //清空聊天记录
        App.mChatlines.clear();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            roomModel = (RoomModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            liveUserModel = roomModel.getUserInfoDBModel();
            if (liveUserModel.userid.equals(userModel.userid)) {
                liveUserModel = userModel;
            }
        }
        if (roomModel == null) {
            finish();
            return;
        }
        FrescoBitmapUtils.getImageBitmap(ChatRoomActivity.this, liveUserModel.headurl, new FrescoBitmapUtils.BitCallBack() {
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
        callFragment.setArguments(bundle);
        readyLiveFragment.setArguments(bundle);
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

//            //美颜
//            int filter = MediaCenter.getVideoFilterType();
//            if (filter == MediaNative.VIDEO_FILTER_BEAUTIFUL) {
//                MediaCenter.setVideoFilter(MediaNative.VIDEO_FILTER_NONE);
//            } else {
//                MediaCenter.setVideoFilter(MediaNative.VIDEO_FILTER_BEAUTIFUL);
//            }
        }
        button_call_disconnect.setOnClickListener(this);
    }

    /**
     * 关闭房间
     */
    public void CloseLiveDialog() {

        CommDialog commDialog = new CommDialog();
        CommDialog.Callback callback = new CommDialog.Callback() {
            @Override
            public void onCancel() {
                isCloseLiveDialog = false;
            }

            @Override
            public void onOK() {
                //如果是直播，发送下麦通知
                if (roomModel.getRoomType().equals(App.LIVE_HOST) && serviceManager != null) {
                    serviceManager.downMic();
                    roomModel.setLivetime(DateTimeTool.DateFormathms(((int) (DateTimeTool.GetDateTimeNowlong() / 1000) - beginTime)));
                    StartActivityHelper.jumpActivity(ChatRoomActivity.this, LiveFinishActivity.class, roomModel);
                } else if (roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
                    //收起键盘
                    readyLiveFragment.closekeybord();
                }
                finish();
            }
        };
        if (!isCloseLiveDialog) {
            isCloseLiveDialog = true;
            if (roomModel.getRoomType().equals(App.LIVE_WATCH)) {
                commDialog.CommDialog(ChatRoomActivity.this, getString(R.string.quit_room), true, callback);
            } else {
                commDialog.CommDialog(ChatRoomActivity.this, getString(R.string.finish_room), true, callback);
            }
        }
    }


    private void roomStart() {
        LoadingDialog.showSysLoadingDialog(ChatRoomActivity.this, getString(R.string.room_conne));
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
            serviceManager = new ServiceManager(ChatRoomActivity.this, socketConfig, roomModel.getId(), uiHandler, userModel);
        } else {
            ToastUtils.showToast(ChatRoomActivity.this, getString(R.string.login_room_fail));
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
    public void doHandler(Message msg) {
        switch (msg.what) {
            case GlobalDef.WM_ROOM_LOGIN_OUT://退出房间
                finish();
                break;
            case GlobalDef.SERVICE_STATUS_FAILD://连接失败
                DebugLogs.e("network test---------faild");
                //如果首次连接失败，给出提示并退出房间
                if (connectionServiceNumber < 1) {
                    ToastUtils.showToast(ChatRoomActivity.this, getString(R.string.the_server_connect_fail));
                    finish();
                }

                break;
            case GlobalDef.SERVICE_STATUS_CONNETN:
                DebugLogs.e("network test---------SERVICE_STATUS_CONNETN");
                //失去了连接，重连5次
                if (NetWorkUtil.getActiveNetWorkType(ChatRoomActivity.this) == NetWorkUtil.TYPE_MOBILE) {
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
                                readyLiveFragment.closekeybord();
                            }
                            finish();
                        }

                        @Override
                        public void onOK() {
                            restartConnection();
                        }
                    };
                    commDialog.CommDialog(ChatRoomActivity.this, getString(R.string.traffic_alert), true, callback);
                } else {
                    restartConnection();
                }

                break;
            case GlobalDef.SERVICE_STATUS_SUCCESS://房间服务器连接成功
                connectionServiceNumber = 1;
                //房间信息没有初始化才进行下一步，防止断线重连后重复初始化房间信息
                if (!isInit && roomModel != null) {
                    callFragment.setRoomInfo(roomModel);
                    //检查关注状态
                    if (roomModel.getRoomType().equals(App.LIVE_HOST)) {
                        callFragment.setIsFollow(-1);
                    } else if (roomModel.getRoomType().equals(App.LIVE_WATCH)) {
                        UserIsFollow();
                    }
                }
                break;
            case GlobalDef.WM_ROOM_LOGIN://房间登录成功，身份验证通过
                //先判断是不是重连的状态，是重连的登录成功，跳过此步骤
                if (fragmentList.size() > 1) {
                    mAbSlidingTabView.setCurrentItem(1);
                }
                callFragment.setCameraSwitchButton(roomModel.getRoomType());
                JSONObject json;
                try {
                    json = new JSONObject(msg.obj.toString());
                    int code = json.getInt("code");

                    if (code == 0) {
                        LoadingDialog.cancelLoadingDialog();
                        int coin = json.getInt("coin");
                        //更新金币
                        userModel.diamonds = String.valueOf(coin);
                        CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, userModel.diamonds, userModel.userid);
                        callFragment.setDiamonds(userModel.diamonds);
                        roomModel.setLikenum(json.getInt("hot"));
                        callFragment.setLikenum(roomModel.getLikenum());
                        serviceManager.getOnlineListUser();
                        BarInfoModel loginMessage = JsonUtil.fromJson(msg.obj.toString(), BarInfoModel.class);
                        if (loginMessage != null) {
                            roomModel.setLikenum(Integer.parseInt(loginMessage.hot));
                        }

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
                            callFragment.initChatMessage(ChatRoomActivity.this);
                        }

                        if (roomModel.getRoomType().equals(App.LIVE_HOST)) {
                            //如果状态是直播，发送直播上麦
                            //roomModel.setRtmpip("rtmp://pili-publish.ps.qiniucdn.com/NIU7PS/0601d-test?key=efdbc36f-8759-44c2-bdd8-873521b6724a");
                            serviceManager.sendRTMP_WM_SDP(roomModel.getRtmpip(), "");
                        } else if (roomModel.getRoomType().equals(App.LIVE_WATCH)) {
                            //如果是观看流程，首先检查是否正在直播

                            //上麦
                            if (loginMessage != null && loginMessage.live == 1) {
                                roomModel.setRtmpwatchaddress(loginMessage.live_uri);
                                DebugLogs.e("rtmp startPlay");

                                DebugLogs.e("rtmp App.screenWidth" + App.screenWidth + "App.screenHeight" +
                                        App.screenHeight + "roomModel.getRtmpwatchaddress()" + roomModel.getRtmpwatchaddress());

                                MediaCenter.startPlay(viewPanel, App.screenWidth, App.screenHeight, roomModel.getRtmpwatchaddress(), ChatRoomActivity.this);
                            } else {
                                //房间未直播
                                liveFinish();
                            }
                        }
                    } else {
                        ToastUtils.showToast(ChatRoomActivity.this, getString(R.string.room_login_failed));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case GlobalDef.WM_SDP:
                //判断房间信息是否加载成功，如果没有加载，设置房间信息
                if (!isInit) {
                    callFragment.setLikenum(roomModel.getLikenum());
                    serviceManager.getOnlineListUser();
                    isInit = true;
                }
                break;
            case GlobalDef.WM_CANDIDATE:
                break;
            case GlobalDef.WM_ROOM_MESSAGE://接受服务器消息
                CommonModel commonModel_chat = JsonUtil.fromJson(msg.obj.toString(), CommonModel.class);
                if (commonModel_chat != null && commonModel_chat.code.equals("0")) {
                    chatManager.receivedChatMessage(msg.obj);
                    callFragment.initChatMessage(ChatRoomActivity.this);
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
                            MediaCenter.startPlay(viewPanel, App.screenWidth, App.screenHeight, roomModel.getRtmpwatchaddress(), ChatRoomActivity.this);
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
                        callFragment.initChatMessage(ChatRoomActivity.this);

                    } else {
                        for (int i = 0; i < ChatRoomActivity.onlineListDatas.size(); i++) {
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
                        callFragment.runAddLove(jsonlikenum.getInt("data") - roomModel.getLikenum());
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
                        roomModel.addLivecoin(callFragment.getGiftCointoId(giftModel.giftid) * giftModel.number);
                    } else if (giftModel.type == 61) {//发送礼物消息。发送者消息结果，
                        //减币
                        CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, String.valueOf(giftModel.coin), userModel.userid);
                        callFragment.setDiamonds(String.valueOf(giftModel.coin));
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
                    callFragment.initChatMessage(ChatRoomActivity.this);
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
            case MSG_OPEN_GIFT_LAYOUT:
                callFragment.getFragmentHandler().obtainMessage(MSG_OPEN_GIFT_LAYOUT).sendToTarget();
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

    //检查是否关注
    private void UserIsFollow() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    //是否关注
                    int isfollow = json.getJSONObject("data").getInt("isfollow");
                    callFragment.setIsFollow(isfollow);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ChatRoom chatRoom = new ChatRoom(this);
        chatRoom.UserIsFollow(CommonUrlConfig.UserIsFollow, userModel.token, userModel.userid, liveUserModel.userid, callback);
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
            callFragment.initChatMessage(ChatRoomActivity.this);
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

    /**
     * 点击物理返回按钮**
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isCloseLiveDialog = false;
            if (!roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
                if (callFragment.getBackState()) {
                    callFragment.keyback();
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
        if (roomModel.getRoomType().equals(App.LIVE_HOST)) {
            MediaCenter.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (roomModel.getRoomType().equals(App.LIVE_HOST)) {
            MediaCenter.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (!boolCloseRoom) {
            roomFinish();
        }
        super.onDestroy();
    }

    /**
     * 退房间操作
     */
    public void roomFinish() {
        if (serviceManager != null) {
            serviceManager.quitRoom();
            serviceManager = null;
        }
        if (roomModel.getRoomType().equals(App.LIVE_WATCH)) {
            MediaCenter.destoryPlay();
        } else {
            MediaCenter.destoryLive();
        }
        boolCloseRoom = true;
    }

    public void openGiftLayout() {
        uiHandler.obtainMessage(MSG_OPEN_GIFT_LAYOUT).sendToTarget();
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
                        if (roomModel.getRoomType().equals(App.LIVE_HOST) && serviceManager != null) {
                            StartActivityHelper.jumpActivity(ChatRoomActivity.this, LiveFinishActivity.class, roomModel);
                        }
                        finish();
                    }

                    @Override
                    public void onOK() {
                        //如果是直播，发送下麦通知
                        if (roomModel.getRoomType().equals(App.LIVE_HOST) && serviceManager != null) {
                            StartActivityHelper.jumpActivity(ChatRoomActivity.this, LiveFinishActivity.class, roomModel);
                        } else if (roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
                            readyLiveFragment.closekeybord();
                        }
                        finish();
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

    @Override
    public void onBeginLive() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // roomModel.setRtmpip("rtmp://pili-publish.ps.qiniucdn.com/NIU7PS/0601d-test?key=efdbc36f-8759-44c2-bdd8-873521b6724a");
                MediaCenter.startLive(roomModel.getRtmpwatchaddress(), ChatRoomActivity.this);
                beginTime = (int) (DateTimeTool.GetDateTimeNowlong() / 1000);
                if (callFragment != null) {
                    fragmentPagerAdapter.setFragmentsList(callFragment);
                    if (fragmentList.size() > 1) {
                        mAbSlidingTabView.setCurrentItem(1);
                    }
                }
                ChatRoomActivity.roomModel.setRoomType(App.LIVE_HOST);
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
     *
     * @param rtmpUrl
     * @param event
     */
    @Override
    public void onPlayCallback(String rtmpUrl, int event) {

        DebugLogs.e("rtmp event" + event);
        switch (event) {
            case MediaNative.RTMP_PLAY_RECONNECTING:
                //ToastUtils.showToast(ChatRoomActivity.this,"网络开小差了");
                //重连
                break;
            case MediaNative.RTMP_PLAY_RECONNECT:
                //重连成功
                break;
            case MediaNative.RTMP_PLAY_CONNECT:
                face.setVisibility(View.GONE);
                break;
            case MediaNative.RTMP_PLAY_STOP:
                break;
            case MediaNative.RTMP_PLAY_CONNECT_ERROR:
                DebugLogs.e("===============RTMP_PLAY_CONNECT_ERROR==================");
                peerdisConnection(getString(R.string.room_net_toast_error));
                break;
        }
    }
}
