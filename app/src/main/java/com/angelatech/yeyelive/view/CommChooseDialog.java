package com.angelatech.yeyelive.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;

/**
 * User: cbl
 * Date: 2016/8/8
 * Time: 16:26
 * 带选择的 对话框
 */
public class CommChooseDialog {
    private AlertDialog dialog;
    private boolean isChoose = true;

    public interface Callback {
        void onCancel();

        void onOK(boolean choose);
    }

    private Callback mCallback;

    /**
     * 通用dialog
     *
     * @param context 上下文
     * @param content 内容
     * @param NotOk   是否需要取消按钮 false 不需要 true 需要
     */
    public void dialog(final Context context, String content, Boolean NotOk, boolean isLiveUser, Callback callback, BasicUserInfoDBModel model) {
        dialog = new AlertDialog.Builder(context).create();
        mCallback = callback;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//指定会全局,可以在后台弹出
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        window.setContentView(R.layout.dialog_end_live);
        TextView tv_content = (TextView) window.findViewById(R.id.content);
        LinearLayout layout_save = (LinearLayout) window.findViewById(R.id.layout_save);
        Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
        final TextView tv_choose = (TextView) window.findViewById(R.id.tv_choose);
        final TextView tips = (TextView) window.findViewById(R.id.tips);
        final ImageView iv_choose = (ImageView) window.findViewById(R.id.iv_choose_save);
        if (model.isv.equals("1")){
            tips.setText(String.format(context.getString(R.string.tops_video),"50"));
        }else{
            tips.setText(String.format(context.getString(R.string.tops_video),"10"));
        }
        if (!isLiveUser){
            tips.setVisibility(View.GONE);
            layout_save.setVisibility(View.GONE);
        }else {
            tips.setVisibility(View.VISIBLE);
            layout_save.setVisibility(View.VISIBLE);
        }
        iv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChoose) {
                    isChoose = false;
                    iv_choose.setImageResource(R.drawable.icon_pub_choose_n);
                    tv_choose.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
                } else {
                    isChoose = true;
                    iv_choose.setImageResource(R.drawable.icon_pub_choose_s);
                    tv_choose.setTextColor(ContextCompat.getColor(context, R.color.color_666666));
                }
            }
        });
        btn_cancel.setVisibility(View.GONE);
        tv_content.setText(content);
        if (NotOk) {
            btn_cancel.setVisibility(View.VISIBLE);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onCancel();
                    }
                    dialog.dismiss();
                }
            });
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onOK(isChoose);
                }
                dialog.dismiss();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mCallback != null) {
                        mCallback.onCancel();
                    }
                }
                return false;
            }
        });
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
    public void dialog(final Context context, String content, Boolean NotOk, Callback callback, String StrOk, String StrCancel, BasicUserInfoDBModel model) {
        dialog = new AlertDialog.Builder(context).create();
        mCallback = callback;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        window.setContentView(R.layout.dialog_end_live);
        TextView tv_content = (TextView) window.findViewById(R.id.content);
        Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
        final TextView tv_choose = (TextView) window.findViewById(R.id.tv_choose);
        final ImageView iv_choose = (ImageView) window.findViewById(R.id.iv_choose_save);
        final TextView tips = (TextView) window.findViewById(R.id.tips);
        if (model.isv.equals("1")){
            tips.setText(String.format(context.getString(R.string.tops_video),"50"));
        }else{
            tips.setText(String.format(context.getString(R.string.tops_video),"10"));
        }
        iv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChoose) {
                    isChoose = false;
                    iv_choose.setImageResource(R.drawable.icon_pub_choose_n);
                    tv_choose.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
                } else {
                    isChoose = true;
                    iv_choose.setImageResource(R.drawable.icon_pub_choose_s);
                    tv_choose.setTextColor(ContextCompat.getColor(context, R.color.color_666666));
                }
            }
        });
        btn_ok.setText(StrOk);
        btn_cancel.setText(StrCancel);
        btn_cancel.setVisibility(View.GONE);
        tv_content.setText(content);
        if (NotOk) {
            btn_cancel.setVisibility(View.VISIBLE);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onCancel();
                    dialog.dismiss();
                }
            });
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onOK(isChoose);
                dialog.dismiss();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mCallback != null) {
                        mCallback.onCancel();
                    }
                }
                return false;
            }
        });
    }

    /**
     * 通用dialog
     *
     * @param context 上下文
     * @param content 内容
     * @param NotOk   是否需要取消按钮 false 不需要 true 需要
     */
    public void dialog(final Context context, String content, Boolean NotOk) {
        dialog = new AlertDialog.Builder(context).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        window.setContentView(R.layout.dialog_end_live);
        TextView tv_content = (TextView) window.findViewById(R.id.content);
        Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.GONE);
        tv_content.setText(content);
        final TextView tv_choose = (TextView) window.findViewById(R.id.tv_choose);
        final ImageView iv_choose = (ImageView) window.findViewById(R.id.iv_choose_save);

        iv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChoose) {
                    isChoose = false;
                    iv_choose.setImageResource(R.drawable.icon_pub_choose_n);
                    tv_choose.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
                } else {
                    isChoose = true;
                    iv_choose.setImageResource(R.drawable.icon_pub_choose_s);
                    tv_choose.setTextColor(ContextCompat.getColor(context, R.color.color_666666));
                }
            }
        });
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
                    if (mCallback != null) {
                        mCallback.onCancel();
                    }
                }
                return false;
            }
        });
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
