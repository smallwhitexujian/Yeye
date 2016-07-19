package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.model.CommonModel;
import com.facebook.drawee.view.SimpleDraweeView;
import com.will.common.string.json.JsonUtil;
import com.angelatech.yeyelive.CommonResultCode;
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.Constant;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.qiniu.QiniuUpload;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.PictureObtain;
import com.angelatech.yeyelive.util.UriHelper;
import com.angelatech.yeyelive.view.ActionSheetDialog;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.angelatech.yeyelive .R;
import com.will.view.ToastUtils;
import com.will.web.callback.HttpCallback;
import com.will.web.handle.HttpBusinessCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息编辑界面
 */
public class UserInfoActivity extends HeaderBaseActivity {
    private RelativeLayout layout_user_photo;
    private RelativeLayout layout_nickName;
    private RelativeLayout layout_sex;//性别
    private LinearLayout layout_user_sign;
    private PictureObtain mObtain;
    private BasicUserInfoDBModel model;
    private SimpleDraweeView user_head_photo;
    private TextView tv_nickName, tv_sign, tv_sex;
    private CacheDataManager cacheDataManager;
    private String path = null;
    private QiniuUpload mQiniuUpload;
    private String sex = Constant.SEX_FEMALE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        initView();
        setView();
        mQiniuUpload = new QiniuUpload(UserInfoActivity.this);
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.userinfo_title));
        headerLayout.showLeftBackButton(R.id.backBtn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout_user_photo = (RelativeLayout) findViewById(R.id.layout_user_photo);
        layout_nickName = (RelativeLayout) findViewById(R.id.layout_nickName);
        layout_sex = (RelativeLayout) findViewById(R.id.layout_sex);
        layout_user_sign = (LinearLayout) findViewById(R.id.layout_user_sign);
        user_head_photo = (SimpleDraweeView) findViewById(R.id.user_head_photo);
        tv_nickName = (TextView) findViewById(R.id.tv_nickName);
        tv_sign = (TextView) findViewById(R.id.tv_sign);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        mObtain = new PictureObtain();
    }

    private void setView() {
        layout_user_photo.setOnClickListener(this);
        layout_nickName.setOnClickListener(this);
        layout_user_sign.setOnClickListener(this);
        layout_sex.setOnClickListener(this);
    }

    private void initData() {
        tv_nickName.setText(model.nickname);
        tv_sign.setText(model.sign);
        if (model.sex == null || model.sex.equals(Constant.SEX_FEMALE)) {
            tv_sex.setText(getString(R.string.user_female));
        } else {
            tv_sex.setText(getString(R.string.user_male));
        }
        if (path == null) {
            user_head_photo.setImageURI(UriHelper.obtainUri(model.headurl));
        } else {
            user_head_photo.setImageURI(UriHelper.obtainUri(path));
        }

    }

    @Override
    public void doHandler(Message msg) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        cacheDataManager = CacheDataManager.getInstance();
        model = cacheDataManager.loadUser();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_user_photo:
                new ActionSheetDialog(UserInfoActivity.this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.camera), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.dispatchTakePictureIntent(UserInfoActivity.this, CommonResultCode.SET_ADD_PHOTO_CAMERA);
                                    }
                                })
                        .addSheetItem(getString(R.string.album), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.getLocalPicture(UserInfoActivity.this, CommonResultCode.SET_ADD_PHOTO_ALBUM);
                                    }
                                }).show();
                break;
            case R.id.layout_nickName:
                Intent intent = new Intent();
                intent.setClass(UserInfoActivity.this, EditActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
            case R.id.layout_user_sign:
                Intent intent_sign = new Intent();
                intent_sign.setClass(UserInfoActivity.this, EditActivity.class);
                intent_sign.putExtra("type", "2");
                startActivity(intent_sign);
                break;
            case R.id.layout_sex:
                new ActionSheetDialog(UserInfoActivity.this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.user_female), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        tv_sex.setText(getString(R.string.user_female));
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("token", model.token);
                                        map.put("userid", model.userid);
                                        map.put("sex", Constant.SEX_FEMALE);
                                        new HttpFunction(UserInfoActivity.this).httpPost(CommonUrlConfig.UserInformationEdit, map, new ChangeGenderTask(model.userid, Constant.SEX_FEMALE));
                                    }
                                })
                        .addSheetItem(getString(R.string.user_male), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        tv_sex.setText(getString(R.string.user_male));
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("token", model.token);
                                        map.put("userid", model.userid);
                                        map.put("sex", Constant.SEX_MALE);
                                        new HttpFunction(UserInfoActivity.this).httpPost(CommonUrlConfig.UserInformationEdit, map, new ChangeGenderTask(model.userid, Constant.SEX_MALE));
                                    }
                                }).show();
                break;
        }
    }

    Uri distUri;

    /**
     * 接收用户返回头像参数
     */
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (resultCode != RESULT_CANCELED && resultCode == RESULT_OK) {

            switch (requestCode) {
                case CommonResultCode.SET_ADD_PHOTO_CAMERA:
                    //拍照
                    distUri = mObtain.obtainUrl();
                    mObtain.notifyChange(UserInfoActivity.this, mObtain.getUri(UserInfoActivity.this));
                    mObtain.cropBig(UserInfoActivity.this, mObtain.getUri(UserInfoActivity.this), distUri, CommonResultCode.REQUEST_CROP_PICTURE, 400, 400);
                    break;
                case CommonResultCode.SET_ADD_PHOTO_ALBUM:
                    //从相册获取
                    if (data != null && data.getData() != null) {
                        distUri = mObtain.obtainUrl();
                        mObtain.cropBig(UserInfoActivity.this, data.getData(), distUri, CommonResultCode.REQUEST_CROP_PICTURE, 400, 400);
                    }
                    break;
                case CommonResultCode.REQUEST_CROP_PICTURE:
                    //裁剪后的图片
                    path = mObtain.getRealPathFromURI(UserInfoActivity.this, distUri);
                    if (!new File(path).exists()) {
                        return;
                    }
                    model.headurl = path;
                    user_head_photo.setImageURI(UriHelper.obtainUri(model.headurl));

                    mQiniuUpload.setQiniuResultCallback(new QiniuUpload.QiniuResultCallback() {
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
                            ToastUtils.showToast(UserInfoActivity.this, getString(R.string.upload_photo_suc));
                            CacheDataManager.getInstance().update(BaseKey.USER_HEAD_URL, key, model.userid);
                        }

                        @Override
                        public void onUpProgress(String key, double percent) {

                        }
                    });
                    mQiniuUpload.doUpload(model.userid, model.token, path);

//                    new IService().UpPicture(path, model.userid, model.token, CommonUrlConfig.PicUpload, "1",
//                            model.userid, uploadPic);
                    break;
                default:
                    break;
            }
        }
    }

    private static class ChangeGenderTask extends HttpBusinessCallback {

        private String mGender;
        private String mUserId;

        public ChangeGenderTask(String userId, String gender) {
            this.mUserId = userId;
            this.mGender = gender;
        }


        @Override
        public void onFailure(Map<String, ?> errorMap) {

        }

        @Override
        public void onSuccess(String response) {
            LoadingDialog.cancelLoadingDialog();
            if (response != null) {
                CommonModel common = JsonUtil.fromJson(response, CommonModel.class);
                if (common != null) {
                    if (common.code.equals(CommonResultCode.INTERFACE_RETURN_CODE) && mUserId != null) {
                        CacheDataManager.getInstance().update(BaseKey.USER_SEX, mGender, mUserId);
                    } else {
                        //错误提示
                    }
                }
            }
        }
    }


    HttpCallback uploadPic = new HttpCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            ToastUtils.showToast(UserInfoActivity.this, getString(R.string.upload_photo_error));
        }

        @Override
        public void onSuccess(String response) {
            CacheDataManager.getInstance().update(BaseKey.USER_HEAD_URL, model.headurl, model.userid);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
