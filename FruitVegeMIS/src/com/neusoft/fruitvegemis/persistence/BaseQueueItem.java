package com.neusoft.fruitvegemis.persistence;

import android.content.ContentValues;

public class BaseQueueItem {
	public static final int QUEUE_ITEM_ACTION_INSERT = 0;
	public static final int QUEUE_ITEM_ACTION_DELETE = 1;
	public static final int QUEUE_ITEM_ACTION_UPDATE = 2;

	public String tableName;
	public ContentValues contentValues;
	public String whereClause;
	public String[] whereArgs;
	public int action;
	public Entity item;

	public BaseQueueItem(String _tableName, Entity _item,
			ContentValues _contentValues, String _whereClause,
			String[] _whereArgs, int _action) {
		tableName = _tableName;
		contentValues = _contentValues;
		whereClause = _whereClause;
		whereArgs = _whereArgs;
		action = _action;
		this.item = _item;
	}
}
