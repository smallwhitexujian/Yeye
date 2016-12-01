package com.angelatech.yeyelive.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.ProductActivity;
import com.angelatech.yeyelive.util.StartActivityHelper;

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

    /**
     * 通用dialog
     *
     * @param context 上下文
     */
    public void CommDialog(final Context context) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
            dialog.show();
            Window window = dialog.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setGravity(Gravity.CENTER);
            window.setContentView(R.layout.dialog_tip);
            Button btn_go = (Button) window.findViewById(R.id.btn_go);
            btn_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartActivityHelper.jumpActivityDefault(context, ProductActivity.class);
                    dialog.dismiss();
                }
            });
        }
    }
}
