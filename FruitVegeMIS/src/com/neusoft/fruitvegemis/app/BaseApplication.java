package com.neusoft.fruitvegemis.app;

import android.app.Application;

public class BaseApplication extends Application {

	public static BaseApplication mBaseApplication;
	public static AppInterface mAppInterface;

	private boolean isLogin = false;

	public static BaseApplication getBaseApplication() {
		return mBaseApplication;
	}

	// 是否需要闪屏
	private static boolean isSPlash = true;

	public boolean getSplash() {
		return isSPlash;
	}

	public void setSplash(boolean enable) {
		isSPlash = enable;
	}

	public boolean getLogin() {
		return isLogin;
	}

	public void setLogin(boolean login) {
		isLogin = login;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mBaseApplication = this;
		mAppInterface = new AppInterface(this);
	}
}
