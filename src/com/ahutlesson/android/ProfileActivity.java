package com.ahutlesson.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.User;
import com.ahutlesson.android.model.UserManager;
import com.ahutlesson.android.utils.GlobalContext;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends BaseActivity {

	private static final int CAMERA_REQUEST = 1994;

	private ImageView ivAvatar;
	private TextView tvUName, tvUInfo,tvSignature;
	private String signature;
	private UserManager userManager;
	private User user;
	private Bitmap avatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		actionBar.setIcon(R.drawable.ic_action_person);
		
		GlobalContext.initImageLoader();
		
		userManager = UserManager.getInstance(this);
		user = userManager.getUser();
		if(user.uxh == null) {
			finish();
			return;
		}
		
		tvUName = (TextView) findViewById(R.id.tvUname);
		tvUInfo = (TextView) findViewById(R.id.tvUInfo);
		tvSignature = (TextView) findViewById(R.id.tvSignature);
		ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
		tvUName.setText(user.uname);
		tvUInfo.setText(user.uxh + " " + user.bj);
		tvSignature.setText(user.signature);
		
        ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(user.uxh), ivAvatar);

		Button btnUploadAvatar = (Button) findViewById(R.id.btnUploadAvatar);
		btnUploadAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openImageIntent();
			}
		});
		
		ImageButton ibEditSignature = (ImageButton) findViewById(R.id.ibEditSignature);
		ibEditSignature.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editSignature();
			}
		});
	}

	private void editSignature() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("编辑个性签名");
		final EditText input = new EditText(this);
		alert.setView(input);
		input.setText(user.signature);
		input.setMaxLines(3);
		alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (!value.contentEquals("")) {
					signature = value;
					new SetSignatureTask().execute();
				}
			}
		});
		alert.setNegativeButton(R.string.cancel, null);
		alert.show();
		
	}

	private class SetSignatureTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... para) {
			try {
				AHUTAccessor.getInstance(ProfileActivity.this).setSignature(signature);
				return null;
			} catch (Exception e) {
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String ret) {
			if(ret == null) {
				makeToast("设置成功！");
				tvSignature.setText(signature);
				user.signature = signature;
				userManager.setUser(user);
			}else{
				makeToast(ret);
			}
		}
		
		
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
			try {
				avatar = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
			} catch (FileNotFoundException e) {
				makeToast("文件未找到");
			} catch (IOException e) {
				makeToast("IO错误");
			}
			new UploadAvatar().execute();
		}
	}
	
	private class UploadAvatar extends AsyncTask<Integer, Integer, String> {
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ProfileActivity.this, "请稍等...", "提交中...", true);
		}

		@Override
		protected String doInBackground(Integer... arg0) {
			try {
				AHUTAccessor.getInstance(ProfileActivity.this).uploadAvatar(avatar);
				return null;
			} catch (Exception e) {
				return e.getMessage();
			} 
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			if(result == null) {
				ImageLoader.getInstance().clearMemoryCache();
				ImageLoader.getInstance().clearDiscCache();
				ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(user.uxh), ivAvatar);
				makeToast("上传成功！");
			}else{
				alert(result + "\n提示：如果手机无法上传，可以到ahutlesson.com网页版上传");
			}
		}
		

	}
}
