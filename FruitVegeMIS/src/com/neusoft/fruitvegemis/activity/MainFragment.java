package com.neusoft.fruitvegemis.activity;

import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.adapter.ImageAdapter;
import com.neusoft.fruitvegemis.adapter.ImageAdapter.SGoodsItemHolder;
import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.app.GoodsObserver;
import com.neusoft.fruitvegemis.app.User;
import com.neusoft.fruitvegemis.datapool.SGoodsRecord;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, null);
		BaseApplication.getBaseApplication().addObserver(goodsObserver);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mGridView = (GridView) view.findViewById(R.id.gridview);
		mImageAdapter = new ImageAdapter(getActivity(), this);
		mGridView.setAdapter(mImageAdapter);
		mLoadGoodsTask = new LoadGoodsTask();
		mLoadGoodsTask.execute(null, null, null);

		mShoppingCart = (ImageView) view.findViewById(R.id.shoppingCart);
		mShoppingCart.setVisibility(View.GONE);
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
			FruitVgDBManager fVgDBManager = (FruitVgDBManager) mApp
					.getManager(AppInterface.FRUITVG);
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
			int position = holder.position;
			startAnimation(v);
		}
	}

	private void startAnimation(View v) {
		float fromXDelta = v.getX();
		float fromYDelta = v.getY();
		float toXDelta = 0;
		float toYDelta = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth() / 2;
		TranslateAnimation animation = new TranslateAnimation(fromXDelta,
				toXDelta, fromYDelta, toYDelta);
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
