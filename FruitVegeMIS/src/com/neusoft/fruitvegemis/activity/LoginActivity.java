package com.neusoft.fruitvegemis.activity;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.app.User;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;
import com.neusoft.fruitvegemis.widget.InputMethodRelativeLayout;
import com.neusoft.fruitvegemis.widget.InputMethodRelativeLayout.onSizeChangedListenner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener,
		onSizeChangedListenner {

	private EditText unameText;
	private EditText pwdText;
	private Button loginBtn;
	private TextView registText;
	private RadioButton buyerRBtn;
	private FruitVgDBManager fDbManager;
	private InputMethodRelativeLayout rootLayout;
	private RelativeLayout loginScrollLayaout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initUI();
		fDbManager = (FruitVgDBManager) BaseApplication.mBaseApplication
				.getAppInterface().getManager(AppInterface.FRUITVG);
	}

	private void initUI() {
		unameText = (EditText) findViewById(R.id.uin);
		pwdText = (EditText) findViewById(R.id.password);
		loginBtn = (Button) findViewById(R.id.login);
		registText = (TextView) findViewById(R.id.register);
		buyerRBtn = (RadioButton) findViewById(R.id.typeBuyer);
		rootLayout = (InputMethodRelativeLayout) findViewById(R.id.login_root);
		loginScrollLayaout = (RelativeLayout) findViewById(R.id.login_scorll);
		loginBtn.setOnClickListener(this);
		registText.setOnClickListener(this);
		rootLayout.setOnSizeChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			login();
			break;
		case R.id.register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void login() {
		String uin = unameText.getText().toString();
		String pwd = pwdText.getText().toString();
		int type = buyerRBtn.isChecked() ? 0 : 1;
		if (TextUtils.isEmpty(uin) || TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		User user = new User(uin, pwd, type);
		if (fDbManager.login(user)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		} else {
			Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		BaseApplication.mBaseApplication.getAppInterface().exit();
		if (!isFinishing()) {
			finish();
		}
	}

	@Override
	public void onSizeChange(boolean isOpen, int preH, int curH) {
		Log.e("Test", "LoginActivity onSizeChange isOpen:" + isOpen + ",preH="
				+ preH + ",curH=" + curH);
		if (isOpen) {// 为了显示登录按钮我ui向上推一下
			int[] location = new int[2];
			loginBtn.getLocationInWindow(location);
			int loginBtnY = location[1];
			rootLayout.getLocationInWindow(location);
			int rootY = location[1];
			int paddingY = loginBtnY - rootY + loginBtn.getHeight() - curH;
			Log.e("Test", "LoginActivity onSizeChange loginBtnY=" + loginBtnY
					+ ",rootY=" + rootY + ",paddingY=" + paddingY);
			if (paddingY > 0) {
				loginScrollLayaout.setPadding(
						loginScrollLayaout.getPaddingLeft(),
						loginScrollLayaout.getPaddingTop() - paddingY,
						loginScrollLayaout.getPaddingRight(),
						loginScrollLayaout.getPaddingBottom());
				registText.setVisibility(View.GONE);
			}
		} else {
			loginScrollLayaout.setPadding(0, 0, 0, 0);
			registText.setVisibility(View.VISIBLE);
		}
	}
}
