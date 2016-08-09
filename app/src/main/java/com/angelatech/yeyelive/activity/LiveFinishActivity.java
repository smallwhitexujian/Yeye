package com.angelatech.yeyelive.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.application.App;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.RoomModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.view.FrescoBitmapUtils;
import com.angelatech.yeyelive.view.GaussAmbiguity;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.drawee.view.SimpleDraweeView;
import com.will.common.string.json.JsonUtil;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;

/**
 * 直播结束页面
 */
public class LiveFinishActivity extends BaseActivity {
    private Button btn_close;
    public RoomModel roomModel;
    private SimpleDraweeView img_head;
    private TextView txt_barname, txt_likenum, txt_live_num, txt_coin, txt_live_time;
    private LinearLayout ly_live;
    private ImageView face;
    private static TextView ticke_num, ticke_title;
    private ChatRoom chatRoom;
    private BasicUserInfoDBModel model;
    private final int MSG_TICKET_SUCCESS = 1;

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
        chatRoom = new ChatRoom(LiveFinishActivity.this);
        model = CacheDataManager.getInstance().loadUser();
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);
        img_head = (SimpleDraweeView) findViewById(R.id.img_head);
        txt_barname = (TextView) findViewById(R.id.txt_barname);
        txt_likenum = (TextView) findViewById(R.id.txt_likenum);
        face = (ImageView) findViewById(R.id.face);
        ly_live = (LinearLayout) findViewById(R.id.ly_live);
        txt_live_num = (TextView) findViewById(R.id.txt_live_num);
        txt_coin = (TextView) findViewById(R.id.txt_coin);
        txt_live_time = (TextView) findViewById(R.id.txt_live_time);
        ticke_num = (TextView) findViewById(R.id.ticke_num);
        ticke_title = (TextView) findViewById(R.id.ticke_title);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            roomModel = (RoomModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            if (roomModel.getUserInfoDBModel().isticket.equals("1")) {
                runOnUiThread(new Thread() {
                    @Override
                    public void run() {
                        payTicketsSet();
                    }
                });

            }
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

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_TICKET_SUCCESS:
                ticke_num.setVisibility(View.VISIBLE);
                ticke_title.setVisibility(View.GONE);
                ticke_num.setText(msg.obj.toString());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                finish();
                break;
        }
    }
}
