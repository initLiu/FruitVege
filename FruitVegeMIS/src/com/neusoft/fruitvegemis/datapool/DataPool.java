package com.neusoft.fruitvegemis.datapool;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

public class DataPool {
	public static final String TAG = "DataPool";
	private ConcurrentHashMap<String, List<SGoodsRecord>> sgoodPool;
	private static ConcurrentHashMap<String, DataPool> poolInstanceMap = new ConcurrentHashMap<String, DataPool>();
	private static final String NULL_UIN = "null";
	protected ConcurrentHashMap<String, Object> lockMap;

	public static DataPool getPoolInstance(String currAccountUin) {
		if (currAccountUin == null) {
			currAccountUin = NULL_UIN;
			Log.e(TAG, "getPoolInstance currAccountUin is null");
		}
		if (!poolInstanceMap.contains(currAccountUin)) {
			synchronized (poolInstanceMap) {
				if (!poolInstanceMap.containsKey(currAccountUin)) {
					poolInstanceMap.put(currAccountUin, new DataPool());
				}
			}
		}
		return poolInstanceMap.get(currAccountUin);
	}

	public static void removePoolInstance(String currAccountUin) {
		if (currAccountUin == null) {
			currAccountUin = NULL_UIN;
			Log.e(TAG, "getPoolInstance currAccountUin is null");
		}
		if (poolInstanceMap.containsKey(currAccountUin)) {
			poolInstanceMap.remove(currAccountUin);
		}
	}

	private DataPool() {
		sgoodPool = new ConcurrentHashMap<String, List<SGoodsRecord>>();
		lockMap = new ConcurrentHashMap<String, Object>();
	}

	public ConcurrentHashMap<String, List<SGoodsRecord>> getSGoodPool() {
		return sgoodPool;
	}

	public Object getDataPoolLock(String curUin) {
		if (!lockMap.containsKey(curUin)) {
			synchronized (lockMap) {
				if (!lockMap.containsKey(curUin)) {
					lockMap.put(curUin, new Object());
				}
			}
		}
		return lockMap.get(curUin);
	}
}
