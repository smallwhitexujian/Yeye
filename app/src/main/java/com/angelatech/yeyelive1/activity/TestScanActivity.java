package com.angelatech.yeyelive1.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.activity.function.UserInfoDialog;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.BasicUserInfoModel;
import com.angelatech.yeyelive1.model.CommonListResult;
import com.angelatech.yeyelive1.model.WebTransportModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
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
 * com.angelatech.yeyelive1.activity
 */

public class TestScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private static final String TAG = TestScanActivity.class.getSimpleName();
    private QRCodeView mQRCodeView;
    private BasicUserInfoDBModel userInfo;
    private WebTransportModel webTransportModel;
    private UserInfoDialog userInfoDialog;
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
        if (result.contains("ScanRecharge")) {
            String iuresult = null;
            if (result.contains("key=")) {
                iuresult = result + "&userid=" + userInfo.userid + "&token=" + userInfo.token + "&sources=" + 2 + "&" + System.currentTimeMillis();
            }
            webTransportModel = new WebTransportModel();
            webTransportModel.url = iuresult;
            webTransportModel.title = getString(R.string.yeye_web_title);
            if (!webTransportModel.url.isEmpty()) {
                LoadingDialog.cancelLoadingDialog();
                StartActivityHelper.jumpActivity(TestScanActivity.this, WebActivity.class, webTransportModel);
            }
        } else {
            if (result.contains("http")) {
                webTransportModel = new WebTransportModel();
                webTransportModel.url = result;
                webTransportModel.title = "";
                if (!webTransportModel.url.isEmpty()) {
                    LoadingDialog.cancelLoadingDialog();
                    Uri uri = Uri.parse(result);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
            //好友二维码

            else if(result.contains("yeye://friend?userId=")){
                String userid = result.replace("yeye://friend?userId=","");
                BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                userInfoModel.Userid = userid;
                StartActivityHelper.jumpActivity(TestScanActivity.this, FriendUserInfoActivity.class, userInfoModel);
                finish();
            }
            //支付二维码
            else if(result.contains("yeye://charge?userId=")){
                String userid = result.replace("yeye://charge?userId=","");
                mQRCodeView.stopCamera();
                loaduserinfo(userid);

            }
            else {
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(TestScanActivity.this, result);
            }
        }
    }

    /**
     * 查询 用户信息
     */
    private void loaduserinfo(String userid) {
        if (userInfoDialog == null) {
            userInfoDialog = new UserInfoDialog(this);
        }
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        params.put("touserid", userid);

        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                CommonListResult<BasicUserInfoDBModel> results = JsonUtil.fromJson(response, new TypeToken<CommonListResult<BasicUserInfoDBModel>>() {
                }.getType());
                if (results != null) {
                    DebugLogs.e("sfsdfsadfasdfaf:"+response);
                    if (HttpFunction.isSuc(results.code)) {
                        if (results.hasData()) {

                            StartActivityHelper.jumpActivity(TestScanActivity.this, TransferActivity.class,  results.data.get(0));
                            finish();
                        }
                    } else {
                        onBusinessFaild(results.code);
                    }
                }
            }
        };
        userInfoDialog.httpGet(CommonUrlConfig.UserInformation, params, callback);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
//        Log.e(TAG, "打开相机出错");
    }

}
