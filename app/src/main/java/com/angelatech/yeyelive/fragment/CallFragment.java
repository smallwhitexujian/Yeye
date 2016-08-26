package com.angelatech.yeyelive.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.GlobalDef;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.RechargeActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.adapter.ChatLineAdapter;
import com.angelatech.yeyelive.adapter.CustomerPageAdapter;
import com.angelatech.yeyelive.adapter.GridViewAdapter;
import com.angelatech.yeyelive.adapter.HorizontalListViewAdapter;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.ChatLineModel;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.GiftAnimationModel;
import com.angelatech.yeyelive.model.GiftModel;
import com.angelatech.yeyelive.model.OnlineListModel;
import com.angelatech.yeyelive.thirdShare.FbShare;
import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.util.BinarySearch;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.DelHtml;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.view.PeriscopeLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.will.common.tool.network.NetWorkUtil;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.util.Cocos2dxGift;
import org.cocos2dx.lib.util.Cocos2dxView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fragment 视频操作类
 */
public class CallFragment extends BaseFragment implements View.OnClickListener {
    private View controlView;
    private final int MSG_DO_FOLLOW = 15;
    private final int MSG_CANCEL_FOLLOW = 16;
    private final int MSG_OPEN_GIFT_LAYOUT = 17;
    private final int MSG_ADAPTER_NOTIFY_GIFT = 172;
    private final int MSG_SET_FOLLOW = 2;
    private final int HANDLER_GIFT_CHANGE_BACKGROUND = 13;
    private final int SHOW_SOFT_KEYB = 14;
    private final int ONSHOW_SOFT_KEYB = 12;
    private ImageView cameraSwitchButton;

    private ImageView btn_Follow, btn_share, iv_vip, btn_beautiful, btn_lamp;
    private TextView txt_barName, txt_likeNum, txt_online, gift_Diamonds, txt_room_des;
    private SimpleDraweeView img_head;
    private PeriscopeLayout loveView;                                                               // 显示心的VIEW
    private EditText txt_msg;
    private LinearLayout ly_send, ly_toolbar, ly_main, giftView;                                    // 礼物界面
    private Spinner roomPopSpinner, roomGiftNumSpinner;                                             // 礼物个数列表
    private int giftId;                                                                             // 礼物的ID
    private int isFollow;
    private ListView chatline;
    private OnCallEvents callEvents;
    private List<OnlineListModel> PopLinkData = new ArrayList<>();
    private final int numArray[] = {1, 10, 22, 55, 77, 100}; //礼物数量列表
    private ArrayList<GiftAnimationModel> giftModelList = new ArrayList<>();
    private ChatLineAdapter<ChatLineModel> mAdapter;

    private RelativeLayout ly_gift_view;                                                            //礼物特效view
    private TextView numText, numText1;                                                             //礼物数量  阴影
    private TextView txt_from_user;                                                  //发送礼物的人，礼物名称
    private SimpleDraweeView imageView, gif_img_head;                                               //礼物图片， 礼物发送人的头像
    private Animation translateAnimation_in, translateAnimation_out, translate_in, scaleAnimation;  //礼物特效

    private boolean giftA = false;                                                                  //礼物特效播放状态

    private boolean isRun = false;
    private boolean isTimeCount = true;                                 // 是否打开倒计时
    private boolean isTimeCount2 = true;                                 // 是否打开倒计时
    private long lastClick;                                             // 点赞点击事件
    private int count = 0;                                              // 统计点赞次数

    private TimeCount timeCount;

    private ViewPager viewPager;
    private List<GridView> gridViews = new ArrayList<>();

    private int GridViewIndex = 0;
    private int GridViewLastIndex = -1;
    private int GridViewItemIndex = 0;
    private int GridViewItemLastIndex = -1;
    private ArrayAdapter<OnlineListModel> PopAdapter;
    private TimeCount2 timeCount2;
    private BasicUserInfoDBModel userModel;  //登录用户信息
    private BasicUserInfoDBModel liveUserModel; //直播用户信息
    public static final Object lock = new Object();
    private ChatRoom chatRoom;
    //cocos2d
    private FrameLayout giftLayout;
    private Cocos2dxView cocos2dxView = new Cocos2dxView();
    private Cocos2dxGift cocos2dxGift = new Cocos2dxGift();

    private GridView grid_online;
    private HorizontalListViewAdapter horizontalListViewAdapter;
    private List<OnlineListModel> showList = new ArrayList<>();
    private RelativeLayout rootView;

    public void setDiamonds(String diamonds) {
        gift_Diamonds.setText(diamonds);
    }


    public interface OnCallEvents {
        //切换摄像头
        void onCameraSwitch();

        //发送消息
        void onSendMessage(String msg);

        //发送礼物
        void onSendGift(int toid, int giftId, int nNum);

        //点赞
        void sendLove(int num);

        //踢人
        void kickedOut(String userId);

        //结束直播
        void closeLive();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        controlView = inflater.inflate(R.layout.fragment_call, container, false);
        initView();
        initCocos2dx();
        return controlView;
    }

    //根据礼物ID获取礼物单价
    public int getGiftCoinToId(int giftId) {
        int k = App.giftdatas.size();
        for (int i = 0; i < k; i++) {
            if (App.giftdatas.get(i).getID() == giftId) {
                return Integer.parseInt(App.giftdatas.get(i).getPrice());
            }
        }
        return 0;
    }

    private void initView() {
        if (App.roomModel.getUserInfoDBModel() != null) {
            liveUserModel = App.roomModel.getUserInfoDBModel();
        }
        userModel = CacheDataManager.getInstance().loadUser();
        chatRoom = new ChatRoom(getActivity());
        cameraSwitchButton = (ImageView) controlView.findViewById(R.id.button_call_switch_camera);
        txt_msg = (EditText) controlView.findViewById(R.id.txt_msg);
        Button btn_send = (Button) controlView.findViewById(R.id.btn_send);
        chatline = (ListView) controlView.findViewById(R.id.chatline);
        initChatMessage(getActivity());
        ImageView img_open_send = (ImageView) controlView.findViewById(R.id.img_open_send);
        ImageView giftBtn = (ImageView) controlView.findViewById(R.id.giftbtn);
        ly_toolbar = (LinearLayout) controlView.findViewById(R.id.ly_toolbar);
        ly_send = (LinearLayout) controlView.findViewById(R.id.ly_send);
        ly_main = (LinearLayout) controlView.findViewById(R.id.ly_main);
        loveView = (PeriscopeLayout) controlView.findViewById(R.id.PeriscopeLayout);
        img_head = (SimpleDraweeView) controlView.findViewById(R.id.img_head);
        giftView = (LinearLayout) controlView.findViewById(R.id.giftView);
        viewPager = (ViewPager) controlView.findViewById(R.id.viewPager); //礼物 viewpager
        roomGiftNumSpinner = (Spinner) controlView.findViewById(R.id.roomGiftNumSpinner);
        roomPopSpinner = (Spinner) controlView.findViewById(R.id.roomPopSpinner);
        LinearLayout gift_send = (LinearLayout) controlView.findViewById(R.id.gift_send);
        txt_barName = (TextView) controlView.findViewById(R.id.txt_barname);
        txt_likeNum = (TextView) controlView.findViewById(R.id.txt_likenum);
        txt_online = (TextView) controlView.findViewById(R.id.txt_online);
        btn_Follow = (ImageView) controlView.findViewById(R.id.btn_Follow);
        btn_share = (ImageView) controlView.findViewById(R.id.btn_share);
        btn_beautiful = (ImageView) controlView.findViewById(R.id.button_beautiful);
        btn_lamp = (ImageView) controlView.findViewById(R.id.button_lamp);
        iv_vip = (ImageView) controlView.findViewById(R.id.iv_vip);
        gift_Diamonds = (TextView) controlView.findViewById(R.id.gift_Diamonds);
        txt_room_des = (TextView) controlView.findViewById(R.id.txt_room_des);
        TextView gift_Recharge = (TextView) controlView.findViewById(R.id.gift_Recharge);
        grid_online = (GridView) controlView.findViewById(R.id.grid_online);
        rootView = (RelativeLayout) controlView.findViewById(R.id.rootView);
        ly_main.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        img_open_send.setOnClickListener(this);
        cameraSwitchButton.setOnClickListener(this);
        gift_send.setOnClickListener(this);
        giftBtn.setOnClickListener(this);
        btn_Follow.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        img_head.setOnClickListener(this);
        txt_barName.setOnClickListener(this);
        btn_lamp.setOnClickListener(this);
        btn_beautiful.setOnClickListener(this);
        gift_Recharge.setOnClickListener(this);

        grid_online.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OnlineListModel onlineModel = showList.get(i);
                BasicUserInfoModel userInfo = new BasicUserInfoModel();
                userInfo.Userid = String.valueOf(onlineModel.uid);
                userInfo.nickname = onlineModel.name;
                userInfo.headurl = onlineModel.headphoto;
                userInfo.isv = onlineModel.isv;
                userInfo.sex = String.valueOf(onlineModel.sex);
                onShowUser(userInfo);
            }
        });

        txt_msg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sendMsg();
                    return true;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                GridViewIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        txt_from_user = (TextView) controlView.findViewById(R.id.txt_from_user);
        imageView = (SimpleDraweeView) controlView.findViewById(R.id.img_gift);
        gif_img_head = (SimpleDraweeView) controlView.findViewById(R.id.gif_img_head);
        ly_gift_view = (RelativeLayout) controlView.findViewById(R.id.ly_gift_view);
        numText = (TextView) controlView.findViewById(R.id.numText);
        numText1 = (TextView) controlView.findViewById(R.id.numText1);

        TextPaint tp1 = numText.getPaint();
        tp1.setStrokeWidth(3);
        tp1.setStyle(Paint.Style.FILL_AND_STROKE);
        tp1.setFakeBoldText(true);

        translateAnimation_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);
        translate_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade2_in_anim);
        translateAnimation_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_anim);
        scaleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.thepinanim);
        translateAnimation_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                String str = "x1";
                numText.setText(str);
                numText1.setText(str);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                numText.setVisibility(View.VISIBLE);
                numText.startAnimation(scaleAnimation);
                if (giftModelList.size() > 0) {
                    addGiftAnimationNum(giftModelList.get(0));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ly_gift_view.startAnimation(translateAnimation_out);
                giftA = false;
                try {
                    if (giftModelList.size() > 0) {
                        giftModelList.remove(0);
                    }
                    if (giftModelList.size() > 0) {
                        startGiftAnimation(giftModelList.get(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }
    int mVisibleHeight;
    //键盘状态监听
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final Rect rect = new Rect();
            rootView.getWindowVisibleDisplayFrame(rect);
            int screenHeight = rootView.getRootView().getHeight();
            int visibleHeight = rect.height();
            int heightDifference = screenHeight - (rect.bottom - rect.top);
            if (mVisibleHeight == 0) {
                mVisibleHeight = visibleHeight;
                return;
            }
            if (mVisibleHeight == visibleHeight) {
                return;
            }
            mVisibleHeight = visibleHeight;
            boolean visible = heightDifference > screenHeight / 3;
            if (visible) {
                getFragmentHandler().obtainMessage(SHOW_SOFT_KEYB, heightDifference).sendToTarget();
            }else{
                getFragmentHandler().obtainMessage(ONSHOW_SOFT_KEYB).sendToTarget();
            }
        }
    };

    //初始化cocos
    private void initCocos2dx() {
        //coco2动画SurfaceView
        cocos2dxView.onCreate(getActivity(), 0);
        Cocos2dxGLSurfaceView.ScaleInfo scaleInfo = new Cocos2dxGLSurfaceView.ScaleInfo();
        scaleInfo.nomal = true;
        scaleInfo.width = ScreenUtils.getScreenWidth(getActivity());
        scaleInfo.height = ScreenUtils.getScreenHeight(getActivity());
        cocos2dxView.setScaleInfo(scaleInfo);
        giftLayout = cocos2dxView.getFrameLayout();
        ((FrameLayout) controlView.findViewById(R.id.gift_layout)).addView(giftLayout);
    }


    /**
     * 初始化列表
     */
    public void InitializeOnline(List<OnlineListModel> lineData) {
        showList.clear();
        String uid;
        for (OnlineListModel item : lineData) {
            synchronized (lock) {
                uid = String.valueOf(item.uid);
                if (!uid.equals(liveUserModel.userid) && !uid.equals(userModel.userid)) {
                    int pos = BinarySearch.binSearch(showList, 0, showList.size(), item);
                    showList.add(pos, item);
                }
            }
        }
        /**
         * 添加当前登录用户
         */
        if (!liveUserModel.userid.equals(userModel.userid)) {
            OnlineListModel model = new OnlineListModel();
            model.uid = Integer.parseInt(userModel.userid);
            model.role = userModel.role;
            model.headphoto = userModel.headurl;
            model.isv = userModel.isv;
            model.name = userModel.nickname;
            model.sex = Integer.parseInt(userModel.sex);
            showList.add(0, model);
        }
        int onlineCount = showList.size();
        int length = 30;
        DisplayMetrics density = ScreenUtils.getScreen(getActivity());
        int gridViewWidth = (int) (onlineCount * (length + 4) * density.density);
        int itemWidth = (int) (length * density.density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        grid_online.setLayoutParams(params);
        grid_online.setColumnWidth(itemWidth);
        grid_online.setStretchMode(GridView.NO_STRETCH);
        grid_online.setNumColumns(onlineCount);
        txt_online.setText(String.valueOf(onlineCount));
        horizontalListViewAdapter = new HorizontalListViewAdapter(getActivity(), showList);
        grid_online.setAdapter(horizontalListViewAdapter);
    }

    /**
     * 更新在线列表
     *
     * @param onlineNotice 进出房间用户
     */
    public void updateOnline(OnlineListModel.OnlineNotice onlineNotice) {
        int onlineCount = onlineNotice.online;
        if (String.valueOf(onlineNotice.user.uid).equals(liveUserModel.userid)) {
            return;
        }
        int index = getIndexOfUserList(onlineNotice.user.uid, showList);
        if (onlineNotice.kind == 0) { //进房间
            if (!liveUserModel.userid.equals(String.valueOf(onlineNotice.user.uid))) {
                if (index >= 0) { //存在用户 替换
                    showList.remove(index);
                }
                showList.add(BinarySearch.binSearch(showList, 0, showList.size(), onlineNotice.user), onlineNotice.user);
            }
        } else {
            if (index >= 0) {
                synchronized (lock) {
                    showList.remove(index);
                }
            }
        }
        int length = 30;
        DisplayMetrics density = ScreenUtils.getScreen(getActivity());
        int gridViewWidth = (int) (onlineCount * (length + 4) * density.density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        grid_online.setLayoutParams(params);
        grid_online.setNumColumns(onlineCount);
        txt_online.setText(String.valueOf(onlineCount - 1));
        if (horizontalListViewAdapter != null) {
            horizontalListViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 查询列表
     *
     * @param userId
     * @param list
     * @return
     */
    private synchronized int getIndexOfUserList(int userId, List<OnlineListModel> list) {
        synchronized (lock) { // 防止查询列表时列表更新或排序
            int k = list.size();
            for (int index = 0; index < k; index++) {
                OnlineListModel user = list.get(index);
                if (user != null && userId == user.uid)
                    return index;
            }
        }
        return -1;
    }

    /**
     * 展示 用户 详细信息
     *
     * @param userInfoModel user
     */
    private void onShowUser(BasicUserInfoModel userInfoModel) {
        if (App.roomModel.getRoomType().equals(App.LIVE_HOST)) {
            userInfoModel.isout = true;
        }
        UserInfoDialogFragment userInfoDialogFragment = new UserInfoDialogFragment();
        userInfoDialogFragment.setUserInfoModel(userInfoModel);
        userInfoDialogFragment.setCallBack(iCallBack);
        userInfoDialogFragment.show(getActivity().getSupportFragmentManager(), "");
    }

    /**
     * 设置房间 下拉框 加载用户
     */
    private void setRoomPopSpinner() {
        // 设置下拉列表的风格
        PopAdapter = new ArrayAdapter<OnlineListModel>(getActivity(), R.layout.simple_spinner_gift_pop_item) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView lbl;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.spinner_item_layout, parent, false);
                    lbl = (TextView) convertView.findViewById(R.id.spinner_item_label);
                    convertView.setTag(lbl);
                } else {
                    lbl = (TextView) convertView.getTag();
                }
                lbl.setTextSize(18);
                lbl.setText(PopLinkData.get(position).name);
                return convertView;
            }
        };
        PopAdapter.setDropDownViewResource(R.layout.spinner_item_layout);

        roomPopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        OnlineListModel onlineListModel = new OnlineListModel();
        onlineListModel.isv = liveUserModel.isv;
        onlineListModel.uid = Integer.parseInt(liveUserModel.userid);
        onlineListModel.name = liveUserModel.nickname;
        onlineListModel.headphoto = liveUserModel.headurl;
        PopLinkData.clear();
        PopLinkData.add(onlineListModel);
        roomPopSpinner.setAdapter(PopAdapter);
        PopAdapter.add(onlineListModel);
        PopAdapter.notifyDataSetChanged();
    }

    //设置房间信息
    public void setRoomInfo() {
        if (liveUserModel != null) {
            if (liveUserModel.headurl != null) {
                img_head.setImageURI(Uri.parse(VerificationUtil.getImageUrl100(liveUserModel.headurl)));
            }
            if (liveUserModel.isv.equals("1")) {
                iv_vip.setVisibility(View.VISIBLE);
            } else {
                iv_vip.setVisibility(View.GONE);
            }
            txt_barName.setText(liveUserModel.nickname);
        }
        if (App.giftdatas.size() <= 0) {
            loadGiftList();
        }
        if (!userModel.userid.equals(liveUserModel.userid)) {
            cameraSwitchButton.setVisibility(View.GONE);
            btn_share.setVisibility(View.VISIBLE);
            UserIsFollow();
        } else {
            cameraSwitchButton.setVisibility(View.VISIBLE);
            btn_share.setVisibility(View.GONE);
            btn_Follow.setVisibility(View.GONE);
        }
        fragmentHandler.sendEmptyMessage(MSG_ADAPTER_NOTIFY_GIFT);
    }

    public void setLikeNum(int likeNum) {
        txt_likeNum.setText(String.valueOf(likeNum));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_beautiful://美颜
                App.chatRoomApplication.livePush.OpenFace();
                if (App.chatRoomApplication.livePush.FLAG_BEAUTY_ON) {//开启美颜
                    btn_beautiful.setImageResource(R.drawable.btn_start_play_beautiful_n);
                } else {
                    btn_beautiful.setImageResource(R.drawable.btn_start_play_beautiful_s);
                }
                break;
            case R.id.button_lamp://闪光灯
                App.chatRoomApplication.livePush.Openlamp();
                if (App.chatRoomApplication.livePush.FLAG_FLASH_MODE_ON) {//开启闪光灯
                    btn_lamp.setImageResource(R.drawable.btn_start_play_flash_s);
                } else {
                    btn_lamp.setImageResource(R.drawable.btn_start_play_flash_n);
                }
                break;
            case R.id.ly_main:
                if (ly_send.getVisibility() == View.VISIBLE) {
                    Utility.closeKeybord(txt_msg, getActivity());
                }
                if (giftView.getVisibility() == View.VISIBLE) {
                    ly_toolbar.setVisibility(View.VISIBLE);
                    giftView.setVisibility(View.GONE);
                }
                if (!liveUserModel.userid.equals(userModel.userid)) {
                    if (NetWorkUtil.isNetworkConnected(getActivity())) {
                        doHeart();
                    }
                }
                break;
            case R.id.btn_send:
                sendMsg();
                break;
            case R.id.button_call_switch_camera:
                callEvents.onCameraSwitch();
                break;
            case R.id.img_open_send:
                ly_send.setVisibility(View.VISIBLE);
                txt_msg.setFocusable(true);
                txt_msg.setFocusableInTouchMode(true);
                txt_msg.requestFocus();
                ly_toolbar.setVisibility(View.GONE);
                Utility.openKeybord(txt_msg, getActivity());
                break;
            case R.id.giftbtn:
                if (gridViews.size() == 0) {
                    fragmentHandler.sendEmptyMessage(MSG_ADAPTER_NOTIFY_GIFT);
                }
                giftView.setVisibility(View.VISIBLE);
                ly_toolbar.setVisibility(View.GONE);
                break;
            case R.id.gift_send:
                int nNum = Integer.parseInt(roomGiftNumSpinner.getSelectedItem().toString());
                OnlineListModel toPeople;
                if (roomPopSpinner.getSelectedItem() != null) {
                    toPeople = (OnlineListModel) (roomPopSpinner.getSelectedItem());
                } else {
                    ToastUtils.showToast(getActivity(), getActivity().getString(R.string.please_select_gift_people));
                    return;
                }
                if (userModel.userid.equals(String.valueOf(toPeople.uid))) {
                    ToastUtils.showToast(getActivity(), getActivity().getString(R.string.not_send_gift_me));
                    break;
                }
                callEvents.onSendGift(toPeople.uid, giftId, nNum);
                giftView.setVisibility(View.GONE);
                ly_toolbar.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_Follow:
                UserFollow();
                break;
            case R.id.btn_share:
                //facebook分享
                FbShare fbShare = new FbShare(getActivity(), listener);
                fbShare.postStatusUpdate(getString(R.string.share_title), App.roomModel.getName(),
                        CommonUrlConfig.shareURL,
                        liveUserModel.headurl);
                break;
            case R.id.img_head:
                BasicUserInfoModel searchItemModel = new BasicUserInfoModel();
                searchItemModel.Userid = liveUserModel.userid;
                searchItemModel.nickname = liveUserModel.nickname;
                searchItemModel.headurl = liveUserModel.headurl;
                searchItemModel.isfollow = String.valueOf(isFollow);
                searchItemModel.isv = liveUserModel.isv;
                searchItemModel.sex = liveUserModel.sex;
                onShowUser(searchItemModel);
                break;
            case R.id.txt_barname:
                txt_room_des.setVisibility(View.VISIBLE);
                new Timer().schedule(new TimerTask() {
                                         public void run() {
                                             fragmentHandler.obtainMessage(3).sendToTarget();
                                         }
                                     },
                        2000);
                break;
            case R.id.gift_Recharge:
                StartActivityHelper.jumpActivityDefault(getActivity(), RechargeActivity.class);
                break;
        }
    }

    private void sendMsg() {
        if (txt_msg.getText().length() > 0) {
            callEvents.onSendMessage(DelHtml.delHTMLTag(txt_msg.getText().toString()));
            txt_msg.setText("");
        } else {
            ToastUtils.showToast(getActivity(), getActivity().getString(R.string.please_input_text));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (App.roomModel.getRoomType().equals(App.LIVE_PREVIEW) && App.chatRoomApplication.isqupai) {
            btn_lamp.setVisibility(View.VISIBLE);
            btn_beautiful.setVisibility(View.VISIBLE);
        }
        cocos2dxView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        cocos2dxView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearTask();
        clearAnimation();
        cocos2dxGift.destroy();
    }

    /**
     * 定时器 取消 操作
     */
    private void clearTask() {
        if (timeCount != null) {
            timeCount.cancel();
            timeCount = null;
        }
    }

    /**
     * 清除动画
     */
    private void clearAnimation() {
        if (translateAnimation_out != null) {
            translateAnimation_out.cancel();
            translateAnimation_out = null;
        }
        if (scaleAnimation != null) {
            scaleAnimation.cancel();
            scaleAnimation = null;
        }
        if (translateAnimation_in != null) {
            translateAnimation_in.cancel();
            translateAnimation_in = null;
        }
        if (translate_in != null) {
            translate_in.cancel();
            translate_in = null;
        }

    }

    /**
     * 聊天记录初始化，
     */
    private void initChatMessage(final Context context) {
        if (mAdapter == null) {
            mAdapter = new ChatLineAdapter<>(context, App.mChatlines, iShowUser);
        }
        if (chatline == null) {
            chatline = (ListView) controlView.findViewById(R.id.chatline);
        }
        mAdapter.notifyDataSetChanged();
        chatline.setAdapter(mAdapter);
        chatline.setSelection(mAdapter.getCount());
    }

    public void notifyData() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (chatline != null) {
                chatline.setSelection(mAdapter.getCount());
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callEvents = (OnCallEvents) context;
    }

    //按下返回键
    public void keyBack() {
        if (ly_send.getVisibility() == View.VISIBLE) {
            ly_send.setVisibility(View.GONE);
            ly_toolbar.setVisibility(View.VISIBLE);
        } else if (giftView.getVisibility() == View.VISIBLE) {
            giftView.setVisibility(View.GONE);
            ly_toolbar.setVisibility(View.VISIBLE);
        }
    }

    //获取是否需要隐藏的面板
    public boolean getBackState() {
        return ly_send != null && (ly_send.getVisibility() == View.VISIBLE || giftView.getVisibility() == View.VISIBLE);
    }

    //关注/取消关注
    public void UserFollow() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("code") == GlobalDef.SUCCESS_1000) {
                        //操作成功
                        if (isFollow == 0) {
                            isFollow = 1;
                        } else {
                            isFollow = 0;
                        }
                        fragmentHandler.obtainMessage(MSG_SET_FOLLOW).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        chatRoom.UserFollow(CommonUrlConfig.UserFollow, userModel.token, userModel.userid,
                liveUserModel.userid, isFollow, callback);
    }


    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_SET_FOLLOW:
                switch (isFollow) {
                    case 0:
                        btn_Follow.setVisibility(View.VISIBLE);
                        btn_Follow.setImageResource(R.drawable.btn_room_concern_n);
                        break;
                    case 1:
                        btn_Follow.setImageResource(R.drawable.btn_room_concern_s);
                        Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.free_fall_down);
                        btn_Follow.startAnimation(rotateAnimation);
                        btn_Follow.setVisibility(View.GONE);
                        break;
                }
                break;
            case 3:
                txt_room_des.setVisibility(View.GONE);
                break;
            case MSG_OPEN_GIFT_LAYOUT:
                giftView.setVisibility(View.VISIBLE);
                ly_toolbar.setVisibility(View.GONE);
                setSpinnerItemSelectedByValue(roomPopSpinner, ((BasicUserInfoModel) msg.obj).nickname);
                break;
            case HANDLER_GIFT_CHANGE_BACKGROUND:
                //还原上次被选中的礼物背景颜色 设置选中的giftId
                if (GridViewItemLastIndex != -1) {
                    gridViews.get(GridViewLastIndex).getChildAt(GridViewItemLastIndex).setBackgroundResource(R.drawable.griditems_bg);
                }
                gridViews.get(GridViewIndex).getChildAt(GridViewItemIndex).setBackgroundResource(R.drawable.griditems_bg_s);
                GiftModel g = (GiftModel) gridViews.get(GridViewIndex).getAdapter().getItem(GridViewItemIndex);
                giftId = g.getID();
                GridViewLastIndex = GridViewIndex;
                GridViewItemLastIndex = GridViewItemIndex;
                break;
            case MSG_CANCEL_FOLLOW:
                isFollow = 0;
                btn_Follow.setVisibility(View.VISIBLE);
                btn_Follow.setImageResource(R.drawable.btn_room_concern_n);
                Animation rotateAnimation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.free_fall_up);
                btn_Follow.startAnimation(rotateAnimation2);
                break;
            case MSG_DO_FOLLOW:
                isFollow = 1;
                btn_Follow.setImageResource(R.drawable.btn_room_concern_s);
                Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.free_fall_down);
                btn_Follow.startAnimation(rotateAnimation);
                btn_Follow.setVisibility(View.GONE);
                break;
            case MSG_ADAPTER_NOTIFY_GIFT:
                initGiftViewpager();
                initGiftNumSpinner();
                setRoomPopSpinner();
                break;
            case SHOW_SOFT_KEYB://键盘弹出事件
//                DebugLogs.d("====键盘弹起-------");
                if (ly_main != null) {
                    ViewGroup.LayoutParams params = ly_main.getLayoutParams();
                    params.height = App.screenDpx.heightPixels - (int) msg.obj;
                    params.width = App.screenDpx.widthPixels;
                    ly_main.setLayoutParams(params);
                    if (ly_send.getVisibility() == View.GONE) {
                        ly_send.setVisibility(View.VISIBLE);
                        ly_send.findFocus();
                        ly_toolbar.setVisibility(View.GONE);
                    }
                }
                break;
            case ONSHOW_SOFT_KEYB://键盘收起了
//                DebugLogs.d("====键盘收齐-------");
                if (ly_main != null) {
                    ViewGroup.LayoutParams params2 = ly_main.getLayoutParams();
                    params2.height = App.screenDpx.heightPixels;
                    params2.width = App.screenDpx.widthPixels;
                    ly_main.setLayoutParams(params2);
                    if (ly_send.getVisibility() == View.VISIBLE) {
                        ly_send.setVisibility(View.GONE);
                        ly_send.clearFocus();
                        ly_toolbar.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    /**
     * 设置 选中 用户
     *
     * @param spinner spinner
     * @param value   value
     */
    private void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i);// 默认选中项
                break;
            }
        }
    }

    /**
     * 初始化礼物数量选择框
     */
    private void initGiftNumSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_gift_num_item) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.spinner_item_layout, parent, false);
                TextView label = (TextView) view.findViewById(R.id.spinner_item_label);
                label.setText(String.valueOf(numArray[position]));
                return view;
            }
        };

        for (int aNumArray : numArray) {
            adapter.add(String.valueOf(aNumArray));
        }
        // 设置下拉列表的风格
        adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        // 将adapter添加到m_Spinner中
        roomGiftNumSpinner.setAdapter(adapter);
        // 添加Spinner事件监听
        roomGiftNumSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int arg2, long arg3) {
                parent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    // 初始化礼物列表
    private void loadGiftList() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                CommonParseListModel<GiftModel> result = JsonUtil.fromJson(response, new TypeToken<CommonParseListModel<GiftModel>>() {
                }.getType());
                if (result != null) {
                    App.giftdatas.clear();
                    App.giftdatas.addAll(result.data);
                }
            }
        };
        ChatRoom chatRoom = new ChatRoom(getActivity());
        chatRoom.loadGiftList(CommonUrlConfig.PropList, CacheDataManager.getInstance().loadUser().token, callback);
    }

    //向礼物队列添加
    public void addGifAnimation(GiftAnimationModel giftaModel) {
        giftModelList.add(giftaModel);
        //如果队列里边只有1个礼物并且当前没有处于播放状态，就开始播放礼物动画
        if (giftModelList.size() == 1 && !giftA) {
            startGiftAnimation(giftModelList.get(0));
        }
    }

    //开始礼物特效
    private void startGiftAnimation(GiftAnimationModel giftModel) {
        giftA = true;
        ly_gift_view.setVisibility(View.VISIBLE);
        ly_gift_view.startAnimation(translateAnimation_in);
        if (giftModel.giftmodel != null && giftModel.giftmodel.getImageURL() != null) {
            imageView.setImageURI(Uri.parse(VerificationUtil.getImageUrl(giftModel.giftmodel.getImageURL())));
        }
        if (giftModel.userheadpoto != null) {
            gif_img_head.setImageURI(Uri.parse(VerificationUtil.getImageUrl(giftModel.userheadpoto)));
        }
        txt_from_user.setText(giftModel.from_uname);
        imageView.startAnimation(translate_in);

    }

    //礼物数量动画
    private void addGiftAnimationNum(final GiftAnimationModel giftModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int k = giftModel.giftnum;
                for (int i = 1; i <= k; i++) {
                    if (isAdded()) {
                        final int finalI = i;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String str = "x" + finalI;
                                translateAnimation_out.start();
                                numText.setText(str);
                                numText1.setText(str);
                                numText1.startAnimation(scaleAnimation);
                                numText.startAnimation(scaleAnimation);
                            }
                        });
                    }
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 点心操作
     */
    private void doHeart() {
        try {
            //大于一秒方个通过 一秒只能点击一次
            if (System.currentTimeMillis() - lastClick <= 200) {
                return;
            }
            lastClick = System.currentTimeMillis();
            if (isTimeCount) {
                timeCount = new TimeCount(10000, 1000);//倒计时
                timeCount.start();
            }
            count++;
            App.roomModel.setLikenum(App.roomModel.getLikenum() + 1);
            txt_likeNum.setText(String.valueOf(App.roomModel.getLikenum()));
            if (loveView != null) {
                loveView.addHeart();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void runAddLove(final int count) {
        if (!isTimeCount2) {
            return;
        }
        if (timeCount2 == null) {
            timeCount2 = new TimeCount2(2000, 200);
            timeCount2.start();
        }
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < count; i++) {
                            if (i > 20 && isRun) {
                                isRun = false;
                                return;
                            }
                            Thread.sleep(200);
                            if (isAdded()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isRun = true;
                                        if (loveView != null) {
                                            loveView.addHeart();
                                            if (App.roomModel != null) {
                                                App.roomModel.setLikenum(App.roomModel.getLikenum() + 1);
                                                txt_likeNum.setText(String.valueOf(App.roomModel.getLikenum()));
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            isTimeCount = false;
        }

        @Override
        public void onFinish() {
            callEvents.sendLove(count);
            isTimeCount = true;
            count = 0;
        }
    }

    private class TimeCount2 extends CountDownTimer {
        public TimeCount2(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            isTimeCount2 = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            isTimeCount2 = false;
        }

        @Override
        public void onFinish() {
            isTimeCount2 = true;
            timeCount2.cancel();
            timeCount2 = null;
        }

    }

    /**
     * 初始化礼物
     */
    @SuppressWarnings("unchecked")
    private void initGiftViewpager() {
        int pageCount;
        int lineNum = 2; //行数
        int columnNum = 4; //列数
        GridViewAdapter gridViewAdapter;
        if (App.giftdatas.size() <= columnNum) {
            viewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ScreenUtils.dip2px(getActivity(), 90))); //使设置好的布局参数应用到控件
        } else {
            viewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ScreenUtils.dip2px(getActivity(), 180))); //使设置好的布局参数应用到控件
        }
        final int giftPageSize = lineNum * columnNum;
        if (App.giftdatas.size() % giftPageSize == 0) {
            pageCount = App.giftdatas.size() / giftPageSize;
        } else {
            pageCount = App.giftdatas.size() / giftPageSize + 1;
        }

        for (int i = 0; i < pageCount; i++) {
            final GridView gridView = new GridView(getActivity());
            gridViewAdapter = new GridViewAdapter(getActivity(), App.giftdatas, i, giftPageSize);
            gridView.setAdapter(gridViewAdapter);
            gridView.setGravity(Gravity.CENTER);
            gridView.setClickable(true);
            gridView.setFocusable(true);
            gridView.setNumColumns(columnNum);
            gridView.setVerticalSpacing(20);
            gridViews.add(gridView);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                    GridViewItemIndex = position;
                    fragmentHandler.obtainMessage(HANDLER_GIFT_CHANGE_BACKGROUND).sendToTarget();
                }
            });
        }
        CustomerPageAdapter pagerAdapter = new CustomerPageAdapter(getActivity(), gridViews);
        viewPager.setAdapter(pagerAdapter);
        //initPoint(pageCount);
    }

    /**
     * 实现 用户信息对话框 的接口函数
     */
    private UserInfoDialogFragment.ICallBack iCallBack = new UserInfoDialogFragment.ICallBack() {
        @Override
        public void sendGift(BasicUserInfoModel user) {
            boolean isLoadUser = false;
            for (OnlineListModel item : PopLinkData) {
                if (user.Userid.equals(String.valueOf(item.uid))) {
                    isLoadUser = true;
                    break;
                }
            }
            if (!isLoadUser) {
                OnlineListModel onlineListModel = new OnlineListModel();
                onlineListModel.isv = user.isv;
                onlineListModel.uid = Integer.parseInt(user.Userid);
                onlineListModel.name = user.nickname;
                onlineListModel.headphoto = user.headurl;
                PopLinkData.add(onlineListModel);
                PopAdapter.add(onlineListModel);
            }
            fragmentHandler.obtainMessage(MSG_OPEN_GIFT_LAYOUT, user).sendToTarget();
        }

        @Override
        public void follow(String val, String userId) {
            if (!liveUserModel.userid.equals(userModel.userid) && userId.equals(liveUserModel.userid)) {
                if (val != null && val.equals("0")) {
                    fragmentHandler.sendEmptyMessage(MSG_DO_FOLLOW);
                } else {
                    fragmentHandler.sendEmptyMessage(MSG_CANCEL_FOLLOW);
                }
            }
        }

        /**
         * 踢人
         * @param userId
         */
        @Override
        public void kickedOut(String userId) {
            callEvents.kickedOut(userId);
        }

        @Override
        public void closeLive() {
            if (callEvents != null) {
                callEvents.closeLive();
            }
        }
    };

    private ChatLineAdapter.IShowUser iShowUser = new ChatLineAdapter.IShowUser() {
        @Override
        public void showUser(BasicUserInfoModel userInfoModel) {
            onShowUser(userInfoModel);
        }
    };

    //检查是否关注
    private void UserIsFollow() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                try {
                    //是否关注
                    isFollow = new JSONObject(response).getJSONObject("data").getInt("isfollow");
                    fragmentHandler.obtainMessage(MSG_SET_FOLLOW).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        chatRoom.UserIsFollow(CommonUrlConfig.UserIsFollow, userModel.token, userModel.userid, liveUserModel.userid, callback);
    }

    /**
     * 分享监听
     */
    public ShareListener listener = new ShareListener() {
        @Override
        public void callBackSuccess(int shareType) {
            ToastUtils.showToast(getActivity(), R.string.success);
        }

        @Override
        public void callbackError(int shareType) {
            ToastUtils.showToast(getActivity(), R.string.error);
        }

        @Override
        public void callbackCancel(int shareType) {
            ToastUtils.showToast(getActivity(), R.string.cancel);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timeCount2 != null) {
            timeCount2.cancel();
        }
        if (timeCount != null) {
            timeCount.cancel();
        }
    }

    public void play(Cocos2dxGift.Cocos2dxGiftModel giftModel) {
        cocos2dxGift.play(cocos2dxView, giftModel);
    }
}