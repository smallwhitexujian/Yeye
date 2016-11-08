package com.will.common.tool.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

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

	/**
	 * 获得屏幕高度
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}
	/**
	 * 获取当前屏幕截图，包含状态栏
	 *
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 *
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 判断是否有虚拟按键
	 * @param context
	 * @return
	 */
	public static boolean checkDeviceHasNavigationBar(Context context) {
		boolean hasNavigationBar = false;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasNavigationBar;
	}

	/**
	 * 获取NavigationBar的高度：
	 */
	public static int getNavigationBarHeight(Context context) {
		int navigationBarHeight = 0;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
		if (id > 0 && checkDeviceHasNavigationBar(context)) {
			navigationBarHeight = rs.getDimensionPixelSize(id);
		}
		return navigationBarHeight;
	}

	public static void toggleHideyBar(Activity activity) {
		// The UI options currently enabled are represented by a bitfield.
		// getSystemUiVisibility() gives us that bitfield.
		int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
		// Navigation bar hiding:  Backwards compatible to ICS.
		if (Build.VERSION.SDK_INT >= 14) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
			newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		}

		// Status bar hiding: Backwards compatible to Jellybean
//		if (Build.VERSION.SDK_INT >= 16) {
//			newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
//		}

		if (Build.VERSION.SDK_INT >= 18) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}
		activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
	}
	
	
	
	
}  