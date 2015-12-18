package com.neusoft.fruitvegemis.manager;

import com.neusoft.fruitvegemis.persistence.SQLiteOpenHelper;

public abstract class DBManagerFactory {
	private static final String CLOSE_EXCEPTION_MSG = "The DBManagerFactory has been already closed";
	private boolean closed;
	private final SQLiteOpenHelper dbHelper;

	public DBManagerFactory(String name) {
		dbHelper = build(name);
	}

	public DBManager createFruitDBManager() {
		if (closed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		DBManager dm = new FruitDBManager(dbHelper);
		closed = false;
		return dm;
	}

	public void close() {
		if (closed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		closed = true;
		dbHelper.close();
	}

	public boolean isOpen() {
		return !closed;
	}

	public abstract SQLiteOpenHelper build(String name);
}
