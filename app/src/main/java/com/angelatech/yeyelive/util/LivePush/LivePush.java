package com.angelatech.yeyelive.util.LivePush;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.angelatech.yeyelive.Contants;
import com.angelatech.yeyelive.application.App;
import com.duanqu.qupai.live.CreateLiveListener;
import com.duanqu.qupai.live.LiveService;
import com.duanqu.qupai.sdk.qupailiverecord.event.DQLiveEvent;
import com.duanqu.qupai.sdk.qupailiverecord.event.DQLiveEventResponse;
import com.duanqu.qupai.sdk.qupailiverecord.event.DQLiveEventSubscriber;
import com.duanqu.qupai.sdk.qupailiverecord.live.DQLiveMediaFormat;
import com.duanqu.qupai.sdk.qupailiverecord.live.DQLiveMediaRecorder;
import com.duanqu.qupai.sdk.qupailiverecord.live.DQLiveMediaRecorderFactory;
import com.duanqu.qupai.sdk.qupailiverecord.live.DQLiveStatusCode;
import com.duanqu.qupai.sdk.qupailiverecord.live.OnLiveRecordErrorListener;
import com.duanqu.qupai.sdk.qupailiverecord.live.OnNetworkStatusListener;
import com.duanqu.qupai.sdk.qupailiverecord.live.OnRecordStatusListener;
import com.duanqu.qupai.sdk.qupailiverecord.model.DQLiveWatermark;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;

import java.util.HashMap;
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
 * 作者: Created by: xujian on Date: 16/8/22.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.util.LivePush
 */

public class LivePush {
    private Context mContext;
    //默认设置
    private boolean screenOrientation = false;          // 横竖屏
    private int cameraFrontFacing = 0;                  // 前后摄像头
    private int videoResolution = DQLiveMediaFormat.OUTPUT_RESOLUTION_360P;//分辨率
    private DQLiveWatermark mWatermark = null;                 // 水印
    private DQLiveMediaRecorder mMediaRecorder;
    private Surface mPreviewSurface;
    private String liveUrl;                             //播放地址
    //控件
    private Map<String, Object> mConfigure = new HashMap<>();
    private boolean isRecording = false;
    private int mPreviewWidth = 0;
    private int mPreviewHeight = 0;
    public boolean FLAG_BEAUTY_ON = true;              //是否开启美颜  默认关闭
    public boolean FLAG_FLASH_MODE_ON = false;          //是否开启闪光灯 默认关闭

    public LivePush() {
        initConfig();
    }

    public void init(Context context, SurfaceView camera_surface) {
        this.mContext = context;

        //创建推流器准备推流
        mMediaRecorder = DQLiveMediaRecorderFactory.createMediaRecorder();
        mMediaRecorder.init(mContext);
        //创建预览界面
        camera_surface.getHolder().addCallback(_CameraSurfaceCallback);
        camera_surface.setOnTouchListener(mOnTouchListener);

        //对焦，缩放
        mDetector = new GestureDetector(camera_surface.getContext(), mGestureDetector);
        mScaleDetector = new ScaleGestureDetector(camera_surface.getContext(), mScaleGestureListener);

        mMediaRecorder.setOnRecordStatusListener(mRecordStatusListener);//设置推流状态回调监听
        mMediaRecorder.setOnNetworkStatusListener(mOnNetworkStatusListener);//设置网络状态监听
        mMediaRecorder.setOnRecordErrorListener(mOnErrorListener);//设置推流错误回调

        mConfigure.put(DQLiveMediaFormat.KEY_CAMERA_FACING, cameraFrontFacing);
        mConfigure.put(DQLiveMediaFormat.KEY_MAX_ZOOM_LEVEL, 3);
        mConfigure.put(DQLiveMediaFormat.KEY_OUTPUT_RESOLUTION, videoResolution);
        mConfigure.put(DQLiveMediaFormat.KEY_MAX_VIDEO_BITRATE, 800000);
        mConfigure.put(DQLiveMediaFormat.KEY_DISPLAY_ROTATION, screenOrientation ? DQLiveMediaFormat.DISPLAY_ROTATION_90 : DQLiveMediaFormat.DISPLAY_ROTATION_0);
        mConfigure.put(DQLiveMediaFormat.KEY_EXPOSURE_COMPENSATION, -1);//曝光度
        if (mWatermark != null) {
            mConfigure.put(DQLiveMediaFormat.KEY_WATERMARK, mWatermark);//水印
        }
    }

    /**
     * 设置水印位置,设置水印方法需要在init之前
     *
     * @param url  水印存放路径
     * @param dx   水印x位置
     * @param dy   水印y位置
     * @param site 水印方位
     */
    public void setWatermark(String url, int dx, int dy, int site) {
        mWatermark = new DQLiveWatermark.Builder()
                .watermarkUrl(url)
                .paddingX(dx)
                .paddingY(dy)
                .site(site)
                .build();
    }


    //开始推流
    public void StartLive(String starturl) {
        if (!starturl.isEmpty()) {
            liveUrl = starturl;
            try {
                mMediaRecorder.startRecord(liveUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRecording = true;
        }
    }

    //开启美颜效果
    public void OpenFace() {
        if (!FLAG_BEAUTY_ON) {//开启美颜
            FLAG_BEAUTY_ON = true;
            mMediaRecorder.addFlag(DQLiveMediaFormat.FLAG_BEAUTY_ON);
        } else {
            FLAG_BEAUTY_ON = false;
            mMediaRecorder.removeFlag(DQLiveMediaFormat.FLAG_BEAUTY_ON);
        }
    }

    //摄像头切换
    public void mCamera() {
        int currFacing = mMediaRecorder.switchCamera();
        if (currFacing == DQLiveMediaFormat.CAMERA_FACING_FRONT) {
            mMediaRecorder.addFlag(DQLiveMediaFormat.FLAG_BEAUTY_ON);
        }
        mConfigure.put(DQLiveMediaFormat.KEY_CAMERA_FACING, currFacing);
    }

    //闪光灯切换
    public void Openlamp() {
        if (!FLAG_FLASH_MODE_ON) {
            FLAG_FLASH_MODE_ON = true;
            mMediaRecorder.addFlag(DQLiveMediaFormat.FLAG_FLASH_MODE_ON);
        } else {
            FLAG_FLASH_MODE_ON = false;
            mMediaRecorder.removeFlag(DQLiveMediaFormat.FLAG_FLASH_MODE_ON);
        }
    }

    /**
     * 直播开始之前的配置 竖屏 后摄像头 分辨率
     * 参数设定完成之后获取直播地址
     */
    private void initConfig() {
        screenOrientation = false;
        cameraFrontFacing = 1;
        videoResolution = DQLiveMediaFormat.OUTPUT_RESOLUTION_360P;
        createLive();//开播地址可以不适用趣拍
    }

    /**
     * 获取流媒体地址
     */
    private void createLive() {
        LiveService.getInstance().createLive(Contants.accessToken, Contants.space, Contants.LIVE_URL);
        LiveService.getInstance().setCreateLiveListener(new CreateLiveListener() {
            @Override
            public void onCreateLiveError(int errorCode, String message) {
                if (App.isDebug) {
                    DebugLogs.d("errorCode:" + errorCode + "message" + message);
                    ToastUtils.showToast(mContext, "errorCode:" + errorCode + "message" + message);
                }
            }

            @Override
            public void onCreateLiveSuccess(String pushUrl, String playUrl) {
                DebugLogs.d("推流地址:" + pushUrl + "播放地址" + playUrl);
//                liveUrl ="rtmp://istream.a8.com/live/121231";
                liveUrl = pushUrl;
            }
        });
    }

    public void onResume() {
        if (mMediaRecorder != null) {
            //开启预览
            if (mPreviewSurface != null) {
                mMediaRecorder.prepare(mConfigure, mPreviewSurface);//预览
                if (isRecording) {
                    mMediaRecorder.startRecord(liveUrl);
                }
            }
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_BITRATE_DOWN, mBitrateDownRes));
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_BITRATE_RAISE, mBitrateUpRes));
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_SUCC, mAudioCaptureSuccRes));
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_DATA_DISCARD, mDataDiscardRes));
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_INIT_DONE, mInitDoneRes));
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_SUCC, mVideoEncoderSuccRes));
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_FAILED, mVideoEncoderFailedRes));
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_VIDEO_ENCODED_FRAMES_FAILED, mVideoEncodeFrameFailedRes));
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_AUDIO_ENCODED_FRAMES_FAILED, mAudioEncodeFrameFailedRes));
            mMediaRecorder.subscribeEvent(new DQLiveEventSubscriber(DQLiveEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_FAILED, mAudioCaptureOpenFailedRes));
        }
    }

    public void onPause() {
        if (mMediaRecorder != null) {
            if (isRecording) {
                mMediaRecorder.stopRecord();
            }
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_BITRATE_DOWN);
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_BITRATE_RAISE);
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_SUCC);
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_DATA_DISCARD);
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_INIT_DONE);
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_SUCC);
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_FAILED);
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_VIDEO_ENCODED_FRAMES_FAILED);
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_AUDIO_ENCODED_FRAMES_FAILED);
            mMediaRecorder.unSubscribeEvent(DQLiveEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_FAILED);
            /**
             * 如果要调用stopRecord和reset()方法，则stopRecord（）必须在reset之前调用，否则将会抛出IllegalStateException
             */
            mMediaRecorder.reset();
        }
    }

    public void onDestroy() {
        try{
            if (mMediaRecorder != null) {
                mMediaRecorder.release();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector.OnGestureListener mGestureDetector = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (mPreviewWidth > 0 && mPreviewHeight > 0) {
                float x = motionEvent.getX() / mPreviewWidth;
                float y = motionEvent.getY() / mPreviewHeight;
                mMediaRecorder.focusing(x, y);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDetector.onTouchEvent(motionEvent);
            mScaleDetector.onTouchEvent(motionEvent);
            return true;
        }
    };

    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mMediaRecorder.setZoom(scaleGestureDetector.getScaleFactor());
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        }
    };


    //采集
    private final SurfaceHolder.Callback _CameraSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            holder.setKeepScreenOn(true);
            mPreviewSurface = holder.getSurface();

            mMediaRecorder.prepare(mConfigure, mPreviewSurface);
            if ((int) mConfigure.get(DQLiveMediaFormat.KEY_CAMERA_FACING) == DQLiveMediaFormat.CAMERA_FACING_FRONT) {
                mMediaRecorder.addFlag(DQLiveMediaFormat.FLAG_BEAUTY_ON);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mMediaRecorder.setPreviewSize(width, height);
            mPreviewWidth = width;
            mPreviewHeight = height;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mPreviewSurface = null;
            mMediaRecorder.stopRecord();
            mMediaRecorder.reset();
        }
    };

    private OnRecordStatusListener mRecordStatusListener = new OnRecordStatusListener() {
        @Override
        public void onDeviceAttach() {
            if(mMediaRecorder !=null){
                mMediaRecorder.addFlag(DQLiveMediaFormat.FLAG_AUTO_FOCUS_ON);
            }
        }

        @Override
        public void onDeviceAttachFailed(int facing) {

        }

        @Override
        public void onSessionAttach() {
            try {
                if (isRecording && !TextUtils.isEmpty(liveUrl)) {
                    mMediaRecorder.startRecord(liveUrl);
                }
                mMediaRecorder.focusing(0.5f, 0.5f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSessionDetach() {

        }

        @Override
        public void onDeviceDetach() {

        }

        @Override
        public void onIllegalOutputResolution() {
            DebugLogs.i("selected illegal output resolution");
        }
    };


    private OnNetworkStatusListener mOnNetworkStatusListener = new OnNetworkStatusListener() {
        @Override
        public void onNetworkBusy() {
            Log.d("network_status", "==== on network busy ====");
        }

        @Override
        public void onNetworkFree() {
            Log.d("network_status", "===== on network free ====");
        }

        @Override
        public void onConnectionStatusChange(int status) {
            switch (status) {
                case DQLiveStatusCode.STATUS_CONNECTION_START:
                    DebugLogs.i("Start live stream connection!");
                    break;
                case DQLiveStatusCode.STATUS_CONNECTION_ESTABLISHED:
                    DebugLogs.i("Live stream connection is established!");
                    break;
                case DQLiveStatusCode.STATUS_CONNECTION_CLOSED:
                    DebugLogs.i("Live stream connection is closed!");
                    if(mMediaRecorder !=null){
                        mMediaRecorder.stopRecord();
                    }
                    break;
            }
        }

        @Override
        public boolean onNetworkReconnect() {
            return true;
        }
    };


    private OnLiveRecordErrorListener mOnErrorListener = new OnLiveRecordErrorListener() {
        @Override
        public void onError(int errorCode) {
            switch (errorCode) {
                case DQLiveStatusCode.ERROR_SERVER_CLOSED_CONNECTION:
                case DQLiveStatusCode.ERORR_OUT_OF_MEMORY:
                case DQLiveStatusCode.ERROR_CONNECTION_TIMEOUT:
                case DQLiveStatusCode.ERROR_BROKEN_PIPE:
                case DQLiveStatusCode.ERROR_ILLEGAL_ARGUMENT:
                case DQLiveStatusCode.ERROR_IO:
                case DQLiveStatusCode.ERROR_NETWORK_UNREACHABLE:
                    DebugLogs.i("Live stream connection error-->" + errorCode);
                    break;
                default:
            }
        }
    };

    private DQLiveEventResponse mBitrateUpRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            Bundle bundle = event.getBundle();
            int preBitrate = bundle.getInt(DQLiveEvent.EventBundleKey.KEY_PRE_BITRATE);
            int currBitrate = bundle.getInt(DQLiveEvent.EventBundleKey.KEY_CURR_BITRATE);
            DebugLogs.i("event->up bitrate, previous bitrate is " + preBitrate +
                    "current bitrate is " + currBitrate);
        }
    };
    private DQLiveEventResponse mBitrateDownRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            Bundle bundle = event.getBundle();
            int preBitrate = bundle.getInt(DQLiveEvent.EventBundleKey.KEY_PRE_BITRATE);
            int currBitrate = bundle.getInt(DQLiveEvent.EventBundleKey.KEY_CURR_BITRATE);
            DebugLogs.i("event->down bitrate, previous bitrate is " + preBitrate +
                    "current bitrate is " + currBitrate);
        }
    };
    private DQLiveEventResponse mAudioCaptureSuccRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            DebugLogs.i("event->audio recorder start success");
        }
    };

    private DQLiveEventResponse mVideoEncoderSuccRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            DebugLogs.i("event->video encoder start success");
        }
    };
    private DQLiveEventResponse mVideoEncoderFailedRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            DebugLogs.i("event->video encoder start failed");
        }
    };
    private DQLiveEventResponse mVideoEncodeFrameFailedRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            DebugLogs.i("event->video encode frame failed");
        }
    };


    private DQLiveEventResponse mInitDoneRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            DebugLogs.i("event->live recorder initialize completely");
        }
    };

    private DQLiveEventResponse mDataDiscardRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            Bundle bundle = event.getBundle();
            int discardFrames = 0;
            if (bundle != null) {
                discardFrames = bundle.getInt(DQLiveEvent.EventBundleKey.KEY_DISCARD_FRAMES);
            }
            DebugLogs.i("event->data discard, the frames num is " + discardFrames);
        }
    };

    private DQLiveEventResponse mAudioCaptureOpenFailedRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            DebugLogs.i("event-> audio capture device open failed");
        }
    };

    private DQLiveEventResponse mAudioEncodeFrameFailedRes = new DQLiveEventResponse() {
        @Override
        public void onEvent(DQLiveEvent event) {
            DebugLogs.i("event-> audio encode frame failed");
        }
    };
}
