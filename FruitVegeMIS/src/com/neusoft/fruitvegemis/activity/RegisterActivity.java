package com.neusoft.fruitvegemis.activity;

import java.util.Observable;
import java.util.Observer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;
import com.neusoft.fruitvegemis.utils.DialogUtils;
import com.neusoft.fruitvegemis.utils.ObserverMessage;

public class RegisterActivity extends BaseActivity implements OnClickListener,
		Observer {

	private EditText unameText, pwdText;
	private Button registBtn;
	private TextView titleLeftText;
	private RadioButton buyerRBtn, sellerRBtn;
	private Dialog loadingDialog;
	public static final int REGISTER_USER_SUCCESS = 0;
	public static final int REGISTER_USER_FAIL = 1;
	public Handler uiHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REGISTER_USER_SUCCESS:
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
				break;
			case REGISTER_USER_FAIL:
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				String reason = (String) msg.obj;
				if (!TextUtils.isEmpty(reason)) {
					Toast.makeText(RegisterActivity.this, reason,
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}

	};

	private FruitVgDBManager fDbManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initUI();
		initTitleBar();
		fDbManager = (FruitVgDBManager) BaseApplication.mBaseApplication
				.getAppInterface().getManager(AppInterface.FRUITVG);
		fDbManager.addObserver(this);
	}

	private void initUI() {
		unameText = (EditText) findViewById(R.id.register_uin);
		pwdText = (EditText) findViewById(R.id.register_password);
		registBtn = (Button) findViewById(R.id.register_rg);
		registBtn.setOnClickListener(this);
		titleLeftText = (TextView) findViewById(R.id.title_btn_left);
		titleLeftText.setOnClickListener(this);
		buyerRBtn = (RadioButton) findViewById(R.id.register_typeBuyer);
		sellerRBtn = (RadioButton) findViewById(R.id.register_typeSeller);
	}

	private void initTitleBar() {
		titleLeftText.setVisibility(View.VISIBLE);
		titleLeftText.setText(R.string.title_back);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_rg:
			loadingDialog = DialogUtils.creatLoadingDialog(this);
			loadingDialog.show();
			String uin = unameText.getText().toString();
			String pwd = pwdText.getText().toString();
			int type = buyerRBtn.isChecked() ? 0 : 1;
			if (TextUtils.isEmpty(uin) || TextUtils.isEmpty(pwd)) {
				Toast.makeText(this, "用户名或密码不能空", Toast.LENGTH_SHORT).show();
				break;
			}
			fDbManager.registerUser(uin, pwd, type);
			break;
		case R.id.title_btn_left:
			finish();
			overridePendingTransition(R.anim.activity_back_in,
					R.anim.activity_back_out);
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		ObserverMessage message = (ObserverMessage) data;
		Object msg = message.msg;
		switch (message.msgId) {
		case ObserverMessage.REGISTERUSER:
			boolean success = (Boolean) msg;
			if (success) {
				uiHandler.removeMessages(REGISTER_USER_SUCCESS);
				uiHandler.sendEmptyMessage(REGISTER_USER_SUCCESS);
			} else {
				Message message2 = uiHandler.obtainMessage(REGISTER_USER_FAIL,
						message.extra);
				uiHandler.removeMessages(REGISTER_USER_FAIL);
				uiHandler.sendMessage(message2);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void finish() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
		super.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
