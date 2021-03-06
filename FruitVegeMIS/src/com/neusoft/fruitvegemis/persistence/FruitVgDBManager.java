package com.neusoft.fruitvegemis.persistence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.app.User;
import com.neusoft.fruitvegemis.datapool.Goods;
import com.neusoft.fruitvegemis.datapool.Order;
import com.neusoft.fruitvegemis.datapool.Order.OrderState;
import com.neusoft.fruitvegemis.datapool.OrderRecord;
import com.neusoft.fruitvegemis.datapool.SGoodsRecord;
import com.neusoft.fruitvegemis.datapool.UOrderRecord;
import com.neusoft.fruitvegemis.manager.FruitDBManager;
import com.neusoft.fruitvegemis.manager.Manager;
import com.neusoft.fruitvegemis.utils.AppConstants;
import com.neusoft.fruitvegemis.utils.AppConstants.TBUser;
import com.neusoft.fruitvegemis.utils.ObserverMessage;

public class FruitVgDBManager extends Observable implements Manager {

	public static final String TAG = "FruitVgDBManager";

	private HandlerThread subThandlerhread;
	private Handler subHandler;
	private ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<String, User>();
	private ConcurrentHashMap<String, Order> orderMap = new ConcurrentHashMap<String, Order>();
	private ConcurrentHashMap<String, UOrderRecord> uOrderMap = new ConcurrentHashMap<String, UOrderRecord>();
	private Order unCommitOrder;
	private Vector<SGoodsqueueItem> sGoodsQueue;
	private Thread writeThread;
	boolean isDestroy = false;
	static final int WRITE_THREAD_TIME_INTERVAL = 1000 * 10;
	public Object transSaveLock = new Object();
	private AppInterface app;
	private FruitDBManager dm;

	public FruitVgDBManager(AppInterface app) {
		Log.e(TAG, "FruitVgDBManager");
		this.app = app;
		sGoodsQueue = new Vector<SGoodsqueueItem>();
		subThandlerhread = new HandlerThread("Sub-Thread");
		subThandlerhread.start();
		subHandler = new Handler(subThandlerhread.getLooper());
		subHandler.post(new Runnable() {

			@Override
			public void run() {
				loadUser();
			}
		});

		writeThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!isDestroy) {
					synchronized (sGoodsQueue) {
						try {
							sGoodsQueue.wait(WRITE_THREAD_TIME_INTERVAL);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (!sGoodsQueue.isEmpty()) {
						transSaveToDatabase();
					}
				}
			}
		});
		writeThread.start();
	}

	/**
	 * 批量存储消息到database
	 */
	public void transSaveToDatabase() {
		transSaveToDatabase(dm);
	}

	/**
	 * 批量存储消息到database
	 */
	public void transSaveToDatabase(FruitDBManager dm) {
		Log.e(TAG, "transSaveToDatabase");
		synchronized (transSaveLock) {
			List<SGoodsqueueItem> items = null;
			synchronized (sGoodsQueue) {
				if (sGoodsQueue.isEmpty()) {
					return;
				}
				items = (List<SGoodsqueueItem>) sGoodsQueue.clone();
				sGoodsQueue.clear();
			}
			if (items != null) {
				try {
					int optCount = 0;
					// 开始事物
					dm.beginTransaction();
					for (SGoodsqueueItem queueItem : items) {
						++optCount;
						String tableName = queueItem.tableName;
						Log.e(TAG, "transSaveToDatabase count=" + optCount + ",tableName=" + tableName);
						switch (queueItem.action) {
						case BaseQueueItem.QUEUE_ITEM_ACTION_INSERT:
							dm.insert(queueItem);
							break;
						case BaseQueueItem.QUEUE_ITEM_ACTION_UPDATE:
							dm.update(queueItem);
							break;
						case BaseQueueItem.QUEUE_ITEM_ACTION_DELETE:
							dm.delete(queueItem);
							break;
						default:
							break;
						}
					}
					dm.commit();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dm.end();
				}
			}
		}
	}

	public List<SGoodsRecord> querySGoods() {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}
		String sql = "select * from " + AppConstants.TBSGoods.name;
		return dm.rawQuerySGoods(sql, null);
	}

	public List<SGoodsRecord> querySGoods(String curUin) {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}
		String sql = "select * from " + AppConstants.TBSGoods.name + " where " + AppConstants.TBSGoods.Cloum.sname
				+ "= ?";
		return dm.rawQuerySGoods(sql, new String[] { curUin });
	}

	private void loadUser() {
		Log.e(TAG, "loaduser");
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}
		Cursor cursor = dm.query(TBUser.name, null, null, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String uname = cursor.getString(cursor.getColumnIndex(TBUser.Cloum.uname));
				String pwd = cursor.getString(cursor.getColumnIndex(TBUser.Cloum.password));
				int type = cursor.getInt(cursor.getColumnIndex(TBUser.Cloum.type));
				User user = new User(uname, pwd, type);
				Log.e(TAG, "loaduser uin=" + uname);
				synchronized (userMap) {
					userMap.put(uname, user);
				}
			} while (cursor.moveToNext());
		}
	}

	public ConcurrentHashMap<String, Order> getUserOrder() {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}
		if (orderMap.isEmpty()) {
			initUserOrder();
		}
		return orderMap;
	}

	public List<Order> getUserCommittedOrder() {
		getUserOrder();
		List<Order> orders = new ArrayList<Order>();
		for (Entry<String, Order> entry : orderMap.entrySet()) {
			orders.add(entry.getValue());
		}

		if (unCommitOrder != null) {
			if (orderMap.containsKey(unCommitOrder.orderId)) {
				orders.remove(unCommitOrder);
			}
		}
		return orders;
	}

	public List<Order> getUserCommitOrderForSeller(String seller) {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}
		List<Order> ret = new ArrayList<Order>();

		List<UOrderRecord> records = dm.queryUOrder(AppConstants.TBUOrder.name);
		int len = records.size();
		for (int i = 0; i < len; i++) {
			String orderid = records.get(i).oid;
			if (records.get(i).ostate == OrderState.unCommit) {
				continue;
			}
			Order order = dm.queryOrderbySeller(orderid, seller);
			order.orderdate = records.get(i).odate;
			order.orderState = records.get(i).ostate;
			if (order.getGoods().size() == 0) {
				continue;
			}
			order.addEmptyGoods(new Goods());
			ret.add(order);
		}
		return ret;
	}

	private void initUserOrder() {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}
		List<UOrderRecord> records = dm.queryCurrentUserUOrder(AppConstants.TBUOrder.name);
		int len = records.size();
		for (int i = 0; i < len; i++) {
			String orderid = records.get(i).oid;
			uOrderMap.put(orderid, records.get(i));

			Order order = dm.queryOrder(orderid);
			order.orderdate = records.get(i).odate;
			order.orderState = records.get(i).ostate;
			if (order.orderState == OrderState.unCommit) {
				order.addEmptyGoods(new Goods());
				unCommitOrder = order;
			}
			orderMap.put(orderid, order);
		}
	}

	public void addGoods2Order(Goods goods) {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}

		if (unCommitOrder == null) {
			unCommitOrder = new Order();
			unCommitOrder.orderState = Order.OrderState.unCommit;
			unCommitOrder.addEmptyGoods(new Goods());
			addUserOrder(unCommitOrder);
			orderMap.put(unCommitOrder.orderId, unCommitOrder);
		}
		unCommitOrder.addGoods(goods);

		addGoods2Order(unCommitOrder, goods);
	}

	private void addGoods2Order(final Order order, final Goods goods) {
		subHandler.post(new Runnable() {

			@Override
			public void run() {
				// 将商品添加到订单中
				ContentValues values = new ContentValues();
				values.put(AppConstants.TBOrder.Cloum.oid, order.orderId);
				values.put(AppConstants.TBOrder.Cloum.sname, goods.sname);
				values.put(AppConstants.TBOrder.Cloum.gname, goods.gname);
				values.put(AppConstants.TBOrder.Cloum.gprice, goods.gprice);
				values.put(AppConstants.TBOrder.Cloum.gpicture, goods.gpicture);

				OrderRecord record = new OrderRecord(unCommitOrder.orderId, goods.sname, goods.gname, goods.gprice,
						goods.gpicture);

				addDataQueue(AppConstants.TBOrder.name, record, values, null, null,
						BaseQueueItem.QUEUE_ITEM_ACTION_INSERT);
			}
		});
	}

	private void addUserOrder(final Order order) {
		subHandler.post(new Runnable() {

			@Override
			public void run() {
				User user = BaseApplication.getBaseApplication().getCurrentAccount();

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = df.format(new Date());

				ContentValues values = new ContentValues();
				values.put(AppConstants.TBUOrder.Cloum.odate, date);
				values.put(AppConstants.TBUOrder.Cloum.oid, order.orderId);
				values.put(AppConstants.TBUOrder.Cloum.oprice, order.getOrderPrice());
				values.put(AppConstants.TBUOrder.Cloum.ostate, order.orderState.ordinal());
				values.put(AppConstants.TBUOrder.Cloum.type, user.getType());
				values.put(AppConstants.TBUOrder.Cloum.uname, user.getUin());

				addDataQueue(AppConstants.TBUOrder.name, null, values, null, null,
						BaseQueueItem.QUEUE_ITEM_ACTION_INSERT);
			}
		});
	}

	public void commitOrder(String oid) {
		if (unCommitOrder != null && unCommitOrder.orderId.equals(oid)) {
			if (dm == null) {
				dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = df.format(new Date());

			ContentValues values = new ContentValues();
			values.put(AppConstants.TBUOrder.Cloum.odate, date);
			values.put(AppConstants.TBUOrder.Cloum.ostate, OrderState.commit.ordinal());

			addDataQueue(AppConstants.TBUOrder.name, null, values, AppConstants.TBUOrder.Cloum.oid + "=?",
					new String[] { oid }, BaseQueueItem.QUEUE_ITEM_ACTION_UPDATE);

			unCommitOrder = null;
			if (orderMap.contains(oid)) {
				orderMap.get(oid).deleEmptyGoods();
			}

			app.getOrderHandler().onReceive(oid);
		}
	}

	public void addSellerGoods(final String gname, final float price, final byte[] bytes) {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}
		subHandler.post(new Runnable() {

			@Override
			public void run() {
				String uname = BaseApplication.mBaseApplication.getCurrentAccount().getUin();
				ContentValues values = new ContentValues();
				values.put(AppConstants.TBSGoods.Cloum.sname, uname);
				values.put(AppConstants.TBSGoods.Cloum.gname, gname);
				values.put(AppConstants.TBSGoods.Cloum.gprice, price);
				values.put(AppConstants.TBSGoods.Cloum.gpicture, bytes);
				SGoodsRecord record = new SGoodsRecord();
				record.init(uname, gname, price, bytes);
				addDataQueue(AppConstants.TBSGoods.name, record, values, null, null,
						BaseQueueItem.QUEUE_ITEM_ACTION_INSERT);
				app.getGoodsHandler().onReceive(record);
			}
		});
	}

	public void deleteSellerGoods(final String sname, final String gname, final float price) {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}
		subHandler.post(new Runnable() {
			public void run() {
				addDataQueue(AppConstants.TBSGoods.name, null, null,
						AppConstants.TBSGoods.Cloum.sname + "= ? and " + AppConstants.TBSGoods.Cloum.gname + "= ? and "
								+ AppConstants.TBSGoods.Cloum.gprice + "= ?",
						new String[] { sname, gname, String.valueOf(price) }, BaseQueueItem.QUEUE_ITEM_ACTION_DELETE);
			}
		});
	}

	public void addDataQueue(String _tableName, Entity _item, ContentValues _contentValues, String _whereClause,
			String[] _whereArgs, int _action) {
		Log.e(TAG, "addDataQueue tableName=" + _tableName);
		SGoodsqueueItem queueItem = new SGoodsqueueItem(_tableName, _item, _contentValues, _whereClause, _whereArgs,
				_action);
		synchronized (sGoodsQueue) {
			sGoodsQueue.add(queueItem);
		}
		if (isDestroy) {
			Log.e(TAG, "addDataQueue after destroy");
			saveNotify();
		}
	}

	/**
	 * 通知队列存储
	 */
	public void saveNotify() {
		Log.e(TAG, "saveNotify isDestroy=" + isDestroy);
		if (isDestroy) {// 处理退出后的入库请求
			transSaveToDatabase();
		} else {
			synchronized (sGoodsQueue) {
				sGoodsQueue.notify();
			}
		}
	}

	public boolean login(User user) {
		String uin = user.getUin();
		if (userMap.containsKey(uin)) {
			if (userMap.get(uin).equals(user)) {
				BaseApplication.mBaseApplication.setLogin(true);
				BaseApplication.mBaseApplication.setCurrentAccount(user);
				return true;
			}
		}
		return false;
	}

	public void registerUser(final String uin, final String pwd, final int type) {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory().createFruitDBManager();
		}
		subHandler.post(new Runnable() {

			@Override
			public void run() {
				boolean sucess = false;
				ObserverMessage message = new ObserverMessage();
				message.msgId = ObserverMessage.REGISTERUSER;
				if (userMap.containsKey(uin)) {
					message.msg = false;
					message.extra = "用户已存在";
				} else {
					ContentValues values = new ContentValues();
					values.put(AppConstants.TBUser.Cloum.uname, uin);
					values.put(AppConstants.TBUser.Cloum.password, pwd);
					values.put(AppConstants.TBUser.Cloum.type, type);
					long id = dm.insert(AppConstants.TBUser.name, values);
					sucess = id == -1 ? false : true;

					message.msg = sucess;
					if (sucess) {
						BaseApplication.mBaseApplication.setLogin(true);
						BaseApplication.mBaseApplication.setCurrentAccount(new User(uin, pwd, type));
						userMap.put(uin, new User(uin, pwd, type));
						message.extra = "注册成功";
					} else {
						message.extra = "注册失败";
					}
				}
				setChanged();
				notifyObservers(message);
			}
		});
	}

	public Vector<SGoodsqueueItem> getSGoodsQueue() {
		return sGoodsQueue;
	}

	@Override
	public void destroy() {
		isDestroy = true;
		subHandler.post(new Runnable() {

			@Override
			public void run() {
				transSaveToDatabase();
			}
		});
		if (sGoodsQueue != null) {
			synchronized (sGoodsQueue) {
				if (sGoodsQueue != null) {
					sGoodsQueue.notify();
				}
			}
		}
		uOrderMap.clear();
		orderMap.clear();
	}
}
