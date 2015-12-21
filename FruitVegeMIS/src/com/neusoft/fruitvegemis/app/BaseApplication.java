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
	private User currentAccount;

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
		setCurrentAccount(null);
	}

	public void setCurrentAccount(User user) {
		this.currentAccount = user;
	}

	public User getCurrentAccount() {
		return currentAccount;
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
		mAppInterface.getDBManagerFactory();
		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());
	}

	public void exit() {
		closeAllActivity();
		System.exit(0);
	}

	public void closeAllActivity() {
		int count = mActivitys.size();
		for (int i = count - 1; i >= 0; --i) {
			WeakReference<BaseActivity> ref = mActivitys.get(i);
			Activity activity = ref != null ? ref.get() : null;
			if (activity == null) {
				mActivitys.remove(i);
			} else if (!activity.isFinishing()) {
				activity.finish();
			} else {
				// 不在finish后立刻执行，而在下一次调用的时候执行
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
