package com.ahutlesson.android;

import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.UserInfo;
import com.ahutlesson.android.utils.ValidateHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActivity extends BaseActivity {

	private String uxh;
	private ImageView ivAvatar;
	private TextView tvUname, tvUxh,tvSignature, tvUserInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);
		
		uxh = getIntent().getExtras().getString("uxh");
		if(!ValidateHelper.isXH(uxh)) {
			this.finish();
			return;
		}

		tvUname = (TextView) findViewById(R.id.tvUname);
		tvUxh = (TextView) findViewById(R.id.tvUxh);
		tvSignature = (TextView) findViewById(R.id.tvSignature);
		tvUserInfo = (TextView) findViewById(R.id.tvUserInfo);
		ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
		
		Button btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
		btnSendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(UserActivity.this, NewMessageActivity.class);
				i.putExtra("uxh", uxh);
				startActivity(i);
			}
		});
		
		Button btnViewTimetable = (Button) findViewById(R.id.btnViewTimetable);
		btnViewTimetable.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(UserActivity.this, TimetableViewerActivity.class);
				i.putExtra("uxh", uxh);
				startActivity(i);
			}
		});
		
		new LoadUserInfo().execute();
	}
	
	private class LoadUserInfo extends AsyncTask<String, String, UserInfo> {

		@Override
		protected UserInfo doInBackground(String... arg0) {
			try {
				return AHUTAccessor.getInstance(UserActivity.this).getUserInfo(uxh);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(UserInfo uinfo) {
			if(uinfo == null)  return;
			
			tvUname.setText(uinfo.uname);
			tvUxh.setText(uinfo.uxh);
			tvSignature.setText(uinfo.signature);
			String userInfo = "性别:" + uinfo.xb + "\n"
					+ "班级:" + uinfo.bj + "\n"
					+ "专业:" + uinfo.zy + "\n"
					+ "学院:" + uinfo.xy + "\n"
					+ "所在级:" + uinfo.rx + "\n\n"
					+ "注册时间:" + uinfo.registerTime + "\n"
					+ "最后登录:" + uinfo.lastloginTime;
			tvUserInfo.setText(userInfo);
			
			if(uinfo.hasAvatar) {
		        ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(uinfo.uxh), ivAvatar);
			}
		}
		
	}
}
