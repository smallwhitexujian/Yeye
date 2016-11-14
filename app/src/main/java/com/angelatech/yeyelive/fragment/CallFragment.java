package com.angelatech.yeyelive.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
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
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.RechargeActivity;
import com.angelatech.yeyelive.activity.WebActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.adapter.ChatLineAdapter;
import com.angelatech.yeyelive.adapter.CustomerPageAdapter;
import com.angelatech.yeyelive.adapter.GridViewAdapter;
import com.angelatech.yeyelive.adapter.HorizontalListViewAdapter;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.BarInfoModel;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
import com.angelatech.yeyelive.model.ChatLineModel;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.Danmu;
import com.angelatech.yeyelive.model.GiftAnimationModel;
import com.angelatech.yeyelive.model.GiftModel;
import com.angelatech.yeyelive.model.OnlineListModel;
import com.angelatech.yeyelive.model.WebTransportModel;
import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.thirdShare.ThirdShareDialog;
import com.angelatech.yeyelive.util.BinarySearch;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.DelHtml;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.MarqueeUilts;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.view.DanmuControl;
import com.angelatech.yeyelive.view.FrescoBitmapUtils;
import com.angelatech.yeyelive.view.PeriscopeLayout;
import com.google.gson.reflect.TypeToken;
import com.will.common.tool.network.NetWorkUtil;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoDrawee;
import com.xj.frescolib.View.FrescoRoundView;

import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.util.Cocos2dxGift;
import org.cocos2dx.lib.util.Cocos2dxView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import master.flame.danmaku.controller.IDanmakuView;

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

    private ImageView btn_Follow;
    private ImageView btn_share, btn_room_more;
    private ImageView iv_vip;
    private ImageView btn_beautiful;
    private ImageView btn_lamp;
    private TextView txt_barName, txt_likeNum, txt_online, gift_Diamonds, txt_room_des, diamondsStr;
    private FrescoRoundView img_head, gif_img_head, gif_img_head_s;
    private PeriscopeLayout loveView;                                                               // 显示心的VIEW
    private EditText txt_msg;
    private LinearLayout ly_send, ly_toolbar, ly_toolbar2, ly_main, giftView;                                    // 礼物界面
    private Spinner roomPopSpinner, roomGiftNumSpinner;                                             // 礼物个数列表
    private int giftId;                                                                             // 礼物的ID
    private int isFollow;
    private ListView chatline;
    private OnCallEvents callEvents;
    private List<OnlineListModel> PopLinkData = new ArrayList<>();
    private final int numArray[] = {1, 10, 22, 55, 77, 100}; //礼物数量列表
    private ArrayList<GiftAnimationModel> giftModelList = new ArrayList<>();
    private GiftAnimationModel GiftAnimationModelA, GiftAnimationModelB;
    private static ChatLineAdapter<ChatLineModel> mAdapter;
    private RelativeLayout ly_gift_view, ly_gift_view_s;                                                            //礼物特效view
    private TextView numText, numText1, numText_s, numText1_s;                                                      //礼物数量  阴影
    private TextView txt_from_user, txt_from_user_s;                                                  //发送礼物的人，礼物名称
    private FrescoDrawee imageView, imageView_s;//礼物图片， 礼物发送人的头像
    private Animation translateAnimation_in, translateAnimation_out, translate_in, scaleAnimation;  //礼物特效
    private Animation translateAnimation_in_s, translateAnimation_out_s, translate_in_s, scaleAnimation_s;  //礼物特效2

    private boolean giftA = false, giftB = false;                                                                  //礼物特效播放状态
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
    private Cocos2dxView cocos2dxView;
    private Cocos2dxGift cocos2dxGift;
    private LinearLayout ViewgiftLayout;

    private GridView grid_online;
    private HorizontalListViewAdapter horizontalListViewAdapter;
    private List<OnlineListModel> showList = new ArrayList<>();
    private RelativeLayout rootView;
    private int mVisibleHeight;
    private FragmentManager fragmentManager;
    //软件盘弹起后所占高度阀值
    private Chronometer timer;
    private boolean bVideoFilter = false, bFlashEnable = false;

    private MarqueeUilts marqueeUtils;
    private LinearLayout marqueeLayout, marquee_layout;

    //弹幕控件
    private ImageView btn_danmu;
    private boolean isdanmu = false;

    private DanmuControl mDanmuControl;
    private ScaleGestureDetector mScaleDetector = null;
    private GestureDetector mGestureDetector = null;

    public void setDiamonds(String diamonds) {
        try {
            gift_Diamonds.setText(diamonds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAnchorDiamonds(String anchorCoin) {
        try {
            if (anchorCoin != null) {
                String str = String.format(getString(R.string.Coins), anchorCoin);
                diamondsStr.setText(str);
            } else {
                diamondsStr.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLikeNum(int likeNum) {
        txt_likeNum.setText(String.valueOf(likeNum));
    }

    public void setOnline(String position) {
        String num = String.valueOf(Integer.valueOf(position) - 1);
        txt_online.setText(num);
    }

    public interface OnCallEvents {
        //切换摄像头
        void onCameraSwitch();

        //发送消息
        void onSendMessage(String msg, boolean isdanmu);

        //发送礼物
        void onSendGift(int toid, int giftId, int nNum);

        //点赞
        void sendLove(int num);

        //踢人
        void kickedOut(String userId);

        //播放幸运礼物动画
        void playXingYunGift();

        //结束直播
        void closeLive();

        //手势监听
        boolean onZoomValueChanged(float factor);

        boolean onSingleTapUp(MotionEvent e);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        controlView = inflater.inflate(R.layout.fragment_call, container, false);
        mDanmuControl = new DanmuControl(getActivity());
        initView();
        fragmentManager = getFragmentManager();
        initControls();
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
        if (ChatRoomActivity.roomModel.getUserInfoDBModel() != null) {
            liveUserModel = ChatRoomActivity.roomModel.getUserInfoDBModel();
        }
        userModel = CacheDataManager.getInstance().loadUser();
        chatRoom = new ChatRoom(getActivity());
        cameraSwitchButton = (ImageView) controlView.findViewById(R.id.button_call_switch_camera);
        txt_msg = (EditText) controlView.findViewById(R.id.txt_msg);
        Button btn_send = (Button) controlView.findViewById(R.id.btn_send);
        chatline = (ListView) controlView.findViewById(R.id.chatline);
        ViewgiftLayout = (LinearLayout) controlView.findViewById(R.id.gift_layout);
        initChatMessage(getActivity());
        ImageView img_open_send = (ImageView) controlView.findViewById(R.id.img_open_send);
        ImageView giftBtn = (ImageView) controlView.findViewById(R.id.giftbtn);
        timer = (Chronometer) controlView.findViewById(R.id.chronometer);
        ly_toolbar = (LinearLayout) controlView.findViewById(R.id.ly_toolbar);
        ly_toolbar2 = (LinearLayout) controlView.findViewById(R.id.ly_toolbar2);
        ly_send = (LinearLayout) controlView.findViewById(R.id.ly_send);
        ly_main = (LinearLayout) controlView.findViewById(R.id.ly_main);
        loveView = (PeriscopeLayout) controlView.findViewById(R.id.PeriscopeLayout);
        img_head = (FrescoRoundView) controlView.findViewById(R.id.img_head);
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
        btn_room_more = (ImageView) controlView.findViewById(R.id.btn_room_more);
        btn_beautiful = (ImageView) controlView.findViewById(R.id.button_beautiful);
        btn_lamp = (ImageView) controlView.findViewById(R.id.button_lamp);
        ImageView btn_room_exchange = (ImageView) controlView.findViewById(R.id.btn_room_exchange);
        iv_vip = (ImageView) controlView.findViewById(R.id.iv_vip);
        gift_Diamonds = (TextView) controlView.findViewById(R.id.gift_Diamonds);
        diamondsStr = (TextView) controlView.findViewById(R.id.diamonds);
        txt_room_des = (TextView) controlView.findViewById(R.id.txt_room_des);
        TextView gift_Recharge = (TextView) controlView.findViewById(R.id.gift_Recharge);
        grid_online = (GridView) controlView.findViewById(R.id.grid_online);
        rootView = (RelativeLayout) controlView.findViewById(R.id.rootView);
        int statusBarHeight = ScreenUtils.getStatusHeight(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.setPadding(0, statusBarHeight, 0, 0);
        } else {
            rootView.setPadding(0, 0, 0, statusBarHeight);
        }
        marqueeLayout = (LinearLayout) controlView.findViewById(R.id.marquee);
        marquee_layout = (LinearLayout) controlView.findViewById(R.id.marquee_layout);
        IDanmakuView mDanmakuView = (IDanmakuView) controlView.findViewById(R.id.danmakuView);
        btn_danmu = (ImageView) controlView.findViewById(R.id.btn_danmu);
        mDanmuControl.setDanmakuView(mDanmakuView);
        ly_main.setOnClickListener(this);
        diamondsStr.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        img_open_send.setOnClickListener(this);
        cameraSwitchButton.setOnClickListener(this);
        gift_send.setOnClickListener(this);
        giftBtn.setOnClickListener(this);
        btn_Follow.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_room_more.setOnClickListener(this);
        img_head.setOnClickListener(this);
        txt_barName.setOnClickListener(this);
        btn_lamp.setOnClickListener(this);
        btn_room_exchange.setOnClickListener(this);
        btn_beautiful.setOnClickListener(this);
        gift_Recharge.setOnClickListener(this);
        btn_danmu.setOnClickListener(this);

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

        txt_msg.addTextChangedListener(textWatcher);

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
        txt_from_user_s = (TextView) controlView.findViewById(R.id.txt_from_user_s);
        imageView = (FrescoDrawee) controlView.findViewById(R.id.img_gift);
        imageView_s = (FrescoDrawee) controlView.findViewById(R.id.img_gift_s);
        gif_img_head = (FrescoRoundView) controlView.findViewById(R.id.gif_img_head);
        gif_img_head_s = (FrescoRoundView) controlView.findViewById(R.id.gif_img_head_s);
        ly_gift_view = (RelativeLayout) controlView.findViewById(R.id.ly_gift_view);
        ly_gift_view_s = (RelativeLayout) controlView.findViewById(R.id.ly_gift_view_s);
        numText = (TextView) controlView.findViewById(R.id.numText);
        numText1 = (TextView) controlView.findViewById(R.id.numText1);
        numText_s = (TextView) controlView.findViewById(R.id.numText_s);
        numText1_s = (TextView) controlView.findViewById(R.id.numText1_s);

        TextPaint tp1 = numText.getPaint();
        tp1.setStrokeWidth(3);
        tp1.setStyle(Paint.Style.FILL_AND_STROKE);
        tp1.setFakeBoldText(true);

        translateAnimation_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);
        translate_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade2_in_anim);
        translateAnimation_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_anim);
        scaleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.thepinanim);

        translateAnimation_in_s = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);
        translate_in_s = AnimationUtils.loadAnimation(getActivity(), R.anim.fade2_in_anim);
        translateAnimation_out_s = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_anim);
        scaleAnimation_s = AnimationUtils.loadAnimation(getActivity(), R.anim.thepinanim);

        //礼物动画
        translateAnimation_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                String str = "x1";
                numText.setText(str);
                numText1.setText(str);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (scaleAnimation != null) {
                    numText.setVisibility(View.VISIBLE);
                    numText.startAnimation(scaleAnimation);
                    //礼物数量动画
                    addGiftAnimationNum(GiftAnimationModelA);
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
                if (ly_gift_view != null && translateAnimation_out != null) {
                    ly_gift_view.startAnimation(translateAnimation_out);
                    giftA = false;
                    try {
                        if (giftModelList.size() > 0) {
                            startGiftAnimation(giftModelList.get(0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //礼物动画2
        translateAnimation_in_s.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                String str = "x1";
                numText_s.setText(str);
                numText1_s.setText(str);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (scaleAnimation_s != null) {
                    numText_s.setVisibility(View.VISIBLE);
                    numText_s.startAnimation(scaleAnimation_s);
                    addGiftAnimationNum_s(GiftAnimationModelB);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        scaleAnimation_s.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (ly_gift_view_s != null && translateAnimation_out_s != null) {
                    ly_gift_view_s.startAnimation(translateAnimation_out_s);
                    giftB = false;
                    try {
                        if (giftModelList.size() > 0) {
                            startGiftAnimation(giftModelList.get(0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (marqueeUtils == null) {
                    marqueeUtils = new MarqueeUilts(getActivity(), App.marqueeData, marqueeLayout);
                    marqueeUtils.Start();
                }
            }
        }).start();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        if (ChatRoomActivity.roomModel.getRoomType().equals(App.LIVE_PREVIEW)) {
            initialize(getActivity());
            ly_main.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event) || mScaleDetector.onTouchEvent(event);
                }
            });
        }
    }

    private void initialize(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, mScaleListener);
        mGestureDetector = new GestureDetector(context, mGestureListener);
    }

    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (callEvents != null) {
                callEvents.onSingleTapUp(e);
            }
            return false;
        }
    };

    private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private float mScaleFactor = 1.0f;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // factor > 1, zoom
            // factor < 1, pinch
            mScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.01f, Math.min(mScaleFactor, 1.0f));
            callEvents.onZoomValueChanged(mScaleFactor);
            return callEvents != null && callEvents.onZoomValueChanged(mScaleFactor);
        }
    };

    /*监听输入事件*/
    private TextWatcher textWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            editStart = txt_msg.getSelectionStart();
            editEnd = txt_msg.getSelectionEnd();
            if (temp.length() > 20 && isdanmu) {
                editable.delete(editStart - 1, editEnd);
                ToastUtils.showToast(getContext(), R.string.danmu_tips);
            }
        }
    };

    private void initControls() {
        if (liveUserModel.userid.equals(userModel.userid)) {
            btn_lamp.setVisibility(View.VISIBLE);
            btn_beautiful.setVisibility(View.VISIBLE);
        }
    }

    public void StartChronometer() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }

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
            } else {
                getFragmentHandler().obtainMessage(ONSHOW_SOFT_KEYB).sendToTarget();
            }
        }
    };

    //初始化cocos
    private void initCocos2dx() {
        //coco2动画SurfaceView
        cocos2dxView = new Cocos2dxView();
        cocos2dxGift = new Cocos2dxGift();
        cocos2dxView.onCreate(getActivity(), 0);
        Cocos2dxGLSurfaceView.ScaleInfo scaleInfo = new Cocos2dxGLSurfaceView.ScaleInfo();
        scaleInfo.nomal = true;
        scaleInfo.width = ScreenUtils.getScreenWidth(getActivity());
        scaleInfo.height = ScreenUtils.getScreenHeight(getActivity());
        cocos2dxView.setScaleInfo(scaleInfo);
        FrameLayout giftLayout = cocos2dxView.getFrameLayout();
        ViewgiftLayout.addView(giftLayout);
    }

    public void setHintCocosView() {
        if (ViewgiftLayout != null) {
            ViewgiftLayout.setVisibility(View.GONE);
            ViewgiftLayout.clearFocus();
            ViewgiftLayout.setFocusable(false);
            if (cocos2dxView != null) {
                cocos2dxView.onPause();
            }
        }
    }

    public void setShowCocosView() {
        if (ViewgiftLayout != null) {
            ViewgiftLayout.setFocusable(true);
            ViewgiftLayout.setVisibility(View.VISIBLE);
            if (cocos2dxView != null) {
                cocos2dxView.onResume();
            }
        }
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
        if (density != null) {
            int gridViewWidth = (int) (onlineCount * (length + 4) * density.density);
            int itemWidth = (int) (length * density.density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            grid_online.setLayoutParams(params);
            grid_online.setColumnWidth(itemWidth);
            grid_online.setStretchMode(GridView.NO_STRETCH);
            grid_online.setNumColumns(onlineCount);
            horizontalListViewAdapter = new HorizontalListViewAdapter(getActivity());
            horizontalListViewAdapter.setData(showList);
            grid_online.setAdapter(horizontalListViewAdapter);
        }
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
            setRoomPopSpinner();
        }
        int length = 30;
        if (isAdded()) {
            DisplayMetrics density = ScreenUtils.getScreen(getActivity());
            if (density != null) {
                int gridViewWidth = (int) (onlineCount * (length + 4) * density.density);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                grid_online.setLayoutParams(params);
            }
            grid_online.setNumColumns(onlineCount);
            txt_online.setText(String.valueOf(onlineCount - 1));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (horizontalListViewAdapter != null) {
                        horizontalListViewAdapter.setData(showList);
                    }
                }
            });
        }
    }

    /**
     * 查询列表
     */
    private synchronized int getIndexOfUserList(int userId, List<OnlineListModel> list) {
        synchronized (lock) { // 防止查询列表时列表更新或排序
            int k = list.size();
            for (int index = 0; index < k; index++) {
                OnlineListModel user = list.get(index);
                if (user != null && userId == user.uid) {
                    return index;
                }
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
        if (isAdded()) {
            if (ChatRoomActivity.roomModel.getRoomType().equals(App.LIVE_HOST)) {
                userInfoModel.isout = true;
            }
            UserInfoDialogFragment userInfoDialogFragment = new UserInfoDialogFragment();
            userInfoDialogFragment.setUserInfoModel(userInfoModel);
            userInfoDialogFragment.setCallBack(iCallBack);
            userInfoDialogFragment.show(getActivity().getSupportFragmentManager(), "");
        }
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
                img_head.setImageURI(VerificationUtil.getImageUrl100(liveUserModel.headurl));
            }
            //0 无 1 v 2 金v 9官
            switch (liveUserModel.isv) {
                case "1":
                    iv_vip.setImageResource(R.drawable.icon_identity_vip_white);
                    iv_vip.setVisibility(View.VISIBLE);
                    break;
                case "2":
                    iv_vip.setImageResource(R.drawable.icon_identity_vip_gold);
                    iv_vip.setVisibility(View.VISIBLE);
                    break;
                case "9":
                    iv_vip.setImageResource(R.drawable.icon_identity_official);
                    iv_vip.setVisibility(View.VISIBLE);
                    break;
                default:
                    iv_vip.setVisibility(View.GONE);
                    break;
            }
            txt_barName.setText(liveUserModel.nickname);
        }
        loadGiftList();
        if (!userModel.userid.equals(liveUserModel.userid)) {
            cameraSwitchButton.setVisibility(View.GONE);
            btn_share.setVisibility(View.VISIBLE);
            btn_room_more.setVisibility(View.GONE);
            UserIsFollow();
        } else {
            btn_room_more.setVisibility(View.VISIBLE);
            cameraSwitchButton.setVisibility(View.VISIBLE);
            btn_share.setVisibility(View.VISIBLE);
            btn_Follow.setVisibility(View.GONE);
        }
        fragmentHandler.sendEmptyMessage(MSG_ADAPTER_NOTIFY_GIFT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_beautiful://美颜
                if (bVideoFilter) {
                    btn_beautiful.setImageResource(R.drawable.btn_start_play_beautiful_s);
                    App.chatRoomApplication.setOpenFB();
                } else {
                    App.chatRoomApplication.setOpenFB();
                    btn_beautiful.setImageResource(R.drawable.btn_start_play_beautiful_n);
                }
                bVideoFilter = !bVideoFilter;
                break;
            case R.id.button_lamp://闪光灯
                if (bFlashEnable) {
                    App.chatRoomApplication.setmTurnLight(false);
                    btn_lamp.setImageResource(R.drawable.btn_start_play_flash_s);
                } else {
                    App.chatRoomApplication.setmTurnLight(true);
                    btn_lamp.setImageResource(R.drawable.btn_start_play_flash_n);
                }
                bFlashEnable = !bFlashEnable;
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
                ly_toolbar2.setVisibility(View.GONE);
                Utility.openKeybord(txt_msg, getActivity());
                break;
            case R.id.giftbtn:
                if (gridViews.size() == 0) {
                    fragmentHandler.sendEmptyMessage(MSG_ADAPTER_NOTIFY_GIFT);
                }
                giftView.setVisibility(View.VISIBLE);
                ly_toolbar.setVisibility(View.GONE);
                ly_toolbar2.setVisibility(View.GONE);
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
                callEvents.onSendMessage(GlobalDef.APPEND_FOLLOW, false);
                btn_Follow.setClickable(false);
                break;
            case R.id.btn_room_more:
                if (ly_toolbar2.getVisibility() == View.VISIBLE) {
                    ly_toolbar2.setVisibility(View.GONE);
                } else if (ly_toolbar2.getVisibility() == View.GONE) {
                    ly_toolbar2.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.btn_room_exchange://房间跳转商城
                WebTransportModel webTransportModel = new WebTransportModel();
                webTransportModel.url = CommonUrlConfig.MallIndex + "?userid=" + userModel.userid + "&token=" + userModel.token + "&hostid=" + liveUserModel.userid + "&time=" + System.currentTimeMillis();
                webTransportModel.title = getString(R.string.gift_center);
                if (!webTransportModel.url.isEmpty()) {
                    StartActivityHelper.jumpActivity(getActivity(), WebActivity.class, webTransportModel);
                }
                break;
            case R.id.btn_share:
                setShowCocosView();
                String imageUrl = liveUserModel.headurl;
                if (imageUrl.indexOf("http://file") > 0) {
                    imageUrl = imageUrl.substring(0, imageUrl.indexOf("?")) + "?imageView2/2/w/1200/h/650";
                }
                ThirdShareDialog.Builder builder = new ThirdShareDialog.Builder(getActivity(), fragmentManager, null);
                String sharetitle = ChatRoomActivity.roomModel.getName();
                if (sharetitle.equals("")) {
                    sharetitle = getString(R.string.shareTitle);
                }

                builder.setShareContent(sharetitle, getString(R.string.shareDescription),
                        CommonUrlConfig.facebookURL + "?uid=" + liveUserModel.userid,
                        imageUrl);
                builder.RegisterCallback(listener);
                builder.create().show();
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
            case R.id.diamonds:
                setShowCocosView();
                TabDialogFragment tabDialogFragment = new TabDialogFragment();
                tabDialogFragment.show(getActivity().getSupportFragmentManager(), "");
                break;
            case R.id.btn_danmu:
                if (isdanmu) {
                    btn_danmu.setImageResource(R.drawable.btn_room_sendtxt_cur_ord);
                    txt_msg.setHint(R.string.please_input);
                } else {
                    btn_danmu.setImageResource(R.drawable.btn_room_sendtxt_cur_barrage);
                    txt_msg.setHint(R.string.send_barrage);
                }
                isdanmu = !isdanmu;
                break;
        }
    }

    /**
     * 发送消息
     */
    private void sendMsg() {
        if (txt_msg.getText().length() > 0 && isAdded()) {
            String msg = DelHtml.delHTMLTag(txt_msg.getText().toString());
            callEvents.onSendMessage(msg, isdanmu);
            txt_msg.setText("");
        } else {
            ToastUtils.showToast(getActivity(), getActivity().getString(R.string.please_input_text));
        }
    }

    public void sendDanmu(String nikename, String headurl, final String msg) {
        final List<Danmu> danmus = new ArrayList<>();
        if (headurl == null || headurl.isEmpty()) {
            //临时代码，发送弹幕时如果用户头像为空，设置一张默认头像
            headurl = "http://b.hiphotos.baidu.com/image/pic/item/810a19d8bc3eb135aa449355a21ea8d3fc1f4458.jpg";
        }

        FrescoBitmapUtils.getImageBitmap(getActivity(), headurl, new FrescoBitmapUtils.BitCallBack() {
            @Override
            public void onNewResultImpl(final Bitmap bitmap) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Danmu danmu1 = new Danmu(0, new Random().nextInt(3), "Comment", bitmap, msg);
                        danmus.add(danmu1);
                        Collections.shuffle(danmus);
                        mDanmuControl.addDanmuList(danmus);
                    }
                });
            }
        });
    }

    /**
     * 发送广播消息
     *
     * @param radioMessage
     */
    private void sendMarquee(BarInfoModel.RadioMessage radioMessage) {
        if (isAdded()) {
            marqueeLayout.invalidate();
            marquee_layout.setFocusable(true);
            marquee_layout.invalidate();
            marqueeUtils.restartAnim();
            final HashMap<String, Object> params = new HashMap<>();
            Spanned htmlStr = Html.fromHtml("<font color='#ffff00'> <b> " + radioMessage.msg + "</b></font>");
            params.put(MarqueeUilts.CONTEXT, htmlStr);
            App.marqueeData.add(params);
        }
    }

    /**
     * 上公聊
     *
     * @param radioMessage
     */
    private void sendPublicMessage(final BarInfoModel.RadioMessage radioMessage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatLineModel chatlinemodel = new ChatLineModel();
                ChatLineModel.from from = new ChatLineModel.from();
                chatlinemodel.type = 10;
                chatlinemodel.message = radioMessage.msg;
                App.mChatlines.add(chatlinemodel);
                if (mAdapter != null) {
                    mAdapter.setDeviceList(App.mChatlines);
                }
            }
        });
    }

    /**
     * 广播消息处理
     */
    public void RadioBroad(BarInfoModel.RadioMessage radioMessage) {
        try {
            if (radioMessage.code == 0) {//表示成功
                if (radioMessage.type_code == 95) {//幸运礼物
                    radioMessage.msg = String.format(getString(R.string.xingyunliwu_tips), radioMessage.from.name, radioMessage.multiple, radioMessage.coin_bonus);
                    if (Float.parseFloat(radioMessage.multiple) > 0.5 && radioMessage.from_room.roomid == ChatRoomActivity.roomModel.getId()) {
                        callEvents.playXingYunGift();
                    }
                }
                if (radioMessage.type == 0 || radioMessage.type == 93) {                                //0或92公聊显示
                    sendPublicMessage(radioMessage);
                } else if (radioMessage.type == 62) {                                                   //62礼物消息
                } else if (radioMessage.type == 9) {                                                    //9他人发的弹幕
                    sendDanmu(radioMessage.from.name, radioMessage.from.headphoto, radioMessage.msg);
                } else if (radioMessage.type == 91) {                                                          //91自己发送弹幕收到的回执
                    sendDanmu(userModel.nickname, userModel.headurl, radioMessage.msg);
                    //更新金币
                    userModel.diamonds = String.valueOf(radioMessage.coin);
                    setDiamonds(userModel.diamonds);
                } else if (radioMessage.type == 92) {                                                   //92喇叭显示
                    sendMarquee(radioMessage);
                } else if (radioMessage.type == 94) {                                                   //94喇叭，飞屏，公聊同时显示
                    //喇叭
                    sendMarquee(radioMessage);
                    //飞屏
                    sendDanmu(radioMessage.from.name, radioMessage.from.headphoto, radioMessage.msg);
                    //公聊
                    sendPublicMessage(radioMessage);
                }

            } else if (radioMessage.code == 1) {
                //ToastUtils.showToast(getActivity(), getString(R.string.char_room_radio_on_coin));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cocos2dxView != null) {
            cocos2dxView.onResume();
        }
        mDanmuControl.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cocos2dxView != null) {
            cocos2dxView.onPause();
        }
        mDanmuControl.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (txt_msg != null) {
            Utility.closeKeybord(txt_msg, getActivity());
        }
        clearTask();
        clearAnimation();
        if (cocos2dxView != null) {
            cocos2dxGift.destroy();
        }
        mDanmuControl.pause();
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

        if (translateAnimation_out_s != null) {
            translateAnimation_out_s.cancel();
            translateAnimation_out_s = null;
        }

        if (scaleAnimation_s != null) {
            scaleAnimation_s.cancel();
            scaleAnimation_s = null;
        }
        if (translateAnimation_in_s != null) {
            translateAnimation_in_s.cancel();
            translateAnimation_in_s = null;
        }
        if (translate_in_s != null) {
            translate_in_s.cancel();
            translate_in_s = null;
        }

    }

    /**
     * 聊天记录初始化，
     */
    private void initChatMessage(final Context context) {
        if (mAdapter == null) {
            mAdapter = new ChatLineAdapter<>(context, iShowUser);
        }
        if (chatline == null) {
            chatline = (ListView) controlView.findViewById(R.id.chatline);
        }
        if (isAdded()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatline.setAdapter(mAdapter);
                    if (mAdapter != null) {
                        mAdapter.setDeviceList(App.mChatlines);
                    }
                }
            });
        }
    }

    public void notifyData() {
        if (isAdded()) {
            if (mAdapter != null) {
                mAdapter.setDeviceList(App.mChatlines);
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
            ly_toolbar2.setVisibility(View.GONE);
        } else if (giftView.getVisibility() == View.VISIBLE) {
            giftView.setVisibility(View.GONE);
            ly_toolbar.setVisibility(View.VISIBLE);
            ly_toolbar2.setVisibility(View.GONE);
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
                try {
                    switch (isFollow) {
                        case 0://未关注
                            btn_Follow.setClickable(true);
                            btn_Follow.setVisibility(View.VISIBLE);
                            btn_Follow.setImageResource(R.drawable.btn_room_concern_n);
                            break;
                        case 1://关注
                            btn_Follow.setImageResource(R.drawable.btn_room_concern_s);
                            Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.free_fall_down);
                            btn_Follow.startAnimation(rotateAnimation);
                            btn_Follow.setVisibility(View.GONE);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                txt_room_des.setVisibility(View.GONE);
                break;
            case MSG_OPEN_GIFT_LAYOUT:
                giftView.setVisibility(View.VISIBLE);
                ly_toolbar.setVisibility(View.GONE);
                ly_toolbar2.setVisibility(View.GONE);
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
            case MSG_CANCEL_FOLLOW://取消关注
                isFollow = 0;
                btn_Follow.setClickable(true);
                btn_Follow.setVisibility(View.VISIBLE);
                btn_Follow.setImageResource(R.drawable.btn_room_concern_n);
                Animation rotateAnimation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.free_fall_up);
                btn_Follow.startAnimation(rotateAnimation2);
                break;
            case MSG_DO_FOLLOW://关注
                isFollow = 1;
                btn_Follow.setImageResource(R.drawable.btn_room_concern_s);
                Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.free_fall_down);
                btn_Follow.startAnimation(rotateAnimation);
                btn_Follow.setVisibility(View.GONE);
                callEvents.onSendMessage(GlobalDef.APPEND_FOLLOW, false);
                break;
            case MSG_ADAPTER_NOTIFY_GIFT:
                initGiftViewpager();
                initGiftNumSpinner();
                setRoomPopSpinner();
                break;
            case SHOW_SOFT_KEYB://键盘弹出事件
                if (ly_main != null) {
                    int getVirtualBarHeigh = ScreenUtils.getVirtualBarHeigh(getActivity());
                    ViewGroup.LayoutParams params = ly_main.getLayoutParams();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        params.height = App.screenDpx.heightPixels - (int) msg.obj + getVirtualBarHeigh;
                    } else {
                        params.height = App.screenDpx.heightPixels - (int) msg.obj;
                    }
                    params.width = App.screenDpx.widthPixels;
                    ly_main.setLayoutParams(params);
                    if (ly_send.getVisibility() == View.GONE) {
                        ly_send.setVisibility(View.VISIBLE);
                        ly_send.findFocus();
                        ly_toolbar.setVisibility(View.GONE);
                        ly_toolbar2.setVisibility(View.GONE);
                    }
                }
                break;
            case ONSHOW_SOFT_KEYB://键盘收起了
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
        if (giftModelList.size() <= 2 && (!giftA || !giftB)) {
            startGiftAnimation(giftModelList.get(0));
        }
    }

    //开始礼物特效
    private void startGiftAnimation(GiftAnimationModel giftModel) {
        if (!giftB) {
            giftB = true;
            ly_gift_view_s.setVisibility(View.VISIBLE);
            ly_gift_view_s.startAnimation(translateAnimation_in_s);
            if (giftModel.giftmodel != null && giftModel.giftmodel.getImageURL() != null) {
                imageView_s.setImageURI(VerificationUtil.getImageUrl(giftModel.giftmodel.getImageURL()));
            }
            if (giftModel.userheadpoto != null) {
                gif_img_head_s.setImageURI(VerificationUtil.getImageUrl(giftModel.userheadpoto));
            }
            txt_from_user_s.setText(giftModel.from_uname);
            imageView_s.startAnimation(translate_in_s);
            GiftAnimationModelB = giftModel;
            giftModelList.remove(giftModel);
        } else if (!giftA) {
            giftA = true;
            ly_gift_view.setVisibility(View.VISIBLE);
            ly_gift_view.startAnimation(translateAnimation_in);
            if (giftModel.giftmodel != null && giftModel.giftmodel.getImageURL() != null) {
                imageView.setImageURI(VerificationUtil.getImageUrl(giftModel.giftmodel.getImageURL()));
            }
            if (giftModel.userheadpoto != null) {
                gif_img_head.setImageURI(VerificationUtil.getImageUrl(giftModel.userheadpoto));
            }
            txt_from_user.setText(giftModel.from_uname);
            imageView.startAnimation(translate_in);
            GiftAnimationModelA = giftModel;
            giftModelList.remove(giftModel);
        }

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
                                if (translateAnimation_out != null) {
                                    translateAnimation_out.start();
                                    numText.setText(str);
                                    numText1.setText(str);
                                    numText1.startAnimation(scaleAnimation);
                                    numText.startAnimation(scaleAnimation);
                                }
                            }
                        });
                    }
                    try {
                        Thread.sleep(220);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //礼物数量动画
    private void addGiftAnimationNum_s(final GiftAnimationModel giftModel) {
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
                                if (translateAnimation_out_s != null) {
                                    translateAnimation_out_s.start();
                                    numText_s.setText(str);
                                    numText1_s.setText(str);
                                    numText1_s.startAnimation(scaleAnimation_s);
                                    numText_s.startAnimation(scaleAnimation_s);
                                }
                            }
                        });
                    }
                    try {
                        Thread.sleep(220);
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
            ChatRoomActivity.roomModel.setLikenum(ChatRoomActivity.roomModel.getLikenum() + 1);
            txt_likeNum.setText(String.valueOf(ChatRoomActivity.roomModel.getLikenum()));
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
//                            if (i > 20 && isRun) {
//                                isRun = false;
//                                return;
//                            }
                            Thread.sleep(200);
                            if (isAdded()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isRun = true;
                                        if (loveView != null) {
                                            loveView.addHeart();
                                            if (ChatRoomActivity.roomModel != null) {
                                                ChatRoomActivity.roomModel.setLikenum(ChatRoomActivity.roomModel.getLikenum() + 1);
                                                txt_likeNum.setText(String.valueOf(ChatRoomActivity.roomModel.getLikenum()));
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
        if (App.giftdatas.size() > 0) {
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
        }
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
        public void follow(final String val, String userId) {
            if (!liveUserModel.userid.equals(userModel.userid) && userId.equals(liveUserModel.userid)) {
                if (val != null && val.equals("0")) {
                    fragmentHandler.obtainMessage(MSG_DO_FOLLOW).sendToTarget();
                } else {
                    fragmentHandler.obtainMessage(MSG_CANCEL_FOLLOW).sendToTarget();
                }
            }
        }

        /**
         * 踢人
         * @param userId 用户id
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
            callEvents.onSendMessage(GlobalDef.APPEND_SHARED, false);
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
        if (mAdapter != null) {
            mAdapter.clearDeviceList();
            mAdapter = null;
        }
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