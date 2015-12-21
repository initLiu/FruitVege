package com.neusoft.fruitvegemis.persistence;

import java.util.List;
import java.util.Observable;
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
import com.neusoft.fruitvegemis.datapool.SGoodsRecord;
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
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory()
					.createFruitDBManager();
		}
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
						Log.e(TAG, "transSaveToDatabase count=" + optCount
								+ ",tableName=" + tableName);
						switch (queueItem.action) {
						case BaseQueueItem.QUEUE_ITEM_ACTION_INSERT:
							dm.insert(queueItem);
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
			dm = (FruitDBManager) app.getDBManagerFactory()
					.createFruitDBManager();
		}
		String sql = "select * from " + AppConstants.TBSGoods.name;
		return dm.rawQuerySGoods(sql, null);
	}

	public List<SGoodsRecord> querySGoods(String curUin) {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory()
					.createFruitDBManager();
		}
		String sql = "select * from " + AppConstants.TBSGoods.name + " where "
				+ AppConstants.TBSGoods.Cloum.sname + "= ?";
		return dm.rawQuerySGoods(sql, new String[] { curUin });
	}

	private void loadUser() {
		Log.e(TAG, "loaduser");
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory()
					.createFruitDBManager();
		}
		Cursor cursor = dm.query(TBUser.name, null, null, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String uname = cursor.getString(cursor
						.getColumnIndex(TBUser.Cloum.uname));
				String pwd = cursor.getString(cursor
						.getColumnIndex(TBUser.Cloum.password));
				int type = cursor.getInt(cursor
						.getColumnIndex(TBUser.Cloum.type));
				User user = new User(uname, pwd, type);
				Log.e(TAG, "loaduser uin=" + uname);
				synchronized (userMap) {
					userMap.put(uname, user);
				}
			} while (cursor.moveToNext());
		}
	}

	public void addSellerGoods(final String gname, final float price,
			final byte[] bytes) {
		if (dm == null) {
			dm = (FruitDBManager) app.getDBManagerFactory()
					.createFruitDBManager();
		}
		subHandler.post(new Runnable() {

			@Override
			public void run() {
				String uname = BaseApplication.mBaseApplication
						.getCurrentAccount().getUin();
				ContentValues values = new ContentValues();
				values.put(AppConstants.TBSGoods.Cloum.sname, uname);
				values.put(AppConstants.TBSGoods.Cloum.gname, gname);
				values.put(AppConstants.TBSGoods.Cloum.gprice, price);
				values.put(AppConstants.TBSGoods.Cloum.gpicture, bytes);
				SGoodsRecord record = new SGoodsRecord();
				record.init(uname, gname, price, bytes);
				addDataQueue(AppConstants.TBSGoods.name, record, values, null,
						null, BaseQueueItem.QUEUE_ITEM_ACTION_INSERT);
				app.getGoodsHandler().onReceive(record);
			}
		});
	}

	public void addDataQueue(String _tableName, Entity _item,
			ContentValues _contentValues, String _whereClause,
			String[] _whereArgs, int _action) {
		Log.e(TAG, "addDataQueue tableName=" + _tableName);
		SGoodsqueueItem queueItem = new SGoodsqueueItem(_tableName, _item,
				_contentValues, _whereClause, _whereArgs, _action);
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
			dm = (FruitDBManager) app.getDBManagerFactory()
					.createFruitDBManager();
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
						BaseApplication.mBaseApplication
								.setCurrentAccount(new User(uin, pwd, type));
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
		isDestroy = false;
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
	}
}
