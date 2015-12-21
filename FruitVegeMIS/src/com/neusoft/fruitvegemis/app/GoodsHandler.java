package com.neusoft.fruitvegemis.app;

public class GoodsHandler extends BusinessHandler {

	public GoodsHandler(BaseApplication app) {
		super(app);
	}

	@Override
	public void onReceive(Object data) {
		notifyUI(data);
	}
}
