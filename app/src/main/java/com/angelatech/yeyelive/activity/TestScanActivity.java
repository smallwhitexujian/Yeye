package com.angelatech.yeyelive.activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.WebTransportModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.will.view.ToastUtils;

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
    private WebTransportModel webTransportModel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        userInfo = CacheDataManager.getInstance().loadUser();
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
        mQRCodeView.showScanRect();
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
        LoadingDialog.showLoadingDialog(this, null);
        Log.i(TAG, "result:" + result);
        vibrate();
        mQRCodeView.startSpot();
        if (result.contains(CommonUrlConfig.ScanRecharge)){
            String iuresult = null;
            if (result.contains("key=")) {
                iuresult = result + "&userid=" + userInfo.userid + "&token=" + userInfo.token+"&"+System.currentTimeMillis();
            }
            webTransportModel = new WebTransportModel();
            webTransportModel.url = iuresult;
            webTransportModel.title = getString(R.string.yeye_web_title);
            if (!webTransportModel.url.isEmpty()) {
                LoadingDialog.cancelLoadingDialog();
                StartActivityHelper.jumpActivity(TestScanActivity.this, WebActivity.class, webTransportModel);
            }
        }else{
           if (result.contains("http")){
               webTransportModel = new WebTransportModel();
               webTransportModel.url = result;
               webTransportModel.title = "";
               if (!webTransportModel.url.isEmpty()) {
                   LoadingDialog.cancelLoadingDialog();
                   StartActivityHelper.jumpActivity(TestScanActivity.this, WebActivity.class, webTransportModel);
               }
           }else{
               ToastUtils.showToast(TestScanActivity.this,result);
           }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
//        Log.e(TAG, "打开相机出错");
    }

}
