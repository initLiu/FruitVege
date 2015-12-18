package com.neusoft.fruitvegemis.persistence;

import android.content.ContentValues;

public class BaseQueueItem {
	public static final int QUEUE_ITEM_ACTION_INSERT = 0;
	public static final int QUEUE_ITEM_ACTION_DELETE = 0;
	public static final int QUEUE_ITEM_ACTION_UPDATE = 0;

	public String tableName;
	public ContentValues contentValues;
	public String whereClause;
	public String[] whereArgs;
	public int action;

	public BaseQueueItem(String _tableName, ContentValues _contentValues,
			String _whereClause, String[] _whereArgs, int _action) {
		tableName = _tableName;
		contentValues = _contentValues;
		whereClause = _whereClause;
		whereArgs = _whereArgs;
		action = _action;
	}
}
