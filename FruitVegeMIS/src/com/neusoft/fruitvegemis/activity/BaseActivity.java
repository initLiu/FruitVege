package com.neusoft.fruitvegemis.activity;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.app.BaseApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BaseApplication.mBaseApplication.addBaseActivity(this);
	}

	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
		overridePendingTransition(R.anim.activity_new_out,
				R.anim.activity_new_in);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.mBaseApplication.removeBaseActivity(this);
	}
}
