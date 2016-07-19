package com.will.common.tool;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.will.common.string.security.Md5;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class DeviceTool {

	public static final int METRIC_BYTE = 0;
	public static final int METRIC_KB = 1;
	public static final int METRIC_MB = 2;


	//get IMEI
	public static String getIMEI(Context ctx){
		String imei = null;
		TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();
		return imei;
	}

	public static String getUniqueID(Context ctx){
		try{
			TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId(); //IMEI码

			//这个同一个厂商同样设备同样的rom下会重复
			//we make this look like a valid IMEI
			String deviceId = "35" +
					Build.BOARD.length() % 10 + Build.BRAND.length() % 10
					+ Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
					+ Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
					+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
					+ Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
					+ Build.TAGS.length() % 10 + Build.TYPE.length() % 10
					+ Build.USER.length() % 10; // 13 digits

			WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
			String wlanMac = wm.getConnectionInfo().getMacAddress();
			String uniqueID = Md5.md5(imei+deviceId+wlanMac);
			return uniqueID;
		}catch (Exception e){
			e.printStackTrace();
		}
		return "";

	}


	// get network status
	public static boolean isConnectNet(Context ctx) {
		ConnectivityManager connectivityManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}
		NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
		return !(networkinfo == null || !networkinfo.isAvailable());
	}


	public static int isScreenChange(Context ctx) {
		Configuration mConfiguration = ctx.getResources().getConfiguration();
		int ori = mConfiguration.orientation;
		return ori;
	}

	public static boolean screenLocked(Context ctx) {
		KeyguardManager mKeyguardManager = (KeyguardManager) ctx
				.getSystemService(Context.KEYGUARD_SERVICE);
		boolean status = mKeyguardManager.inKeyguardRestrictedInputMode();
		return status;
	}
	public static boolean haveSdcard(){
		return (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED));
	}

	public long getSDFreeSize(int metric) {
		if(haveSdcard()){
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			long blockSize = sf.getBlockSize();
			long freeBlocks = sf.getAvailableBlocks();
			switch (metric) {
				case METRIC_BYTE:
					return freeBlocks * blockSize;
				case METRIC_KB:
					return (freeBlocks * blockSize) / 1024;
				case METRIC_MB:
					return (freeBlocks * blockSize) / 1024 / 1024;
			}
		}
		return -1;
	}


	public static DisplayMetrics getScreenInfo(Context ctx){
		return ctx.getResources().getDisplayMetrics();
	}


	public static String[] StorageList(Context ctx) {
		String[] paths = null;
		StorageManager mStorageManager = (StorageManager)ctx.getSystemService(Activity.STORAGE_SERVICE);
		try {
			Method mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
			paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
			for(String path:paths){
				System.out.println("qiang "+path);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return paths;
	}


	//获取语言
	public static Locale[] getSystemLanguageList(){
		//获取Android系统上的语言列表
		Locale mLanguagelist[] = Locale.getAvailableLocales();
		return mLanguagelist;
	}


	//获取系统当前使用的语言
	public static String getCurrentLauguage(){
		String mCurrentLanguage = Locale.getDefault().getLanguage();
		return mCurrentLanguage;
	}


	/**
	 * 获得当前系统语言
	 */
	public static String getCurrentLauguageUseResources(Context context){
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();// 获得语言码
		return language;
	}


}
