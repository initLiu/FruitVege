package com.neusoft.fruitvegemis.persistence;

import java.util.ArrayList;
import java.util.List;

import com.neusoft.fruitvegemis.utils.AppConstants;

import android.content.ContentValues;
import android.database.Cursor;

public class SQLiteDatabase {
	public android.database.sqlite.SQLiteDatabase db;

	private List<String> tableList = new ArrayList<String>();

	SQLiteDatabase(android.database.sqlite.SQLiteDatabase db) {
		this.db = db;
	}

	public boolean registerUser(String uin, String password, int type) {
		String tableName = getUserTablename(uin);
		if (!tableList.contains(tableName)) {
			if (createUserTable(tableName, type)) {
				ContentValues values = new ContentValues();
				values.put(AppConstants.TBUin.Cloum.uname, uin);
				values.put(AppConstants.TBUin.Cloum.password, password);
				values.put(AppConstants.TBUin.Cloum.type, type);
				return db.insert(AppConstants.TBUin.name, null, values) == -1 ? false
						: true;
			}
		}
		return false;
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);
	}

	public String getUserTablename(String str) {
		return "tb_" + str;
	}

	public boolean createUserTable(String tablename, int type) {
		String sql = "";
		if (type == 0) {
			sql = "create table IF NOT EXISTS "
					+ tablename
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,uname TEXT NOT NULL,orderId TEXT NOT NULL,price REAL NOT NULL)";
		} else if (type == 1) {

		}
		try {
			db.execSQL(sql);
			tableList.add(tablename);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void close() {
		try {
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
