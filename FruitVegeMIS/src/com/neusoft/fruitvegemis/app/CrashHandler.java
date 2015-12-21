package com.neusoft.fruitvegemis.app;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler mCrashHandler;
	private UncaughtExceptionHandler defaultHandler;

	private CrashHandler() {

	}

	public static CrashHandler getInstance() {
		if (mCrashHandler == null) {
			mCrashHandler = new CrashHandler();
		}
		return mCrashHandler;
	}

	public void init(Context context) {
		if (defaultHandler == null) {
			defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		}

	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Log.e("Crash", "FruitVgMIS has crashed");
		ex.printStackTrace();
		BaseApplication.getBaseApplication().exit();
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				BaseApplication.getBaseApplication().getAppInterface()
						.release();
			}
		};
		if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
			new Handler(Looper.getMainLooper()).postAtFrontOfQueue(runnable);
		} else {
			runnable.run();
		}
	}
}
