package com.neusoft.fruitvegemis.widget;

import java.lang.reflect.Field;

import com.neusoft.fruitvegemis.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


/**
 * 带清除按钮的 EditText扩展控件
 * 
 */
public class ClearableEditText extends EditText {
	
	// 全选
	private static final int ID_SELECT_ALL = android.R.id.selectAll;
	// 复制
	private static final int ID_COPY = android.R.id.copy;
	// 粘贴
    private static final int ID_PASTE = android.R.id.paste;
	    
	/**
	 * 清除按钮 drawable
	 */
	Drawable mClearBtnDrawable;
	
	/**
	 * 清除按钮drawable宽度
	 */
	private int mWidth;
	
	/**
	 * 清除按钮drawable高度
	 */
	private int mHeight;
	
	/**
	 * 屏幕density
	 */
	private float density;

	/**
	 * 清空EditText监听
	 */
	OnTextClearedListener mListener;

//	private boolean isSingleLine;
	public ClearableEditText(Context context) {
		this( context, null, android.R.attr.editTextStyle );
	}

	public ClearableEditText(Context context, AttributeSet attrs) {
		this( context, attrs, android.R.attr.editTextStyle );
	}

	public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context, attrs);
	}

	/**
	 * 初始话成员变�? 加载清除按钮drawable资源
	 */
	private void init(Context context, AttributeSet attrs) {

		density = context.getResources().getDisplayMetrics().density;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.clearableEditText);

		mClearBtnDrawable = a.getDrawable(R.styleable.clearableEditText_clearBtnDrawable);
		mWidth = a.getDimensionPixelSize(R.styleable.clearableEditText_clearBtnDrawableWidth, -1);
		mHeight = a.getDimensionPixelSize(R.styleable.clearableEditText_clearBtnDrawableHeight, -1);

		System.out.println("mcleardrawable:" + mClearBtnDrawable);
		if (mClearBtnDrawable == null) {
			mClearBtnDrawable = getResources().getDrawable(R.drawable.cross);
		}

		if (mClearBtnDrawable != null) {
			/**
			 * 如果宽度或高度为-1，则使用图片本身的宽度和高度
			 */
			if (mWidth == -1 || mHeight == -1) {
				mWidth = (int)(density*19);
				mHeight = (int)(density*19);
			}
			
			mClearBtnDrawable.setBounds(0, 0, mWidth, mHeight);

			setClearButtonVisible(false);
		}
		
		
		/**
		 * 处理x点击事件
		 */
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ClearableEditText editText = ClearableEditText.this;
				if (editText.getCompoundDrawables()[2] == null) {
					return false;
				}

				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}

				boolean tappedX = event.getX() > (getWidth()
						- getPaddingRight() - mClearBtnDrawable.getIntrinsicWidth());

				if (tappedX) {
					setText("");
					setClearButtonVisible(false);
					if (mListener != null) {
						mListener.afterTextCleared();
					}
				}

				return false;
			}
			
		});
		
		/**
		 * 监听内容变化
		 */
		this.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String text = ClearableEditText.this.getText().toString();
				if (text == null || text.length() == 0) {
					setClearButtonVisible(false);
				} else {
					if (isSingleLine()) {
						setClearButtonVisible(true);
					} else {
						setClearButtonVisible(false);
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {				
			}
			
		});
		

		a.recycle();

	}
	
	boolean clearButtonVisible = false;

	/**
	 * 设置右侧 clear button的显�?
	 * 
	 * @param visible
	 */
	public void setClearButtonVisible(boolean visible) {
		if (clearButtonVisible != visible) {
			Drawable d = visible ? mClearBtnDrawable : null;
			setCompoundDrawables(getCompoundDrawables()[0],
					getCompoundDrawables()[1], d, getCompoundDrawables()[3]);
			clearButtonVisible = visible;
		}
	}

	

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		
//		if (!focused) {
//			setCursorVisible(false);
//			setClearButtonVisible(false);
//		}
//		else {
//			setCursorVisible(true);
//			
//			String text = getText().toString();
//			if (text.length() != 0) {
//				setClearButtonVisible(true);
//			}
//		}
	}

	private boolean isSingleLine() {
		// 默认值为false,因为默认为使不显示X�?
		boolean isSingleLine = false;
		try {
			Field field = TextView.class.getDeclaredField("mSingleLine");
			field.setAccessible(true);
			Object obj = field.get(this);
			if (obj instanceof Boolean) {
				isSingleLine = ((Boolean) obj).booleanValue();
			}
			// Log.d(this.getClass().getName(),"mSingleLIne="+obj);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return isSingleLine;
	}

	/**
	 * 设置内容清空监听�?
	 * 
	 * @param lis
	 */
	public void setTextClearedListener(OnTextClearedListener lis) {
		mListener = lis;
	}

	/**
	 * 内容清空监听�?
	 * 
	 * @author willyli
	 * 
	 */
	public interface OnTextClearedListener {
		
		/**
		 * EditText内容清空后回调此方法
		 */
		public void afterTextCleared();
	}

//	public void setSingleLine(boolean singleLine){
//		super.setSingleLine(singleLine);
//		isSingleLine = singleLine;
//	}
}
