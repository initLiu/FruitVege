package com.neusoft.fruitvegemis.adapter;

import java.util.ArrayList;
import java.util.List;

import com.neusoft.fruitvegemis.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DrawerAdapter extends BaseAdapter {

	private List<String> drawerItems = new ArrayList<String>();
	private Context context;

	public DrawerAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<String> data) {
		drawerItems = data;
	}

	@Override
	public int getCount() {
		return drawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return drawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.drawer_item, null);
		}
		TextView drawerItem = (TextView) convertView
				.findViewById(R.id.drawer_item);
		String content = (String) getItem(position);
		drawerItem.setText(content);
		convertView.setTag(content);
		return convertView;
	}
}
