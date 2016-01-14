package com.neusoft.fruitvegemis.activity;

import java.util.Map;

import android.app.Dialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.adapter.OrderAdapter;
import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.app.OrderObserver;
import com.neusoft.fruitvegemis.datapool.Order;
import com.neusoft.fruitvegemis.datapool.Order.OrderState;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;
import com.neusoft.fruitvegemis.utils.DialogUtils;

public class OrderFragment extends Fragment implements CommitOrderListener {
	public static final String TAG = "OrderFragment";

	private ExpandableListView mOrderList;
	private OrderAdapter mAdapter;
	private AppInterface mApp;
	private Map<String, Order> orders;
	private LoadOrderTask loadOrderTask;
	private Dialog mDialog;

	private static final int REFRESH_ORDER_LIST = 0;
	private Handler uiHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_ORDER_LIST:
				refreshList();
				break;

			default:
				break;
			}
		}
	};

	private OrderObserver orderObserver = new OrderObserver() {

		@Override
		protected void updateUI(String oid) {
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
			}
			if (orders.containsKey(oid)) {
				Order order = orders.get(oid);
				order.orderState = OrderState.commit;
				order.deleEmptyGoods();
				mAdapter.updateUnCommitOrder(oid);
			}
		}
	};

	public static OrderFragment getInstance() {
		OrderFragment fragment = new OrderFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mApp = BaseApplication.mBaseApplication.getAppInterface();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_order, null);
		BaseApplication.getBaseApplication().addObserver(orderObserver);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initUI(view);
		initData();
		loadOrderTask = new LoadOrderTask();
		loadOrderTask.execute(null, null, null);
	}

	private void initUI(View view) {
		mOrderList = (ExpandableListView) view.findViewById(R.id.orderList);
	}

	private void initData() {
		mAdapter = new OrderAdapter(getActivity(), this);
		mOrderList.setAdapter(mAdapter);
	}

	private void refreshList() {
		mAdapter.setData(orders);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (loadOrderTask != null) {
			loadOrderTask.cancel(true);
			loadOrderTask = null;
		}
		BaseApplication.getBaseApplication().removeObserver(orderObserver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	private class LoadOrderTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			FruitVgDBManager fVgDBManager = (FruitVgDBManager) mApp
					.getManager(AppInterface.FRUITVG);
			orders = fVgDBManager.getUserOrder();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			uiHandler.removeMessages(REFRESH_ORDER_LIST);
			Message msg = uiHandler.obtainMessage(REFRESH_ORDER_LIST);
			uiHandler.sendMessage(msg);
		}
	}

	@Override
	public void commitOrder(String oid) {
		if (orders.containsKey(oid)) {
			mDialog = DialogUtils.creatLoadingDialog(getActivity());
			mDialog.show();
			FruitVgDBManager fVgDBManager = (FruitVgDBManager) mApp
					.getManager(AppInterface.FRUITVG);
			fVgDBManager.commitOrder(oid);
		}
	}
}
