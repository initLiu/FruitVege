package com.neusoft.fruitvegemis.app;

import android.content.Context;

import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;

public class AppInterface {
	public FruitVgDBManager fruitVgDBManager;

	public Context mContext;;

	public enum Manager {
		FruitVg
	}

	public AppInterface(Context context) {
		this.mContext = context;
	}

	public Manager getManager(Manager manager) {
		switch (manager) {
		case FruitVg:
			if (fruitVgDBManager == null) {
				fruitVgDBManager = new FruitVgDBManager(mContext);
			}
			break;

		default:
			break;
		}
		return null;
	}
}
