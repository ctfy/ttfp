package com.ttfp;

import android.app.Application;
import android.content.Context;

public class TtfpGameApplication extends Application{
	private static TtfpGameApplication mInstance;
	
	public static TtfpGameApplication getInsance() {
		return mInstance;
	}
	
	public static Context getContext() {
		return mInstance.getApplicationContext();
	}
	
	public static void getVersion() {
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}
}
