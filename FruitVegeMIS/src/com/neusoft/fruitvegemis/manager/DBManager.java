package com.neusoft.fruitvegemis.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.neusoft.fruitvegemis.persistence.SQLiteDatabase;
import com.neusoft.fruitvegemis.persistence.SQLiteOpenHelper;

public class DBManager {
	public static final String TAG="DBManager";
	private SQLiteOpenHelper dbHelper;
	private SQLiteDatabase db;

	DBManager(SQLiteOpenHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public void beginTransaction() {
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		db.beginTransaction();
	}

	public void commit() {
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		db.commit();
	}

	public void end() {
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		db.end();
	}

	public long insert(String table, ContentValues values) {
		Log.e(TAG, "insert");
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		return db.insert(table, values);
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		return db.rawQuery(sql, selectionArgs);
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		return db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);
	}
}
