package com.neusoft.fruitvegemis.activity;

import java.util.List;

import org.w3c.dom.Text;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.adapter.ImageAdapter;
import com.neusoft.fruitvegemis.adapter.ImageAdapter.SGoodsItemHolder;
import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.app.GoodsObserver;
import com.neusoft.fruitvegemis.app.User;
import com.neusoft.fruitvegemis.datapool.Goods;
import com.neusoft.fruitvegemis.datapool.SGoodsRecord;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;
import com.neusoft.fruitvegemis.utils.CustomMenu;
import com.neusoft.fruitvegemis.utils.CustomMenuItem;
import com.neusoft.fruitvegemis.utils.ItemContextMenu;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFragment extends Fragment implements Callback, OnClickListener {

	public static final String TAG = "MainFragment";
	private int mUserType;
	private GridView mGridView;
	private ImageView mShoppingCart;
	private ImageAdapter mImageAdapter;
	private Handler uiHandler;
	private AppInterface mApp;
	private List<SGoodsRecord> goodsRecords;
	private LoadGoodsTask mLoadGoodsTask;
	public static final int REFRESH_GOODS_LIST = 0;

	public static MainFragment getInstance(int type) {
		MainFragment fragment = new MainFragment();
		Bundle arg = new Bundle();
		arg.putInt("type", type);
		fragment.setArguments(arg);
		return fragment;
	}

	private GoodsObserver goodsObserver = new GoodsObserver() {

		@Override
		protected void updateUI(SGoodsRecord record) {
			goodsRecords.clear();
			goodsRecords.add(record);

			uiHandler.removeMessages(REFRESH_GOODS_LIST);
			Message msg = uiHandler.obtainMessage(REFRESH_GOODS_LIST);
			uiHandler.sendMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mUserType = getArguments().getInt("type", 0);
		uiHandler = new Handler(Looper.getMainLooper(), this);
		mApp = BaseApplication.mBaseApplication.getAppInterface();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, null);
		BaseApplication.getBaseApplication().addObserver(goodsObserver);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mGridView = (GridView) view.findViewById(R.id.gridview);
		if (mUserType == 1) {
			mImageAdapter = new ImageAdapter(getActivity());
		} else if (mUserType == 0) {
			mImageAdapter = new ImageAdapter(getActivity(), this);
		}
		mGridView.setAdapter(mImageAdapter);

		if (mUserType == 1) {
			mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					showContextMenu(view);
					return true;
				}
			});
		}

		mLoadGoodsTask = new LoadGoodsTask();
		mLoadGoodsTask.execute(null, null, null);

		mShoppingCart = (ImageView) view.findViewById(R.id.shoppingCart);
		mShoppingCart.setVisibility(View.GONE);
	}

	private void showContextMenu(View view) {
		final SGoodsItemHolder holder = (SGoodsItemHolder) view.getTag();
		CustomMenu menu = new CustomMenu();
		menu.add(0, "删除");
		ItemContextMenu.showAsDropDown(view, menu, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v instanceof TextView) {
					String menuItem = ((TextView) v).getText().toString();
					if (!TextUtils.isEmpty(menuItem) && menuItem.equals("删除")) {
						deleteGoods(holder.sname, holder.gname, holder.gprice, holder.position);
					}
				}
			}
		});
	}

	private void deleteGoods(String sname, String gname, float gprice, int position) {
		FruitVgDBManager dbManager = (FruitVgDBManager) BaseApplication.mBaseApplication.getAppInterface()
				.getManager(AppInterface.FRUITVG);
		dbManager.deleteSellerGoods(sname, gname, gprice);
		mImageAdapter.deleteItem(position);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (mUserType == 1) {
			inflater.inflate(R.menu.main1, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add) {
			addGoods();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addGoods() {
		Intent intent = new Intent(getActivity(), UploadDialogActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case REFRESH_GOODS_LIST:
			if (goodsRecords != null) {
				mImageAdapter.setDataList(goodsRecords);
			}
			return true;

		default:
			break;
		}
		return false;
	}

	private class LoadGoodsTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			FruitVgDBManager fVgDBManager = (FruitVgDBManager) mApp.getManager(AppInterface.FRUITVG);
			User user = BaseApplication.mBaseApplication.getCurrentAccount();
			String uin = null;
			if (mUserType == 0) {
				goodsRecords = fVgDBManager.querySGoods();
			} else {
				goodsRecords = fVgDBManager.querySGoods(user.getUin());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			uiHandler.removeMessages(REFRESH_GOODS_LIST);
			Message msg = uiHandler.obtainMessage(REFRESH_GOODS_LIST);
			uiHandler.sendMessage(msg);
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (mLoadGoodsTask != null) {
			mLoadGoodsTask.cancel(true);
			mLoadGoodsTask = null;
		}
		BaseApplication.getBaseApplication().removeObserver(goodsObserver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (uiHandler != null) {
			uiHandler.removeMessages(REFRESH_GOODS_LIST);
			uiHandler = null;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() instanceof SGoodsItemHolder) {
			SGoodsItemHolder holder = (SGoodsItemHolder) v.getTag();
			startAnimation(v);
			addGoodToOrder(holder);
		}
	}

	private void addGoodToOrder(SGoodsItemHolder goods) {
		FruitVgDBManager dbManager = (FruitVgDBManager) BaseApplication.mBaseApplication.getAppInterface()
				.getManager(AppInterface.FRUITVG);
		dbManager.addGoods2Order(new Goods(goods.gname, goods.gprice, goods.sname, goods.gpicture));
	}

	private void startAnimation(View v) {
		float fromXDelta = v.getX();
		float fromYDelta = v.getY();
		float toXDelta = 0;
		float toYDelta = getActivity().getWindowManager().getDefaultDisplay().getWidth() / 2;
		TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				mShoppingCart.setVisibility(View.GONE);
			}
		});
		animation.setFillAfter(false);
		animation.setDuration(1000l);
		animation.setInterpolator(new LinearInterpolator());

		mShoppingCart.setVisibility(View.VISIBLE);
		mShoppingCart.startAnimation(animation);
	}
}
