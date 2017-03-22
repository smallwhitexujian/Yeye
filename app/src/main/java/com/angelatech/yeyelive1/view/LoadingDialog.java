package com.angelatech.yeyelive1.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive1.R;

/**
 * @author xujian 加载进度条
 */
@SuppressLint("NewApi")
public class LoadingDialog {
    private static AnimationDrawable animationDrawable = null;
    public static AlertDialog loadingDialog = null;

    /**
     * 自定义帧动画加载弹窗
     *
     * @param con
     * @param title
     * es
     */
    public static void showLoadingDialog(Context con, String title) {
        try{
            if (loadingDialog != null) {
                Window window = loadingDialog.getWindow();
                TextView dTitle = (TextView) window.findViewById(R.id.dialog_loading_title);
                if (title != null) {
                    dTitle.setText(title);
                }
                return;
            }
            loadingDialog = new AlertDialog.Builder(con).create();
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// set background was transparent
            loadingDialog.getWindow().setDimAmount(0f);// 设置弹框遮盖层隐藏
            loadingDialog.setCanceledOnTouchOutside(false);
            if (!loadingDialog.isShowing()) {
                loadingDialog.show();
            }
            if (loadingDialog !=null){
                Window window = loadingDialog.getWindow();
                window.setGravity(Gravity.CENTER);// 居顶显示
                window.setContentView(R.layout.dialog_loading);
                ImageView manView = (ImageView) window.findViewById(R.id.dialog_loading_iamge);
                TextView dTitle = (TextView) window.findViewById(R.id.dialog_loading_title);
                if (title != null) {
                    dTitle.setText(title);
                }
                manView.setImageResource(R.drawable.dialog_loading_animations);
                animationDrawable = (AnimationDrawable) manView.getDrawable();
                animationDrawable.start();

                loadingDialog.setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (animationDrawable.isRunning()) {
                            animationDrawable.stop();
                        }
                    }
                });

                loadingDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (animationDrawable.isRunning()) {
                            animationDrawable.stop();
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 模拟系统加载框
     *
     * @param con
     * @param title
     */
    public static void showSysLoadingDialog(Context con, String title) {
        if (loadingDialog != null) {
            Window window = loadingDialog.getWindow();
            TextView dTitle = (TextView) window.findViewById(R.id.dialog_loading_title);
            if (title != null) {
                dTitle.setText(title);
            }
            return;
        }
        loadingDialog = new AlertDialog.Builder(con).create();
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// set background was transparent
        loadingDialog.getWindow().setDimAmount(0.5f);// 设置弹框遮盖层隐藏
        loadingDialog.show();
        Window window = loadingDialog.getWindow();
        window.setGravity(Gravity.CENTER);// 居顶显示
        window.setContentView(R.layout.dialog_progress);
        TextView dTitle = (TextView) window.findViewById(R.id.dialog_loading_title);
        if (title != null) {
            dTitle.setText(title);
        }
    }


    public static void showLoadingDialog(Context con) {
        showSysLoadingDialog(con, null);
    }

    public static void cancelLoadingDialog() {
        try {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        } catch (Exception e) {
        }
    }
}
