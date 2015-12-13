package com.neusoft.fruitvegemis.activity;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.app.User;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText unameText;
	private EditText pwdText;
	private Button loginBtn;
	private TextView registText;
	private RadioButton buyerRBtn;
	private FruitVgDBManager fDbManager;

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
		loginBtn.setOnClickListener(this);
		registText.setOnClickListener(this);
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
			finish();
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
			Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		User user = new User(uin, pwd, type);
		if (fDbManager.login(user)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		} else {
			Toast.makeText(this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		System.exit(0);
	}
}
