package com.neusoft.fruitvegemis.adapter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.activity.CommitOrderListener;
import com.neusoft.fruitvegemis.datapool.Goods;
import com.neusoft.fruitvegemis.datapool.Order;
import com.neusoft.fruitvegemis.datapool.Order.OrderState;
import com.neusoft.fruitvegemis.utils.UIUtils;

public class OrderAdapter extends BaseExpandableListAdapter implements
		OnClickListener {

	public static final String TAG = "OrderAdapter";
	private Context mContext;
	private Map<String, Order> orders = new HashMap<String, Order>();
	private Set<String> oids = new TreeSet<String>(new CustomComparator());

	private CommitOrderListener mListener;

	public OrderAdapter(Context context, CommitOrderListener listener) {
		mContext = context;
		mListener = listener;
	}

	public void setData(Map<String, Order> datas) {
		orders.putAll(datas);
		oids.addAll(orders.keySet());
		notifyDataSetChanged();
	}

	public void updateUnCommitOrder(String oid) {
		if (orders.containsKey(oid)) {
			Order order = orders.get(oid);
			order.orderState = OrderState.commit;
			order.deleEmptyGoods();
			notifyDataSetChanged();
		}
	}

	@Override
	public int getGroupCount() {
		return oids.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		String oid = (String) oids.toArray()[groupPosition];
		if (orders.containsKey(oid)) {
			return orders.get(oid).getGoods().size();
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return oids.toArray()[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		String oid = (String) getGroup(groupPosition);
		return orders.containsKey(oid) ? orders.get(oid).getGoods()
				.get(childPosition) : null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getGroupPosition(String orderId) {
		Object[] arrOid = oids.toArray();
		int len = arrOid.length;
		for (int i = 0; i < len; i++) {
			if (arrOid[i].equals(orderId)) {
				return i;
			}
		}
		return -1;
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
					R.layout.item_order_group, null);
		}
		TextView textView = (TextView) convertView
				.findViewById(R.id.item_order_group_oid);
		textView.setText("订单号:" + getGroup(groupPosition) + "");

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		int type = getChildType(groupPosition, childPosition);
		if (type == Goods.ITEM_TYPE_GOODS) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_order_child, null);
				OrderItemHolder itemHolder = new OrderItemHolder();
				itemHolder.gpicture = (ImageView) convertView
						.findViewById(R.id.item_order_child_gpicture);
				itemHolder.gname = (TextView) convertView
						.findViewById(R.id.item_order_child_gname);
				itemHolder.gprice = (TextView) convertView
						.findViewById(R.id.item_order_child_gprice);
				itemHolder.sname = (TextView) convertView
						.findViewById(R.id.item_order_child_sname);
				convertView.setTag(itemHolder);
			}
			OrderItemHolder itemHolder = (OrderItemHolder) convertView.getTag();
			Goods goods = (Goods) getChild(groupPosition, childPosition);

			itemHolder.gpicture.setImageBitmap(UIUtils
					.decodeSampledBitmapFromByte(goods.gpicture, 100, 100));
			itemHolder.gname.setText(goods.gname);
			itemHolder.gprice.setText(goods.gprice + "元");
			itemHolder.sname.setText(goods.sname);
		} else if (type == Goods.ITEM_TYPE_COMMIT) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_order_child_commit, null);
				OrderItemHolder itemHolder = new OrderItemHolder();
				itemHolder.commit = (Button) convertView
						.findViewById(R.id.item_order_child_commit);
				itemHolder.commit.setTag(getGroup(groupPosition));
				itemHolder.commit.setOnClickListener(this);
				convertView.setTag(itemHolder);
			}
			OrderItemHolder itemHolder = (OrderItemHolder) convertView.getTag();
			String oid = (String) getGroup(groupPosition);
			if (orders.containsKey(oid)) {
				if (orders.get(oid).orderState == OrderState.unCommit) {
					itemHolder.commit.setText(mContext.getResources()
							.getString(R.string.commit_order));
				}
			}
		}

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.commitOrder(v.getTag().toString());
		}
	}

	class OrderItemHolder extends Itemholder {
		public ImageView gpicture;
		public TextView gname;
		public TextView gprice;
		public TextView sname;
		public Button commit;
	}

	class CustomComparator implements Comparator<String> {

		@Override
		public int compare(String lhs, String rhs) {
			return lhs.compareTo(rhs) > 0 ? -1 : 1;
		}
	}
}
