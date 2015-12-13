package com.neusoft.fruitvegemis.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;

public class CutomDialog extends Dialog {

	boolean disableback = true;

	public CutomDialog(Context context, int themeResId) {
		super(context, themeResId);
	}

	public CutomDialog(Context context) {
		super(context);
	}

	public void setdiableBackKey(boolean disableback) {
		this.disableback = disableback;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (disableback) {
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
