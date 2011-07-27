package com.ttfp.netlog;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;

public class NetLogInfo implements Serializable {
	public String appName = "";
	public String packageName = "";
	public String versionName = "";
	public int versionCode = 0;
	
	public String phoneNumber = "";
	public String imsi = "";
	public String imei = "";
	public String deviceId = "";

	// 消息的版本
	public final String LOG_TYPE_STR = "str";
	public final String LOG_TYPE_JSON = "json";
	public final String LOG_TYPE_XML = "xml";
	
	public long updateTime = System.currentTimeMillis();
	public String log = "";
	public String logType = LOG_TYPE_JSON;
	public int logVersion = 1;
	
	public boolean hasSync = false;
	
	public NetLogInfo(Context c, String msg) {
		this(c);
		this.log = msg;
	}

	public NetLogInfo(Context c) {
		try {
			PackageInfo info = c.getPackageManager().getPackageInfo(
					c.getPackageName(), 0);
			this.appName = info.packageName;
			this.packageName = info.packageName;
			this.versionCode = info.versionCode;
			this.versionName = info.versionName;
			
			TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
			this.phoneNumber = tm.getLine1Number();
			this.deviceId = tm.getDeviceId();
			this.imsi = tm.getSubscriberId();
			this.imei = tm.getDeviceId();
		} catch (Exception e) {
		}
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject jo = new JSONObject();
		jo.put("appName", appName);
		jo.put("packageName", packageName);
		jo.put("versionName", versionName);
		jo.put("versionCode", versionCode);
		
		jo.put("phoneNumber", phoneNumber);
		jo.put("imsi", imsi);
		jo.put("imei", imei);
		jo.put("deviceId", deviceId);

		jo.put("updateTime", updateTime);
		jo.put("log", log);
		jo.put("logType", LOG_TYPE_JSON);
		jo.put("logVersion", logVersion);
		
		return jo;
	}

	public String toJSONString() {
		try {
			return toJSONObject().toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	public int hashCode() {
		return toJSONString().toString().hashCode();
	}
}