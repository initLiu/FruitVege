package com.neusoft.fruitvegemis.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.neusoft.fruitvegemis.activity.BaseActivity;

import android.app.Activity;
import android.app.Application;

public class BaseApplication extends Application {

	public static BaseApplication mBaseApplication;
	private AppInterface mAppInterface;
	private final List<WeakReference<BaseActivity>> mActivitys = new ArrayList<WeakReference<BaseActivity>>();

	private boolean isLogin = false;

	public static BaseApplication getBaseApplication() {
		return mBaseApplication;
	}

	public AppInterface getAppInterface() {
		return mAppInterface;
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
		initRuntime();
	}

	public void initRuntime() {
		// 创建数据库
		mAppInterface.getManager(AppInterface.FRUITVG);
	}

	public void closeAllActivity() {
		int count = mActivitys.size();
		for (int i = 0; i < count; i++) {
			WeakReference<BaseActivity> ref = mActivitys.get(i);
			Activity activity = ref != null ? ref.get() : null;
			if (activity == null) {
				mActivitys.remove(i);
			} else if (!activity.isFinishing()) {
				activity.finish();
			} else {
				mActivitys.remove(i);
			}
		}
	}

	public void addBaseActivity(BaseActivity activity) {
		mActivitys.add(new WeakReference<BaseActivity>(activity));
	}

	public void removeBaseActivity(BaseActivity activity) {
		mActivitys.remove(new WeakReference<BaseActivity>(activity));
	}
}
