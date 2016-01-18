package com.neusoft.fruitvegemis.activity;

import java.util.List;

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
import com.neusoft.fruitvegemis.adapter.BillAdapter;
import com.neusoft.fruitvegemis.adapter.BillAdapterSeller;
import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.datapool.Order;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;

public class BillFragmentSeller extends Fragment {
	public static final String TAG = "BillFragment";

	private ExpandableListView mListView;
	private BillAdapterSeller mAdapter;
	private LoadBillTask mBillTask;
	private AppInterface mApp;
	private List<Order> orders;

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

	public static BillFragmentSeller getInstance() {
		BillFragmentSeller fragment = new BillFragmentSeller();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = BaseApplication.mBaseApplication.getAppInterface();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bill_seller, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListView = (ExpandableListView) view
				.findViewById(R.id.fragment_bill_list_seller);

		mAdapter = new BillAdapterSeller(getActivity());
		mListView.setAdapter(mAdapter);

		mBillTask = new LoadBillTask();
		mBillTask.execute(null, null, null);
	}

	private void refreshList() {
		mAdapter.setData(orders);
	}

	private class LoadBillTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			FruitVgDBManager fVgDBManager = (FruitVgDBManager) mApp
					.getManager(AppInterface.FRUITVG);
			String seller = ((MainActivity) getActivity()).getCurrentUser()
					.getUin();
			orders = fVgDBManager.getUserCommitOrderForSeller(seller);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			uiHandler.removeMessages(REFRESH_ORDER_LIST);
			Message msg = uiHandler.obtainMessage(REFRESH_ORDER_LIST);
			uiHandler.sendMessage(msg);
		}
	}
}
