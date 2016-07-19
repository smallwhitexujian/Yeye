package com.will.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author xujian
 *         消息提示toast
 */
public class ToastUtils {
    public static final int TOAST_DURATION = 3000;

    public static void showToast(Context context, int resId) {
        showToast2(context, context.getString(resId));
    }

    public static void showToast(Context context, String tips) {
        if (null == tips || TextUtils.isEmpty(tips.trim())) {
            return;
        }
        showToast2(context, tips);
    }

    public static void showToast(Context context, int resId, int time) {
        String toastString = context.getString(resId);
        Toast mToast = Toast.makeText(context, "", time);
        mToast.setText(toastString);
        mToast.show();
    }

    public static void showToast(Context context, String str, int time) {
        Toast mToast = Toast.makeText(context, "", time);
        mToast.setText(str);
        mToast.show();
    }

    public static void showToast2(Context context, String toastString) {
        Toast mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mToast.setText(toastString);
        mToast.show();
    }

    public static void showToastCenter(Context context, String toastString) {
        Toast mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mToast.setText(toastString);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    /**
     * 带图片的toast提示
     *
     * @param context
     * @param ImageResourceId
     */
    public static void ImageToast(Context context, int ImageResourceId) {
        //创建带图片Toast提示消息   

        Toast mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        //设置Toast提示消息在屏幕上的位于中间  
        mToast.setGravity(Gravity.CENTER, 0, 0);
        //获取Toast提示消息里原有的View   
//      View toastView = mToast.getView();  
        //创建图像ImageView   
        ImageView img = new ImageView(context);
        img.setImageResource(ImageResourceId);
        //创建一个对象LineLayout容器   
        LinearLayout ll = new LinearLayout(context);
        //向LinearLayout中添加ImageView和Toast原有的View   
        ll.addView(img);
//      ll.addView(toastView);  
        mToast.setGravity(Gravity.CENTER, 0, 0);
        //将LineLayout容器设置为toast的View   
        mToast.setView(ll);
        //显示消息   
        mToast.show();
    }
}
