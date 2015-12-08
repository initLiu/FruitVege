package com.neusoft.fruitvegemis.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.app.BaseApplication;

public class SplashActivity extends BaseActivity {

	private ImageView splashView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		showSplash();
	}

	private void showSplash() {
		boolean isSplash = BaseApplication.getBaseApplication().getSplash();
		if (isSplash) {
			splashView = (ImageView) findViewById(R.id.splashView);
			ObjectAnimator animator = ObjectAnimator.ofFloat(splashView,
					"alpha", 0f, 1f);
			animator.setDuration(2000);
			animator.start();
			animator.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							startMainActivity();
						}
					}, 500);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub

				}
			});
			BaseApplication.getBaseApplication().setSplash(false);
		} else {
			startMainActivity();
		}
	}

	public void startMainActivity() {
		Intent intent = new Intent();
		boolean isLogin = BaseApplication.getBaseApplication().getLogin();
		if (!isLogin) {
			intent.setClass(this, LoginActivity.class);
		} else {
			intent.setClass(this, MainActivity.class);
		}
		startActivity(intent);
		finish();
	}
}
