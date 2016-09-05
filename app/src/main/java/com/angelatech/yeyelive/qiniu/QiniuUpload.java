package com.angelatech.yeyelive.qiniu;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.angelatech.yeyelive.handler.CommonDoHandler;
import com.angelatech.yeyelive.handler.CommonHandler;
import com.angelatech.yeyelive.model.CommonParseModel;
import com.angelatech.yeyelive.model.QiniuTokenModel;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.util.StringHelper;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.out.QiniuSimpleManager;
import com.qiniu.android.out.SimpleUpCompletionHandler;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;
import com.will.common.log.DebugLogs;
import com.will.common.string.security.Md5;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Author: jjfly
 * Since: 2016年05月13日 10:35
 * Desc: 七牛上传类：
 * 调用doUpload
 * 流程：
 * 1、从业务服务器获取上传token
 * 2、上传到七牛服务器
 * 3、成功返回则调用业务服务器
 * 提供上传错误回调接口
 * <p/>
 * FIXME:
 */
public class QiniuUpload implements CommonDoHandler {

    private final int MSG_UPLOAD_SUC = 1;
    private final int MSG_UPLOAD_FAILD = 2;


    private QiniuUtil mQiniuUtil;
    private CommonHandler<QiniuUpload> mHandler;
    private QiniuSimpleManager mQiniuSimpleManager;
    private QiniuResultCallback mQiniuResultCallback;
    private String mQiniuToken = null;

    public QiniuUpload(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        this.mQiniuUtil = new QiniuUtil(context);
        mHandler = new CommonHandler(this);
        mQiniuSimpleManager = new QiniuSimpleManager();
        mQiniuResultCallback = new QiniuResultCallback() {
            @Override
            public void onUpTokenError() {
//                ToastUtils.showToast(context, R.string.qiniu_upload_faild);
            }

            @Override
            public void onUpQiniuError() {

            }

            @Override
            public void onCallServerError() {

            }

            @Override
            public void onUpQiniuSuc(String key) {
//                ToastUtils.showToast(context, R.string.qiniu_upload_suc);
            }

            @Override
            public void onUpProgress(String key, double percent) {

            }
        };
    }

    //get token
//    private boolean getCertificate() {
//        if (mQiniuTokenModel == null) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    private void upload(String data, String key, UpCompletionHandler upCompletionHandler, UploadOptions uploadOptions) {
        if (mQiniuSimpleManager == null) {
            mQiniuSimpleManager = new QiniuSimpleManager();
        }
        mQiniuSimpleManager.upPhoto(mQiniuToken, data, key, upCompletionHandler, uploadOptions);
    }

    private void callWebServer(String userid, String token, String imageName, String id) {
        mHandler.post(new CallServerTask(userid, token, imageName, id));
    }


    public void doUpload(String userid, String token, String data, String key, UpCompletionHandler upCompletionHandler) {
        mHandler.post(new CertificateTask(userid, token, data, key, upCompletionHandler));
    }

    public void doUpload(final String userid, final String token, final String data, String key) {
        UpCompletionHandler upCompletionHandler = new SimpleUpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                mQiniuToken = null;//每次调用一边就清空
                Log.e("QiniuProxy", "====" + key +"====="+data+"======"+ info.toString() + "====" + response);
                DebugLogs.e("=====" +info.toString());
                if(info != null && info.isOK()){
                    if (response == null) {
                        if(mQiniuResultCallback != null){
                            mQiniuResultCallback.onUpQiniuError();
                        }
                    } else {
                        try {
                            //{"error":"file exists"}
                            String hash = response.getString("hash");
                            String keyStr = response.getString("key");
                            callWebServer(userid, token, key, userid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    if(mQiniuResultCallback != null){
                        mQiniuResultCallback.onUpQiniuError();
                    }
                }
            }
        };
        mHandler.post(new CertificateTask(userid, token, data, key, upCompletionHandler));
    }


    public void doUpload(String userid, String token, String data, UpCompletionHandler upCompletionHandler) {
        //随便传递一个key值
        doUpload(userid,token,data,"",upCompletionHandler);
    }

    public void doUpload(String userid, String token, String data) {
        //随便传递一个key值
        doUpload(userid,token,data,"");
    }


    private class CertificateTask implements Runnable {
        private String mUserId;//用户id
        private String mToken;//用户token

        private String mSrcFilePath;//
        private String mKey;//
        private UpCompletionHandler mUpCompletionHandler;

        public CertificateTask(String userid, String token, String srcFilePath, String key, UpCompletionHandler upCompletionHandler) {
            this.mUserId = userid;
            this.mToken = token;

            this.mSrcFilePath = srcFilePath;
            this.mKey = key;
            this.mUpCompletionHandler = upCompletionHandler;

        }

        public CertificateTask(String userid, String token, String srcFilePath,UpCompletionHandler upCompletionHandler) {
            this.mUserId = userid;
            this.mToken = token;

            this.mSrcFilePath = srcFilePath;
            this.mUpCompletionHandler = upCompletionHandler;

        }


        @Override
        public void run() {
            HttpBusinessCallback businessCallback = new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    super.onFailure(errorMap);
                }

                @Override
                public void onSuccess(String response) {
                    DebugLogs.e("====" + response);
                    CommonParseModel<QiniuTokenModel> result = JsonUtil.fromJson(response, new TypeToken<CommonParseModel<QiniuTokenModel>>() {
                    }.getType());
                    if (result != null) {
                        if (mQiniuUtil.isSuc(result.code)) {
                            DebugLogs.e("=====" + result.data);
                            if(result.data != null ){
                                String suffix = StringHelper.getExtensionName(mSrcFilePath);
                                mQiniuToken =  result.data.uptoken;
                                mKey = result.data.filename+"."+suffix;
                                upload(mSrcFilePath, mKey, mUpCompletionHandler, new UploadOptions(null, null, false,
                                        new UpProgressHandler(){
                                            public void progress(String key, double percent){
                                                if(mQiniuResultCallback != null){
                                                    mQiniuResultCallback.onUpProgress(key,percent);
                                                }
                                            }
                                        }, null));
                            }
                        } else {
                            //提示
                            onBusinessFaild(result.code, response);
                        }
                    }
                }
            };
            mQiniuUtil.getQiniuCertificate(mUserId, mToken, businessCallback);
        }
    }

    //上传回调服务器
    private class CallServerTask implements Runnable {
        private String mUserId;
        private String mToken;

        private String mImageName;
        private String mId;

        public CallServerTask(String userId, String token, String imageName, String id) {
            this.mUserId = userId;
            this.mToken = token;
            this.mImageName = imageName;
            this.mId = id;
        }

        @Override
        public void run() {
            HttpBusinessCallback businessCallback = new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    super.onFailure(errorMap);
                }

                @Override
                public void onSuccess(String response) {
                    DebugLogs.e("====" + response);
                    Map map = JsonUtil.fromJson(response, Map.class);
                    if (mQiniuUtil.isSuc((String) map.get("code"))) {
                        mHandler.obtainMessage(MSG_UPLOAD_SUC,map.get("data")).sendToTarget();

                    }
                    else {
                        onBusinessFaild((String) map.get("code"));
                    }
                }
            };
            mQiniuUtil.callBusinessServer(mUserId, mToken, mImageName, mId, businessCallback);
        }

    }


    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_UPLOAD_SUC:
                if(mQiniuResultCallback != null){
                    mQiniuResultCallback.onUpQiniuSuc((String)msg.obj);
                }
                break;
        }

    }

    public String createUniqueName(String userId,String filename){
        if(userId == null){
            throw new IllegalArgumentException();
        }
        long timestamp = System.currentTimeMillis();
        String newName = Md5.get32MD5Lower(userId+timestamp);
        return newName;

    }

    public void setQiniuResultCallback(QiniuResultCallback callback){
        mQiniuResultCallback = callback;
    }



    public interface QiniuResultCallback {
        /***
         * 失败部分
         */
        //获取上传token失败
        void onUpTokenError();
        //上传到七牛服务器失败
        void onUpQiniuError();
        //上传七牛成功回调业务
        void onCallServerError();

        /**
         * 成功部分
         */
        void onUpQiniuSuc(String key);


        void onUpProgress(String key, double percent);


    }


}
