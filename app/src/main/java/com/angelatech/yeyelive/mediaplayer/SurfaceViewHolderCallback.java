package com.angelatech.yeyelive.mediaplayer;

import android.os.Handler;
import android.view.SurfaceHolder;

import com.will.common.log.DebugLogs;


public class SurfaceViewHolderCallback implements SurfaceHolder.Callback {

    private final String TAG = "SurfaceViewHolderCallback";

    private static int currentPosition;
    private VideoPlayer mVideoPlayer;
    private Handler mHandler = new Handler();

    public SurfaceViewHolderCallback(VideoPlayer videoPlayer) {
        this.mVideoPlayer = videoPlayer;
    }

    // SurfaceHolder被修改的时候回调
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        DebugLogs.e("SurfaceHolder 被销毁 " + currentPosition);
        // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
        currentPosition = mVideoPlayer.getCurrentPosition();
        DebugLogs.e("SurfaceHolder 被销毁重新获得的 " + currentPosition);
        mVideoPlayer.stop();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        DebugLogs.e("SurfaceHolder 被创建 " + currentPosition);
//        mVideoPlayer.setSurfaceHolder(holder);
        if (currentPosition > 0) {
            // 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
            if(mVideoPlayer.getStatus() != VideoPlayer.STATUS_COMPLETE){
                if(mVideoPlayer.getStatus() == VideoPlayer.STATUS_PAUSE){
                    mVideoPlayer.play();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mVideoPlayer.pause();
                        }
                    },100);
                }
                else{
                    mVideoPlayer.play(currentPosition);
                }
            }
            currentPosition = 0;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        mVideoPlayer.setSurfaceHolder(holder);
        DebugLogs.e("SurfaceHolder 大小被改变 " + currentPosition);
    }

    public static void clearCurrentPosition(){
        currentPosition = 0;
    }
}

