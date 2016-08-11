package org.cocos2dx.lib.util;

import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxHandler;
import org.cocos2dx.lib.Cocos2dxHelper;
import org.cocos2dx.lib.Cocos2dxHelper.Cocos2dxHelperListener;
import org.cocos2dx.lib.Cocos2dxRenderer;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class Cocos2dxView {

	private  Activity mActivity;
	private  Cocos2dxHelperListener mCocos2dxHelperListener;
	private  Cocos2dxGLSurfaceView mCocos2dxGLSurfaceView;
	private  FrameLayout mFrameLayout;
	private  volatile boolean isInit = false;
	private  Handler mHandler;
	private  int mAlphaSize = 255;


	/**
	 * 设置透明度
	 * @param alphaSize
	 * alphaSize:0-255 0:透明，255:完全不透明
	 */
	private void setAlpha(int alphaSize){
		mCocos2dxGLSurfaceView.setZOrderOnTop(true);
		mCocos2dxGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mCocos2dxGLSurfaceView.setEGLConfigChooser(8,8,8,8,16,alphaSize);

	}

	public  void onCreate(Activity a,int alphaSize){
		if(!isInit){
			initStart(a,alphaSize);
			initSetNativeLibraries();
			initEnd();
			isInit = true;
		}
	}

	//设置参数
	protected  void initStart(Activity activity,int alphaSize){
		mActivity = activity;
		mAlphaSize = alphaSize;
	}
	//加载so
	protected  void initSetNativeLibraries() {
		try {
			ApplicationInfo ai = mActivity.getPackageManager().getApplicationInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			String libName = bundle.getString("android.app.lib_name");
			System.loadLibrary(libName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	protected  void initEnd(){
		Cocos2dxHelper.init(mActivity,mCocos2dxHelperListener);
		mCocos2dxGLSurfaceView = new Cocos2dxGLSurfaceView(mActivity);
		setAlpha(mAlphaSize);
		mCocos2dxGLSurfaceView.setCocos2dxRenderer(new Cocos2dxRenderer());
		mHandler = new Handler();
		ViewGroup.LayoutParams framelayout_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		mFrameLayout = new FrameLayout(mActivity);
		mFrameLayout.setLayoutParams(framelayout_params);
		mFrameLayout.addView(mCocos2dxGLSurfaceView);
	}


	public  void setScaleInfo(boolean nomal,int HeightRatio,int WidthRatio,int mBase){
		mCocos2dxGLSurfaceView.setScaleInfo(nomal, HeightRatio, WidthRatio, mBase);
	}


	public  FrameLayout getFrameLayout(){
		if(!isInit){
			throw new RuntimeException();
		}
		return mFrameLayout;
	}

	public  void updateView(final Runnable r){
		if(!isInit){
			throw new RuntimeException();
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mCocos2dxGLSurfaceView.queueEvent(r);
			}
		}, 100);
	}

	public  void invalidate(Runnable r){
		mCocos2dxGLSurfaceView.queueEvent(r);
	}

	public  void onResume(){
		if(!isInit){
			throw new RuntimeException();
		}
		mCocos2dxGLSurfaceView.onResume();
	}

	public  void onPause(){
		if(!isInit){
			throw new RuntimeException();
		}
		mCocos2dxGLSurfaceView.onPause();
	}
	
}
