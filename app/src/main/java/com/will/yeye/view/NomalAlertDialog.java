package com.will.yeye.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by jjfly on 16-3-7.
 *
 */
public class NomalAlertDialog {

    private static Dialog dialog;

    public static void cancelableShow(final Context context,String title,String msg,String strBtnOk,String strBtnCancel,final HandlerDialog handlerDialog){
        dialog = new AlertDialog.Builder(context).setTitle(title)// 设置标题
                .setMessage(msg)// 设置内容
                .setPositiveButton(strBtnOk,// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 点击“确定”转向网络设置界面
                                if(handlerDialog != null){
                                    handlerDialog.handleOk();
                                }
                            }
                        })
                .setNeutralButton(strBtnCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        if(handlerDialog != null){
                            handlerDialog.handleCancel();
                        }
                    }
                }).create();// 创建按钮
        // 显示对话框
        dialog.show();
    }

    public static void alwaysShow(final Context context,String title,String msg,String strBtnOk,String strBtnCancel,final HandlerDialog handlerDialog){
        dialog = new AlertDialog.Builder(context).setTitle(title)// 设置标题
                .setMessage(msg)// 设置内容
                .setPositiveButton(strBtnOk,// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 点击“确定”转向网络设置界面
                                if(handlerDialog != null){
                                    handlerDialog.handleOk();
                                }
                            }
                        })
                .setNeutralButton(strBtnCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        if(handlerDialog != null){
                            handlerDialog.handleCancel();
                        }
                    }
                }).create();// 创建按钮
        // 显示对话框
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void alwaysShow2(final Context context,String title,String msg,String strBtnOk,final HandlerDialog handlerDialog){
        dialog = new AlertDialog.Builder(context).setTitle(title)// 设置标题
                .setMessage(msg)// 设置内容
                .setPositiveButton(strBtnOk,// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 点击“确定”转向网络设置界面
                                if(handlerDialog != null){
                                    handlerDialog.handleOk();
                                }
                            }
                        }).create();// 创建按钮
        // 显示对话框
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }



    public static void dismiss(){
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }


    public interface HandlerDialog{
        void handleOk();
        void handleCancel();

    }

}
