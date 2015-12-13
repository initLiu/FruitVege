package com.neusoft.fruitvegemis.persistence;

import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.neusoft.fruitvegemis.app.User;
import com.neusoft.fruitvegemis.manager.Manager;
import com.neusoft.fruitvegemis.utils.AppConstants;
import com.neusoft.fruitvegemis.utils.AppConstants.TBUin;
import com.neusoft.fruitvegemis.utils.ObserverMessage;

public class FruitVgDBManager extends Observable implements Manager {

	public static final String TAG = "FruitVgDBManager";
	private SQLiteOpenHelper helper;

	private HandlerThread subThandlerhread;
	private Handler subHandler;
	private ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<String, User>();

	public FruitVgDBManager(Context context) {
		Log.e(TAG, "FruitVgDBManager");
		helper = new SQLiteOpenHelper(context, AppConstants.DBNAME,
				AppConstants.DBVERSION);
		subThandlerhread = new HandlerThread("Sub-Thread");
		subThandlerhread.start();
		subHandler = new Handler(subThandlerhread.getLooper());
		subHandler.post(new Runnable() {

			@Override
			public void run() {
				loadUser();
			}
		});
	}

	private void loadUser() {
		Log.e(TAG, "loaduser");
		Cursor cursor = helper.getWritableDatabase().query(
				AppConstants.TBUin.name, null, null, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String uname = cursor.getString(cursor
						.getColumnIndex(TBUin.Cloum.uname));
				String pwd = cursor.getString(cursor
						.getColumnIndex(TBUin.Cloum.password));
				int type = cursor.getInt(cursor
						.getColumnIndex(TBUin.Cloum.type));
				User user = new User(uname, pwd, type);
				Log.e(TAG, "loaduser uin=" + uname);
				synchronized (userMap) {
					userMap.put(uname, user);
				}
			} while (cursor.moveToNext());
		}
	}

	public boolean login(User user) {
		String uin = user.getUin();
		if (userMap.containsKey(uin)) {
			return userMap.get(uin).equals(user);
		}
		return false;
	}

	public void registerUser(final String uin, final String pwd, final int type) {
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
					// 0卖家 ，1卖家
					sucess = helper.getWritableDatabase().registerUser(uin,
							pwd, type);
					if (sucess) {
						userMap.put(uin, new User(uin, pwd, type));
					}
					message.msg = sucess;
					message.extra = "注册失败";
				}
				setChanged();
				notifyObservers(message);
			}
		});
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}
