package com.ttfp;

import android.app.Application;
import android.content.Context;

import com.ttfp.netlog.NetLogInfo;
import com.ttfp.util.AppNetLogUtil;

public class TtfpGameApplication extends Application{
	private static TtfpGameApplication mInstance;
	
	public static TtfpGameApplication getInsance() {
		return mInstance;
	}
	
	public static Context getContext() {
		return mInstance.getApplicationContext();
	}
		
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		AppNetLogUtil.log("Application.onCreate()");
	}
}
