package com.angelatech.yeyelive.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.mediaplayer.handler.CommonDoHandler;
import com.angelatech.yeyelive.mediaplayer.handler.CommonHandler;
import com.angelatech.yeyelive.thirdShare.FbShare;
import com.angelatech.yeyelive.thirdShare.QqShare;
import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.thirdShare.SinaShare;
import com.angelatech.yeyelive.thirdShare.WxShare;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.Utility;
import com.will.common.log.DebugLogs;
import com.will.common.tool.time.DateTimeTool;
import com.will.view.ToastUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * User: cbl
 * Date: 2016/8/4
 * Time: 18:11
 * 截屏
 */
public class RoomScreenshotsDialogFragment extends DialogFragment implements View.OnClickListener {

    private View view;
    private Context context;
    private WxShare wxShare;

    private ImageView img_body, btn_close, btn_WXSceneSession, btn_WXSceneTimeline;
    private Bitmap image;

    private static final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        view = inflater.inflate(R.layout.dialog_room_screenshots, container, false);
        initView();
        setView();
        return view;
    }

    public RoomScreenshotsDialogFragment(Context mcontext,  Bitmap img) {
        this.context = mcontext;
        image = img;

        saveImage(img);

    }


    private void saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "yeye/camera");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    private void saveFile(Bitmap bm, String fileName, String path) throws IOException {
        String subForder = SAVE_PIC_PATH + path;
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(foder);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {
        img_body = (ImageView) view.findViewById(R.id.img_body);
        btn_close = (ImageView) view.findViewById(R.id.btn_close);
        btn_WXSceneSession = (ImageView) view.findViewById(R.id.btn_WXSceneSession);
        btn_WXSceneTimeline = (ImageView) view.findViewById(R.id.btn_WXSceneTimeline);
    }

    private void setView() {
        img_body.setImageBitmap(image);
        btn_close.setOnClickListener(this);
        btn_WXSceneSession.setOnClickListener(this);
        btn_WXSceneTimeline.setOnClickListener(this);
        wxShare = new WxShare(getActivity(), shareListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_WXSceneSession:
                wxShare.shareImage(image, "", 0);
                break;
            case R.id.btn_WXSceneTimeline:
                wxShare.shareImage(image, "", 1);
                break;
        }
    }

    /**
     * 分享 回调
     */
    private ShareListener shareListener = new ShareListener() {
        @Override
        public void callBackSuccess(int shareType) {
            switch (shareType) {

                case WxShare.SHARE_TYPE_WX:
                    dismiss();
                    break;
            }
        }

        @Override
        public void callbackError(int shareType) {
            switch (shareType) {
                case WxShare.SHARE_TYPE_WX:
                    break;
            }
        }

        @Override
        public void callbackCancel(int shareType) {
            switch (shareType) {
                case WxShare.SHARE_TYPE_WX:
                    break;
            }
        }
    };
}
