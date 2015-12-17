package com.neusoft.fruitvegemis.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

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

	public synchronized void close() {
		if (dbR != null) {
			dbR.close();
		}
		if (dbW != null) {
			dbW.close();
		}
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
					+ AppConstants.TBUser.name
					+ "(uname TEXT PRIMARY KEY,password TEXT NOT NULL,type INTEGER NOT NULL)";
			db.execSQL(sql);

			sql = "create table " + AppConstants.TBUOrder.name + "("
					+ AppConstants.TBUOrder.Cloum.id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ AppConstants.TBUOrder.Cloum.uname + " TEXT NOT NULL,"
					+ AppConstants.TBUOrder.Cloum.type + " INTERER NOT NULL,"
					+ AppConstants.TBUOrder.Cloum.oid + " TEXT,"
					+ AppConstants.TBUOrder.Cloum.ostate
					+ " INTERGER,"
					+ AppConstants.TBUOrder.Cloum.oprice + " REAL DEFAULT 0.0)";
			db.execSQL(sql);

			sql = "create table " + AppConstants.TBOrder.name + "("
					+ AppConstants.TBOrder.Cloum.id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ AppConstants.TBOrder.Cloum.oid + " TEXT NOT NULL,"
					+ AppConstants.TBOrder.Cloum.gname + " TEXT NOT NULL,"
					+ AppConstants.TBOrder.Cloum.gpicture + " BLOB,"
					+ AppConstants.TBOrder.Cloum.gprice + " REAL NOT NULL)";
			db.execSQL(sql);

			sql = "create table " + AppConstants.TBSGoods.name + "("
					+ AppConstants.TBSGoods.Cloum.id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ AppConstants.TBSGoods.Cloum.sname + " TEXT NOT NULL,"
					+ AppConstants.TBSGoods.Cloum.gname + " TEXT NOT NULL,"
					+ AppConstants.TBOrder.Cloum.gpicture + " BLOB,"
					+ AppConstants.TBSGoods.Cloum.gprice + " REAL NOT NULL)";
			db.execSQL(sql);

		}

		@Override
		public void onUpgrade(android.database.sqlite.SQLiteDatabase db,
				int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}

	}
}
