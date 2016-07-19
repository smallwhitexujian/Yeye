package com.will.common.tool.view;


import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DisplayTool {

	public static int px2dp(Context context, float pxValue) {
		float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,pxValue, context.getResources().getDisplayMetrics());
		return (int) (value + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,dipValue, context.getResources().getDisplayMetrics());
		return (int) (value + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,pxValue, context.getResources().getDisplayMetrics());
		return (int) (value+ 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,spValue, context.getResources().getDisplayMetrics());
		return (int) (value + 0.5f);
	}
	
	//
	public static int px2sp_(Context context,float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;  
      return (int) (pxValue / scale + 0.5f);
	}
	
	public static int dp2px(Context context,float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return  (int) (pxValue *scale + 0.5f);
	}
	
	
	public static DisplayMetrics getDisplayMetrics(Context context){
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics;
	}
	
	
	
	
	
}  