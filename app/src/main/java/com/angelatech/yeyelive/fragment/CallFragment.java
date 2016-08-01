package com.angelatech.yeyelive.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.GlobalDef;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.RechargeActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.adapter.ChatLineAdapter;
import com.angelatech.yeyelive.adapter.CustomerPageAdapter;
import com.angelatech.yeyelive.adapter.GridViewAdapter;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.ChatLineModel;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.GiftAnimationModel;
import com.angelatech.yeyelive.model.GiftModel;
import com.angelatech.yeyelive.model.OnlineListModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.model.UserInfoModel;
import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.thirdShare.ThirdShareDialog;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.view.PeriscopeLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.string.json.JsonUtil;
import com.will.common.tool.network.NetWorkUtil;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

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
public class CallFragment extends BaseFragment implements View.OnLayoutChangeListener, View.OnClickListener {
    private View controlView;

    private final int MSG_ADAPTER_NOTIFY = 1;
    private final int MSG_SET_FOLLOW = 2;
    private final int HANDLER_GIFT_CHANGE_BACKGROUND = 13;
    private ImageView cameraSwitchButton;

    private ImageView btn_Follow;
    private ImageView btn_share;
    private ImageView img_open_send;
    private ImageView iv_vip;
    private TextView txt_barName, txt_likeNum, txt_online,
            gift_Diamonds, txt_room_des, gift_Recharge;
    private SimpleDraweeView img_head;
    private PeriscopeLayout loveView;                                                               // 显示心的VIEW
    private EditText txt_msg;
    private Button btn_send;
    private LinearLayout gift_send;
    private LinearLayout ly_send, ly_toolbar, ly_main, giftView;                                    // 礼物界面
    private Spinner roomPopSpinner, roomGiftNumSpinner;                                             // 礼物个数列表
    private int giftId;                                                                             // 礼物的ID
    private int isFollow;
    private static ListView chatline;
    private OnCallEvents callEvents;

    private List<OnlineListModel> PoplinkData = new ArrayList<>();

    protected FragmentManager fragmentManager;
    public static CallFragment instance = null;
    private final int numArray[] = {1, 10, 22, 55, 77, 100}; //礼物数量列表

    private ArrayList<GiftAnimationModel> giftModelList = new ArrayList<>();

    //软键盘弹起后所占高度阀值
    private int keyHeight = 100;
    private static ChatLineAdapter<ChatLineModel> mAdapter;
    private BasicUserInfoDBModel userModel;

    private RelativeLayout ly_gift_view;                                                            //礼物特效view
    private TextView numText, numText1;                                                             //礼物数量  阴影
    private TextView txt_from_user;                                                  //发送礼物的人，礼物名称
    private SimpleDraweeView imageView, gif_img_head;                                               //礼物图片， 礼物发送人的头像
    private Animation translateAnimation_in, translateAnimation_out, translate_in, scaleAnimation;  //礼物特效

    private boolean giftA = false;                                                                  //礼物特效播放状态

    private boolean isRun = false;
    private boolean isTimeCount = true;                                 // 是否打开倒计时
    private long lastClick;                                             // 点赞点击事件
    private int count = 0;                                              // 统计点赞次数

    private TimerTask task;
    private Timer timer;
    private TimeCount timeCount;

    private ViewPager viewPager;
    private List<GridView> gridViews = new ArrayList<>();

    private int GridViewIndex = 0;
    private int GridViewLastIndex = -1;
    private int GridViewItemIndex = 0;
    private int GridViewItemLastIndex = -1;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        controlView = inflater.inflate(R.layout.fragment_call, container, false);
        initView();
        fragmentManager = getFragmentManager();
        instance = this;
        return controlView;
    }

    //根据礼物ID获取礼物单价
    public int getGiftCointoId(int giftId) {
        for (int i = 0; i < App.giftdatas.size(); i++) {
            if (App.giftdatas.get(i).getID() == giftId) {
                return Integer.parseInt(App.giftdatas.get(i).getPrice());
            }
        }
        return 0;
    }


    private void initView() {
        cameraSwitchButton = (ImageView) controlView.findViewById(R.id.button_call_switch_camera);
        txt_msg = (EditText) controlView.findViewById(R.id.txt_msg);
        btn_send = (Button) controlView.findViewById(R.id.btn_send);
        chatline = (ListView) controlView.findViewById(R.id.chatline);
        img_open_send = (ImageView) controlView.findViewById(R.id.img_open_send);
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
        gift_send = (LinearLayout) controlView.findViewById(R.id.gift_send);
        txt_barName = (TextView) controlView.findViewById(R.id.txt_barname);
        txt_likeNum = (TextView) controlView.findViewById(R.id.txt_likenum);
        txt_online = (TextView) controlView.findViewById(R.id.txt_online);
        btn_Follow = (ImageView) controlView.findViewById(R.id.btn_Follow);
        btn_share = (ImageView) controlView.findViewById(R.id.btn_share);
        iv_vip = (ImageView) controlView.findViewById(R.id.iv_vip);
        gift_Diamonds = (TextView) controlView.findViewById(R.id.gift_Diamonds);
        txt_room_des = (TextView) controlView.findViewById(R.id.txt_room_des);
        gift_Recharge = (TextView) controlView.findViewById(R.id.gift_Recharge);

        userModel = CacheDataManager.getInstance().loadUser();
        gift_Diamonds.setText(userModel.diamonds);

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
        gift_Recharge.setOnClickListener(this);

        txt_msg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sendmsg();
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
                numText.setText("X1");
                numText1.setText("X1");
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
    }

    /**
     * 中间在线人数列表
     */
    public void initPeopleView(final List<OnlineListModel> linkData) {
        try {
            LayoutInflater mInflater = LayoutInflater.from(getActivity());
            LinearLayout mGallery = (LinearLayout) controlView.findViewById(R.id.id_gallery);
            mGallery.removeAllViews();
            PoplinkData.clear();
            for (int i = 0; i < linkData.size(); i++) {
                if (linkData.get(i).uid != Integer.parseInt(userModel.userid)) {
                    //礼物发送者列表中排除自己，避免自己给自己发送礼物
                    if (linkData.get(i).uid == Integer.parseInt(ChatRoomActivity.roomModel.getUserInfoDBModel().userid)) {
                        //房主默认选中
                        PoplinkData.add(0, linkData.get(i));
                    } else {
                        PoplinkData.add(linkData.get(i));
                    }
                }

                //在线列表上过滤主播
                if (Integer.parseInt(ChatRoomActivity.roomModel.getUserInfoDBModel().userid) != linkData.get(i).uid) {
                    View view = mInflater.inflate(R.layout.item_chatroom_gallery, mGallery, false);
                    SimpleDraweeView img = (SimpleDraweeView) view.findViewById(R.id.item_chatRoom_gallery_image);
                    //ImageView iv_vip = (ImageView) view.findViewById(R.id.iv_vip);
                    //if (linkData.get(i))
                    String str = linkData.get(i).headphoto;
                    img.setBackgroundResource(R.drawable.default_face_icon);
                    img.setImageURI(Uri.parse(str));
                    final int finalI = i;
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserInfoModel searchItemModel = new UserInfoModel();
                            searchItemModel.userid = String.valueOf(linkData.get(finalI).uid);
                            searchItemModel.nickname = linkData.get(finalI).name;
                            searchItemModel.headurl = linkData.get(finalI).headphoto;
                            if (ChatRoomActivity.roomModel.getRoomType().equals(App.LIVE_HOST)) {
                                searchItemModel.isout = true;
                            }
                            UserInfoDialogFragment userInfoDialogFragment = new UserInfoDialogFragment();
                            userInfoDialogFragment.setUserInfoModel(searchItemModel);
                            userInfoDialogFragment.show(getActivity().getSupportFragmentManager(), "");
                        }
                    });
                    mGallery.addView(view);
                }
            }

            //在线人数要排除主播,所以在总数的基础上减1
            txt_online.setText(String.valueOf(linkData.size() - 1));

            ArrayAdapter<OnlineListModel> PopAdapter = new ArrayAdapter<OnlineListModel>(getActivity(), R.layout.simple_spinner_gift_pop_item) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.spinner_item_layout, parent, false);
                    TextView label = (TextView) view.findViewById(R.id.spinner_item_label);
                    label.setText(PoplinkData.get(position).name);
                    return view;
                }
            };

            // 设置下拉列表的风格
            PopAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
            roomPopSpinner.setAdapter(PopAdapter);
            roomPopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    parent.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            PopAdapter.clear();
            PopAdapter.addAll(PoplinkData);
            PopAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置房间信息
    public void setRoomInfo(RoomModel roommodel) {
        if (roommodel.getUserInfoDBModel() != null && roommodel.getUserInfoDBModel().headurl != null && !roommodel.getUserInfoDBModel().headurl.equals("") && Uri.parse(roommodel.getUserInfoDBModel().headurl) != null) {
            img_head.setImageURI(Uri.parse(roommodel.getUserInfoDBModel().headurl));
            if (roommodel.getUserInfoDBModel().isv.equals("1")) {
                iv_vip.setVisibility(View.VISIBLE);
            } else {
                iv_vip.setVisibility(View.GONE);
            }
        }
        txt_barName.setText(roommodel.getUserInfoDBModel().nickname);
        if (App.giftdatas.size() <= 0) {
            loadGiftList();
        }
        fragmentHandler.obtainMessage(MSG_ADAPTER_NOTIFY).sendToTarget();
    }

    public void setLikenum(int likenum) {
        txt_likeNum.setText(String.valueOf(likenum));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_main:
                if (ly_send.getVisibility() == View.VISIBLE) {
                    Utility.closeKeybord(txt_msg, getActivity());
                }
                if (giftView.getVisibility() == View.VISIBLE) {
                    ly_toolbar.setVisibility(View.VISIBLE);
                    giftView.setVisibility(View.GONE);
                }
                if (ChatRoomActivity.roomModel.getRoomType().equals(App.LIVE_WATCH)) {
                    if (NetWorkUtil.isNetworkConnected(getActivity())) {
                        doHeart();
                    }
                }
                break;
            case R.id.btn_send:
                sendmsg();
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
                giftView.setVisibility(View.VISIBLE);
                ly_toolbar.setVisibility(View.GONE);
                break;
            case R.id.gift_send:
                int nNum = Integer.parseInt(roomGiftNumSpinner.getSelectedItem().toString());
                OnlineListModel toPeople;
                if (roomPopSpinner.getSelectedItem() != null) {
                    toPeople = (OnlineListModel) (roomPopSpinner.getSelectedItem());
                } else {
                    ToastUtils.showToast(getActivity(), getActivity().getString(R.string.please_select_gift_pople));
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
                //分享组件
                ThirdShareDialog.Builder builder = new ThirdShareDialog.Builder(getActivity(), fragmentManager, null);
                builder.setShareContent(getString(R.string.share_title), ChatRoomActivity.roomModel.getName(),
                        CommonUrlConfig.shareURL,
                        ChatRoomActivity.roomModel.getUserInfoDBModel().headurl);
                builder.RegisterCallback(listener);
                builder.create().show();
                break;
            case R.id.img_head:
                UserInfoModel searchItemModel = new UserInfoModel();
                searchItemModel.userid = ChatRoomActivity.roomModel.getUserInfoDBModel().userid;
                searchItemModel.nickname = ChatRoomActivity.roomModel.getUserInfoDBModel().nickname;
                searchItemModel.headurl = ChatRoomActivity.roomModel.getUserInfoDBModel().headurl;
                searchItemModel.isfollow = String.valueOf(isFollow);

                UserInfoDialogFragment userInfoDialogFragment = new UserInfoDialogFragment();
                userInfoDialogFragment.setUserInfoModel(searchItemModel);
                userInfoDialogFragment.show(getActivity().getSupportFragmentManager(), "");

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

    private void sendmsg() {
        if (txt_msg.getText().length() > 0) {
            callEvents.onSendMessage(txt_msg.getText().toString());
            txt_msg.setText("");
        } else {
            ToastUtils.showToast(getActivity(), getActivity().getString(R.string.please_input_text));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //添加layout大小发生改变监听器,用来监听键盘
        ly_main.addOnLayoutChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearTask();
        clearAnimation();
        instance = null;
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

    //监听键盘弹起
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起

        DebugLogs.e("bottom" + bottom + "oldBottom" + oldBottom);
        if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            //键盘收起了
            if (ly_send.getVisibility() == View.VISIBLE) {
                ly_send.setVisibility(View.GONE);
                ly_toolbar.setVisibility(View.VISIBLE);

//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                //lp.setMargins(0, 0, 0, bottom - oldBottom );
//                lp.setMargins(0, 0, 0, 0);
//                App.chatRoomApplication.viewPanel.setLayoutParams(lp);
            }
        } else if (bottom - oldBottom < 0) {

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, bottom - oldBottom);
            App.chatRoomApplication.viewPanel.setLayoutParams(lp);
        }
    }

    public void setCameraSwitchButton(String liveType) {
        if (liveType.equals(App.LIVE_HOST)) {
            cameraSwitchButton.setVisibility(View.VISIBLE);
            btn_share.setVisibility(View.GONE);
        } else {
            cameraSwitchButton.setVisibility(View.GONE);
            btn_share.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 聊天记录初始化，
     */
    public void initChatMessage(Context context) {
        if (mAdapter == null) {
            mAdapter = new ChatLineAdapter<>(context, App.mChatlines);
        }
        if (chatline == null) {
            chatline = (ListView) controlView.findViewById(R.id.chatline);
        }
        mAdapter.notifyDataSetChanged();
        chatline.setAdapter(mAdapter);
        chatline.setSelection(mAdapter.getCount());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }

    //按下返回键
    public void keyback() {
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

    //设置关注状态
    public void setIsFollow(int value) {
        isFollow = value;
        fragmentHandler.obtainMessage(MSG_SET_FOLLOW).sendToTarget();
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
        ChatRoom chatRoom = new ChatRoom(getActivity());
        chatRoom.UserFollow(CommonUrlConfig.UserFollow, userModel.token, userModel.userid,
                ChatRoomActivity.roomModel.getUserInfoDBModel().userid, isFollow, callback);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_ADAPTER_NOTIFY:
                initGiftViewpager();
                initGiftNumSpinner();
                break;
            case MSG_SET_FOLLOW:
                switch (isFollow) {
                    case -1:
                        btn_Follow.setVisibility(View.GONE);
                        break;
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
            case ChatRoomActivity.MSG_OPEN_GIFT_LAYOUT:
                giftView.setVisibility(View.VISIBLE);
                ly_toolbar.setVisibility(View.GONE);
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
        }
    }

    public Handler followHandle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    isFollow = 0;
                    btn_Follow.setVisibility(View.VISIBLE);
                    btn_Follow.setImageResource(R.drawable.btn_room_concern_n);
                    Animation rotateAnimation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.free_fall_up);
                    btn_Follow.startAnimation(rotateAnimation2);
                    break;
                case 1:
                    isFollow = 1;
                    btn_Follow.setImageResource(R.drawable.btn_room_concern_s);
                    Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.free_fall_down);
                    btn_Follow.startAnimation(rotateAnimation);
                    btn_Follow.setVisibility(View.GONE);
                    break;
            }
            super.handleMessage(msg);
        }

    };

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
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                arg0.setVisibility(View.VISIBLE);
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
                App.giftdatas.clear();
                if (result != null) {
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
            imageView.setImageURI(Uri.parse(giftModel.giftmodel.getImageURL()));
        }
        if (giftModel.userheadpoto != null) {
            gif_img_head.setImageURI(Uri.parse(giftModel.userheadpoto));
        }
        txt_from_user.setText(giftModel.from_uname);
        // txt_gift_name.setText(giftaModel.giftmodel.getName());
        imageView.startAnimation(translate_in);

    }

    //礼物数量动画
    private void addGiftAnimationNum(final GiftAnimationModel giftModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= giftModel.giftnum; i++) {
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

    //根据礼物ID获取礼物链接
    public GiftModel getGifPath(int giftIndex) {
        for (int i = 0; i < App.giftdatas.size(); i++) {
            if (App.giftdatas.get(i).getID() == giftIndex) {
                return App.giftdatas.get(i);
            }
        }
        return null;
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
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < count; i++) {
                        if (i > 30 && isRun) {
                            isRun = false;
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isRun = true;
                                if (loveView != null) {
                                    loveView.addHeart();
                                    ChatRoomActivity.roomModel.setLikenum(ChatRoomActivity.roomModel.getLikenum() + 1);
                                    txt_likeNum.setText(String.valueOf(ChatRoomActivity.roomModel.getLikenum()));
                                }
                            }
                        });
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    class TimeCount extends CountDownTimer {
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
}