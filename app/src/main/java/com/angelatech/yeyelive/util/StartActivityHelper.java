package com.angelatech.yeyelive.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.angelatech.yeyelive.TransactionValues;

import java.io.Serializable;

/**
 * Created by jjfly on 16-3-7.
 */
public class StartActivityHelper {



    public static void jumpActivityDefault(Context context,Class<? extends Activity> activityClass){
        Intent intent = new Intent(context,activityClass);
        context.startActivity(intent);
    }

    public static void jumpActivityForResult(Activity context, Class<? extends Activity> activityClass, int requestCode) {
        Intent intent = new Intent(context, activityClass);
        context.startActivityForResult(intent, requestCode);
    }

    //对外功能区域
    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> void jumpActivity(Context context, Class<? extends Activity> activityClass, T model) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(TransactionValues.UI_2_UI_KEY_OBJECT,model);
        context.startActivity(intent);
    }

    //对外功能区域
    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> void jumpActivity(Context context,int flag, String action, Class<? extends Activity> activityClass, T model) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(TransactionValues.UI_2_UI_KEY_OBJECT,model);
        intent.setAction(action);

        intent.setFlags(flag);
        context.startActivity(intent);
    }
    //对外功能区域
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> void jumpActivity(Context context, Class<? extends Activity> activityClass, T model) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(TransactionValues.UI_2_UI_KEY_OBJECT,model);
        context.startActivity(intent);
    }


    //获取单个参数的Parcelable
    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> T getTransactionParcelable_1(Activity activity){
        return activity.getIntent().getParcelableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
    }

    //获取单个参数的Serializable
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T getTransactionSerializable_1(Activity activity){
        return (T) activity.getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
    }

}
