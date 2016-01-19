package com.neusoft.fruitvegemis.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.neusoft.fruitvegemis.R;
import com.neusoft.fruitvegemis.app.AppInterface;
import com.neusoft.fruitvegemis.app.BaseApplication;
import com.neusoft.fruitvegemis.persistence.FruitVgDBManager;
import com.neusoft.fruitvegemis.utils.UIUtils;

public class UploadDialogActivity extends BaseActivity implements
		android.view.View.OnClickListener {

	private ImageView phView;
	private EditText gNameText, gPriceText;
	private Button uploadBtn, cancleBtn;
	private Bitmap bitmap;
	private static final int SELECT_PHOTO = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_dialog);

		LayoutParams params = getWindow().getAttributes();
		params.width = UIUtils.dp2px(250, getResources());
		params.height = UIUtils.dp2px(350, getResources());
		getWindow().setAttributes(params);

		initUI();
		setFinishOnTouchOutside(false);
	}

	private void initUI() {
		phView = (ImageView) findViewById(R.id.goods_pict);
		phView.setOnClickListener(this);
		gNameText = (EditText) findViewById(R.id.good_name);
		gPriceText = (EditText) findViewById(R.id.good_price);
		uploadBtn = (Button) findViewById(R.id.upload);
		uploadBtn.setOnClickListener(this);
		cancleBtn = (Button) findViewById(R.id.cancle);
		cancleBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.goods_pict:// 选取图片
			enterPhotoSelect();
			break;
		case R.id.upload:
			insertToDB();
			finish();
			break;
		case R.id.cancle:
			finish();
			break;
		default:
			break;
		}
	}

	private void insertToDB() {
		String gname = gNameText.getText().toString();
		String gprice = gPriceText.getText().toString();
		if (TextUtils.isEmpty(gname) || TextUtils.isEmpty(gprice)) {
			Toast.makeText(this, "商品名商品价格不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			if (bitmap == null) {
				bitmap = UIUtils.decodeSampledBitmapFromResource(
						getResources(), R.drawable.default_goods, 100, 100);
			}
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] bytes = stream.toByteArray();
			FruitVgDBManager dbManager = (FruitVgDBManager) BaseApplication.mBaseApplication
					.getAppInterface().getManager(AppInterface.FRUITVG);
			dbManager.addSellerGoods(gname, Float.parseFloat(gprice), bytes);// 将图片插入到数据库
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void setGoodPicture(Uri uri) {
//		bitmap = ((BitmapDrawable) phView.getDrawable()).getBitmap();
//		if (bitmap != null && !bitmap.isRecycled()) {
//			bitmap.recycle();
//		}
		try {
			bitmap = UIUtils.decodeSampledBitmapFromStream(this, uri, 100, 100);
		} catch (FileNotFoundException e) {
			bitmap = null;
			e.printStackTrace();
		}
		if (bitmap != null) {
			phView.setImageBitmap(bitmap);
		}
	}

	// private Bitmap getBitmap(Uri uri){
	// try {
	// BitmapFactory.Options opt = new BitmapFactory.Options();
	// DisplayMetrics dm = new DisplayMetrics();
	//
	// getWindowManager().getDefaultDisplay().getMetrics(dm);
	//
	// bitmap = BitmapFactory.decodeStream(getContentResolver()
	// .openInputStream(uri));
	//
	// float fHS = (float) dm.heightPixels / (float) bitmap.getHeight();
	//
	// float fWS = (float) dm.widthPixels / (float) bitmap.getWidth();
	//
	// float fFinalS = Math.min(fHS, fWS);
	//
	// opt.inSampleSize = (int) (1 / fFinalS);
	// opt.inPreferredConfig = Config.RGB_565;
	//
	// bitmap = BitmapFactory.decodeStream(getContentResolver()
	// .openInputStream(uri), null, opt);
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private Bitmap getBitmap(int resId) {
	// BitmapFactory.Options opt = new BitmapFactory.Options();
	// DisplayMetrics dm = new DisplayMetrics();
	//
	// getWindowManager().getDefaultDisplay().getMetrics(dm);
	//
	// bitmap = BitmapFactory.decodeResource(getResources(), resId);
	//
	// float fHS = (float) dm.heightPixels / (float) bitmap.getHeight();
	//
	// float fWS = (float) dm.widthPixels / (float) bitmap.getWidth();
	//
	// float fFinalS = Math.min(fHS, fWS);
	//
	// opt.inSampleSize = (int) (1 / fFinalS);
	// opt.inPreferredConfig = Config.RGB_565;
	//
	// bitmap = BitmapFactory.decodeResource(getResources(), resId, opt);
	// return bitmap;
	// }

	private void enterPhotoSelect() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_PHOTO);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		bitmap = ((BitmapDrawable) phView.getDrawable()).getBitmap();
//		if (bitmap != null && !bitmap.isRecycled()) {
//			bitmap.recycle();
//		}
//		bitmap = null;
//		phView.setImageBitmap(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == Activity.RESULT_OK) {
				setGoodPicture(data.getData());
			}
			return;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
