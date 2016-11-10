package com.angelatech.yeyelive.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.thirdShare.ShareListener;
import com.angelatech.yeyelive.thirdShare.WxShare;

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
@SuppressLint("ValidFragment")
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

    public RoomScreenshotsDialogFragment(Context mcontext, Bitmap img) {
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
            if(!bmp.isRecycled()){
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
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
                if (image != null) {
                    image.recycle();
                    image = null;
                }
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
                    if (image != null) {
                        image.recycle();
                        image = null;
                    }
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
