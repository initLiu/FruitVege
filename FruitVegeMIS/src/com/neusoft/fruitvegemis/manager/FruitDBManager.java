package com.neusoft.fruitvegemis.manager;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.datapool.Goods;
import com.neusoft.fruitvegemis.datapool.Order;
import com.neusoft.fruitvegemis.datapool.Order.OrderState;
import com.neusoft.fruitvegemis.datapool.SGoodsRecord;
import com.neusoft.fruitvegemis.datapool.UOrderRecord;
import com.neusoft.fruitvegemis.persistence.SGoodsqueueItem;
import com.neusoft.fruitvegemis.persistence.SQLiteOpenHelper;
import com.neusoft.fruitvegemis.utils.AppConstants;

public class FruitDBManager extends DBManager {

	public static final String TAG = "FruitDBManager";

	FruitDBManager(SQLiteOpenHelper dbHelper) {
		super(dbHelper);
	}

	public void insert(SGoodsqueueItem item) {
		Log.e(TAG, "insert");
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

	public List<UOrderRecord> queryUOrder(String table) {
		List<UOrderRecord> rst = new ArrayList<UOrderRecord>();
		String selection = AppConstants.TBUOrder.Cloum.uname + "=?";
		String[] selectionArgs = { BaseApplication.getBaseApplication()
				.getCurrentAccount().getUin() };
		Cursor cursor = query(table, null, selection, selectionArgs, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String odate = cursor.getString(cursor
						.getColumnIndex(AppConstants.TBUOrder.Cloum.odate));
				String oid = cursor.getString(cursor
						.getColumnIndex(AppConstants.TBUOrder.Cloum.oid));
				float oprice = cursor.getFloat(cursor
						.getColumnIndex(AppConstants.TBUOrder.Cloum.oprice));
				int ostate = cursor.getInt(cursor
						.getColumnIndex(AppConstants.TBUOrder.Cloum.ostate));
				int type = cursor.getInt(cursor
						.getColumnIndex(AppConstants.TBUOrder.Cloum.type));
				String uname = cursor.getString(cursor
						.getColumnIndex(AppConstants.TBUOrder.Cloum.uname));
				UOrderRecord record = new UOrderRecord(odate, oid, oprice,
						OrderState.values()[ostate], type, uname);
				rst.add(record);
			} while (cursor.moveToNext());
		}
		return rst;
	}

	public Order queryOrder(String oid) {
		Order order = new Order(oid);
		String selection = "oid=?";
		String[] selectionArgs = { oid };
		Cursor cursor = query(AppConstants.TBOrder.name, null, selection,
				selectionArgs, null, null, null);
		cursor.moveToFirst();
		if (cursor != null && cursor.getCount() > 0) {
			do {
				String gname = cursor.getString(cursor
						.getColumnIndex(AppConstants.TBOrder.Cloum.gname));
				byte[] gpicture = cursor.getBlob(cursor
						.getColumnIndex(AppConstants.TBOrder.Cloum.gpicture));
				float gprice = cursor.getFloat(cursor
						.getColumnIndex(AppConstants.TBOrder.Cloum.gprice));
				String sname = cursor.getString(cursor
						.getColumnIndex(AppConstants.TBOrder.Cloum.sname));
				order.addGoods(new Goods(gname, gprice, sname, gpicture));
			} while (cursor.moveToNext());
		}
		return order;
	}
}
