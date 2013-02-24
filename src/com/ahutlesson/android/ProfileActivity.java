package com.ahutlesson.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.UserManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ProfileActivity extends BaseActivity {

	private static final int CAMERA_REQUEST = 1994;

	private ImageView ivAvatar;
	private UserManager userManager;
	private String uxh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		actionBar.setIcon(R.drawable.account);
		userManager = UserManager.getInstance(this);
		uxh = userManager.getUserXH();
		if(uxh == null) {
			finish();
			return;
		}
		
		ivAvatar = (ImageView) findViewById(R.id.ivAvatar);

        ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(uxh), ivAvatar);

		Button btnUploadAvatar = (Button) findViewById(R.id.btnUploadAvatar);
		btnUploadAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openImageIntent();
			}
		});
	}

	private Uri outputFileUri;

	private void openImageIntent() {
		final File root = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "AHUTLesson" + File.separator);
		root.mkdirs();
		final String fname = "avatar.jpg";
		final File sdImageMainDirectory = new File(root, fname);
		outputFileUri = Uri.fromFile(sdImageMainDirectory);

		// Camera.
		final List<Intent> cameraIntents = new ArrayList<Intent>();
		final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		final PackageManager packageManager = getPackageManager();
		final List<ResolveInfo> listCam = packageManager.queryIntentActivities(
				captureIntent, 0);
		for (ResolveInfo res : listCam) {
			final String packageName = res.activityInfo.packageName;
			final Intent intent = new Intent(captureIntent);
			intent.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));
			intent.setPackage(packageName);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			cameraIntents.add(intent);
		}

		// File system
		final Intent galleryIntent = new Intent();
		galleryIntent.setType("image/*");
		galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

		final Intent chooserIntent = Intent
				.createChooser(galleryIntent, "选择图片来源");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
				cameraIntents.toArray(new Parcelable[] {}));
		startActivityForResult(chooserIntent, CAMERA_REQUEST);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
			final boolean isCamera;
			if (data == null) {
				isCamera = true;
			} else {
				final String action = data.getAction();
				if (action == null) {
					isCamera = false;
				} else {
					isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				}
			}

			Uri selectedImageUri;
			if (isCamera) {
				selectedImageUri = outputFileUri;
			} else {
				selectedImageUri = data == null ? null : data.getData();
			}
			Bitmap bitmap;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
				ivAvatar.setImageBitmap(bitmap);
				String result = AHUTAccessor.getInstance(this).uploadAvatar(bitmap);
				if(result.contentEquals("0")) {
					makeToast("上传成功！");
					ImageLoader.getInstance().clearMemoryCache();
					ImageLoader.getInstance().clearDiscCache();
					ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(uxh), ivAvatar); 
				}else if(result.startsWith("1")) {
					makeToast(result.substring(2));
				}else{
					makeToast("发生错误");
				}
			} catch (Exception e) {
				e.printStackTrace();
				makeToast(e.getMessage());
			} 
		}
	}
}
