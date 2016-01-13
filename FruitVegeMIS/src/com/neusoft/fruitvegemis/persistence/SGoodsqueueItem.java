package com.neusoft.fruitvegemis.persistence;

import android.content.ContentValues;

public class SGoodsqueueItem extends BaseQueueItem {
	
	public SGoodsqueueItem(String _tableName, Entity _item,
			ContentValues _contentValues, String _whereClause,
			String[] _whereArgs, int _action) {
		super(_tableName, _item, _contentValues, _whereClause, _whereArgs,
				_action);
	}
}
