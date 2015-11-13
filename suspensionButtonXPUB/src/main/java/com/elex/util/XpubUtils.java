package com.elex.util;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;


import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.elex.CallBackFunciton;
import com.elex.SuspensionButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class XpubUtils {

public static String getDeiviceId(Context context) {
	String deviceID = "";
	try {
		// Requires READ_PHONE_STATE
		TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		// gets the imei (GSM) or MEID/ESN (CDMA)
		deviceID = phoneManager.getDeviceId();
		if(deviceID == null || deviceID.length() == 0) {
			// requires ACCESS_WIFI_STATE
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			// gets the MAC address
			if (wm.isWifiEnabled()) {
				deviceID = wm.getConnectionInfo().getMacAddress();
				Log.d(SuspensionButton.TAG,"getMacAddress is " + deviceID);
			}
		} else {
			Log.d(SuspensionButton.TAG,"getDeviceId is " + deviceID);
			if(deviceID.equals("111111111111111") || deviceID.equals("000000000000000")  || deviceID.equals("0")  || deviceID.equals("1")) {
				Log.d(SuspensionButton.TAG,"deviceID.equals(\"111111111111111\") || deviceID.equals(\"000000000000000\")  || deviceID.equals(\"0\")  || deviceID.equals(\"1\")");
				String deviceIdCache = getValue("deviceID",context);
				if(deviceIdCache != null && deviceIdCache.length() > 0) {
					deviceID = deviceIdCache;
					Log.d(SuspensionButton.TAG,"getDeviceId(from cache) is " + deviceID);
				}
			} else {
				setValue("deviceID", deviceID,context);
			}
		}
	} catch (Exception e) {
		Log.e(SuspensionButton.TAG,"getDeviceId is fail, " + e.getMessage());
	}
	return deviceID + "uuid";
}

public static void setValue(String key, String value,Context context) {
	try {
		Settings.System.putString(context.getContentResolver(), key, value);
		Log.d(SuspensionButton.TAG,"[setValue] " + key + "=" + value);
	} catch (Exception e) {
		Log.e(SuspensionButton.TAG,"setValue is fail, " + e.getMessage());
	}
}

public static String getValue(String key,Context context) {
	try {
		String value = Settings.System.getString(context.getContentResolver(), key);
		Log.d(SuspensionButton.TAG,"[getValue] " + key + "=" + value);
		return value;
	} catch (Exception e) {
		Log.e(SuspensionButton.TAG,"getValue is fail, " + e.getMessage());
	}
	return null;
}
//wx get system Date
@SuppressLint("SimpleDateFormat")
public static String getCurrentDay(){
	String date="";
	try{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	date=sdf.format(new java.util.Date());
	}catch(Exception e){
		Log.e(SuspensionButton.TAG, "XpubUtils GETCURRENTDATE");
		date="12";
	}
	return date;
}

public static void generateShortUrlAsync(String xpub_uid,final CallBackFunciton onGetShortUrl,CallBackFunciton onError){
	JSONObject obj = new JSONObject();
	try {
		obj.put("xpub_user_id", xpub_uid);		
	} catch (JSONException e) {
		e.printStackTrace();
	}
	Branch branch = SuspensionButton.getInstance().getBranchInstance();
	if(branch == null) return; 
	branch.getShortUrl(obj,new Branch.BranchLinkCreateListener() {
		@Override
		public void onLinkCreate(String url, BranchError error) {
			Log.i(SuspensionButton.TAG, "Ready to share my link = " + url);
			onGetShortUrl.call(url);
		}
	});
}

public static boolean isVersionAndLarger(String verA,String verB){
	if(verA.equals(verB)) return true;
	String[] verAary=verA.split(".");
	String[] verBary=verB.split(".");
	//TODO 判断版本号大于等于3位并且格式正确。每一位都是数字。
	
	
	int countMax = Math.max(verAary.length,verBary.length);
	for(int i=0;i<countMax;i++){
		if(Integer.parseInt(verAary[i]) > Integer.parseInt(verBary[i])){
			return true;
		}
	}
	return false;
}

}
