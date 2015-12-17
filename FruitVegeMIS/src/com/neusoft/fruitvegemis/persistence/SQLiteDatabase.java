package com.neusoft.fruitvegemis.persistence;

import android.content.ContentValues;
import android.database.Cursor;

import com.neusoft.fruitvegemis.utils.AppConstants;

public class SQLiteDatabase {
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
	
	public void insert(String sql){
		
	}
}
