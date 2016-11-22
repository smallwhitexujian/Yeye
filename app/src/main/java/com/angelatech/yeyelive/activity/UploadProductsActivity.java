package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonResultCode;
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.qiniu.QiniuUpload;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.PictureObtain;
import com.angelatech.yeyelive.view.ActionSheetDialog;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoDrawee;

import java.io.File;
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
 * 作者: Created by: xujian on Date: 2016/11/22.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity
 */

public class UploadProductsActivity extends HeaderBaseActivity {
    private EditText product_name,product_price,product_describe,product_facebook,product_weichat,product_phone;
    private PictureObtain mObtain;
    private FrescoDrawee btn_upload;
    private Uri distUri;
    private QiniuUpload qiNiuUpload;
    private BasicUserInfoDBModel userInfo;
    private MainEnter mainEnter;
    private boolean ispull = false;
    private String picPath ="";
    private TextView textView10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadproducts);
        initView();
        initData();
    }

    private void initData() {
        mObtain = new PictureObtain();
        qiNiuUpload = new QiniuUpload(this);
        userInfo = CacheDataManager.getInstance().loadUser();
        mainEnter = new MainEnter(this);
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.uploadProducts));
        headerLayout.showLeftBackButton(R.id.backBtn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_upload = (FrescoDrawee) findViewById(R.id.btn_upload);
        Button btn_confirm = (Button) findViewById(R.id.btn_confirm);
        Button btn_Link = (Button) findViewById(R.id.btn_Link);
        product_name = (EditText)findViewById(R.id.product_name);
        product_price = (EditText)findViewById(R.id.product_price);
        product_describe = (EditText)findViewById(R.id.product_describe);
        product_facebook = (EditText)findViewById(R.id.product_facebook);
        product_weichat = (EditText)findViewById(R.id.product_weichat);
        product_phone = (EditText)findViewById(R.id.product_phone);
        textView10 = (TextView)findViewById(R.id.textView10);
        btn_upload.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_Link.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_upload:
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.camera), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.dispatchTakePictureIntent(UploadProductsActivity.this, CommonResultCode.SET_ADD_PHOTO_CAMERA);
                                    }
                                })
                        .addSheetItem(getString(R.string.album), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.getLocalPicture(UploadProductsActivity.this, CommonResultCode.SET_ADD_PHOTO_ALBUM);
                                    }
                                }).show();
                break;
            case R.id.btn_confirm:
                if(ispull){
                    uploadProduct();
                }else{
                    ToastUtils.showToast(UploadProductsActivity.this,getString(R.string.product_tips));
                }
                break;
            case R.id.btn_Link:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CommonResultCode.SET_ADD_PHOTO_CAMERA:
                    //拍照
                    distUri = mObtain.obtainUrl();
                    mObtain.notifyChange(this, mObtain.getUri(this));
                    mObtain.cropBig(this, mObtain.getUri(this), distUri, CommonResultCode.REQUEST_CROP_PICTURE, 800, 800);
                    break;
                case CommonResultCode.SET_ADD_PHOTO_ALBUM:
                    //从相册获取
                    if (data != null) {
                        distUri = mObtain.obtainUrl();
                        mObtain.cropBig(this, data.getData(), distUri, CommonResultCode.REQUEST_CROP_PICTURE, 800, 800);
                    }
                    break;
                case CommonResultCode.REQUEST_CROP_PICTURE:
                    String imgPath = null;
                    //裁剪后的图片
                    String path = mObtain.getRealPathFromURI(this, distUri);
                    if (!new File(path).exists()) {
                        return;
                    }
                    try {
                        Bitmap bitmap = mObtain.getimage(path);
                        imgPath = mObtain.saveBitmapFile(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    btn_upload.setImageURI(imgPath);
                    textView10.setText("");
                    qiNiuUpload.setQiniuResultCallback(new QiniuUpload.QiniuResultCallback() {
                        @Override
                        public void onUpTokenError() {

                        }

                        @Override
                        public void onUpQiniuError() {
                        }

                        @Override
                        public void onCallServerError() {

                        }

                        @Override
                        public void onUpQiniuSuc(String key) {

                        }

                        @Override
                        public void onUpQinniuResult(String key) {
                            ispull = true;
                            DebugLogs.d("图片上传成功"+key);
                            picPath = key;
                        }

                        @Override
                        public void onUpProgress(String key, double percent) {

                        }
                    });
                    qiNiuUpload.doUpload(userInfo.userid, userInfo.token, imgPath, userInfo.userid, "10");
                    break;
            }
        }
    }


    private void uploadProduct(){
        String name = product_name.getText().toString();
        String price = product_price.getText().toString();
        String describe = product_describe.getText().toString();
        String str_facebook = product_facebook.getText().toString();
        String str_weichat = product_weichat.getText().toString();
        String str_phone = product_phone.getText().toString();
        String contact = str_facebook+","+str_weichat+","+str_phone;
        if (picPath.isEmpty()){
            return;
        }
        mainEnter.UserMallIns(CommonUrlConfig.UserMallIns, userInfo.userid, userInfo.token,name,picPath,price,describe,contact,callback);
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
                    CommonListResult<String> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<String>>() {
                    }.getType());
                    if (datas == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(datas.code)) {
                        ToastUtils.showToast(UploadProductsActivity.this,getString(R.string.product_tips1));
                    } else {
                        onBusinessFaild(datas.code);
                    }
                }
            });
        }
    };
}
