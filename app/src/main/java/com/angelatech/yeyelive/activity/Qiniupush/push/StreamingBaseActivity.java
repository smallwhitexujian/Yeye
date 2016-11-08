package com.angelatech.yeyelive.activity.Qiniupush.push;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.ChatRoomActivity;
import com.angelatech.yeyelive.activity.Qiniupush.push.gles.FBO;
import com.angelatech.yeyelive.activity.Qiniupush.push.ui.RotateLayout;
import com.angelatech.yeyelive.activity.base.BaseActivity;
import com.angelatech.yeyelive.application.App;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting.CAMERA_FACING_ID;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingPreviewCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.SurfaceTextureCallback;
import com.will.common.log.DebugLogs;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by jerikc on 15/7/6.
 * 七牛推流工具类
 */
public class StreamingBaseActivity extends BaseActivity implements
        StreamStatusCallback,
        StreamingPreviewCallback,
        SurfaceTextureCallback,
        AudioSourceCallback,
        CameraPreviewFrameView.Listener,
        StreamingSessionListener,
        StreamingStateChangedListener {

    private static final String TAG = "StreamingBaseActivity";
    private static final int ZOOM_MINIMUM_WAIT_MILLIS = 33; //ms
    private Context mContext;
    private RotateLayout mRotateLayout;

    private boolean mShutterButtonPressed = false;            //判断是否停止推流了。
    private boolean mIsNeedMute = false;
    private boolean mIsNeedFB = false;
    private boolean isEncOrientationPort = true;

    private static final int MSG_START_STREAMING = 0;
    private static final int MSG_STOP_STREAMING = 1;
    private static final int MSG_SET_ZOOM = 2;
    private static final int MSG_MUTE = 3;
    private static final int MSG_FB = 4;

    protected MediaStreamingManager mMediaStreamingManager;
    protected CameraStreamingSetting mCameraStreamingSetting;
    protected MicrophoneStreamingSetting mMicrophoneStreamingSetting;
    protected StreamingProfile mProfile;
    private boolean mOrientationChanged = false;
    private boolean mIsReady = false;       //判断是否准备好了。

    private int mCurrentZoom = 0;
    private int mMaxZoom = 0;
    private FBO mFBO = new FBO();
    public Screenshooter mScreenshooter = new Screenshooter();
    private Switcher mSwitcher = new Switcher();
    private EncodingOrientationSwitcher mEncodingOrientationSwitcher = new EncodingOrientationSwitcher();
    private StreamCallback streamCallback;
    private int mCurrentCamFacingIndex;

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_STREAMING://开始推流操作
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // disable the shutter button before startStreaming
                            boolean res = mMediaStreamingManager.startStreaming();
                            mShutterButtonPressed = true;
                            Log.i(TAG, "res:" + res);
                            if (!res) {
                                mShutterButtonPressed = false;
                            }
                        }
                    }).start();
                    break;
                case MSG_STOP_STREAMING://停止推流操作
                    if (mShutterButtonPressed) {
                        // disable the shutter button before stopStreaming
                        boolean res = mMediaStreamingManager.stopStreaming();
                        if (!res) {
                            mShutterButtonPressed = true;
                        }
                    }
                    break;
                case MSG_SET_ZOOM://设置放缩功能
                    mMediaStreamingManager.setZoomValue(mCurrentZoom);
                    break;
                case MSG_MUTE://设置是否禁音
                    mIsNeedMute = !mIsNeedMute;
                    mMediaStreamingManager.mute(mIsNeedMute);
                    updateMuteButtonText();
                    break;
                case MSG_FB://是否开启美颜功能
                    mIsNeedFB = !mIsNeedFB;
                    mMediaStreamingManager.setVideoFilterType(mIsNeedFB ?
                            CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY
                            : CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_NONE);
                    break;
                default:
                    break;
            }
        }
    };

    public StreamingBaseActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        isEncOrientationPort = true;
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(Config.SCREEN_ORIENTATION);
        if (ChatRoomActivity.roomModel.getRoomType().equals(App.LIVE_PREVIEW)){
            init();
        }
    }

    private void init() {
        mContext = this;
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 96 * 1024);
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(16, 1000 * 700, 48);
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
        mProfile = new StreamingProfile();
        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_MEDIUM1)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM1)
//                .setPreferredVideoEncodingSize(960, 544)
                .setEncodingSizeLevel(Config.ENCODING_LEVEL)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
//                .setAdaptiveBitrateEnable(true)//自动适应码率
//                .setAVProfile(avProfile)
                .setDnsManager(getMyDnsManager())//设置dns加速
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))//设置每隔3秒钟进行回调
//                .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));

        CAMERA_FACING_ID cameraFacingId = chooseCameraFacingId();
        mCurrentCamFacingIndex = cameraFacingId.ordinal();
        mCameraStreamingSetting = new CameraStreamingSetting();
        mCameraStreamingSetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setContinuousFocusModeEnabled(true)//设置自动对焦功能
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)//设置对焦模式:FOCUS_MODE_CONTINUOUS_PICTURE 对焦会比 FOCUS_MODE_CONTINUOUS_VIDEO 更加频繁
                .setResetTouchFocusDelayInMs(3000)//触发手动对焦之后恢复自动对焦功能。
                .setRecordingHint(false)//以此来提升数据源的帧率。 但是在部分机型上会出现卡顿情况
                .setCameraFacingId(cameraFacingId)
                .setBuiltInFaceBeautyEnabled(true)
                .setFrontCameraMirror(true)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)//摄像头采集模式
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);
        mIsNeedFB = true;
        mMicrophoneStreamingSetting = new MicrophoneStreamingSetting();
        mMicrophoneStreamingSetting.setBluetoothSCOEnabled(false);//是否开启蓝牙麦克风
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMediaStreamingManager!=null){
            mMediaStreamingManager.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsReady = false;
        mShutterButtonPressed = false;
        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
        if(mMediaStreamingManager!=null){
            mMediaStreamingManager.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaStreamingManager!=null){
            mMediaStreamingManager.destroy();
        }
    }

    //音频失败处理
    @Override
    public boolean onRecordAudioFailedHandled(int err) {
        mMediaStreamingManager.updateEncodingType(AVCodecType.SW_VIDEO_CODEC);
        mMediaStreamingManager.startStreaming();
        return true;
    }

    //视频失败处理
    @Override
    public boolean onRestartStreamingHandled(int err) {
        return mMediaStreamingManager.startStreaming();
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        Camera.Size size = null;
        if (list != null) {
            for (Camera.Size s : list) {
                if (s.height >= 480) {
                    size = s;
                    break;
                }
            }
        }
        return size;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp X:" + e.getX() + ",Y:" + e.getY());
        if (mIsReady) {
            setFocusAreaIndicator();
            mMediaStreamingManager.doSingleTapUp((int) e.getX(), (int) e.getY());
            return true;
        }
        return false;
    }

    /**
     * 屏幕缩放
     */
    @Override
    public boolean onZoomValueChanged(float factor) {
        if (mIsReady && mMediaStreamingManager.isZoomSupported()) {
            mCurrentZoom = (int) (mMaxZoom * factor);
            mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
            mCurrentZoom = Math.max(0, mCurrentZoom);
            DebugLogs.d("zoom ongoing, scale: " + mCurrentZoom + ",factor:" + factor + ",maxZoom:" + mMaxZoom);
            if (!mHandler.hasMessages(MSG_SET_ZOOM)) {
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ZOOM), ZOOM_MINIMUM_WAIT_MILLIS);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSurfaceCreated() {
        mFBO.initialize(this);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        mFBO.updateSurfaceSize(width, height);
    }

    @Override
    public void onSurfaceDestroyed() {
        mFBO.release();
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight, float[] transformMatrix) {
        return mFBO.drawFrame(texId, texWidth, texHeight);
    }

    @Override
    public void onAudioSourceAvailable(ByteBuffer byteBuffer, int size, long tsInNanoTime, boolean eof) {
    }

    @Override
    public void notifyStreamStatusChanged(final StreamingProfile.StreamStatus streamStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DebugLogs.i("bitrate:" + streamStatus.totalAVBitrate / 1024 + " kbps"
                        + "\naudio:" + streamStatus.audioFps + " fps"
                        + "\nvideo:" + streamStatus.videoFps + " fps");
            }
        });
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        Log.i(TAG, "StreamingState streamingState:" + streamingState + ",extra:" + extra);
        switch (streamingState) {
            case PREPARING://准备阶段,
                break;
            case READY://准备好了
                mIsReady = true;
                mMaxZoom = mMediaStreamingManager.getMaxZoom();
                // start streaming when READY
                startStreaming();//开始推流
                break;
            case CONNECTING://连接
                if (streamCallback != null) {
                    streamCallback.connecting();
                }
                break;
            case STREAMING://开始推流
                break;
            case SHUTDOWN://结束推流
                if (mOrientationChanged) {
                    mOrientationChanged = false;
                    startStreaming();
                }
                break;
            case IOERROR://错误
                DebugLogs.d("------流服务器完全断开------");
                if (streamCallback != null) {
                    streamCallback.ioerror();
                }
                stopStreaming();
                break;
            case UNKNOWN://未知错误
                break;
            case CAMERA_SWITCHED:
                break;
            case TORCH_INFO:
                break;
            case SENDING_BUFFER_EMPTY:
                break;
            case SENDING_BUFFER_FULL:
                break;
            case AUDIO_RECORDING_FAIL:
                break;
            case OPEN_CAMERA_FAIL:
                Log.e(TAG, "Open Camera Fail. id:" + extra);
                break;
            case SENDING_BUFFER_HAS_FEW_ITEMS:
                break;
            case SENDING_BUFFER_HAS_MANY_ITEMS:
                break;
            case DISCONNECTED://网络断开、sendTimeOut 后没有发出数据、网络链接被服务端断开等网络异常后
                if (streamCallback != null) {
                    streamCallback.disconnected();
                }
                DebugLogs.d("------流媒体重连------");
                startStreaming();
                break;
            case INVALID_STREAMING_URL://地址错误
                Log.e(TAG, "Invalid streaming url:" + extra);
                break;
            case UNAUTHORIZED_STREAMING_URL:
                Log.e(TAG, "Unauthorized streaming url:" + extra);
                break;
            case NO_SUPPORTED_PREVIEW_SIZE:
                break;
            case NO_NV21_PREVIEW_FORMAT:
                break;
            case CONNECTED:
                break;
        }
    }

    public void setStreamCallback(StreamCallback callback) {
        this.streamCallback = callback;
    }

    @Override
    public boolean onPreviewFrame(byte[] bytes, int i, int i1, int i2, int i3, long l) {
        return false;
    }

    public interface StreamCallback {
        void disconnected();

        void ioerror();

        void connecting();
    }


    /**
     * 注意在AndroidManifest.xml设置android:configChanges="orientation|screenSize"
     * 设置播放端显示视频是横向还是竖屏
     * 设置 ENCODING_ORIENTATION.PORT 之后，播放端会观看竖屏的画面；
     * 设置 ENCODING_ORIENTATION.LAND 之后，播放端会观看横屏的画面。
     * mProfile.setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT);
     * mCameraStreamingManager.setStreamingProfile(mProfile); // notify CameraStreamingManager that StreamingProfile had been changed.
     */
    private void setmEncodingOrientationSwitcher() {
        mHandler.removeCallbacks(mEncodingOrientationSwitcher);
        mHandler.post(mEncodingOrientationSwitcher);
    }

    /**
     * 设置推流地址,并开始推流
     */
    public void setStartStreaming(final String publish) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mProfile != null) {
                    //推流地址
                    String publishUrl = Config.EXTRA_PUBLISH_URL_PREFIX + publish;
                    Log.i(TAG, "直播推流地址:" + publishUrl);
                    if (publishUrl.startsWith(Config.EXTRA_PUBLISH_URL_PREFIX)) {//暂时只用到这个,
                        try {
                            mProfile.setPublishUrl(publishUrl.substring(Config.EXTRA_PUBLISH_URL_PREFIX.length()));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    } else if (publishUrl.startsWith(Config.EXTRA_PUBLISH_JSON_PREFIX)) {
                        try {
                            JSONObject mJSONObject = new JSONObject(publishUrl.substring(Config.EXTRA_PUBLISH_JSON_PREFIX.length()));
                            StreamingProfile.Stream stream = new StreamingProfile.Stream(mJSONObject);
                            mProfile.setStream(stream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mMediaStreamingManager.setStreamingProfile(mProfile);
                    mMediaStreamingManager.notifyActivityOrientationChanged();
                    mMediaStreamingManager.startStreaming();
                }
            }
        }).start();

    }

    /**
     * 设置美颜强度
     *
     */
    public void setBeauty() {
        CameraStreamingSetting.FaceBeautySetting fbSetting = mCameraStreamingSetting.getFaceBeautySetting();
        fbSetting.beautyLevel = 50 / 100.0f;
        fbSetting.whiten = 50 / 100.0f;
        fbSetting.redden = 50 / 100.0f;
        mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
    }

    /**
     * 设置灯的开关
     * false 是开 true 是关闭
     */
    public void setmTurnLight(final boolean mIsTorchOn) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mIsTorchOn) {
                    mMediaStreamingManager.turnLightOn();
                } else {
                    mMediaStreamingManager.turnLightOff();
                }
            }
        }).start();
    }

    /**
     * 设置截屏
     */
    public void setScreenshooter() {
        mHandler.removeCallbacks(mScreenshooter);
        mHandler.postDelayed(mScreenshooter, 100);
    }

    /**
     * 开始推流操作
     */
    protected void startStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_STREAMING), 50);
    }

    /**
     * 停止推流操作
     */
    protected void stopStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_STOP_STREAMING), 50);
    }

    /**
     * 设置摄像头旋转,
     */
    protected void setCameraSwitch() {
        mHandler.removeCallbacks(mSwitcher);
        mHandler.postDelayed(mSwitcher, 100);
    }

    /**
     * 聚焦功能
     */
    protected void setFocusAreaIndicator() {
        if (mRotateLayout == null) {
            mRotateLayout = (RotateLayout) findViewById(R.id.focus_indicator_rotate_layout);
            mMediaStreamingManager.setFocusAreaIndicator(mRotateLayout, mRotateLayout.findViewById(R.id.focus_indicator));
        }
    }

    /**
     * 设置是否开启美颜,
     */
    public void setOpenFB() {
        if (!mHandler.hasMessages(MSG_FB)) {
            mHandler.sendEmptyMessage(MSG_FB);//是否开启美颜
        }
    }

    /**
     * 设置是否开启声音按钮
     */
    public void updateMuteButtonText() {
        if (!mHandler.hasMessages(MSG_MUTE)) {
            mHandler.sendEmptyMessage(MSG_MUTE);
        }
    }


    //保存截图到本地目录
    private void saveToSDCard(String filename, Bitmap bmp) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
                bmp.recycle();
                bmp = null;
            } finally {
                if (bos != null) bos.close();
            }

            final String info = "Save frame to:" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, info, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * 设置dns解析管理
     */
    private static DnsManager getMyDnsManager() {
        IResolver r0 = new DnspodFree();
        IResolver r1 = AndroidDnsServer.defaultResolver();
        IResolver r2 = null;
        try {
            r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }

    /**
     * 设置相机的模式
     *
     * @return
     */
    private CAMERA_FACING_ID chooseCameraFacingId() {
        if (CameraStreamingSetting.hasCameraFacing(CAMERA_FACING_ID.CAMERA_FACING_3RD)) {
            return CAMERA_FACING_ID.CAMERA_FACING_3RD;
        } else if (CameraStreamingSetting.hasCameraFacing(CAMERA_FACING_ID.CAMERA_FACING_FRONT)) {
            return CAMERA_FACING_ID.CAMERA_FACING_FRONT;
        } else {
            return CAMERA_FACING_ID.CAMERA_FACING_BACK;
        }
    }

    /**
     * 摄像头旋转
     */
    private class Switcher implements Runnable {
        @Override
        public void run() {
            mCurrentCamFacingIndex = (mCurrentCamFacingIndex + 1) % CameraStreamingSetting.getNumberOfCameras();
            CAMERA_FACING_ID facingId;
            if (mCurrentCamFacingIndex == CAMERA_FACING_ID.CAMERA_FACING_BACK.ordinal()) {
                facingId = CAMERA_FACING_ID.CAMERA_FACING_BACK;
            } else if (mCurrentCamFacingIndex == CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal()) {
                facingId = CAMERA_FACING_ID.CAMERA_FACING_FRONT;
            } else {
                facingId = CAMERA_FACING_ID.CAMERA_FACING_3RD;
            }
            Log.i(TAG, "switchCamera:" + facingId);
            mMediaStreamingManager.switchCamera(facingId);
        }
    }

    /**
     * 改变编码方式
     */
    private class EncodingOrientationSwitcher implements Runnable {
        @Override
        public void run() {
            Log.i(TAG, "isEncOrientationPort:" + isEncOrientationPort);
            stopStreaming();
            mOrientationChanged = !mOrientationChanged;
            isEncOrientationPort = !isEncOrientationPort;
            mProfile.setEncodingOrientation(isEncOrientationPort ? StreamingProfile.ENCODING_ORIENTATION.PORT : StreamingProfile.ENCODING_ORIENTATION.LAND);
            mMediaStreamingManager.setStreamingProfile(mProfile);
            setRequestedOrientation(isEncOrientationPort ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mMediaStreamingManager.notifyActivityOrientationChanged();
            Toast.makeText(StreamingBaseActivity.this, Config.HINT_ENCODING_ORIENTATION_CHANGED, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "EncodingOrientationSwitcher -");
        }
    }

    //截取图片
    private class Screenshooter implements Runnable {

        public Bitmap bitmap;

        @Override
        public void run() {
            final String fileName = "PLStreaming_" + System.currentTimeMillis() + ".jpg";
            mMediaStreamingManager.captureFrame(100, 100, new FrameCapturedCallback() {


                @Override
                public void onFrameCaptured(Bitmap bmp) {
                    if (bmp == null) {
                        return;
                    }
                    bitmap = bmp;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                saveToSDCard(fileName, bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (bitmap != null) {
                                    bitmap.recycle();
                                    bitmap = null;
                                }
                            }
                        }
                    }).start();
                }
            });
        }
    }
}
