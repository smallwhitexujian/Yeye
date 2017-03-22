package com.angelatech.yeyelive1.mediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.will.common.log.DebugLogs;

/**
 * Author: jjfly
 * Date: 2016-06-07 15:53
 * FIXME:
 * DESC: 基于mediaplay 播放视频
 */
public class VideoPlayer {


    public static final int MSG_PLAYER_ONPREPARED = 1;
    public static final int MSG_PLAYER_ONCOMPLETIION = 2;
    public static final int MSG_PLAYER_ONSTARTPLAY = 3;
    public static final int MSG_PLAYER_ONPLAYING = 4;
    public static final int MSG_PLAYER_ONERROR = 5;


    public static final int MSG_PLAYER_START = 6;
    public static final int MSG_PLAYER_PAUSE = 7;
    public static final int MSG_PLAYER_STOP = 8;
    public static final int MSG_PLAYER_REPLAY = 9;


    public static final int MSG_PLAYER_BUFFER_START = 10;
    public static final int MSG_PLAYER_BUFFER_END = 11;


    /**
     * status
     */

    public static final int STATUS_INIT = -1;
    public static final int STATUS_PREPARE = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_PAUSE = 2;
    public static final int STATUS_STOP = 3;
    public static final int STATUS_COMPLETE = 4;


    private final String TAG = "VideoPlayer";

    private MediaPlayer mMediaPlayer;
    //    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private Handler mHandler;
    private String mPath;
    private boolean isPlaying;

    private int status = STATUS_INIT;


    public VideoPlayer(SurfaceView surfaceView, Handler handler, String path) {
        this.mSurfaceView = surfaceView;
        this.mHandler = handler;
        this.mPath = path;
    }


    public VideoPlayer(Handler handler, String path) {
        this.mHandler = handler;
        this.mPath = path;
    }

    public void play(final int msec) {
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
            mMediaPlayer.setDataSource(mPath);
            // 设置显示视频的SurfaceHolder
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            mMediaPlayer.prepare();
            mHandler.obtainMessage(MSG_PLAYER_START).sendToTarget();
            mMediaPlayer.start();
            // 按照初始位置播放
            mMediaPlayer.seekTo(msec);
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                }
            });

            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    DebugLogs.e("==========" + what);
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            DebugLogs.e("==========开始缓存");
                            mHandler.obtainMessage(MSG_PLAYER_BUFFER_START).sendToTarget();
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            DebugLogs.e("==========缓存结束");
                            mHandler.obtainMessage(MSG_PLAYER_BUFFER_END).sendToTarget();
                            break;
                    }
                    return false;
                }
            });

            status = STATUS_PLAYING;
            new Thread() {
                @Override
                public void run() {
                    try {
                        isPlaying = true;
                        while (isPlaying) {
                            int current = mMediaPlayer.getCurrentPosition();
                            mHandler.obtainMessage(MSG_PLAYER_ONPLAYING, current).sendToTarget();
                            sleep(500);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

//
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    status = STATUS_COMPLETE;
                    mHandler.obtainMessage(MSG_PLAYER_ONCOMPLETIION).sendToTarget();
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
                    mHandler.obtainMessage(MSG_PLAYER_ONERROR).sendToTarget();
                    isPlaying = false;
                    status = STATUS_INIT;
                    return false;
                }
            });
        } catch (Exception e) {
            status = STATUS_INIT;
            e.printStackTrace();
        }
    }

    public void prepare() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
            mMediaPlayer.setDataSource(mPath);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {

                }
            });

            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    DebugLogs.e("==========" + what);
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            DebugLogs.e("==========开始缓存");
                            mHandler.obtainMessage(MSG_PLAYER_BUFFER_START).sendToTarget();
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            DebugLogs.e("==========缓存结束");
                            mHandler.obtainMessage(MSG_PLAYER_BUFFER_END).sendToTarget();
                            break;
                    }
                    return false;
                }
            });

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 按照初始位置播放
                    status = STATUS_PREPARE;
                    // 发送handler 此时设置进度条的最大进度为视频流的最大播放时长
                    mHandler.obtainMessage(MSG_PLAYER_ONPREPARED).sendToTarget();
                    // 开始线程，更新进度条的刻度
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    status = STATUS_COMPLETE;
                    mHandler.obtainMessage(MSG_PLAYER_ONCOMPLETIION).sendToTarget();
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
//                    prepare();
                    mHandler.obtainMessage(MSG_PLAYER_ONERROR).sendToTarget();
                    isPlaying = false;
                    status = STATUS_INIT;
                    return false;
                }
            });
        } catch (Exception e) {
            status = STATUS_INIT;
            e.printStackTrace();
        }
    }

    public void rplay(final int msec) {
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
            mMediaPlayer.setDataSource(mPath);
            // 设置显示视频的SurfaceHolder
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            mMediaPlayer.prepare();
            mHandler.obtainMessage(MSG_PLAYER_START).sendToTarget();
            mMediaPlayer.start();
            // 按照初始位置播放
            mMediaPlayer.seekTo(msec);
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                }
            });

            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    DebugLogs.e("==========" + what);
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            DebugLogs.e("==========开始缓存");
                            mHandler.obtainMessage(MSG_PLAYER_BUFFER_START).sendToTarget();
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            DebugLogs.e("==========缓存结束");
                            mHandler.obtainMessage(MSG_PLAYER_BUFFER_END).sendToTarget();
                            break;
                    }
                    return false;
                }
            });

            status = STATUS_PLAYING;
            new Thread() {
                @Override
                public void run() {
                    try {
                        isPlaying = true;
                        while (isPlaying) {
                            int current = mMediaPlayer.getCurrentPosition();
                            mHandler.obtainMessage(MSG_PLAYER_ONPLAYING, current).sendToTarget();
                            sleep(500);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    status = STATUS_COMPLETE;
                    mHandler.obtainMessage(MSG_PLAYER_ONCOMPLETIION).sendToTarget();
                }
            });
//
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
//                    play(0);
                    mHandler.obtainMessage(MSG_PLAYER_ONERROR).sendToTarget();
                    isPlaying = false;
                    status = STATUS_INIT;
                    return false;
                }
            });
        } catch (Exception e) {
            status = STATUS_INIT;
            e.printStackTrace();
        }
    }


    public void play() {
        try {
            Log.i(TAG, "装载完成");
            // 设置显示视频的SurfaceHolder
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            mMediaPlayer.start();
            // 按照初始位置播放
            status = STATUS_PLAYING;
            // 发送handler 此时设置进度条的最大进度为视频流的最大播放时长
            mHandler.obtainMessage(MSG_PLAYER_START).sendToTarget();
            // 开始线程，更新进度条的刻度
            new Thread() {
                @Override
                public void run() {
                    try {
                        isPlaying = true;
                        while (isPlaying) {
                            int current = mMediaPlayer.getCurrentPosition();
                            mHandler.obtainMessage(MSG_PLAYER_ONPLAYING, current).sendToTarget();
                            sleep(500);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(SurfaceHolder surfaceHolder) {
        try {
            Log.i(TAG, "装载完成");
            // 设置显示视频的SurfaceHolder
            mMediaPlayer.setDisplay(surfaceHolder);
            mMediaPlayer.start();
            // 按照初始位置播放
            status = STATUS_PLAYING;
            // 发送handler 此时设置进度条的最大进度为视频流的最大播放时长
            mHandler.obtainMessage(MSG_PLAYER_START).sendToTarget();
            // 开始线程，更新进度条的刻度
            new Thread() {
                @Override
                public void run() {
                    try {
                        isPlaying = true;
                        while (isPlaying) {
                            int current = mMediaPlayer.getCurrentPosition();
                            mHandler.obtainMessage(MSG_PLAYER_ONPLAYING, current).sendToTarget();
                            sleep(500);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void pause() {
        if (status == STATUS_PAUSE) {
            mMediaPlayer.start();
            status = STATUS_PLAYING;
            mHandler.obtainMessage(MSG_PLAYER_PAUSE, status).sendToTarget();
            return;
        }
        if (status == STATUS_PLAYING) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                status = STATUS_PAUSE;
                mHandler.obtainMessage(MSG_PLAYER_PAUSE, status).sendToTarget();
            }
            return;
        }

    }

    public void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            isPlaying = false;
            status = STATUS_STOP;
            mMediaPlayer = null;
            mHandler.obtainMessage(MSG_PLAYER_STOP).sendToTarget();
        }
    }

    public void replay() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(0);
            mHandler.obtainMessage(MSG_PLAYER_REPLAY).sendToTarget();
            return;
        }
        isPlaying = false;
        rplay(0);
    }

    public void seekTo(int progress) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            // 设置当前播放的位置
            mMediaPlayer.seekTo(progress);
        }
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            try {
                return mMediaPlayer.getCurrentPosition();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            try {
                return mMediaPlayer.getDuration();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

//    public void setSurfaceHolder(SurfaceHolder holder) {
//        this.mSurfaceHolder = holder;
//    }


    public int[] getVideoSize() {
        if (mMediaPlayer != null) {
            int[] videoSizeAry = new int[2];
            videoSizeAry[0] = mMediaPlayer.getVideoWidth();
            videoSizeAry[1] = mMediaPlayer.getVideoHeight();
            return videoSizeAry;
        }
        return null;
    }

    public void destroy() {
        isPlaying = false;
        SurfaceViewHolderCallback.clearCurrentPosition();
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getStatus() {

        return status;
    }

}