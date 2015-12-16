package com.neusoft.fruitvegemis.persistence;

import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.app.User;
import com.neusoft.fruitvegemis.manager.Manager;
import com.neusoft.fruitvegemis.utils.AppConstants;
import com.neusoft.fruitvegemis.utils.AppConstants.TBUser;
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
		Cursor cursor = helper.getWritableDatabase().query(TBUser.name, null,
				null, null, null, null, null);
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
					sucess = helper.getWritableDatabase().registerUser(uin,
							pwd, type);
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

	@Override
	public void destroy() {
		helper.close();
	}
}
