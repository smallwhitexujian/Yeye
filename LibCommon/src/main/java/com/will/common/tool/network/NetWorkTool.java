package com.will.common.tool.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

public class NetWorkTool {
	
	private final static String TAG = "NetWorkUtil";
	
	public static final int TYPE_OF_NO_PASS = 1;//WIFICIPHER_NOPASS
	public static final int TYPE_OF_WEP = 2;//WIFICIPHER_WEP
	public static final int TYPE_OF_WPA = 3;//WIFICIPHER_WPA

	
	public static void ctlWifi(Context context){
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
		else{
			mWifiManager.setWifiEnabled(true);
		}
	}
	
	//true:open ; false:close
	public static void ctlWifi(Context context,boolean tag) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (mWifiManager.isWifiEnabled()) {
			if(!tag){
				mWifiManager.setWifiEnabled(false);
			}
		}
		else{
			if(tag){
				mWifiManager.setWifiEnabled(true);
			}
		}
	}
	
	//检查当前Wifi网卡状态
	public static int checkWifiState(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (mWifiManager.getWifiState() == 0) {
			Log.i(TAG, "网卡正在关闭");
			return 0;
		} else if (mWifiManager.getWifiState() == 1) {
			Log.i(TAG, "网卡已经关闭");
			return 1;
		} else if (mWifiManager.getWifiState() == 2) {
			Log.i(TAG, "网卡正在打开");
			return 2;
		} else if (mWifiManager.getWifiState() == 3) {
			Log.i(TAG, "网卡已经打开");
			return 3;
		} else {
			Log.i(TAG, "没有获取到状态");
			return 4;
		}
	}

	/**
	 * 扫描周边网络
	 */
	public static List<ScanResult> scanWifi(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiManager.startScan();
		List<ScanResult> listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");
		} else {
			Log.i(TAG, "当前区域没有无线网络");
		}
		return listResult;
	}

	/**
	 * 得到扫描结果
	 */
	public static String getScanResult(Context context) {
		// 每次点击扫描之前清空上一次的扫描结果
		StringBuffer mStringBuffer = new StringBuffer();
		ScanResult mScanResult = null;
		// 开始扫描网络
		List<ScanResult> listResult = scanWifi(context);
		if (listResult != null) {
			for (int i = 0; i < listResult.size(); i++) {
				mScanResult = listResult.get(i);
				mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
						.append(" :").append(mScanResult.SSID).append("->")
						.append(mScanResult.BSSID).append("->")
						.append(mScanResult.capabilities).append("->")
						.append(mScanResult.frequency).append("->")
						.append(mScanResult.level).append("->")
						.append(mScanResult.describeContents()).append("\n\n");
			}
		}
		Log.i(TAG, mStringBuffer.toString());
		return mStringBuffer.toString();
	}

	/**
	 * 断开当前连接的网络
	 */
	public static void disconnectWifi(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int netId = getNetworkId(context);
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
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

	/**
	 * 得到连接的ID
	 */
	public static int getNetworkId(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return (wifiInfo == null) ? 0 : wifiInfo.getNetworkId();
	}

	/**
	 * 得到IP地址
	 */
	public static int getIPAddress(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return (wifiInfo == null) ? 0 : wifiInfo.getIpAddress();
	}

	// 指定配置好的网络进行连接
	public static boolean connectConfiguration(Context context,int index) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
		if (index >= list.size()) {
			return false;
		}
		mWifiManager.disconnect();
		mWifiManager.enableNetwork(list.get(index).networkId,true);
		return mWifiManager.reconnect();
	}
	
	public static boolean connect(Context context,String networkSSID) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration i : list) {
			if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
				mWifiManager.disconnect();
				mWifiManager.enableNetwork(i.networkId, true);
				return mWifiManager.reconnect();
			}
		}
		return false;
	}
	
	// 得到MAC地址
	public static String getMacAddress(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return (wifiInfo == null) ? "NULL" : wifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public static String getBSSID(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return (wifiInfo == null) ? "NULL" : wifiInfo.getBSSID();
	}

	// 得到WifiInfo的所有信息包
	public static String getWifiInfo(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return (wifiInfo == null) ? "NULL" : wifiInfo.toString();
	}
	
	public static WifiConfiguration createWifiInfo(Context context,String SSID, String password, int Type){
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		WifiConfiguration tempConfig = isExsits(mWifiManager,context,SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}
		if (Type == TYPE_OF_NO_PASS) {// WIFICIPHER_NOPASS
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == TYPE_OF_WEP) {// WIFICIPHER_WEP
//			config.hiddenSSID = true;
//			config.status = WifiConfiguration.Status.DISABLED;     
//			config.priority = 40;
//			config.wepKeys[0] = "\"" + Password + "\"";
//			config.wepTxKeyIndex = 0;
//			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//		    config.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
//		    config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//		    config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//		    config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//		    config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//		    config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//		    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//		    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

			//if your password is in hex, you do not need to surround it by quotes
			config.hiddenSSID = true;
			if(Pattern.matches("[0-9a-fA-F]+", password) && password.length()%2 == 0){
				config.wepKeys[0] =  password ;
				System.out.println("qiang ma dan");
			}
			else{
				config.wepKeys[0] = "\"" + password + "\"";
			}
			config.wepTxKeyIndex = 0;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		}
		if (Type == TYPE_OF_WPA) { // WIFICIPHER_WPA
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		mWifiManager.addNetwork(config);
		return config;
    }   

	private static WifiConfiguration isExsits(WifiManager mWifiManager,Context context,String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs){
			if (existingConfig.SSID.equals("\"" + SSID + "\"")){
				return existingConfig;
			}
		}
		return null;
	}
	
	
	//数据流量开关
	public static void toggleMobileData(Context ctx, boolean enabled) {
	    ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    Class<?> conMgrClass = null; // ConnectivityManager类
	    Field iConMgrField = null; // ConnectivityManager类中的字段
	    Object iConMgr = null; // IConnectivityManager类的引用
	    Class<?> iConMgrClass = null; // IConnectivityManager类 
	    Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法  
	    try {
	        // 取得ConnectivityManager类 
		    conMgrClass = Class.forName(conMgr.getClass().getName()); 
		    // 取得ConnectivityManager类中的对象mService 
		    iConMgrField = conMgrClass.getDeclaredField("mService");
		    // 设置mService可访问
		        iConMgrField.setAccessible(true);
		    // 取得mService的实例化类IConnectivityManager
		    iConMgr = iConMgrField.get(conMgr);
		    // 取得IConnectivityManager类 
		    iConMgrClass = Class.forName(iConMgr.getClass().getName());
		    // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法 
		    setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE); 
		    // 设置setMobileDataEnabled方法可访问
		    setMobileDataEnabledMethod.setAccessible(true);
		    // 调用setMobileDataEnabled方法 
		    setMobileDataEnabledMethod.invoke(iConMgr, enabled);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (NoSuchFieldException e){
	        e.printStackTrace();
	    } catch (SecurityException e) {
	        e.printStackTrace();
	    } catch (NoSuchMethodException e){
	        e.printStackTrace();
	    } catch (IllegalArgumentException e){
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();    
	    }
	}  
	
}
