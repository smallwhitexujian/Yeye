package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.GlobalDef;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.TransactionValues;
import com.angelatech.yeyelive.activity.Qiniupush.PLVideoTextureUtils;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.VideoModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoDrawee;

import org.json.JSONException;
import org.json.JSONObject;

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
 * 作者: Created by: xujian on Date: 16/9/28.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */

public class PlayPLActivity extends BaseActivity implements PLVideoTextureUtils.PLVideoCallBack
        , View.OnClickListener {
    private final int MSG_SET_FLLOW = 211221;
    private final int MSG_REPORT_SUCCESS = 1200;
    private final int MSG_REPORT_ERROR = 1201;
    private final int MSG_HIDE_PLAYER_CTL = 1202;
    private PLVideoTextureUtils plUtils;
    private VideoModel videoModel;
    private BasicUserInfoDBModel userModel;
    private String path;
    private FrescoDrawee default_img;
    private SeekBar player_seekBar;
    private ImageView player_play_btn, btn_Follow, btn_share, backBtn, video_loading;
    private Button player_replay_btn, btn_back;
    private LinearLayout player_ctl_layout;
    private RelativeLayout ly_playfinish;
    private TextView player_total_time, player_current_time, tv_report, player_split_line;
    private PLVideoTextureView plVideoTextureView;

    private int isFollow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            videoModel = (VideoModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            if (videoModel == null) {
                finish();
                return;
            }
            path = videoModel.playaddress;
            default_img.setImageURI(videoModel.barcoverurl);
            if (!videoModel.userid.equals(userModel.userid)) {
                UserIsFollow();
            }
        }
        // 为进度条添加进度更改事件
        plUtils = new PLVideoTextureUtils();
        plUtils.init(this, plVideoTextureView, PLVideoTextureUtils.REMEDIACODEC, PLVideoTextureUtils.LIVESTREAMING, path, null);
        plUtils.setCallBack(this);
        plUtils.setSeekBar(player_seekBar);
    }

    private void initView() {
        userModel = CacheDataManager.getInstance().loadUser();
        plVideoTextureView = (PLVideoTextureView) findViewById(R.id.plVideoView);
        default_img = (FrescoDrawee) findViewById(R.id.default_img);
        player_seekBar = (SeekBar) findViewById(R.id.player_seekBar);
        player_play_btn = (ImageView) findViewById(R.id.player_play_btn);
        player_replay_btn = (Button) findViewById(R.id.player_replay_btn);

        btn_Follow = (ImageView) findViewById(R.id.btn_Follow);
        btn_share = (ImageView) findViewById(R.id.btn_share);
        btn_back = (Button) findViewById(R.id.btn_back);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        player_ctl_layout = (LinearLayout) findViewById(R.id.player_ctl_layout);

        ly_playfinish = (RelativeLayout) findViewById(R.id.ly_playfinish);
        player_total_time = (TextView) findViewById(R.id.player_total_time);
        player_current_time = (TextView) findViewById(R.id.player_current_time);
        tv_report = (TextView) findViewById(R.id.tv_report);
        player_split_line = (TextView) findViewById(R.id.player_split_line);
        default_img.setVisibility(View.VISIBLE);

        player_play_btn.setOnClickListener(this);
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
                    isFollow = json.getJSONObject("data").getInt("isfollow");
                    uiHandler.obtainMessage(MSG_SET_FLLOW).sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ChatRoom chatRoom = new ChatRoom(this);
        chatRoom.UserIsFollow(CommonUrlConfig.UserIsFollow, userModel.token, userModel.userid, videoModel.userid, callback);
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
                        uiHandler.obtainMessage(MSG_SET_FLLOW).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ChatRoom chatRoom = new ChatRoom(PlayPLActivity.this);
        chatRoom.UserFollow(CommonUrlConfig.UserFollow, userModel.token, userModel.userid,
                videoModel.userid, isFollow, callback);
    }

    @Override
    public void doHandler(Message msg) {
        super.doHandler(msg);
        switch (msg.what) {
            case MSG_SET_FLLOW:
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
                        btn_Follow.setVisibility(View.GONE);
                        break;
                }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        default_img.setVisibility(View.GONE);
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        default_img.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTimeOut() {

    }

    @Override
    public void setCurrentTime(String CurrentTime, String endTime) {
        player_total_time.setText(endTime);
        player_split_line.setVisibility(View.VISIBLE);
        player_current_time.setText(CurrentTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        plUtils.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        plUtils.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        plUtils.onResume();
    }
}
