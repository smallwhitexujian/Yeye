package com.angelatech.yeyelive1.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.angelatech.yeyelive1.R;

/**
 * Created by xujian on 15/11/27.
 * 通用dialog
 */
public class CommDialog {
    private AlertDialog dialog = null;

    public interface Callback {
        void onCancel();

        void onOK();
    }

    private Callback mcallback;

    /**
     * 通用dialog
     *
     * @param context 上下文
     * @param content 内容
     * @param NotOk   是否需要取消按钮 false 不需要 true 需要
     */
    public void CommDialog(Context context, String content, Boolean NotOk, Callback callback) {
        if (content == null || context == null) {
            return;
        }
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            mcallback = callback;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
//            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);//指定会全局,可以在后台弹出
            dialog.show();
            Window window = dialog.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setGravity(Gravity.CENTER);
            window.setContentView(R.layout.dialog_othermic);
            TextView tv_content = (TextView) window.findViewById(R.id.content);
            Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
            Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
            btn_cancel.setVisibility(View.GONE);
            tv_content.setText(content);
            if (NotOk) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mcallback != null) {
                            mcallback.onCancel();
                        }
                        dialog.dismiss();
                    }
                });
            }
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mcallback != null) {
                        mcallback.onOK();
                    }
                    dialog.dismiss();
                }
            });
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mcallback != null) {
                            mcallback.onCancel();
                        }
                    }
                    return false;
                }
            });
        }
    }

    /**
     * 通用dialog
     *
     * @param context   上下文
     * @param content   内容
     * @param NotOk     是否需要取消按钮 false 不需要 true 需要
     * @param StrOk     确认按钮内容
     * @param StrCancel 取消按钮内容
     */
    public void CommDialog(Context context, String content, Boolean NotOk, Callback callback, String StrOk, String StrCancel) {
        if (content == null || context == null || StrOk == null || StrCancel == null) {
            return;
        }
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            mcallback = callback;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            dialog.show();
            Window window = dialog.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setGravity(Gravity.CENTER);
            window.setContentView(R.layout.dialog_othermic);
            TextView tv_content = (TextView) window.findViewById(R.id.content);
            Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
            Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
            btn_ok.setText(StrOk);
            btn_cancel.setText(StrCancel);
            btn_cancel.setVisibility(View.GONE);
            tv_content.setText(content);
            if (NotOk) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mcallback.onCancel();
                        dialog.dismiss();
                    }
                });
            }
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mcallback.onOK();
                    dialog.dismiss();
                }
            });
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mcallback != null) {
                            mcallback.onCancel();
                        }
                    }
                    return false;
                }
            });
        }
    }

    /**
     * 通用dialog
     *
     * @param context 上下文
     * @param content 内容
     * @param NotOk   是否需要取消按钮 false 不需要 true 需要
     */
    public void CommDialog(Context context, String content, Boolean NotOk) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            dialog.show();
            Window window = dialog.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setGravity(Gravity.CENTER);
            window.setContentView(R.layout.dialog_othermic);
            TextView tv_content = (TextView) window.findViewById(R.id.content);
            Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
            Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
            btn_cancel.setVisibility(View.GONE);
            tv_content.setText(content);
            if (NotOk) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mcallback != null) {
                            mcallback.onCancel();
                        }
                    }
                    return false;
                }
            });
        }
    }


    /**
     * 取消窗口
     */
    public void cancelDialog() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
