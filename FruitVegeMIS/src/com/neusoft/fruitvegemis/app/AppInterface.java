package com.neusoft.fruitvegemis.app;

import com.neusoft.fruitvegemis.manager.DBManagerFactory;
import com.neusoft.fruitvegemis.manager.FruitDBManagerFactory;
import com.neusoft.fruitvegemis.manager.Manager;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;
import com.neusoft.fruitvegemis.utils.AppConstants;

public class AppInterface {
	private Manager[] managers = new Manager[10];

	private BaseApplication mContext;;

	private FruitDBManagerFactory dmFactory;
	private GoodsHandler mGoodsHandler;

	public static final int FRUITVG = 0;

	public AppInterface(BaseApplication context) {
		this.mContext = context;
	}

	public DBManagerFactory getDBManagerFactory() {
		if (dmFactory != null) {
			return dmFactory;
		}

		synchronized (this) {
			if (dmFactory == null) {
				dmFactory = new FruitDBManagerFactory(AppConstants.DBNAME);
			}
		}
		return dmFactory;
	}

	public BusinessHandler getGoodsHandler() {
		if (mGoodsHandler == null) {
			mGoodsHandler = new GoodsHandler(mContext);
		}
		return mGoodsHandler;
	}

	public Manager getManager(int name) {
		Manager mgr = managers[name];
		if (mgr == null) {
			synchronized (managers) {
				mgr = managers[name];
				if (mgr != null) {
					return mgr;
				}
				switch (name) {
				case FRUITVG:
					mgr = new FruitVgDBManager(this);
					break;
				default:
					break;
				}
				if (mgr != null) {
					addManager(name, mgr);
				}
			}
		}
		return mgr;
	}

	protected void addManager(int name, Manager manager) {
		if (managers[name] != null) {
			return;
		}
		managers[name] = manager;
	}

	public BaseApplication getApplication() {
		return mContext;
	}

	public void release() {
		for (Manager manager : managers) {
			if (manager != null) {
				manager.destroy();
				manager = null;
			}
		}
	}

	/**
	 * exit时不会调用onDestroy
	 */
	public void exit() {
		release();
		mContext.exit();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (dmFactory != null) {
			dmFactory.close();
		}
	}
}
