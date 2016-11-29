package com.angelatech.yeyelive.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.SimpleFragmentPagerAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.fragment.LeftFragment;
import com.angelatech.yeyelive.model.CommonParseListModel;
import com.angelatech.yeyelive.model.GiftModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.model.SystemMessage;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.SPreferencesTool;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.UploadApp;
import com.angelatech.yeyelive.util.Utility;
import com.angelatech.yeyelive.util.VerificationUtil;
import com.angelatech.yeyelive.util.roomSoundState;
import com.angelatech.yeyelive.view.FrescoBitmapUtils;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.will.common.log.DebugLogs;
import com.will.common.tool.DeviceTool;
import com.will.common.tool.view.DisplayTool;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoRoundView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private final int MSG_SUCC = 1;
    private final int MSG_ERR = -1;
    private final int MSG_LOAD_SUC = 2;

    private List<Map> roomListData = new ArrayList<>();
    private BasicUserInfoDBModel userModel;
    private SlidingMenu Slidmenu;
    private CommonAdapter<Map> commonAdapter;
    private FrescoRoundView mFaceIcon;//头像
    private ImageView iv_vip;
    private TextView hotTab, followTab, newTab, pot;
    private FragmentManager fragmentManager = null;
    private MainEnter mainEnter;
    private SimpleFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private GestureDetector gestureDetector;
    private ImageView home_guide;
    private boolean isShowOpen;
    private Drawable drawable;
    private String versionCode = null;
    private String versionName = null;
    private SystemMessage systemMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        versionCode = Utility.getVersionCode(MainActivity.this);
        versionName = Utility.getVersionName(MainActivity.this);
        initView();
        setView();
        roomSoundState roomsoundState = roomSoundState.getInstance();
        roomsoundState.init(this);
        initMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userModel = CacheDataManager.getInstance().loadUser();
        if (systemMessage.haveNewSystemMsg(MainActivity.this)) {
            pot.setVisibility(View.VISIBLE);
        } else {
            pot.setVisibility(View.GONE);
        }
        setPhoto();
//        if (SPreferencesTool.getInstance().getBooleanValue(this, "cancel", false)) {
//            return;
//        } else {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                upApk();
            }
        });
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        systemMessage = new SystemMessage();
        userModel = CacheDataManager.getInstance().loadUser();
        mark(MainActivity.this, userModel.userid);
        hotTab = (TextView) findViewById(R.id.hot_textview);
        followTab = (TextView) findViewById(R.id.follow_textview);
        newTab = (TextView) findViewById(R.id.new_textview);
        pot = (TextView) findViewById(R.id.pot);
        ImageView searchIcon = (ImageView) findViewById(R.id.search_icon);
        ImageView img_live = (ImageView) findViewById(R.id.img_live);
        mFaceIcon = (FrescoRoundView) findViewById(R.id.face_icon);
        home_guide = (ImageView) findViewById(R.id.home_guide);
        iv_vip = (ImageView) findViewById(R.id.iv_vip);
        home_guide.setOnClickListener(this);
        hotTab.setOnClickListener(this);
        followTab.setOnClickListener(this);
        newTab.setOnClickListener(this);
        searchIcon.setOnClickListener(this);
        mFaceIcon.setOnClickListener(this);
        img_live.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        fragmentManager = getSupportFragmentManager();
    }

    private void setPhoto() {
        if (userModel != null) {
            mFaceIcon.setImageURI(VerificationUtil.getImageUrl(userModel.headurl));
            if (userModel.isv.equals("1")) {
                iv_vip.setVisibility(View.VISIBLE);
            } else {
                iv_vip.setVisibility(View.GONE);
            }
        }
    }

    private void setView() {
        drawable = ContextCompat.getDrawable(this, R.drawable.btn_navigation_bar_hot_n);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mainEnter = new MainEnter(this);
        commonAdapter = new CommonAdapter<Map>(this, roomListData, R.layout.item_room) {
            @Override
            public void convert(ViewHolder helper, final Map item, final int position) {
                helper.setText(R.id.tv_describe, item.get("barname").toString());
                helper.setImageURI(R.id.img_body, item.get("barimage").toString());
                helper.setText(R.id.tv_address, item.get("roomserverip").toString());
                helper.setOnClick(R.id.img_body, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//观看进入房间
                        RoomModel roomModel = new RoomModel();
                        roomModel.setId(Integer.parseInt((String) item.get("barid")));
                        roomModel.setName((String) item.get("barname"));
                        String roomserverip = (String) item.get("roomserverip");
                        roomModel.setIp(roomserverip.split(":")[0]);
                        roomModel.setPort(Integer.valueOf(roomserverip.split(":")[1]));
                        roomModel.setRoomType(App.LIVE_WATCH);
                        ChatRoom.enterChatRoom(MainActivity.this, roomModel);
                    }
                });
            }
        };
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                clearTabColor();
                clearTabTextSize();
                String hotStr = getString(R.string.live_hot);
                String followStr = getString(R.string.live_follow);
                String newStr = getString(R.string.live_new);
                final float textSize = DisplayTool.dip2px(MainActivity.this, 15);
                if (hotStr.equals(pagerAdapter.getPageTitle(position))) {
                    hotTab.setCompoundDrawables(null, null, null, drawable);
                    hotTab.setTextSize(textSize);
                    hotTab.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_d80c18));
                }
                if (followStr.equals(pagerAdapter.getPageTitle(position))) {
                    followTab.setCompoundDrawables(null, null, null, drawable);
                    followTab.setTextSize(textSize);
                    followTab.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_d80c18));
                }
                if (newStr.equals(pagerAdapter.getPageTitle(position))) {
                    newTab.setCompoundDrawables(null, null, null, drawable);
                    newTab.setTextSize(textSize);
                    newTab.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_d80c18));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setCurrentItem(1);
        clearTabColor();
        clearTabTextSize();
        hotTab.setCompoundDrawables(null, null, null, drawable);
        hotTab.setTextSize(DisplayTool.dip2px(this, 15));
        hotTab.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));

        //预加载礼物列表
        loadGiftList();

        boolean isguide = SPreferencesTool.getInstance().getBooleanValue(this, SPreferencesTool.home_guide_key);
        if (isguide) {
            home_guide.setVisibility(View.VISIBLE);
        } else {
            home_guide.setVisibility(View.GONE);
        }
    }

    // 初始化礼物列表
    private void loadGiftList() {
        if (userModel != null) {
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
                        for (int i = 0; i < App.giftdatas.size(); i++) {
                            String url = App.giftdatas.get(i).getImageURL();
                            url = VerificationUtil.getImageUrl100(url);
                            FrescoBitmapUtils.getImageBitmap(MainActivity.this, url, new FrescoBitmapUtils.BitCallBack() {
                                @Override
                                public void onNewResultImpl(Bitmap bitmap) {
                                }
                            });
                        }
                    }
                }
            };
            ChatRoom chatRoom = new ChatRoom(this);
            chatRoom.loadGiftList(CommonUrlConfig.PropList, userModel.token, callback);
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_SUCC:
                commonAdapter.notifyDataSetChanged();
                break;
            case MSG_ERR:
                ToastUtils.showToast(this, R.string.fail);
                break;
            case MSG_LOAD_SUC:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_live:
                RoomModel roomModel = new RoomModel();
                roomModel.setId(0);
                roomModel.setRoomType(App.LIVE_PREVIEW);
                roomModel.setUserInfoDBModel(userModel);
                StartActivityHelper.jumpActivity(this, ChatRoomActivity.class, roomModel);
                break;
            case R.id.hot_textview:
                viewPager.setCurrentItem(0);
                clearTabColor();
                hotTab.setCompoundDrawables(null, null, null, drawable);
                hotTab.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));
                break;
            case R.id.follow_textview:
                viewPager.setCurrentItem(2);
                clearTabColor();
                followTab.setCompoundDrawables(null, null, null, drawable);
                followTab.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));
                break;
            case R.id.new_textview:
                viewPager.setCurrentItem(1);
                clearTabColor();
                newTab.setCompoundDrawables(null, null, null, drawable);
                newTab.setTextColor(ContextCompat.getColor(this, R.color.color_d80c18));
                break;
            case R.id.search_icon:
                StartActivityHelper.jumpActivityDefault(this, SearchActivity.class);
                break;
            case R.id.face_icon:
                Slidmenu.showMenu();
                break;
            case R.id.home_guide:
                home_guide.setVisibility(View.GONE);
                SPreferencesTool.getInstance().putValue(this, SPreferencesTool.home_guide_key, false);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(event);
        }
        return false;
    }

    private void clearTabColor() {
        hotTab.setCompoundDrawables(null, null, null, null);
        followTab.setCompoundDrawables(null, null, null, null);
        newTab.setCompoundDrawables(null, null, null, null);
    }

    private void clearTabTextSize() {
        final float textSize = DisplayTool.dip2px(this, 14);
        hotTab.setTextSize(textSize);
        followTab.setTextSize(textSize);
        newTab.setTextSize(textSize);
    }

    /**
     * 初始化菜单选项
     */
    private void initMenu() {
        // configure the SlidingMenu
        Slidmenu = new SlidingMenu(this);
        Slidmenu.setMode(SlidingMenu.LEFT);// 设置触摸屏幕的模式
        Slidmenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        Slidmenu.setShadowWidthRes(R.dimen.shadow_width);
//        Slidmenu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单视图的宽度
//        Slidmenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        Slidmenu.setFadeDegree(0.35f);
        //把滑动菜单添加进所有的Activity中，可选值SLIDING_CONTENT ， SLIDING_WINDOW
        Slidmenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        Slidmenu.setMenu(R.layout.frame_left_menu);
        Slidmenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        final LeftFragment leftFragment = new LeftFragment();
        Slidmenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                isShowOpen = true;
                leftFragment.setPhoto();
            }
        });
        fragmentManager.beginTransaction().replace(R.id.left_menu, leftFragment).commit();
    }

    public MainEnter getMainEnter() {
        return mainEnter;
    }

    public void closeMenu() {
        Slidmenu.toggle();
    }

    public void registerFragmentTouch(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowOpen) {
                Slidmenu.toggle();
                isShowOpen = false;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //强制升级
    private void upApk() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                DebugLogs.e("response=========err==");
            }

            @Override
            public void onSuccess(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String data = jsonObject.getString("data");
                            JSONObject json = new JSONObject(data);
                            String AppURL = json.getString("AppURL");
                            String Content = json.getString("Content");
                            String apkVersion = json.getString("AppVersion");
                            int isUp = Integer.valueOf(json.getString("isUp"));
                            if (isUp == 1) {
                                SPreferencesTool.getInstance().saveUpLoadApk(MainActivity.this, true, apkVersion, Content, AppURL);
                            }
                            if (Integer.valueOf(apkVersion) > Integer.valueOf(versionCode)) {
                                UploadApp uploadApp = new UploadApp(Utility.getSDCardDir(MainActivity.this, App.FILEPATH_UPAPK));
                                uploadApp.showUpApk(MainActivity.this, Content, AppURL, isUp);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        ChatRoom chatRoom = new ChatRoom(this);
        chatRoom.upApk(CommonUrlConfig.apkUp, versionCode, callback);
    }

    /**
     * 统计策略
     */
    private SPreferencesTool sp;
    private String ACCOUNT_TIME_STAMP = "accountTimeStamp";

    private boolean markStrategy(Context context) {
        if (sp == null) {
            sp = SPreferencesTool.getInstance();
        }
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        long today = Long.valueOf(sdf.format(dt));
        long recordDay = sp.getLongValue(MainActivity.this, ACCOUNT_TIME_STAMP);
        if (recordDay == -1) {
            return true;
        } else if (today != recordDay) {
            return true;
        }
        return false;
    }

    public void mark(final Context context, String userId) {
        //不符合策略则不进行统计
        if (!markStrategy(context)) {
            return;
        }
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                //写文件
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                long today = Long.valueOf(sdf.format(dt));
                sp.putValue(context, ACCOUNT_TIME_STAMP, today);
            }
        };
        ChatRoom chatRoom = new ChatRoom(this);
        chatRoom.setMark(CommonUrlConfig.PlatformIntoLogIns, userId, DeviceTool.getUniqueID(context), Build.BRAND, versionName, callback);
    }
}
