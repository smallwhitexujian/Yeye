package com.angelatech.yeyelive.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.view.FrescoBitmapUtils;
import com.angelatech.yeyelive.view.GaussAmbiguity;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;

/**
 * 直播结束页面
 * 直播者 自己 结束界面
 */
public class LiveFinishActivity extends BaseActivity {
    public RoomModel roomModel;
    private ImageView face;
    private TextView ticke_num;
    private ChatRoom chatRoom;
    private BasicUserInfoDBModel model;
    private final int MSG_TICKET_SUCCESS = 1;
    private final int MSG_LOAD_SUC = 2;
    private MainEnter mainEnter;
    private TextView fansNum;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_finish);
        chatRoom = new ChatRoom(this);
        model = CacheDataManager.getInstance().loadUser();
        Button btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);
        SimpleDraweeView img_head = (SimpleDraweeView) findViewById(R.id.img_head);
        TextView txt_barname = (TextView) findViewById(R.id.txt_barname);
        TextView txt_likenum = (TextView) findViewById(R.id.txt_likenum);
        fansNum = (TextView) findViewById(R.id.fans_num);
        face = (ImageView) findViewById(R.id.face);
        LinearLayout ly_live = (LinearLayout) findViewById(R.id.ly_live);
        final LinearLayout ticke = (LinearLayout) findViewById(R.id.ticke);
        final View line2 = (View) findViewById(R.id.line2);
        final View line1 = (View) findViewById(R.id.line1);
        TextView txt_live_num = (TextView) findViewById(R.id.txt_live_num);
        TextView txt_coin = (TextView) findViewById(R.id.txt_coin);
        TextView txt_live_time = (TextView) findViewById(R.id.txt_live_time);
        ticke_num = (TextView) findViewById(R.id.ticke_num);
        mainEnter = new MainEnter(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            roomModel = (RoomModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    if (roomModel.getUserInfoDBModel().isticket.equals("1")) {
                        payTicketsSet();
                        ticke.setVisibility(View.VISIBLE);
                        line1.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.VISIBLE);
                    }else{
                        ticke.setVisibility(View.GONE);
                        line1.setVisibility(View.GONE);
                        line2.setVisibility(View.GONE);
                    }
                    load();
                }
            });
            img_head.setImageURI(Uri.parse(roomModel.getUserInfoDBModel().headurl));
            txt_barname.setText(roomModel.getUserInfoDBModel().nickname);
            txt_likenum.setText(String.valueOf(roomModel.getLikenum()));
            if (roomModel.getRoomType().equals(App.LIVE_HOST)) {
                ly_live.setVisibility(View.VISIBLE);
                txt_coin.setText(String.valueOf(roomModel.getLivecoin()));
                txt_live_num.setText(String.valueOf(roomModel.getLivenum()));
                txt_live_time.setText(String.format(getString(R.string.txt_live_time), roomModel.getLivetime()));
            } else {
                ly_live.setVisibility(View.INVISIBLE);
                FrescoBitmapUtils.getImageBitmap(LiveFinishActivity.this, roomModel.getUserInfoDBModel().headurl, new FrescoBitmapUtils.BitCallBack() {
                    @Override
                    public void onNewResultImpl(Bitmap bitmap) {
                        final Drawable drawable = GaussAmbiguity.BlurImages(bitmap, LiveFinishActivity.this);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                face.setImageDrawable(drawable);
                                face.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                        });
                    }
                });
            }
        }
    }

    private void payTicketsSet() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(final String response) {
                Map map = JsonUtil.fromJson(response, Map.class);
                if (map != null) {
                    if (HttpFunction.isSuc(map.get("code").toString())) {
                        uiHandler.obtainMessage(MSG_TICKET_SUCCESS, map.get("data")).sendToTarget();
                    } else {
                        onBusinessFaild(map.get("code").toString());
                    }
                }
            }
        };
        chatRoom.payTicketsSet(model.userid, model.token, callback);
    }

    private void load() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                LoadingDialog.cancelLoadingDialog();
                CommonListResult<BasicUserInfoDBModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<BasicUserInfoDBModel>>() {
                }.getType());
                if (datas != null && HttpFunction.isSuc(datas.code)) {
                    BasicUserInfoDBModel basicUserInfoDBModel = datas.data.get(0);
                    uiHandler.obtainMessage(MSG_LOAD_SUC, basicUserInfoDBModel).sendToTarget();
                }
            }
        };
        mainEnter.loadUserInfo(CommonUrlConfig.UserInformation, model.userid, model.userid, model.token, callback);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_TICKET_SUCCESS:
                ticke_num.setText(msg.obj.toString());
                break;
            case MSG_LOAD_SUC:
                BasicUserInfoDBModel basicUserInfoDBModel = (BasicUserInfoDBModel) msg.obj;
                int fans_num = Integer.valueOf(basicUserInfoDBModel.fansNum) - Integer.valueOf(model.fansNum);
                fansNum.setText(String.valueOf(fans_num));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                ChatRoom.closeChatRoom();
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ChatRoom.closeChatRoom();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
