package com.neusoft.fruitvegemis.adapter;

import java.util.ArrayList;
import java.util.List;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.datapool.SGoodsRecord;
import com.neusoft.fruitvegemis.utils.UIUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private List<SGoodsRecord> goodsList = new ArrayList<SGoodsRecord>();
	private OnClickListener mClickListener;

	public ImageAdapter(Context context, OnClickListener listener) {
		mContext = context;
		this.mClickListener = listener;
	}

	public ImageAdapter(Context context) {
		this(context, null);
	}

	public void setDataList(List<SGoodsRecord> list) {
		goodsList.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return goodsList.size();
	}

	@Override
	public Object getItem(int position) {
		return goodsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void deleteItem(int position) {
		goodsList.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods, null);
			SGoodsItemHolder holder = new SGoodsItemHolder();
			convertView.setTag(holder);
		}
		if (mClickListener != null) {
			convertView.setOnClickListener(mClickListener);
		}
		ImageView pic = (ImageView) convertView.findViewById(R.id.item_pict);
		TextView gname = (TextView) convertView.findViewById(R.id.item_name);
		TextView gprice = (TextView) convertView.findViewById(R.id.item_price);

		SGoodsRecord record = goodsList.get(position);

		SGoodsItemHolder holder = (SGoodsItemHolder) convertView.getTag();

		holder.gname = record.gname;
		holder.gprice = record.gprice;
		holder.sname = record.sname;
		holder.gpicture = record.gpicture;
		holder.position = position;
		convertView.setTag(holder);

		pic.setImageBitmap(UIUtils.decodeSampledBitmapFromByte(holder.gpicture, 100, 100));
		gname.setText(holder.gname);
		gprice.setText(holder.gprice + "");
		return convertView;
	}

	// private Bitmap byte2Bitmap(byte[] pic) {
	// BitmapFactory.Options opt = new BitmapFactory.Options();
	// DisplayMetrics dm = new DisplayMetrics();
	//
	// ((Activity) mContext).getWindowManager().getDefaultDisplay()
	// .getMetrics(dm);
	//
	// Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
	//
	// float fHS = (float) dm.heightPixels / (float) bitmap.getHeight();
	//
	// float fWS = (float) dm.widthPixels / (float) bitmap.getWidth();
	//
	// float fFinalS = Math.min(fHS, fWS);
	//
	// opt.inSampleSize = (int) (1 / fFinalS);
	// opt.inPreferredConfig = Config.RGB_565;
	//
	// bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length, opt);
	// return bitmap;
	// }

	public static class SGoodsItemHolder extends Itemholder {
		public String gname;
		public float gprice;
		public String sname;
		public byte[] gpicture;
		public int position;
	}
}
