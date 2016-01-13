package com.neusoft.fruitvegemis.app;

public class OrderHandler extends BusinessHandler {

	public OrderHandler(BaseApplication app) {
		super(app);
	}

	@Override
	public void onReceive(Object data) {
		notifyUI(data);
	}
}
