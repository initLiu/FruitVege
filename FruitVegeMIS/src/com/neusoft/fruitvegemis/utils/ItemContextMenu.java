package com.neusoft.fruitvegemis.utils;

import com.neusoft.fruitvegemis.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ItemContextMenu {
	public static boolean menuShowing = false;

	public static PopupWindow showAsDropDown(View view, CustomMenu menu, OnClickListener onClickListener) {
		PopupWindow popup = new PopupWindow(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		popup.setContentView(createContent(popup, view.getContext(), menu, onClickListener));
		popup.setBackgroundDrawable(new ColorDrawable(0));
		popup.setOutsideTouchable(true);
		popup.setFocusable(true);
		popup.showAsDropDown(view);
		return popup;
	}

	private static View createContent(final PopupWindow popup, Context ctx, CustomMenu menu, OnClickListener listener) {
		LinearLayout contaier = new LinearLayout(ctx);
		contaier.setGravity(Gravity.CENTER);
		contaier.setBackgroundColor(Color.BLACK);
		int size = menu.size();
		for (int i = 0; i < size; i++) {
			CustomMenuItem item = menu.getItem(i);
			TextView btn = new TextView(ctx) {

				@Override
				public boolean performClick() {
					boolean result = super.performClick();
					popup.dismiss();
					return result;
				}

			};
			btn.setText(item.getTitle());
			btn.setTextSize(13);
			final Resources res = ctx.getResources();
			TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			mTextPaint.density = res.getDisplayMetrics().density;
			int textWidth = (int) Layout.getDesiredWidth(item.getTitle(), mTextPaint);
			int dPadding = 5;
			if (mTextPaint.density >= 2) {
				dPadding = 10;
			}
			int padding = (int) (dPadding * mTextPaint.density);
			btn.setMinimumWidth(textWidth + padding * 2);
			btn.setPadding(padding, 0, padding, 0);
			btn.setId(item.getItemId());
			btn.setTextColor(Color.WHITE);
			btn.setBackgroundDrawable(null);
			btn.setIncludeFontPadding(true);
			btn.setOnClickListener(listener);
			btn.setGravity(Gravity.CENTER);
			contaier.addView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
			if (i != size - 1) {
				// 插入分割符icon
				ImageView imageView = new ImageView(ctx);
				imageView.setBackgroundResource(R.drawable.bubble_popup_bg_space);
				contaier.addView(imageView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
		}
		return contaier;
	}
}
