//package com.angelatech.yeyelive.activity.Qiniupush;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.angelatech.yeyelive.R;
//import com.pili.pldroid.player.PLMediaPlayer;
//import com.pili.pldroid.player.widget.PLVideoTextureView;
//
//public class MainActivity extends Activity implements PLVideoTextureUtils.PLVideoCallBack
//        , View.OnClickListener {
//    private PLVideoTextureView plVideoTextureView;
//    private LinearLayout LoadingView;
//    private PLVideoTextureUtils plUtils;
//    private String mVideoPath = "http://video.iamyeye.com/recordings/z1.yeye.1061172/10611721473924484.mp4";//播放地址
//    private Button btn, btn1, btn2, btn3, btn4, btn5;
//    private TextView info;
//    private SeekBar mProgress;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        plVideoTextureView = (PLVideoTextureView) findViewById(R.id.VideoView);
//        LoadingView = (LinearLayout) findViewById(R.id.LoadingView);
//        mProgress = (SeekBar) findViewById(R.id.player_seekBar);
//        info = (TextView) findViewById(R.id.info);
//        btn = (Button) findViewById(R.id.btn);
//        btn1 = (Button) findViewById(R.id.btn1);
//        btn2 = (Button) findViewById(R.id.btn2);
//        btn3 = (Button) findViewById(R.id.btn3);
//        btn4 = (Button) findViewById(R.id.btn4);
//        btn5 = (Button) findViewById(R.id.btn5);
//        btn.setOnClickListener(this);
//        btn1.setOnClickListener(this);
//        btn2.setOnClickListener(this);
//        btn3.setOnClickListener(this);
//        btn4.setOnClickListener(this);
//        btn5.setOnClickListener(this);
//        // 为进度条添加进度更改事件
//        plUtils = new PLVideoTextureUtils();
//        plUtils.init(this, plVideoTextureView, PLVideoTextureUtils.REMEDIACODEC, PLVideoTextureUtils.LIVESTREAM, mVideoPath, LoadingView);
//        plUtils.setCallBack(this);
////        plUtils.setSeekBar(mProgress);
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        plUtils.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        plUtils.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        plUtils.onResume();
//    }
//
//
//    @Override
//    public void onPrepared(PLMediaPlayer plMediaPlayer) {
//
//    }
//
//    @Override
//    public void onCompletion(PLMediaPlayer plMediaPlayer) {
//        //播放结束处理
//    }
//
//    @Override
//    public void onTimeOut() {
//        //超时处理
//    }
//
//    @Override
//    public void setCurrentTime(String CurrentTime, String endTime) {
//        info.setText(CurrentTime + "/" + endTime);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn://旋转
//                plUtils.onClickRotate();
//                break;
//            case R.id.btn1://放大
//                plUtils.onClickSwitchScreen();
//                break;
//            case R.id.btn2://开始
//                plUtils.onClickPlay();
//                break;
//            case R.id.btn3://暂停
//                plUtils.onClickPause();
//                break;
//            case R.id.btn4://继续
//                plUtils.onClickResume();
//                break;
//            case R.id.btn5://结束
//                plUtils.onClickDestroy();
//                break;
//        }
//    }
//}
