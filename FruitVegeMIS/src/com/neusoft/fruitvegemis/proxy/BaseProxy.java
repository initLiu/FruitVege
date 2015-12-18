package com.neusoft.fruitvegemis.proxy;

import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;

public abstract class BaseProxy {
	protected AppInterface app;
	protected FruitVgDBManager fVgDBManager;

	public BaseProxy(AppInterface _app, FruitVgDBManager _fVgDBManager) {
		app = _app;
		fVgDBManager = _fVgDBManager;
	}

	protected abstract void init();

	protected abstract void destroy();
}
