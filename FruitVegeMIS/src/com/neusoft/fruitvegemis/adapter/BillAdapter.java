package com.neusoft.fruitvegemis.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.datapool.Order;

public class BillAdapter extends BaseAdapter {

	private Context mContext;
	private List<Order> orders = new ArrayList<Order>();

	public BillAdapter(Context context) {
		mContext = context;
	}

	public void setData(List<Order> data) {
		orders.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orders.size();
	}

	@Override
	public Object getItem(int position) {
		return orders.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_bills, null);
			BillItemHolder itemHolder = new BillItemHolder();
			itemHolder.orderDate = (TextView) convertView
					.findViewById(R.id.item_bills_orderdate);
			itemHolder.orderId = (TextView) convertView
					.findViewById(R.id.item_bills_orderid);
			itemHolder.orderPrice = (TextView) convertView
					.findViewById(R.id.item_bills_orderprice);
			convertView.setTag(itemHolder);
		}
		BillItemHolder itemHolder = (BillItemHolder) convertView.getTag();
		Order order = (Order) getItem(position);

		itemHolder.orderDate.setText(order.orderdate);
		itemHolder.orderId.setText("订单号:" + order.orderId);
		itemHolder.orderPrice.setText("价格:" + order.getOrderPrice() + "元");
		itemHolder.oid = order.orderId;
		return convertView;
	}

	public static class BillItemHolder extends Itemholder {
		public TextView orderDate;
		public TextView orderId;
		public TextView orderPrice;
		public String oid;
	}
}
