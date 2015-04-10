package com.xinlan.imageedit;

import java.io.File;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.xinlan.imageedit.editimage.EditImageActivity;
import com.xinlan.imageedit.picchooser.SelectPictureActivity;

public class MainActivity extends BaseActivity {

	public static final int SELECT_GALLERY_IMAGE_CODE = 7;
	public static final int TAKE_PHOTO_CODE = 8;
	public static final int ACTION_REQUEST_EDITIMAGE = 9;
	public static final int ACTION_STICKERS_IMAGE = 10;
	private MainActivity context;
	private ImageView imgView;
	private View openAblum;
	private View editImage;// 
	private View stickersImage;//
	private Bitmap mainBitmap;
	public Uri mImageUri;//
	public String mOutputFilePath;// 
	private int imageWidth, imageHeight;// 
	private String path;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_takephoto);
		initView();
	}

	private void initView() {
		context = this;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		imageWidth = (int) ((float) metrics.widthPixels / 1.5);
		imageHeight = (int) ((float) metrics.heightPixels / 1.5);

		imgView = (ImageView) findViewById(R.id.img);
		openAblum = findViewById(R.id.select_ablum);
		editImage = findViewById(R.id.edit_image);

		openAblum.setOnClickListener(new SelectClick());
		editImage.setOnClickListener(new EditImageClick());
	}

	private final class SelectClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			MainActivity.this.startActivityForResult(new Intent(
					MainActivity.this, SelectPictureActivity.class),
					SELECT_GALLERY_IMAGE_CODE);
		}
	}// end inner class

	/**
	 * 
	 * @author panyi
	 * 
	 */
	private final class EditImageClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent it = new Intent(MainActivity.this, EditImageActivity.class);
			it.putExtra(EditImageActivity.FILE_PATH, path);
			File outputFile = FileUtils.getEmptyFile("tietu"
					+ System.currentTimeMillis() + ".jpg");
			it.putExtra(EditImageActivity.EXTRA_OUTPUT,
					outputFile.getAbsolutePath());
			MainActivity.this.startActivityForResult(it,
					ACTION_REQUEST_EDITIMAGE);
		}
	}// end inner class

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// System.out.println("RESULT_OK");
			switch (requestCode) {
			case SELECT_GALLERY_IMAGE_CODE://
				handleSelectFromAblum(data);
				break;
			case ACTION_REQUEST_EDITIMAGE://
				handleEditorImage(data);
				break;
			}// end switch
		}
	}

	private void handleSelectFromAblum(Intent data) {
		String filepath = data.getStringExtra("imgPath");
		path = filepath;
		// System.out.println("path---->"+path);
		LoadImageTask task = new LoadImageTask();
		task.execute(path);
	}

	private void handleEditorImage(Intent data) {
		updateMedia(mOutputFilePath);
		String newFilePath = data.getStringExtra("save_file_path");
		// System.out.println("newFilePath---->" + newFilePath);
		LoadImageTask loadTask = new LoadImageTask();
		loadTask.execute(newFilePath);
	}

	private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
		private Dialog loadDialog;

		@Override
		protected Bitmap doInBackground(String... params) {
			return getSampledBitmap(params[0], imageWidth, imageHeight);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			loadDialog.dismiss();
		}

		@Override
		protected void onCancelled(Bitmap result) {
			super.onCancelled(result);
			loadDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadDialog = BaseActivity.getLoadingDialog(MainActivity.this,
					"图片载入中...", false);
			loadDialog.show();
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			loadDialog.dismiss();
			if (mainBitmap != null) {
				mainBitmap.recycle();
				mainBitmap = null;
				System.gc();
			}
			mainBitmap = result;
			imgView.setImageBitmap(mainBitmap);
		}
	}// end inner class

	private void updateMedia(String filepath) {
		MediaScannerConnection.scanFile(getApplicationContext(),
				new String[] { filepath }, null, null);
	}

	public static Bitmap getSampledBitmap(String filePath, int reqWidth,
			int reqHeight) {
		Options options = new Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filePath, options);

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = (int) FloatMath
						.floor(((float) height / reqHeight) + 0.5f); // Math.round((float)height
																		// /
																		// (float)reqHeight);
			} else {
				inSampleSize = (int) FloatMath
						.floor(((float) width / reqWidth) + 0.5f); // Math.round((float)width
																	// /
																	// (float)reqWidth);
			}
		}
		// System.out.println("inSampleSize--->"+inSampleSize);

		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}
}// end class
