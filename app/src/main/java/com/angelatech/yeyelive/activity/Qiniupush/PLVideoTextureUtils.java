package com.angelatech.yeyelive.activity.Qiniupush;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.angelatech.yeyelive.activity.Qiniupush.widget.MediaController;
import com.angelatech.yeyelive.activity.Qiniupush.widget.Utils;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.will.common.log.DebugLogs;

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
 * 作者: Created by: xujian on Date: 16/9/26.
 * 邮箱: xj626361950@163.com
 * 播放工具管理类
 */

public class PLVideoTextureUtils {
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    private static final int SHOW_PROGRESS = 2;
    public static final int LIVESTREAM = 0;//录播
    public static final int LIVESTREAMING = 1;//直播
    public static final int REMEDIACODEC = 0;//软编
    public static final int HWMEDIACODEC = 1;//硬编

    private int mDisplayAspectRatio = PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT; //default
    private int mCodec = 0;//编码方式 1-> hw codec  0 ->disable [recommended]
    private int isLiveStreaming = 0;//设置点播或者录播
    private PLVideoTextureView mVideoView;//播放界面
    private boolean mIsActivityPaused = true;
    private Activity mcontext;
    private String mVideoPath;
    private Toast mToast = null;
    private View LoadingView;
    private int mRotation = 0;
    private PLVideoCallBack callBack;
    private boolean isDebug = true;
    private long isRunTiem = 0;
    private SeekBar mProgress;
    private long mDuration;

    public PLVideoTextureUtils() {

    }

    //初始化控件
    public void init(Activity context, PLVideoTextureView plVideoTextureView, int mediaCodec, int liveStreaming, String videoPath, View loadingView) {
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.mVideoView = plVideoTextureView;
        this.mCodec = mediaCodec;
        this.isLiveStreaming = liveStreaming;
        this.mcontext = context;
        this.mVideoPath = videoPath;
        this.LoadingView = loadingView;
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
        setOptions();
    }

    public void setSeekBar(SeekBar seekBar) {
        mProgress = seekBar;
        mProgress.setOnSeekBarChangeListener(mSeekListener);
    }

    //设置参数
    private void setOptions() {
        AVOptions options = new AVOptions();
        //超时时间设置
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, mCodec);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, isLiveStreaming);
        if (isLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }
        mVideoView.setAVOptions(options);//设置选择
        // You can also use a custom `MediaController` widget
        MediaController mMediaController = new MediaController(mcontext, false, isLiveStreaming == 1);//关联播放器的控制
//        mVideoView.setMediaController(mMediaController);//设置播放器的控制关联
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnSeekCompleteListener(onSeekCompleteListener);
        mVideoView.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mVideoView.setOnPreparedListener(onPreparedListener);
        mVideoView.setOnInfoListener(onInfoListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        if (LoadingView != null) {
            mVideoView.setBufferingIndicator(LoadingView);//设置播放器的加载动画
        }
        //设置预览模式
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        mVideoView.setVideoPath(mVideoPath);//设置播放地址
        mVideoView.start();//开始播放
    }

    public void onPause() {
        mToast = null;
        mVideoView.pause();
        mIsActivityPaused = true;
    }

    public void onResume() {
        mIsActivityPaused = false;
        mVideoView.start();
    }

    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mVideoView.stopPlayback();
    }

    public void onResetStart(String videoPath) {
        mVideoPath = videoPath;
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.start();
    }

    //重连或者开始播放
    public void onClickPlay() {
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.start();
    }
    //暂停
    public void onClickPause() {
        mVideoView.pause();
    }
    //重新播放
    public void onClickResume() {
        mVideoView.start();
    }
    //结束播放
    public void onClickDestroy() {
        mVideoView.stopPlayback();
    }

    //设置屏幕填充模式,
    public void onClickSwitchScreen(int mDisplayAspectRatio) {
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
    }

    public void onClickSwitchScreen() {
        mDisplayAspectRatio = (mDisplayAspectRatio + 1) % 5;
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        switch (mVideoView.getDisplayAspectRatio()) {
            case PLVideoTextureView.ASPECT_RATIO_ORIGIN:
                showToastTips("Origin mode");
                break;
            case PLVideoTextureView.ASPECT_RATIO_FIT_PARENT://宽度填充
                showToastTips("Fit parent !");
                break;
            case PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT://高度填充
                showToastTips("Paved parent !");
                break;
            case PLVideoTextureView.ASPECT_RATIO_16_9:
                showToastTips("16 : 9 !");
                break;
            case PLVideoTextureView.ASPECT_RATIO_4_3:
                showToastTips("4 : 3 !");
                break;
            default:
                break;
        }
    }

    //设置旋转角度
    public void onClickRotate() {
        mRotation = (mRotation + 90) % 360;
        mVideoView.setDisplayOrientation(mRotation);
    }

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            DebugLogs.d("------errorCode------->"+errorCode);
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToastTips("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToastTips("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToastTips("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToastTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToastTips("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToastTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToastTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToastTips("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToastTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToastTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToastTips("unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
            } else {
                if (callBack != null) {
                    callBack.onTimeOut();
                }
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    /**
     * 该对象用于监听播放结束的消息，关于该回调的时机，有如下定义：
     * <p>
     * 如果是播放文件，则是播放到文件结束后产生回调
     * 如果是在线视频，则会在读取到码流的EOF信息后产生回调，回调前会先播放完已缓冲的数据
     * 如果播放过程中产生onError，并且没有处理的话，最后也会回调本接口
     * 如果播放前设置了 setLooping(true)，则播放结束后会自动重新开始，不会回调本接口
     */
    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            if (callBack != null) {
                callBack.onCompletion(plMediaPlayer);
            }
        }
    };

    //该回调用于监听当前播放器已经缓冲的数据量占整个视频时长的百分比，在播放直播流中无效，仅在播放文件和回放时才有效。
    private PLMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int i) {
            isRunTiem = plMediaPlayer.getCurrentPosition();
        }
    };

    //该回调用于监听 seek 完成的消息，当调用的播放器的 seekTo 方法后，SDK 会在 seek 成功后触发该回调。
    private PLMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new PLMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
        }
    };

    private PLMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int i, int i1) {

        }
    };

    //该对象用于监听播放器的 prepare 过程，该过程主要包括：创建资源、建立连接、请求码流等等，
    //当 prepare 完成后，SDK 会回调该对象的 onPrepared 接口，下一步则可以调用播放器的 start() 启动播放。
    private PLMediaPlayer.OnPreparedListener onPreparedListener = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer plMediaPlayer) {
            if (callBack != null) {
                callBack.onPrepared(plMediaPlayer);
            }
            setProgress();
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    //该对象用于监听播放器的状态消息，在播放器启动后，SDK 会在播放器发生状态变化时调用该对象的 onInfo 方法，同步状态信息。
    private PLMediaPlayer.OnInfoListener onInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int i, int i1) {
            return false;
        }
    };

    public void setCallBack(PLVideoCallBack b) {
        this.callBack = b;
    }

    public interface PLVideoCallBack {
        void onPrepared(PLMediaPlayer plMediaPlayer);//开播前的信息

        void onCompletion(PLMediaPlayer plMediaPlayer);//播放结束

        void onTimeOut();//连接超时

        void setCurrentTime(String CurrentTime,String endTime);

        void sendReconnectMessage();
    }


    private void showToastTips(final String tips) {
        if (!isDebug) {
            return;
        }
        mcontext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(mcontext, tips, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    private void sendReconnectMessage() {
//        showToastTips("正在重连...");
        if (LoadingView!=null){
            LoadingView.setVisibility(View.VISIBLE);
        }
        if (callBack!=null){
            callBack.sendReconnectMessage();
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 1000);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_ID_RECONNECTING:
                    if (mIsActivityPaused || !Utils.isLiveStreamingAvailable()) {
                        mcontext.finish();
                        return;
                    }
                    if (!Utils.isNetworkAvailable(mcontext)) {
                        sendReconnectMessage();
                        return;
                    }
                    if (isLiveStreaming == LIVESTREAM) {
                        mVideoView.setVideoPath(mVideoPath);
                        mVideoView.start();
                        mVideoView.seekTo(isRunTiem);
                    } else {
                        mVideoView.setVideoPath(mVideoPath);
                        mVideoView.start();
                    }
                    break;
                case SHOW_PROGRESS:
                    long pos = setProgress();
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    break;
            }
        }
    };

    /**
     * 进度条设置
     */
    private long setProgress() {
        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        if (mProgress != null) {
            mProgress.setMax(1000);
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mVideoView.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }
        mDuration = duration;
        String currentTime = Utils.generateTime(position);
        String endTime = Utils.generateTime(mDuration);
        if (callBack != null){
            callBack.setCurrentTime(currentTime,endTime);
        }
        return position;
    }



    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            long newposition = (mDuration * progress) / 1000;
            mVideoView.seekTo(newposition);
            String time = Utils.generateTime(newposition);
            String endTime = Utils.generateTime(mDuration);
            if (callBack != null){
                callBack.setCurrentTime(time,endTime);
            }
            mVideoView.start();
            mHandler.removeMessages(SHOW_PROGRESS);
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }
    };
}
