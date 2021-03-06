package com.angelatech.yeyelive1.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.angelatech.yeyelive1.CommonResultCode;
import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive1.activity.function.MainEnter;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.CommonModel;
import com.angelatech.yeyelive1.model.ProductModel;
import com.angelatech.yeyelive1.qiniu.QiniuUpload;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.PictureObtain;
import com.angelatech.yeyelive1.view.ActionSheetDialog;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
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
 * com.angelatech.yeyelive1.activity
 */

public class UploadProductsActivity extends HeaderBaseActivity {
    private EditText product_name, product_price, product_describe, product_facebook, product_weichat, product_phone;
    private PictureObtain mObtain;
    private FrescoDrawee btn_upload;
    private Uri distUri;
    private QiniuUpload qiNiuUpload;
    private BasicUserInfoDBModel userInfo;
    private MainEnter mainEnter;
    private boolean ispull = false;
    private String picPath = "";
    private TextView textView10;
    private ProductModel productModel;
    private boolean isMidfiy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadproducts);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            productModel = (ProductModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        }
        initView();
        initData();
        setData();
    }

    private void setData() {
        if (productModel != null) {
            isMidfiy = true;
            product_name.setText(productModel.tradename);
            product_price.setText(productModel.voucher);
            product_describe.setText(productModel.describe);
            btn_upload.setImageURI(productModel.tradeurl);
            picPath = productModel.tradeurl;
            String[] temp = productModel.contact.split(",");
            if (temp.length > 3) {
                product_facebook.setText(temp[0]);
                product_weichat.setText(temp[1]);
                product_phone.setText(temp[2]);
            }
            textView10.setText("");
        }
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
        product_name = (EditText) findViewById(R.id.product_name);
        product_price = (EditText) findViewById(R.id.product_price);
        product_describe = (EditText) findViewById(R.id.product_describe);
        product_facebook = (EditText) findViewById(R.id.product_facebook);
        product_weichat = (EditText) findViewById(R.id.product_weichat);
        product_phone = (EditText) findViewById(R.id.product_phone);
        textView10 = (TextView) findViewById(R.id.textView10);
        btn_upload.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_Link.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
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
                if (isMidfiy) {
                    ModifyProduct();
                } else {
                    if (ispull) {
                        uploadProduct();
                    } else {
                        ToastUtils.showToast(UploadProductsActivity.this, getString(R.string.product_tips));
                    }
                }
                break;
            case R.id.btn_Link:
                String[] email = {"support@iamyeye.com"}; // 需要注意，email必须以数组形式传入
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822"); // 设置邮件格式
                intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
                intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.product_update)); // 主题
                startActivity(Intent.createChooser(intent, ""));
                //系统分享文字
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//                intent.putExtra(Intent.EXTRA_TEXT, "哈哈哈");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(Intent.createChooser(intent, getTitle()));

//                //分享图片
//                String imagePath = Environment.getExternalStorageDirectory() + File.separator + "test.jpg";
//                //由文件得到uri
//                Uri imageUri = Uri.fromFile(new File(imagePath));
//                //输出：file:///storage/emulated/0/test.jpg
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                shareIntent.setType("image/*");
//                startActivity(Intent.createChooser(shareIntent, "分享到"));
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
                            DebugLogs.d("图片上传成功" + key);
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

    //添加商品
    private void uploadProduct() {
        String name = product_name.getText().toString();
        String price = product_price.getText().toString();
        String describe = product_describe.getText().toString();
        String str_facebook = product_facebook.getText().toString();
        String str_weichat = product_weichat.getText().toString();
        String str_phone = product_phone.getText().toString();
        String contact = str_facebook + "," + str_weichat + "," + str_phone;
        if (picPath.isEmpty()) {
            return;
        }
        mainEnter.UserMallIns(CommonUrlConfig.UserMallIns, userInfo.userid, userInfo.token, name, picPath, price, describe, contact, callback);
    }

    //修改商品
    private void ModifyProduct() {
        String name = product_name.getText().toString();
        String price = product_price.getText().toString();
        String describe = product_describe.getText().toString();
        String str_facebook = product_facebook.getText().toString();
        String str_weichat = product_weichat.getText().toString();
        String str_phone = product_phone.getText().toString();
        String mallId = productModel.mallid;
        String contact = str_facebook + "," + str_weichat + "," + str_phone;
        if (picPath.isEmpty()) {
            return;
        }
        mainEnter.UserMallUpt(CommonUrlConfig.UserMallUpt, userInfo.userid, userInfo.token, name, picPath, price, describe, contact, mallId, callback);
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
                    CommonModel common = JsonUtil.fromJson(response, CommonModel.class);
                    if (common == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(common.code)) {
                        if (isMidfiy) {
                            ToastUtils.showToast(UploadProductsActivity.this, getString(R.string.product_midfey));
                        } else {
                            ToastUtils.showToast(UploadProductsActivity.this, getString(R.string.product_tips1));
                        }
                        finish();
                    } else {
                        onBusinessFaild(common.code);
                    }
                }
            });
        }
    };
}
