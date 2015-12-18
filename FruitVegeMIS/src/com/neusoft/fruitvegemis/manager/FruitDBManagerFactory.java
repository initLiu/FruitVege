package com.neusoft.fruitvegemis.manager;

import android.content.Context;

import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.persistence.SQLiteOpenHelper;
import com.neusoft.fruitvegemis.utils.AppConstants;

public class FruitDBManagerFactory extends DBManagerFactory {

	protected SQLiteOpenHelper dbHelper;

	public FruitDBManagerFactory(String name) {
		super(name);
	}

	@Override
	public SQLiteOpenHelper build(String name) {
		if (dbHelper == null) {
			Context context = BaseApplication.mBaseApplication
					.getApplicationContext();
			dbHelper = new SQLiteOpenHelper(context, name,
					AppConstants.DBVERSION);
		}
		return dbHelper;
	}

}
