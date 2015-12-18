package com.neusoft.fruitvegemis.manager;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.neusoft.fruitvegemis.datapool.SGoodsRecord;
import com.neusoft.fruitvegemis.persistence.SGoodsqueueItem;
import com.neusoft.fruitvegemis.persistence.SQLiteOpenHelper;
import com.neusoft.fruitvegemis.utils.AppConstants;

public class FruitDBManager extends DBManager {

	public static final String TAG = "FruitDBManager";

	FruitDBManager(SQLiteOpenHelper dbHelper) {
		super(dbHelper);
	}

	public void insert(SGoodsqueueItem item) {
		insert(item.tableName, item.contentValues);
	}

	public List<SGoodsRecord> rawQuerySGoods(String sql, String[] selectionArgs) {
		Cursor cursor = rawQuery(sql, selectionArgs);
		List<SGoodsRecord> results = new ArrayList<SGoodsRecord>();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String sname = cursor.getString(cursor
						.getColumnIndex(AppConstants.TBSGoods.Cloum.sname));
				String gname = cursor.getString(cursor
						.getColumnIndex(AppConstants.TBSGoods.Cloum.gname));
				float gprice = cursor.getFloat(cursor
						.getColumnIndex(AppConstants.TBSGoods.Cloum.gprice));
				byte[] gpicture = cursor.getBlob(cursor
						.getColumnIndex(AppConstants.TBSGoods.Cloum.gpicture));
				SGoodsRecord record = new SGoodsRecord();
				record.init(sname, gname, gprice, gpicture);
				results.add(record);
			} while (cursor.moveToNext());
		}
		return results;
	}
}
