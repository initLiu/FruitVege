package com.neusoft.fruitvegemis.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.widget.RelativeLayout;

import com.neusoft.fruitvegemis.utils.UIUtils;

public class InputMethodRelativeLayout extends RelativeLayout {

	private int widthMeasureSpec;
	private int heightMeasureSpec;
	private int screenWidth;
	private int screenHeight;
	private onSizeChangedListenner sizeChangedListenner;

	public InputMethodRelativeLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
	}

	public InputMethodRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public InputMethodRelativeLayout(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.widthMeasureSpec = widthMeasureSpec;
		this.heightMeasureSpec = heightMeasureSpec;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private boolean isOpen = false;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w == oldw && oldh != 0 && oldw != 0) {
			if (h < oldh) {// 高度变小，则认为键盘开启
				if (Math.abs(h - screenHeight) > UIUtils.dp2px(150,
						getResources())) {
					isOpen = true;
				} else {
					isOpen = false;
				}
			} else if (Math.abs(h - oldh) < UIUtils.dp2px(150, getResources())) {// 高度变大，并且变化不大，则认为键盘还是开启着的
				isOpen = true;
			} else {
				isOpen = false;
			}
			if (sizeChangedListenner != null) {
				sizeChangedListenner.onSizeChange(isOpen, oldh, h);
			}
			measure(widthMeasureSpec - w + getWidth(), heightMeasureSpec - h
					+ getHeight());
		}
	}

	public interface onSizeChangedListenner {
		void onSizeChange(boolean isOpen, int preH, int curH);
	}

	public void setOnSizeChangeListener(onSizeChangedListenner l) {
		sizeChangedListenner = l;
	}
}
