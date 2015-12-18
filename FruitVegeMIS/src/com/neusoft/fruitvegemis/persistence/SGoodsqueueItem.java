package com.neusoft.fruitvegemis.persistence;

import android.content.ContentValues;

public class SGoodsqueueItem extends BaseQueueItem {
	public SGoodsqueueItem(String _tableName, ContentValues _contentValues,
			String _whereClause, String[] _whereArgs,int _action) {
		super(_tableName, _contentValues, _whereClause, _whereArgs,_action);
	}
}
