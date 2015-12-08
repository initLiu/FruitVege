package com.neusoft.fruitvegemis.persistence;

public class SQLiteDatabase {
	public android.database.sqlite.SQLiteDatabase db;

	SQLiteDatabase(android.database.sqlite.SQLiteDatabase db) {
		this.db = db;
	}

	public void close() {
		try {
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
