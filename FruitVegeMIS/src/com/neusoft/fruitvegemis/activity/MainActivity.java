package com.neusoft.fruitvegemis.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.adapter.DrawerAdapter;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.app.User;

public class MainActivity extends BaseActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private DrawerAdapter mAdapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private static final int[] drawerItems_buyer = new int[] {
			R.string.draweritem_main, R.string.draweritem_order,
			R.string.draweritem_bill };

	private static final int[] drawerItems_seller = new int[] {
			R.string.draweritem_main, R.string.draweritem_bill };

	private User currentAccout;
	private int curPos = -1,clickPos = -1;
	private String tmpTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().show();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);

		initUI();
		initData();
	}

	@Override
	public void setTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}

	private void initData() {
		curPos = -1;
		currentAccout = BaseApplication.mBaseApplication.getCurrentAccount();
		List<String> items = new ArrayList<String>();
		if (currentAccout.getType() == 0) {
			for (int id : drawerItems_buyer) {
				items.add(getResources().getString(id));
			}
		} else {
			for (int id : drawerItems_seller) {
				items.add(getResources().getString(id));
			}
		}

		mAdapter = new DrawerAdapter(this);
		mAdapter.setData(items);
		mDrawerList.setAdapter(mAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		Object obj = mAdapter.getItem(0);
		String title = obj != null ? (String) obj
				: getString(R.string.app_name);
		setSelectItem(title, 0);
	}

	private class DrawerItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mDrawerLayout.closeDrawer(Gravity.START);
			tmpTitle = (String) view.getTag();
			clickPos = position;
		}
	}

	private void setSelectItem(String title, int position) {
		if (curPos == position) {
			return;
		}
		if (currentAccout.getType() == 0) {
			setSelectItemBuyer(title, position);
		} else {
			setSelectItemSeller(title, position);
		}
		curPos = position;
	}

	private void setSelectItemBuyer(String title, int position) {
		setTitle(title);
		switch (position) {
		case 0:
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame,
							MainFragment.getInstance(currentAccout.getType()),
							MainFragment.TAG).commitAllowingStateLoss();
			break;
		case 1:
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, OrderFragment.getInstance(),
							MainFragment.TAG).commitAllowingStateLoss();
		case 2:
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, BillFragment.getInstance(),
							MainFragment.TAG).commitAllowingStateLoss();
			break;
		default:
			break;
		}
	}

	private void setSelectItemSeller(String title, int position) {
		setTitle(title);
		switch (position) {
		case 0:
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame,
							MainFragment.getInstance(currentAccout.getType()),
							MainFragment.TAG).commitAllowingStateLoss();
			break;
		case 1:
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, BillFragment.getInstance(),
							MainFragment.TAG).commitAllowingStateLoss();
			break;
		default:
			break;
		}
	}

	private void initUI() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_root);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.open_drawer, R.string.close_drawer) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				setSelectItem(tmpTitle, clickPos);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
			}

			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.action_exit:
					exitApp();
					return true;
				default:
					break;
				}
				return super.onOptionsItemSelected(item);
			}

		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	protected void exitApp() {
		BaseApplication.mBaseApplication.getAppInterface().release();
		BaseApplication.mBaseApplication.setLogin(false);
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		MainActivity.this.startActivity(intent);
		MainActivity.this.finish();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawer(Gravity.START);
			} else {
				moveTaskToBack(true);
			}
			return true;
		default:
			break;
		}
		return false;
	}
}
