package com.angelatech.yeyelive.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.will.common.log.DebugLogs;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

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
 * 作者: Created by: xujian on Date: 2016/10/20.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */

public class TestScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private static final String TAG = TestScanActivity.class.getSimpleName();
    private QRCodeView mQRCodeView;
    private BasicUserInfoDBModel userInfo;
    private MainEnter mainEnter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        userInfo = CacheDataManager.getInstance().loadUser();
        mainEnter = new MainEnter(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mQRCodeView = (ZBarView) findViewById(R.id.zbarview);
        mQRCodeView.setDelegate(this);
        toolbar.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQRCodeView.startCamera();
        mQRCodeView.startSpot();
        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(final String result) {
        Log.i(TAG, "result:" + result);
        vibrate();
        mQRCodeView.startSpot();
        mainEnter.ScanRecharge(CommonUrlConfig.ScanRecharge, userInfo.userid, userInfo.token, result, callback);
    }



    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            LoadingDialog.cancelLoadingDialog();
        }

        @Override
        public void onSuccess(final String response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelLoadingDialog();
                    DebugLogs.d("==========" + response);
                }
            });
        }
    };

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

}
