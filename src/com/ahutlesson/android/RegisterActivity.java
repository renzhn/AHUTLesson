package com.ahutlesson.android;

import com.ahutlesson.android.model.UserManager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends BaseActivity {

	private String uxh, password, confirmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		disableHomeButton();
		setContentView(R.layout.register);
		
		Button btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText etRegisterXH = (EditText) findViewById(R.id.etRegisterXH);
				EditText etRegisterPassword = (EditText) findViewById(R.id.etRegisterPassword);
				EditText etRegisterConfirmPassword = (EditText) findViewById(R.id.etRegisterConfirmPassword);
				uxh = etRegisterXH.getText().toString();
				password = etRegisterPassword.getText().toString();
				confirmPassword = etRegisterConfirmPassword.getText().toString();
				if(uxh.length() == 0 || password.length() == 0) {
					alert("用户名和密码不能为空!");
				}else if(!confirmPassword.contentEquals(password)) {
					alert("两次输入的密码不一致!");
				}else{
					new RegisterTask().execute();
				}
					
			}
		});
		
		Button btnToLogin = (Button) findViewById(R.id.btnToLogin);
		btnToLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity(LoginActivity.class);
				RegisterActivity.this.finish();
			}
		});
	}

	private class RegisterTask extends AsyncTask<Integer, Integer, Boolean> {

		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RegisterActivity.this, "请稍等...", "提交中...", true);
		}
		

		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
    			UserManager.getInstance(RegisterActivity.this).registerUser(uxh, password);
				UserManager.getInstance(RegisterActivity.this).updateLessonDB();
				return true;
			} catch (Exception e) {
				alert(e.getMessage());
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean ret) {
			progressDialog.dismiss();
			if(ret) {
				openActivity(MainActivity.class);
				RegisterActivity.this.finish();
			}
		}
	}
}
