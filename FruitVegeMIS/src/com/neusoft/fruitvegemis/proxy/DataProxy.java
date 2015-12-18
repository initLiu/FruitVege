package com.neusoft.fruitvegemis.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.util.Log;

import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.datapool.DataPool;
import com.neusoft.fruitvegemis.datapool.SGoodsRecord;
import com.neusoft.fruitvegemis.manager.FruitDBManager;
import com.neusoft.fruitvegemis.persistence.BaseQueueItem;
import com.neusoft.fruitvegemis.persistence.SGoodsqueueItem;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;
import com.neusoft.fruitvegemis.utils.AppConstants;

public class DataProxy extends BaseProxy {

	public static final String TAG = "DataProxy";
	private FruitDBManager dm = null;
	private Object dmLock = new Object();

	public DataProxy(AppInterface _app, FruitVgDBManager _fVgDBManager) {
		super(_app, _fVgDBManager);
		// TODO Auto-generated constructor stub
	}

	List<SGoodsRecord> init(String curUin) {
		synchronized (DataPool.getPoolInstance(curUin).getDataPoolLock(curUin)) {
			List<SGoodsRecord> itemList = DataPool.getPoolInstance(curUin)
					.getSGoodPool().get(curUin);
			if (itemList != null) {
				Log.d("TAG", "init from cache curUin=" + curUin);
				return itemList;
			}
			String sql = getInitSql(curUin);
			if (needTransSaveToDatabase(curUin)) {
				fVgDBManager.transSaveToDatabase();
			}
			List<SGoodsqueueItem> sgoodsListFromQueue = getSGoodsListFromQueue();
			List<E>getDM().rawQuerySGoods(sql, null);
		}
	}

	protected FruitDBManager getDM() {
		if (dm == null) {
			synchronized (dmLock) {
				if (dm == null) {
					dm = (FruitDBManager) app.getDBManagerFactory()
							.createFruitDBManager();
				}
			}
		}
		return dm;
	}

	/**
	 * 在读DB之后，再读一下操作队列中的消息
	 * */
	protected List<SGoodsqueueItem> getSGoodsListFromQueue() {
		synchronized (fVgDBManager.getSGoodsQueue()) {
			Vector<SGoodsqueueItem> sGoodsQueue = fVgDBManager.getSGoodsQueue();
			List<SGoodsqueueItem> result = new ArrayList<SGoodsqueueItem>();
			for (SGoodsqueueItem item : result) {
				if (item.action == BaseQueueItem.QUEUE_ITEM_ACTION_INSERT) {
					result.add(item);
				}
			}
			Log.e(TAG, "getSGoodsListFromQueue size=" + result.size());
			return result;
		}
	}

	/**
	 * 在读DB之前，需要先判断一下，是不是要同步写DB
	 * */
	protected boolean needTransSaveToDatabase(String curUin) {
		if (curUin == null) {
			return false;
		}
		synchronized (fVgDBManager.getSGoodsQueue()) {
			Vector<SGoodsqueueItem> dataQueue = fVgDBManager.getSGoodsQueue();
			for (SGoodsqueueItem item : dataQueue) {
				if (item.action == BaseQueueItem.QUEUE_ITEM_ACTION_DELETE
						|| item.action == BaseQueueItem.QUEUE_ITEM_ACTION_INSERT
						|| item.action == BaseQueueItem.QUEUE_ITEM_ACTION_UPDATE) {
					Log.d(TAG, "needTransSaveToDatabase uin=" + curUin);
					return true;
				}
			}
			return false;
		}
	}

	private String getInitSql(String curUin) {
		String sql = "select * from " + AppConstants.TBSGoods.name + " where "
				+ AppConstants.TBSGoods.Cloum.sname + "=" + curUin;
		return sql;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void destroy() {
		// TODO Auto-generated method stub

	}

}
