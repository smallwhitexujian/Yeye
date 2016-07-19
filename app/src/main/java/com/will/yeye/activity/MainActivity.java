package com.will.yeye.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.datasource.DataSource;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.will.common.string.json.JsonUtil;
import com.will.common.tool.view.DisplayTool;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.will.yeye.CommonUrlConfig;
import com.will.yeye.R;
import com.will.yeye.activity.base.BaseActivity;
import com.will.yeye.activity.function.ChatRoom;
import com.will.yeye.activity.function.MainEnter;
import com.will.yeye.adapter.CommonAdapter;
import com.will.yeye.adapter.SimpleFragmentPagerAdapter;
import com.will.yeye.adapter.ViewHolder;
import com.will.yeye.application.App;
import com.will.yeye.db.model.BasicUserInfoDBModel;
import com.will.yeye.fragment.LeftFragment;
import com.will.yeye.model.CommonParseListModel;
import com.will.yeye.model.GiftModel;
import com.will.yeye.model.RoomModel;
import com.will.yeye.receiver.NetworkReceiver;
import com.will.yeye.util.CacheDataManager;
import com.will.yeye.util.LoadBitmap;
import com.will.yeye.util.SPreferencesTool;
import com.will.yeye.util.StartActivityHelper;
import com.will.yeye.util.UriHelper;
import com.will.yeye.util.roomSoundState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {
    public final int MSG_SUCC = 1;
    public final int MSG_ERR = -1;
    private BroadcastReceiver receiver;
    private List<Map> roomListData = new ArrayList<>();
    private BasicUserInfoDBModel userModel;
    private SlidingMenu Slidmenu;
    private CommonAdapter<Map> commonAdapter;
    private SimpleDraweeView mFaceIcon;//头像
    private ImageView searchIcon, img_live;
    private TextView hotTab, followTab;
    private FragmentManager fragmentManager = null;
    private MainEnter mainEnter;
    private SimpleFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private LeftFragment leftFragment;
    private GestureDetector gestureDetector;
    private ImageView home_guide;
    private SharedPreferences preferences;
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setView();
        roomSoundState roomsoundState = roomSoundState.getInstance();
        roomsoundState.init(this);
        initMenu();
        receiver = new NetworkReceiver(new NetworkReceiver.NetWorkHandler() {
            @Override
            public void onActive(int networkType) {
            }

            @Override
            public void onInactive() {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //重新加载
        userModel = CacheDataManager.getInstance().loadUser();
        if (userModel != null) {
            mFaceIcon.setImageURI(UriHelper.obtainUri(userModel.headurl));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        hotTab = (TextView) findViewById(R.id.hot_textview);
        followTab = (TextView) findViewById(R.id.follow_textview);
        searchIcon = (ImageView) findViewById(R.id.search_icon);
        img_live = (ImageView) findViewById(R.id.img_live);
        mFaceIcon = (SimpleDraweeView) findViewById(R.id.face_icon);
        home_guide = (ImageView) findViewById(R.id.home_guide);

        home_guide.setOnClickListener(this);
        hotTab.setOnClickListener(this);
        followTab.setOnClickListener(this);
        searchIcon.setOnClickListener(this);
        mFaceIcon.setOnClickListener(this);
        img_live.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        fragmentManager = getSupportFragmentManager();
    }

    private void setView() {
        drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.btn_navigation_bar_hot_n);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mainEnter = new MainEnter(MainActivity.this);
        commonAdapter = new CommonAdapter<Map>(MainActivity.this, roomListData, R.layout.item_room) {
            @Override
            public void convert(ViewHolder helper, final Map item, final int position) {
                helper.setText(R.id.tv_describe, item.get("barname").toString());
                helper.setImageViewByImageLoader(R.id.img_body, item.get("barimage").toString());
                helper.setText(R.id.tv_address, item.get("roomserverip").toString());
                helper.setOnClick(R.id.img_body, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RoomModel roomModel = new RoomModel();
                        roomModel.setId(Integer.parseInt((String) item.get("barid")));
                        roomModel.setName((String) item.get("barname"));
                        String roomserverip = (String) item.get("roomserverip");
                        roomModel.setIp(roomserverip.split(":")[0]);
                        roomModel.setPort(Integer.valueOf(roomserverip.split(":")[1]));
//                        roomModel.setHeatDay((String)item.get("heatday"));
//                        roomModel.setLevel((String)item.get("barlevel"));
                        roomModel.setRoomType("watch");
                        ChatRoom.enterChatRoom(MainActivity.this, roomModel);
                    }
                });
            }
        };
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                clearTabColor();
                clearTabTextSize();
                String hotStr = getString(R.string.live_hot);
                final float textSize = DisplayTool.dip2px(MainActivity.this, 17);
                if (hotStr.equals(pagerAdapter.getPageTitle(position))) {
                    //hotTab.setTextColor(0xFFFFFFFF);
                    hotTab.setCompoundDrawables(null, null, null, drawable);
                    hotTab.setTextSize(textSize);
                    hotTab.setTextColor(0xFFF9A155);
                    // hotTab.setBackgroundResource(R.color.color_white);
                } else {
                    // followTab.setTextColor(0xFFFFFFFF);
                    followTab.setCompoundDrawables(null, null, null, drawable);
                    followTab.setTextSize(textSize);
                    followTab.setTextColor(0xFFF9A155);
                    //followTab.setBackgroundResource(R.color.color_white);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setCurrentItem(0);
        clearTabColor();
        clearTabTextSize();
        hotTab.setCompoundDrawables(null, null, null, drawable);
        hotTab.setTextSize(DisplayTool.dip2px(MainActivity.this, 17));
        hotTab.setTextColor(0xFFF9A155);

        //预加载礼物列表
        if (App.giftdatas.size() <= 0) {
            loadGiftList();
        }

        preferences = getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
        boolean isguide = preferences.getBoolean("home_guide", true);
        if (isguide) {
            home_guide.setVisibility(View.VISIBLE);
        } else {
            home_guide.setVisibility(View.GONE);
        }
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
                    for (int i = 0; i < App.giftdatas.size(); i++) {
                        LoadBitmap.loadBitmap(MainActivity.this, Uri.parse(App.giftdatas.get(i).getImageURL()), new LoadBitmap.LoadBitmapCallback() {
                            @Override
                            public void onLoadSuc(@Nullable Bitmap bitmap) {
                            }

                            @Override
                            public void onLoadFaild(DataSource dataSource) {
                            }
                        });
                    }
                }
            }
        };
        ChatRoom chatRoom = new ChatRoom(MainActivity.this);
        chatRoom.loadGiftList(CommonUrlConfig.PropList, CacheDataManager.getInstance().loadUser().token, callback);
    }


    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_SUCC:
                commonAdapter.notifyDataSetChanged();
                // ToastUtils.showToast(MainActivity.this, "成功");
                break;
            case MSG_ERR:
                ToastUtils.showToast(MainActivity.this, R.string.fail);
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
                StartActivityHelper.jumpActivity(MainActivity.this, ChatRoomActivity.class, roomModel);
                break;
            case R.id.hot_textview:
                viewPager.setCurrentItem(0);
                clearTabColor();

                hotTab.setCompoundDrawables(null, null, null, drawable);
                break;
            case R.id.follow_textview:
                viewPager.setCurrentItem(1);
                clearTabColor();

                followTab.setCompoundDrawables(null, null, null, drawable);
                break;
            case R.id.search_icon:
                StartActivityHelper.jumpActivityDefault(MainActivity.this, SearchActivity.class);
                break;
            case R.id.face_icon:
                Slidmenu.showMenu();
                break;
            case R.id.home_guide:
                home_guide.setVisibility(View.GONE);
                SPreferencesTool.getInstance().putValue(this, "home_guide", false);
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
        hotTab.setTextColor(0xFFFFCA9C);
        followTab.setCompoundDrawables(null, null, null, null);
        followTab.setTextColor(0xFFFFCA9C);
    }

    private void clearTabTextSize() {
        final float textSize = DisplayTool.dip2px(MainActivity.this, 14);
        hotTab.setTextSize(textSize);
        followTab.setTextSize(textSize);
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
        Slidmenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {

            }
        });

        Slidmenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {

            }
        });
        leftFragment = new LeftFragment();
        fragmentManager.beginTransaction().replace(R.id.left_menu, leftFragment).commit();

    }

    public MainEnter getMainEnter() {

        return mainEnter;
    }

    public void openMenu() {
        Slidmenu.showMenu();
    }

    public void closeMenu() {
        Slidmenu.toggle();
    }

    public void registerFragmentTouch(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    public void unRegisterFragmentTouch() {
        this.gestureDetector = null;
    }

    public void selectTab(int tab) {
        viewPager.setCurrentItem(tab);
    }
}
