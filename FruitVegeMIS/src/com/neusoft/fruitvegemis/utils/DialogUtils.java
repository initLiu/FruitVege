package com.neusoft.fruitvegemis.utils;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.widget.CutomDialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class DialogUtils {
	public static Dialog creatLoadingDialog(Activity activity) {
		View view = LayoutInflater.from(activity).inflate(
				R.layout.common_dialog_loading, null);
		CutomDialog dialog = new CutomDialog(activity, R.style.commondialog);
		dialog.setdiableBackKey(true);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		LayoutParams params = window.getAttributes();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;
		window.setAttributes(params);
		return dialog;
	}
}
