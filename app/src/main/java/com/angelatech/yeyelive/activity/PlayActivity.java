package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
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
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.activity.function.ChatRoom;
import com.angelatech.yeyelive.activity.function.UserControl;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.handler.CommonHandler;
import com.angelatech.yeyelive.mediaplayer.SurfaceViewHolderCallback;
import com.angelatech.yeyelive.mediaplayer.VideoPlayer;
import com.angelatech.yeyelive.mediaplayer.util.PlayerUtil;
import com.angelatech.yeyelive.model.VideoModel;
import com.angelatech.yeyelive.thirdShare.FbShare;
import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.thirdShare.SinaShare;
import com.angelatech.yeyelive.thirdShare.ThirdShareDialog;
import com.angelatech.yeyelive.thirdShare.WxShare;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.ScreenUtils;
import com.angelatech.yeyelive.view.CommDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by:      sandy
 * Version          ${version}
 * Date:            16/6/15
 * Description(描述):
 * Modification  History(历史修改): 播放器页面
 * Date              Author          Version
 * ---------------------------------------------------------
 */
public class PlayActivity extends BaseActivity {
    private final int MSG_SET_FLLOW = 211221;
    private final int MSG_REPORT_SUCCESS = 1200;
    private final int MSG_REPORT_ERROR = 1201;
    private final int MSG_HIDE_PLAYER_CTL = 1202;
    private SurfaceView player_surfaceView;
    private Button player_replay_btn, btn_back;
    private LinearLayout player_ctl_layout;
    private RelativeLayout ly_playfinish;
    private SeekBar player_seekBar;
    private TextView player_total_time, player_current_time, tv_report, player_split_line;
    private ImageView btn_share, btn_Follow, player_play_btn, backBtn;
    private VideoPlayer mVideoPlayer;

    private String path;
    private int isFollow = 0;
    private VideoModel videoModel;
    private BasicUserInfoDBModel userModel;
    private SimpleDraweeView default_img;

    private boolean boolReport = false; //是否举报

    private ImageView video_loading;
    private AnimationDrawable animationDrawable;

    private volatile int time = 5000;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (time == 5000) {
                //去隐藏
                uiHandler.obtainMessage(MSG_HIDE_PLAYER_CTL).sendToTarget();
            }
            time = 5000;
            uiHandler.postDelayed(this, 5000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        setView();
        uiHandler.postDelayed(runnable, 5000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        scaleVideo(getResources().getConfiguration().orientation);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        time = 0;
        if (mVideoPlayer != null && mVideoPlayer.getStatus() == VideoPlayer.STATUS_COMPLETE) {
            player_seekBar.setVisibility(View.GONE);
            player_ctl_layout.setVisibility(View.GONE);
        } else {
            player_seekBar.setVisibility(View.VISIBLE);
            player_ctl_layout.setVisibility(View.VISIBLE);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        default_img = (SimpleDraweeView) findViewById(R.id.default_img);
        player_seekBar = (SeekBar) findViewById(R.id.player_seekBar);
        player_surfaceView = (SurfaceView) findViewById(R.id.player_surfaceView);
        player_play_btn = (ImageView) findViewById(R.id.player_play_btn);
        player_replay_btn = (Button) findViewById(R.id.player_replay_btn);

        btn_Follow = (ImageView) findViewById(R.id.btn_Follow);
        btn_share = (ImageView) findViewById(R.id.btn_share);
        btn_back = (Button) findViewById(R.id.btn_back);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        player_ctl_layout = (LinearLayout) findViewById(R.id.player_ctl_layout);

        video_loading = (ImageView) findViewById(R.id.video_loading);
        animationDrawable = (AnimationDrawable) video_loading.getDrawable();

        ly_playfinish = (RelativeLayout) findViewById(R.id.ly_playfinish);
        player_total_time = (TextView) findViewById(R.id.player_total_time);
        player_current_time = (TextView) findViewById(R.id.player_current_time);
        tv_report = (TextView) findViewById(R.id.tv_report);
        player_split_line = (TextView) findViewById(R.id.player_split_line);
        default_img.setVisibility(View.VISIBLE);
    }

    private void setView() {
        animationDrawable.start();
        userModel = CacheDataManager.getInstance().loadUser();
        player_play_btn.setOnClickListener(click);
        player_replay_btn.setOnClickListener(click);

        btn_Follow.setOnClickListener(click);
        btn_share.setOnClickListener(click);
        btn_back.setOnClickListener(click);
        tv_report.setOnClickListener(click);
        backBtn.setOnClickListener(click);
        // 为进度条添加进度更改事件
        player_seekBar.setOnSeekBarChangeListener(change);

        CommonHandler<PlayActivity> mCommonHandler = new CommonHandler<>(this);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            videoModel = (VideoModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            if (videoModel == null) {
                finish();
                return;
            }
            path = videoModel.playaddress;
            default_img.setImageURI(Uri.parse(videoModel.barcoverurl));
            if (!videoModel.userid.equals(userModel.userid)) {
                UserIsFollow();
            }
        }

        mVideoPlayer = new VideoPlayer(player_surfaceView, mCommonHandler, path);
        // 为SurfaceHolder添加回调
        player_surfaceView.getHolder().addCallback(new SurfaceViewHolderCallback(mVideoPlayer));
        // 4.0版本之下需要设置的属性
        // 设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到界面
        // player_surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mVideoPlayer.prepare();
    }

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            time = 0;
            if (mVideoPlayer.getStatus() == VideoPlayer.STATUS_INIT) {
                seekBar.setProgress(0);
                return;
            }
            int progress = seekBar.getProgress();
//          // 设置当前播放的位置
//            currentPosition = progress;
//            mVideoPlayer.seekTo(progress);
            mVideoPlayer.play();
            mVideoPlayer.seekTo(progress);
            player_play_btn.setImageResource(R.drawable.btn_playback_stop);
            default_img.setVisibility(View.GONE);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
    };

    private View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.player_play_btn:
                    default_img.setVisibility(View.GONE);
                    if (mVideoPlayer.getStatus() == VideoPlayer.STATUS_PREPARE) {
                        mVideoPlayer.play();
                        ClickToWatch();
                        player_play_btn.setImageResource(R.drawable.btn_playback_stop);
//                        }
                    } else {
                        mVideoPlayer.pause();
                    }
                    break;
                case R.id.player_replay_btn:
                    ly_playfinish.setVisibility(View.GONE);
                    player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                    mVideoPlayer.replay();
                    break;
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.backBtn:
                    ClosePlay();
                    break;
                case R.id.btn_Follow:
                    UserFollow();
                    break;
                case R.id.btn_share:
                    //分享组件
                    ThirdShareDialog.Builder builder = new ThirdShareDialog.Builder(PlayActivity.this, getSupportFragmentManager(), null);
                    builder.setShareContent(videoModel.introduce, getString(R.string.shareDescription),
                            CommonUrlConfig.facebookURL + "?uid=" + videoModel.userid + "&videoid=" + videoModel.videoid,
                            videoModel.barcoverurl);
                    builder.create().show();
                    break;
                case R.id.tv_report:
                    if (!boolReport) {
                        CommDialog dialog = new CommDialog();
                        dialog.CommDialog(PlayActivity.this, getString(R.string.report_prompt), true, new CommDialog.Callback() {
                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onOK() {
                                UserControl userControl = new UserControl(PlayActivity.this);
                                userControl.report(userModel.userid, userModel.token, String.valueOf(UserControl.SOURCE_REPORT_VIDEO),
                                        videoModel.videoid, "", reportBack);
                            }
                        });

                    } else {
                        ToastUtils.showToast(PlayActivity.this, getString(R.string.report_repeat));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 退出 关闭 播放
     */
    private void ClosePlay() {
        uiHandler.removeCallbacksAndMessages(null);
        mVideoPlayer.stop();
        mVideoPlayer.destroy();
        finish();
    }

    /**
     * 举报 回调函数
     */
    private HttpBusinessCallback reportBack = new HttpBusinessCallback() {
        @Override
        public void onSuccess(String response) {
            uiHandler.sendEmptyMessage(MSG_REPORT_SUCCESS);
        }

        @Override
        public void onFailure(Map<String, ?> errorMap) {
            uiHandler.obtainMessage(MSG_REPORT_ERROR, errorMap.get("code")).sendToTarget();
        }
    };

    //接受回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 分享监听
     */
    public ShareListener listener = new ShareListener() {
        @Override
        public void callBackSuccess(int shareType) {
            int pType = 0;
            switch (shareType) {
                case FbShare.SHARE_TYPE_FACEBOOK:
                    ToastUtils.showToast(PlayActivity.this, getString(R.string.success));
                    break;
                case WxShare.SHARE_TYPE_WX:

                    break;
                case SinaShare.SHARE_TYPE_SINA:

                    break;

            }

        }

        @Override
        public void callbackError(int shareType) {
        }

        @Override
        public void callbackCancel(int shareType) {
        }
    };

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
        ChatRoom chatRoom = new ChatRoom(PlayActivity.this);
        chatRoom.UserFollow(CommonUrlConfig.UserFollow, userModel.token, userModel.userid,
                videoModel.userid, isFollow, callback);
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

    //录播计数
    private void ClickToWatch() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                DebugLogs.e("response=========err==");
            }

            @Override
            public void onSuccess(String response) {
                DebugLogs.e("response===========" + response);
            }
        };
        ChatRoom chatRoom = new ChatRoom(this);
        chatRoom.ClickToWatch(CommonUrlConfig.ClickToWatch, userModel, videoModel.videoid, callback);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case VideoPlayer.MSG_PLAYER_START:
                player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                break;
            case VideoPlayer.MSG_PLAYER_STOP:
                player_play_btn.setImageResource(R.drawable.btn_playback_play);
                break;
            case VideoPlayer.MSG_PLAYER_PAUSE:
                int arg1 = (Integer) msg.obj;
                if (arg1 == VideoPlayer.STATUS_PAUSE) {
                    player_play_btn.setImageResource(R.drawable.btn_playback_play);
                } else {
                    player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                }
                break;
            case VideoPlayer.MSG_PLAYER_REPLAY:
                ToastUtils.showToast(this, getString(R.string.restart_play));
                player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                break;
            case VideoPlayer.MSG_PLAYER_ONPREPARED:
                //加载完成
//                LoadingDialog.cancelLoadingDialog();
                video_loading.setVisibility(View.GONE);
                animationDrawable.stop();
                scaleVideo(getResources().getConfiguration().orientation);
                //setVideoSize();
                int duration = mVideoPlayer.getDuration();
                player_seekBar.setMax(duration);
                player_total_time.setText(PlayerUtil.showTime(duration));
                player_split_line.setVisibility(View.VISIBLE);
                player_current_time.setText(PlayerUtil.showTime(0));

                mVideoPlayer.play();

                ClickToWatch();
                player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                default_img.setVisibility(View.GONE);
                break;
            case VideoPlayer.MSG_PLAYER_ONPLAYING:
                player_seekBar.setProgress(mVideoPlayer.getCurrentPosition());
                player_current_time.setText(PlayerUtil.showTime(mVideoPlayer.getCurrentPosition()));
                break;
            case VideoPlayer.MSG_PLAYER_ONCOMPLETIION:
                ly_playfinish.setVisibility(View.VISIBLE);
                player_play_btn.setEnabled(true);
                player_play_btn.setImageResource(R.drawable.btn_playback_play);
                break;
            case VideoPlayer.MSG_PLAYER_ONERROR:
                break;
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
//                        Animation rotateAnimation = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.free_fall_down);
//                        btn_Follow.startAnimation(rotateAnimation);
                        btn_Follow.setVisibility(View.GONE);
                        break;
                }
                break;
            case MSG_REPORT_SUCCESS:
                boolReport = true;
                ToastUtils.showToast(this, getString(R.string.userinfo_dialog_report_suc));
                break;
            case MSG_REPORT_ERROR:
                ToastUtils.showToast(this, getString(R.string.userinfo_dialog_report_faild));
                break;
            case VideoPlayer.MSG_PLAYER_BUFFER_START:
                //ToastUtils.showToast(PlayActivity.this,"开始缓存");
                video_loading.setVisibility(View.VISIBLE);
                animationDrawable.start();

                break;
            case VideoPlayer.MSG_PLAYER_BUFFER_END:
                video_loading.setVisibility(View.GONE);
                animationDrawable.stop();
                //ToastUtils.showToast(PlayActivity.this,"结束缓存");
                break;
            case MSG_HIDE_PLAYER_CTL:
                player_seekBar.setVisibility(View.GONE);
                player_ctl_layout.setVisibility(View.GONE);
                break;
        }
    }

    private void scaleVideo(int orientation) {
        int[] videoSizeAry = mVideoPlayer.getVideoSize();
        if (videoSizeAry == null || videoSizeAry[0] == 0 || videoSizeAry[1] == 0) {
            return;
        }
        int[] calVideoSizeAry = PlayerUtil.scaleVideoSize(orientation, videoSizeAry[0], videoSizeAry[1], ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this));
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) player_surfaceView.getLayoutParams();
        if (lp == null) {
            lp = new RelativeLayout.LayoutParams(calVideoSizeAry[0], calVideoSizeAry[1]);
        }
        lp.width = calVideoSizeAry[0];
        lp.height = calVideoSizeAry[1];
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        player_surfaceView.setLayoutParams(lp);
    }
}
