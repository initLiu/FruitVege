package com.neusoft.fruitvegemis.persistence;

import android.content.Context;

import com.neusoft.fruitvegemis.manager.Manager;
import com.neusoft.fruitvegemis.utils.AppConstants;

public class FruitVgDBManager implements Manager {

	private SQLiteOpenHelper helper;

	public FruitVgDBManager(Context context) {
		helper = new SQLiteOpenHelper(context, AppConstants.DBNAME,
				AppConstants.DBVERSION);
	}
	

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}
