package com.neusoft.fruitvegemis.app;

import android.content.Context;

import com.neusoft.fruitvegemis.manager.Manager;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;

public class AppInterface {
	private FruitVgDBManager fruitVgDBManager;

	private Context mContext;;

	public static final int FRUITVG = 0;

	public AppInterface(Context context) {
		this.mContext = context;
	}

	public Manager getManager(int managerId) {
		switch (managerId) {
		case FRUITVG:
			if (fruitVgDBManager == null) {
				fruitVgDBManager = new FruitVgDBManager(mContext);
			}
			return fruitVgDBManager;

		default:
			break;
		}
		return null;
	}
}
