package com.angelatech.yeyelive.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.angelatech.yeyelive.activity.base.HeaderBaseActivity;
import com.angelatech.yeyelive.db.BaseKey;
import com.angelatech.yeyelive.model.CommonModel;
import com.angelatech.yeyelive.qiniu.QiniuUpload;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.facebook.drawee.view.SimpleDraweeView;
import com.will.common.string.Encryption;
import com.will.common.string.json.JsonUtil;
import com.angelatech.yeyelive.CommonResultCode;
import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.PictureObtain;
import com.angelatech.yeyelive.util.StartActivityHelper;
import com.angelatech.yeyelive.util.UriHelper;
import com.angelatech.yeyelive.view.ActionSheetDialog;
import com.angelatech.yeyelive .R;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends HeaderBaseActivity {

    private final int LEN_LIMIT = 24;
    private final int MSG_INPUT_LIMIT = 1;

    private SimpleDraweeView user_head_photo;
    private TextView tv_input_limit;
    private EditText edit_user_name;
    private TextView tv_submit;
    private RadioGroup radio_group;
    private int user_gender = 1;
    private String user_name;
    private RadioButton radioButton_male, radioButton_female;
    private boolean upload_photo = false, check_gender = false, input_name = false;

    private PictureObtain mObtain;
    private Uri distUri;
    private BasicUserInfoDBModel model;

    private QiniuUpload qiNiuUpload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_insert);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.userinfo_title));
        headerLayout.showLeftBackButton(R.id.backBtn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        user_head_photo = (SimpleDraweeView) findViewById(R.id.user_head_photo);
        edit_user_name = (EditText) findViewById(R.id.edit_user_name);
        tv_input_limit = (TextView) findViewById(R.id.tv_input_limit);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        radioButton_male = (RadioButton) findViewById(R.id.radio_user_male);
        radioButton_female = (RadioButton) findViewById(R.id.radio_user_female);

        mObtain = new PictureObtain();
        CacheDataManager cacheDataManager = CacheDataManager.getInstance();
        model = cacheDataManager.loadUser();
        tv_submit.setOnClickListener(this);
        user_head_photo.setOnClickListener(this);

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                setUserGender(checkedId);
                setBackground();
            }
        });
        edit_user_name.addTextChangedListener(textWatcher);

        qiNiuUpload = new QiniuUpload(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_submit:
                saveUserInfo();
                break;
            case R.id.user_head_photo:
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.camera), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.dispatchTakePictureIntent(ProfileActivity.this, CommonResultCode.SET_ADD_PHOTO_CAMERA);
                                    }
                                })
                        .addSheetItem(getString(R.string.album), ActionSheetDialog.SheetItemColor.BLACK_222222,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.getLocalPicture(ProfileActivity.this, CommonResultCode.SET_ADD_PHOTO_ALBUM);
                                    }
                                }).show();
                break;
        }
    }

    private void saveUserInfo() {
        LoadingDialog.showLoadingDialog(ProfileActivity.this);
        user_name = edit_user_name.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("token", model.token);
        map.put("userid", model.userid);
        map.put("nickname", Encryption.utf8ToUnicode(user_name));
        map.put("sex", String.valueOf(user_gender));
        new HttpFunction(this).httpPost(CommonUrlConfig.UserInformationEdit, map, httpCallback);
    }

    /*监听输入事件*/
    private TextWatcher textWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            editStart = edit_user_name.getSelectionStart();
            editEnd = edit_user_name.getSelectionEnd();
            if (temp.length() > LEN_LIMIT) {
                editable.delete(editStart - 1, editEnd);
            }
            uiHandler.obtainMessage(MSG_INPUT_LIMIT, 0, 0, editable.toString()).sendToTarget();

        }
    };

    private void setBackground() {
        if (input_name && check_gender) {
            tv_submit.setBackgroundResource(R.drawable.common_btn_bg);
            tv_submit.setEnabled(true);
            tv_submit.setTextColor(0xFF222222);
        } else {
            tv_submit.setBackgroundResource(R.drawable.common_btn_unuse_bg);
            tv_submit.setEnabled(false);
            tv_submit.setTextColor(0xFFA6A6A6);
        }
    }

    @SuppressWarnings("deprecation")
    private void setUserGender(int viewId) {
        if (viewId == R.id.radio_user_male) {
            user_gender = 1;

            Drawable img_off = getResources().getDrawable(R.drawable.btn_login_sex_boy);
            if (img_off != null) {
                img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                radioButton_male.setCompoundDrawables(img_off, null, null, null);
            }

            img_off = getResources().getDrawable(R.drawable.btn_login_sex);
            if (img_off != null) {
                img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                radioButton_female.setCompoundDrawables(img_off, null, null, null);
            }
        } else {

            Drawable img_off = getResources().getDrawable(R.drawable.btn_login_sex_girl);
            if (img_off != null) {
                img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                radioButton_female.setCompoundDrawables(img_off, null, null, null);
            }
            img_off = getResources().getDrawable(R.drawable.btn_login_sex);
            if (img_off != null) {
                img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                radioButton_male.setCompoundDrawables(img_off, null, null, null);
            }
            user_gender = 0;
        }
        check_gender = true;
    }

    /**
     * 接收用户返回头像参数
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
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
                    //裁剪后的图片
                    String path = mObtain.getRealPathFromURI(this, distUri);
                    if (!new File(path).exists()) {
                        return;
                    }
                    user_head_photo.setImageURI(UriHelper.fromFile(path));
                    model.headurl = path;
                    LoadingDialog.showLoadingDialog(this);
//                    new IService().UpPicture(path, model.userid, model.token, CommonUrlConfig.PicUpload, "1",
//                            model.userid, uploadPicBack);

                    qiNiuUpload.setQiniuResultCallback(new QiniuUpload.QiniuResultCallback() {
                        @Override
                        public void onUpTokenError() {

                        }

                        @Override
                        public void onUpQiniuError() {
                            LoadingDialog.cancelLoadingDialog();
                            ToastUtils.showToast(ProfileActivity.this, getString(R.string.upload_photo_error));
                            upload_photo = false;
                        }

                        @Override
                        public void onCallServerError() {

                        }

                        @Override
                        public void onUpQiniuSuc(String key) {
                            LoadingDialog.cancelLoadingDialog();
                            if (key == null) {
                                return;
                            }
                            upload_photo = true;
                            CacheDataManager.getInstance().update(BaseKey.USER_HEAD_URL, key, model.userid);
                        }

                        @Override
                        public void onUpProgress(String key, double percent) {

                        }
                    });
                    qiNiuUpload.doUpload(model.userid, model.token, path);

                    setBackground();
                    break;
            }
        }
    }

    HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {

        }

        @Override
        public void onSuccess(String response) {
            LoadingDialog.cancelLoadingDialog();
            if (response != null) {
                CommonModel common = JsonUtil.fromJson(response, CommonModel.class);
                if (common != null) {
                    if (HttpFunction.isSuc(common.code)) {
                        CacheDataManager.getInstance().update(BaseKey.USER_HEAD_URL, model.headurl, model.userid);
                        CacheDataManager.getInstance().update(BaseKey.USER_SEX, String.valueOf(user_gender), model.userid);
                        CacheDataManager.getInstance().update(BaseKey.USER_NICKNAME, user_name, model.userid);
                        StartActivityHelper.jumpActivity(ProfileActivity.this, Intent.FLAG_ACTIVITY_CLEAR_TASK, null, MainActivity.class, null);
                    } else {
                        //错误提示
                        onBusinessFaild(common.code);
                    }
                }
            }
        }
    };

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_INPUT_LIMIT:
                String inputStr = (String) msg.obj;
                if (inputStr != null && inputStr.length() > 0) {
                    input_name = true;
                    String str = inputStr.length() + "/" + LEN_LIMIT;
                    tv_input_limit.setText(str);
                } else {
                    input_name = false;
                }
                setBackground();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
