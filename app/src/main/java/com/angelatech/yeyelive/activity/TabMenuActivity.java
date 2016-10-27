package com.angelatech.yeyelive.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.fragment.LeftFragment;
import com.angelatech.yeyelive.fragment.ListFragment;
import com.angelatech.yeyelive.model.BasicUserInfoModel;
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
import com.will.common.log.DebugLogs;
import com.will.common.tool.DeviceTool;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
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
 * 作者: Created by: xujian on Date: 2016/10/18.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */

public class TabMenuActivity extends BaseActivity {
    private BasicUserInfoDBModel userModel;
    private String versionCode = null;
    private String versionName = null;
    private SPreferencesTool sp;
    private SystemMessage systemMessage;
    private String ACCOUNT_TIME_STAMP = "accountTimeStamp";
    private FragmentManager fragmentManager = null;
    private Fragment peopleFragment;
    private ListFragment listFragment;
    private TextView btn_list, btn_me, pot;
    private RoomModel roomModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            roomModel = (RoomModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        }
        setContentView(R.layout.activity_tab_menu);
        initData();
        initView();
    }

    private void initView() {
        btn_list = (TextView) findViewById(R.id.iv_tab_room);
        btn_me = (TextView) findViewById(R.id.iv_tab_people);
        pot = (TextView) findViewById(R.id.pot);
        ImageView img_live = (ImageView) findViewById(R.id.img_live);
        LinearLayout layout_list = (LinearLayout) findViewById(R.id.layout_room);
        RelativeLayout layout_me = (RelativeLayout) findViewById(R.id.layout_people);

        img_live.setOnClickListener(this);
        layout_list.setOnClickListener(this);
        layout_me.setOnClickListener(this);
    }

    //数据初始化
    private void initData() {
        versionCode = Utility.getVersionCode(TabMenuActivity.this);
        versionName = Utility.getVersionName(TabMenuActivity.this);
        roomSoundState roomsoundState = roomSoundState.getInstance();
        roomsoundState.init(this);
        systemMessage = new SystemMessage();
        userModel = CacheDataManager.getInstance().loadUser();
        mark(TabMenuActivity.this, userModel.userid);
        loadGiftList();
        initFragment();
        if (roomModel != null) {
            BasicUserInfoModel loginUser = new BasicUserInfoModel();//登录信息
            loginUser.Userid = userModel.userid;
            loginUser.Token = userModel.token;
            roomModel.setLoginUser(loginUser);
            ChatRoom.enterChatRoom(TabMenuActivity.this, roomModel);
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
            case R.id.layout_people:
                clearSelectIcon();
                setSelectedMenu(R.id.layout_people);
                btn_me.setTextColor(ContextCompat.getColor(TabMenuActivity.this, R.color.color_d80c18));
                Drawable drawable = ContextCompat.getDrawable(TabMenuActivity.this, R.drawable.btn_menu_me_s);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                btn_me.setCompoundDrawables(null, drawable, null, null);
                break;
            case R.id.layout_room:
                clearSelectIcon();
                setSelectedMenu(R.id.layout_room);
                btn_list.setTextColor(ContextCompat.getColor(TabMenuActivity.this, R.color.color_d80c18));
                Drawable drawable2 = ContextCompat.getDrawable(TabMenuActivity.this, R.drawable.btn_menu_home_s);
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());//必须设置图片大小，否则不显示
                btn_list.setCompoundDrawables(null, drawable2, null, null);
                break;
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (listFragment == null && fragment instanceof ListFragment) {
            listFragment = (ListFragment) fragment;
        } else if (peopleFragment == null && fragment instanceof LeftFragment) {
            peopleFragment = fragment;
        }
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        if (listFragment == null) {
            listFragment = new ListFragment();
        }
        if (peopleFragment == null) {
            peopleFragment = new LeftFragment();
        }
        FragmentTransaction barTransaction = fragmentManager.beginTransaction();
        if (!listFragment.isAdded()) {
            barTransaction.add(R.id.contentFrame, listFragment, "ListFragment").show(listFragment);
        } else {
            barTransaction.show(listFragment);
        }
        barTransaction.commitAllowingStateLoss();
        FragmentTransaction peopleTransaction = fragmentManager.beginTransaction();
        if (!peopleFragment.isAdded()) {
            peopleTransaction.add(R.id.contentFrame, peopleFragment, "peopleFragment").hide(peopleFragment);
        } else {
            peopleTransaction.hide(peopleFragment);
        }
        peopleTransaction.commitAllowingStateLoss();
    }

    private void setSelectedMenu(int viewId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (viewId) {
            case R.id.layout_room:
                if (listFragment == null) {
                    listFragment = new ListFragment();
                    transaction.add(R.id.contentFrame, listFragment);
                } else {
                    listFragment.onResume();
                    transaction.show(listFragment);
                }
                break;
            case R.id.layout_people:
                if (peopleFragment == null) {
                    peopleFragment = new LeftFragment();
                    transaction.add(R.id.contentFrame, peopleFragment);
                } else {
                    peopleFragment.onResume();
                    transaction.show(peopleFragment);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (listFragment != null) {
            transaction.hide(listFragment);
        }
        if (peopleFragment != null) {
            transaction.hide(peopleFragment);
        }
    }

    private void clearSelectIcon() {
        Drawable drawable2 = ContextCompat.getDrawable(TabMenuActivity.this, R.drawable.btn_menu_me_n);
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());//必须设置图片大小，否则不显示
        btn_me.setCompoundDrawables(null, drawable2, null, null);
        Drawable drawable = ContextCompat.getDrawable(TabMenuActivity.this, R.drawable.btn_menu_home_n);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
        btn_list.setCompoundDrawables(null, drawable, null, null);
        btn_me.setTextColor(ContextCompat.getColor(TabMenuActivity.this, R.color.color_999999));
        btn_list.setTextColor(ContextCompat.getColor(TabMenuActivity.this, R.color.color_999999));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        roomModel = (RoomModel) intent.getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        if (roomModel != null) {
            BasicUserInfoModel loginUser = new BasicUserInfoModel();//登录信息
            loginUser.Userid = userModel.userid;
            loginUser.Token = userModel.token;
            roomModel.setLoginUser(loginUser);
            ChatRoom.enterChatRoom(TabMenuActivity.this, roomModel);
        }
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
        try {
            userModel = CacheDataManager.getInstance().loadUser();
            String str = String.valueOf(SystemMessage.getInstance().getQueryAllpot(BaseKey.NOTIFICATION_ISREAD, userModel.userid).size());
            if (str.equals("0")) {
                SystemMessage.getInstance().clearUnReadTag(TabMenuActivity.this);
            }
            if (systemMessage.haveNewSystemMsg(TabMenuActivity.this)) {
                pot.setVisibility(View.VISIBLE);
            } else {
                pot.setVisibility(View.GONE);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    upApk();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//                            if (isUp == 1) {
//                                SPreferencesTool.getInstance().saveUpLoadApk(TabMenuActivity.this, true, apkVersion, Content, AppURL);
//                            }
                            if (Integer.valueOf(apkVersion) > Integer.valueOf(versionCode)) {
                                UploadApp uploadApp = new UploadApp(Utility.getSDCardDir(TabMenuActivity.this, App.FILEPATH_UPAPK));
                                uploadApp.showUpApk(TabMenuActivity.this, Content, AppURL, isUp);
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

    private boolean markStrategy(Context context) {
        if (sp == null) {
            sp = SPreferencesTool.getInstance();
        }
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        long today = Long.valueOf(sdf.format(dt));
        long recordDay = sp.getLongValue(context, ACCOUNT_TIME_STAMP);
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
                            FrescoBitmapUtils.getImageBitmap(TabMenuActivity.this, url, new FrescoBitmapUtils.BitCallBack() {
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
}
