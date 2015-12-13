package com.neusoft.fruitvegemis.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.neusoft.fruitvegemis.utils.AppConstants;

public class SQLiteOpenHelper {
	private android.database.sqlite.SQLiteOpenHelper helper;
	private SQLiteDatabase dbW, dbR;

	public SQLiteOpenHelper(Context context, String name, int version) {
		helper = new SQLiteOpenHelperImpl(context, name + ".db", null, version);
	}

	public synchronized SQLiteDatabase getWritableDatabase() {
		android.database.sqlite.SQLiteDatabase db = helper
				.getWritableDatabase();
		if (dbW == null || dbW.db != db) {
			dbW = new SQLiteDatabase(db);
		}
		return dbW;
	}

	public synchronized SQLiteDatabase getReadableDatabase() {
		android.database.sqlite.SQLiteDatabase db = helper
				.getReadableDatabase();
		if (dbR == null || dbR.db != db) {
			dbR = new SQLiteDatabase(db);
		}
		return dbR;
	}

	private static class SQLiteOpenHelperImpl extends
			android.database.sqlite.SQLiteOpenHelper {

		public SQLiteOpenHelperImpl(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(android.database.sqlite.SQLiteDatabase db) {
			String sql = "create table "
					+ AppConstants.TBUin.name
					+ "(_id INTERGER AUTOINCREM,uname TEXT PRIMARY KEY,password TEXT NOT NULL,type INTERGER NOT NULL)";
			 Log.e("Test", "SQLiteOpenHelperImpl sql=" + sql);
			db.execSQL(sql);

		}

		@Override
		public void onUpgrade(android.database.sqlite.SQLiteDatabase db,
				int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}
}
