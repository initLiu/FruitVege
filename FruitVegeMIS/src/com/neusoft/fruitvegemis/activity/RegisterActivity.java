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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;
import com.neusoft.fruitvegemis.utils.DialogUtils;
import com.neusoft.fruitvegemis.utils.ObserverMessage;
import com.neusoft.fruitvegemis.widget.InputMethodRelativeLayout;
import com.neusoft.fruitvegemis.widget.InputMethodRelativeLayout.onSizeChangedListenner;

public class RegisterActivity extends BaseActivity implements OnClickListener,
		onSizeChangedListenner, Observer {

	private EditText unameText, pwdText;
	private Button registBtn;
	private TextView titleLeftText;
	private RadioButton buyerRBtn, sellerRBtn;
	private Dialog loadingDialog;
	private InputMethodRelativeLayout rootLayout;
	private RelativeLayout registScrollLayout;
	public static final int REGISTER_USER_SUCCESS = 0;
	public static final int REGISTER_USER_FAIL = 1;
	public static final int CREATE_REGISTER_LOADING_DIALOG = 1;
	public Handler uiHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REGISTER_USER_SUCCESS:
				dismissDialog(CREATE_REGISTER_LOADING_DIALOG);
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
				break;
			case REGISTER_USER_FAIL:
				dismissDialog(CREATE_REGISTER_LOADING_DIALOG);
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
		rootLayout = (InputMethodRelativeLayout) findViewById(R.id.regist_root);
		registScrollLayout = (RelativeLayout) findViewById(R.id.regist_scroll);
		rootLayout.setOnSizeChangeListener(this);
	}

	private void initTitleBar() {
		titleLeftText.setVisibility(View.VISIBLE);
		titleLeftText.setText(R.string.title_back);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_rg:
			showDialog(CREATE_REGISTER_LOADING_DIALOG);
			String uin = unameText.getText().toString();
			String pwd = pwdText.getText().toString();
			int type = buyerRBtn.isChecked() ? 0 : 1;
			if (TextUtils.isEmpty(uin) || TextUtils.isEmpty(pwd)) {
				Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_SHORT)
						.show();
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
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case CREATE_REGISTER_LOADING_DIALOG:
			dialog = DialogUtils.creatLoadingDialog(this);
			break;

		default:
			break;
		}
		return dialog;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSizeChange(boolean isOpen, int preH, int curH) {
		Log.e("Test", "LoginActivity onSizeChange isOpen:" + isOpen + ",preH="
				+ preH + ",curH=" + curH);
		if (isOpen) {// 为了显示登录按钮我ui向上推一下
			int[] location = new int[2];
			registBtn.getLocationInWindow(location);
			int loginBtnY = location[1];
			rootLayout.getLocationInWindow(location);
			int rootY = location[1];
			int paddingY = loginBtnY - rootY + registBtn.getHeight() - curH;
			Log.e("Test", "LoginActivity onSizeChange loginBtnY=" + loginBtnY
					+ ",rootY=" + rootY + ",paddingY=" + paddingY);
			if (paddingY > 0) {
				registScrollLayout.setPadding(
						registScrollLayout.getPaddingLeft(),
						registScrollLayout.getPaddingTop() - paddingY,
						registScrollLayout.getPaddingRight(),
						registScrollLayout.getPaddingBottom());
			}
		} else {
			registScrollLayout.setPadding(0, 0, 0, 0);
		}
	}
}
