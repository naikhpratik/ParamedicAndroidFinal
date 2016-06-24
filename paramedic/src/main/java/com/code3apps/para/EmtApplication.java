package com.code3apps.para;

import com.code3apps.para.utils.CrashHandler;

import android.app.Application;

public class EmtApplication extends Application {

	CrashHandler INSTANCE;
	public final static boolean DEBUG = false ;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		if (DEBUG) {
			INSTANCE = CrashHandler.getInstance();
			INSTANCE.init(this);
		}
	}
}
