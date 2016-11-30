package com.angelatech.yeyelive.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.util.Utility;

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
 * 作者: Created by: xujian on Date: 2016/11/30.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.view
 */

public class DialogInputPwd {
    private AlertDialog dialog = null;
    private String str;

    public interface Callback {
        void onCancel();

        void onOK(String pwd);
    }

    private DialogInputPwd.Callback mcallback;

    /**
     * 通用dialog
     *
     * @param context 上下文
     * @param name    名称
     */
    public void CommDialog(final Context context, String name, DialogInputPwd.Callback callback) {
        if (name == null || context == null) {
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
            window.setContentView(R.layout.dialog_input_pwd);
            final StringBuilder builder = new StringBuilder();
            final ImageView tv_p1 = (ImageView) window.findViewById(R.id.tv_p1);
            final ImageView tv_p2 = (ImageView) window.findViewById(R.id.tv_p2);
            final ImageView tv_p3 = (ImageView) window.findViewById(R.id.tv_p3);
            final ImageView tv_p4 = (ImageView) window.findViewById(R.id.tv_p4);
            final ImageView tv_p5 = (ImageView) window.findViewById(R.id.tv_p5);
            final ImageView tv_p6 = (ImageView) window.findViewById(R.id.tv_p6);
            final EditText lock_password = (EditText) window.findViewById(R.id.lock_password);
            lock_password.setFocusable(true);
            final ImageView[] imageViews = new ImageView[]{tv_p1, tv_p2, tv_p3, tv_p4, tv_p5, tv_p6};
            TextView strName = (TextView) window.findViewById(R.id.name);
            Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
            Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
            strName.setText(context.getString(R.string.dialog_tips_1) + name + context.getString(R.string.dialog_tips_2));
            lock_password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() == 0) {
                        return;
                    }
                    if (builder.length() < 6) {
                        builder.append(s.toString());
                        setTextValue(context, builder, imageViews, lock_password);
                    }
                    s.delete(0, s.length());
                }
            });

            lock_password.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) {
                        delTextValue(builder, imageViews);
                        return true;
                    }
                    return false;
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mcallback != null) {
                        mcallback.onCancel();
                    }
                    dialog.dismiss();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mcallback != null) {
                        mcallback.onOK(str);
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

    //设置密码显示
    private void setTextValue(Context context, StringBuilder builder, ImageView[] imageViews, EditText editText) {
        try {
            str = builder.toString();
            int len = str.length();
            if (len <= 6 && len > 0) {
                imageViews[len - 1].setVisibility(View.VISIBLE);
            }
            if (len == 6) {//设置密码
                Utility.closeKeybord(editText, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除密码设置
    private void delTextValue(StringBuilder builder, ImageView[] imageViews) {
        str = builder.toString();
        int len = str.length();
        if (len == 0) {
            return;
        }
        if (len > 0 && len <= 6) {
            builder.delete(len - 1, len);
        }
        imageViews[len - 1].setVisibility(View.INVISIBLE);
    }

}
