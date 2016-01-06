package com.neusoft.fruitvegemis.datapool;

import java.util.ArrayList;
import java.util.List;

import com.neusoft.fruitvegemis.persistence.Entity;

public class Order extends Entity {
	private List<Goods> goodsList = new ArrayList<Goods>();
	public final String orderId;
	public OrderState orderState;

	public enum OrderState {
		unCommit, commit
	}

	public Order() {
		orderId = Long.toString(System.currentTimeMillis());
	}

	public void addGoods(Goods goods) {
		goodsList.add(goods);
	}

	public float getOrderPrice() {
		float rst = 0;
		for (Goods goods : goodsList) {
			rst += goods.gprice;
		}
		return rst;
	}
}
