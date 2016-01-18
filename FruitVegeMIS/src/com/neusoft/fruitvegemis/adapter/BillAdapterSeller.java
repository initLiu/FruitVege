package com.neusoft.fruitvegemis.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.datapool.Goods;
import com.neusoft.fruitvegemis.datapool.Order;
import com.neusoft.fruitvegemis.utils.UIUtils;

public class BillAdapterSeller extends BaseExpandableListAdapter {
	private List<Order> orders = new ArrayList<Order>();
	private Context mContext;

	public BillAdapterSeller(Context context) {
		mContext = context;
	}

	public void setData(List<Order> data) {
		orders.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		return orders.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return orders.get(groupPosition).getGoods().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return orders.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Order order = (Order) getGroup(groupPosition);
		return order.getGoods().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		Goods goods = (Goods) getChild(groupPosition, childPosition);
		return goods.item_type;
	}

	@Override
	public int getChildTypeCount() {
		return 2;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_bills_seller_group, null);
		}
		TextView textView = (TextView) convertView
				.findViewById(R.id.item_bills_seller_group_oid);
		Order order = (Order) getGroup(groupPosition);
		textView.setText(order.orderId);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		int childType = getChildType(groupPosition, childPosition);
		if (childType == Goods.ITEM_TYPE_GOODS) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_bills_seller_child, null);

				BillsSellerItemHolder itemHolder = new BillsSellerItemHolder();
				itemHolder.gpicture = (ImageView) convertView
						.findViewById(R.id.item_bills_seller_child_gpicture);
				itemHolder.gname = (TextView) convertView
						.findViewById(R.id.item_bills_seller_child_gname);
				itemHolder.gprice = (TextView) convertView
						.findViewById(R.id.item_bills_seller_child_gprice);
				itemHolder.sname = (TextView) convertView
						.findViewById(R.id.item_bills_seller_child_sname);
				convertView.setTag(itemHolder);
			}
			BillsSellerItemHolder itemHolder = (BillsSellerItemHolder) convertView
					.getTag();
			Goods goods = (Goods) getChild(groupPosition, childPosition);

			itemHolder.gpicture.setImageBitmap(UIUtils
					.decodeSampledBitmapFromByte(goods.gpicture, 100, 100));
			itemHolder.gname.setText(goods.gname);
			itemHolder.gprice.setText(goods.gprice + "元");
			itemHolder.sname.setText(goods.sname);

		} else if (childType == Goods.ITEM_TYPE_COMMIT) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_bills_seller_child_commit, null);
				BillsSellerItemHolder itemHolder = new BillsSellerItemHolder();
				itemHolder.odate = (TextView) convertView
						.findViewById(R.id.item_bills_seller_child_odate);
				itemHolder.odate = (TextView) convertView
						.findViewById(R.id.item_bills_seller_child_oprice);

				convertView.setTag(itemHolder);
			}
			BillsSellerItemHolder itemHolder = (BillsSellerItemHolder) convertView
					.getTag();

			Order order = (Order) getGroup(groupPosition);
			itemHolder.odate.setText(order.orderdate);
			itemHolder.oprice.setText(order.getOrderPrice() + "元");
		}

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	class BillsSellerItemHolder extends Itemholder {
		public ImageView gpicture;
		public TextView gname;
		public TextView gprice;
		public TextView sname;
		public TextView odate;
		public TextView oprice;
	}
}
