package com.neusoft.fruitvegemis.app;


public class OrderObserver implements BusinessObserver {

	@Override
	public void onUpdate(boolean isSuccess, Object data) {
		if (isSuccess) {
			String oid = (String) data;
			updateUI(oid);
		}
	}

	protected void updateUI(String oid) {

	}
}
