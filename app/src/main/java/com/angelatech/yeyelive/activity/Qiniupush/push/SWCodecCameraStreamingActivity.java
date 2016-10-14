package com.angelatech.yeyelive.activity.Qiniupush.push;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.angelatech.yeyelive.R;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.WatermarkSetting;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;

/**
 * Created by jerikc on 15/10/29.
 * 配置宿主
 */
public class SWCodecCameraStreamingActivity extends StreamingBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swcamera_activity);
        initQiniuSDK();
    }

    private void initQiniuSDK() {
        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.FULL);
        CameraPreviewFrameView cameraPreviewFrameView = (CameraPreviewFrameView) findViewById(R.id.cameraPreview_surfaceView);
        cameraPreviewFrameView.setListener(this);

        WatermarkSetting watermarksetting = new WatermarkSetting(this);
        watermarksetting.setResourceId(R.drawable.logo_watermask)
                .setAlpha(100)
                .setLocation(WatermarkSetting.WATERMARK_LOCATION.NORTH_EAST)
                .setSize(WatermarkSetting.WATERMARK_SIZE.SMALL);

        mMediaStreamingManager = new MediaStreamingManager(this, afl, cameraPreviewFrameView,
                AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC); // sw codec

        mMediaStreamingManager.prepare(mCameraStreamingSetting, mMicrophoneStreamingSetting, watermarksetting, mProfile);
        mMediaStreamingManager.setStreamingStateListener(this);
        mMediaStreamingManager.setSurfaceTextureCallback(this);
        mMediaStreamingManager.setStreamingSessionListener(this);
        mMediaStreamingManager.setStreamStatusCallback(this);
        mMediaStreamingManager.setStreamingPreviewCallback(this);
        mMediaStreamingManager.setAudioSourceCallback(this);
        setFocusAreaIndicator();//设置聚焦功能
        setBeauty();//设置默认美颜功能
        ImageView button_call_disconnect = (ImageView)findViewById(R.id.button_call_disconnect);
        button_call_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUrl = "rtmp://pili-publish.iamyeye.com/yeye/1073105?key=bc776494fba31144";
                setStartStreaming(inputUrl);
            }
        });
    }

}
