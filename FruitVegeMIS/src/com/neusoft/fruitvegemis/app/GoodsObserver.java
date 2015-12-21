package com.neusoft.fruitvegemis.app;

import com.neusoft.fruitvegemis.datapool.SGoodsRecord;

public class GoodsObserver implements BusinessObserver {

	@Override
	public void onUpdate(boolean isSuccess, Object data) {
		if (isSuccess) {
			SGoodsRecord record = (SGoodsRecord) data;
			updateUI(record);
		}
	}

	protected void updateUI(SGoodsRecord record) {

	}
}
