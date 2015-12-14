package com.neusoft.fruitvegemis.utils;

import android.content.res.Resources;

public class UIUtils {
	public static int dp2px(float dp, Resources res) {
		return (int) (dp * res.getDisplayMetrics().density + 0.5f);
	}
}
