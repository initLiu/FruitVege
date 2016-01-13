package com.neusoft.fruitvegemis.persistence;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.neusoft.fruitvegemis.utils.AppConstants;

public class SQLiteDatabase {
	public static final String TAG = "SQLiteDatabase";
	public android.database.sqlite.SQLiteDatabase db;

	SQLiteDatabase(android.database.sqlite.SQLiteDatabase db) {
		this.db = db;
	}

	public boolean registerUser(String uin, String password, int type) {
		ContentValues values = new ContentValues();
		values.put(AppConstants.TBUser.Cloum.uname, uin);
		values.put(AppConstants.TBUser.Cloum.password, password);
		values.put(AppConstants.TBUser.Cloum.type, type);
		return db.insert(AppConstants.TBUser.name, null, values) == -1 ? false
				: true;
	}

	public void beginTransaction() {
		if (db != null) {
			db.beginTransaction();
		}
	}

	public void commit() {
		db.setTransactionSuccessful();
	}

	public void end() {
		db.endTransaction();
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);
	}

	public void close() {
		try {
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long insert(String table, ContentValues values) {
		Log.e(TAG, "insert");
		return db.insert(table, null, values);
	}

	public long update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		Log.e(TAG, "update");
		return db.update(table, values, whereClause, whereArgs);
	}
}
