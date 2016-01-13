package com.neusoft.fruitvegemis.datapool;

import java.util.ArrayList;
import java.util.List;

import com.neusoft.fruitvegemis.persistence.Entity;

public class Order extends Entity {
	private List<Goods> goodsList = new ArrayList<Goods>();
	public final String orderId;
	public OrderState orderState;
	public String orderdate;
	private Goods emptyGoods;

	public enum OrderState {
		unCommit, commit
	}

	public Order(String oid) {
		orderId = oid != null ? oid : Long.toString(System.currentTimeMillis());
	}

	public Order() {
		orderId = Long.toString(System.currentTimeMillis());
	}

	public void addGoods(Goods goods) {
		Goods tmp = emptyGoods;
		deleEmptyGoods();
		goodsList.add(goods);
		addEmptyGoods(tmp);

	}

	public void addEmptyGoods(Goods goods) {
		if (emptyGoods == null) {
			emptyGoods = goods;
			goodsList.add(emptyGoods);
		}
	}

	public void deleEmptyGoods() {
		goodsList.remove(emptyGoods);
		emptyGoods = null;
	}

	public List<Goods> getGoods() {
		return goodsList;
	}

	public float getOrderPrice() {
		float rst = 0;
		for (Goods goods : goodsList) {
			rst += goods.gprice;
		}
		return rst;
	}
}
