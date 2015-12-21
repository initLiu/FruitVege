package com.neusoft.fruitvegemis.app;

import android.os.Handler;
import android.os.Looper;

public abstract class BusinessHandler {
	public static final int ADD_GOODS_REFRESH_UI = 0;

	private BaseApplication app;
	private static Handler uiHandler;

	{
		uiHandler = new Handler(Looper.getMainLooper());
	}

	public BusinessHandler(BaseApplication app) {
		this.app = app;
	}

	public abstract void onReceive(Object data);

	public void notifyUI(Object data) {
		synchronized (app.uiObservers) {
			for (BusinessObserver observer : app.uiObservers) {
				if (observer != null) {
					dispathMessage(data, observer, uiHandler);
				}
			}
		}
	}

	private void dispathMessage(final Object data,
			final BusinessObserver observer, Handler handler) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				observer.onUpdate(true, data);
			}
		};
		uiHandler.post(runnable);
	}
}
