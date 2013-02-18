package com.ahutlesson.android;

import com.ahutlesson.android.model.UserManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity {

	private ProgressDialog progressDialog;
	private Handler handler = new Handler();
	private String uxh, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		disableHomeButton();
		setContentView(R.layout.login);

		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText etLoginXH = (EditText) findViewById(R.id.etLoginXH);
				EditText etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
				uxh = etLoginXH.getText().toString();
				password = etLoginPassword.getText().toString();
				if(uxh.length() == 0 || password.length() == 0) {
					alert("用户名和密码不能为空!");
				}else{
					doLogin();
				}
			}
		});

		Button btnToRegister = (Button) findViewById(R.id.btnToRegister);
		btnToRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(i);
				LoginActivity.this.finish();
			}
		});
	}

	private void doLogin() {
		progressDialog = ProgressDialog.show(LoginActivity.this, "请稍等...", "验证帐号中...", true);
        new Thread() {
            public void run() {
                try{
        			UserManager.getInstance(LoginActivity.this).verifyUser(uxh,	password);
                	handler.post(new Runnable() {
                		public void run() {
            				progressDialog.setMessage("正在下载课表...");
                		}
                	});
    				UserManager.getInstance(LoginActivity.this).updateLessonDB();
                	handler.post(new Runnable() {
                		public void run() {
                			progressDialog.dismiss();
                		}
                	});
    				Intent i = new Intent(LoginActivity.this, MainActivity.class);
    				startActivity(i);
    				LoginActivity.this.finish();
                }catch(final Exception e) {
	            	e.printStackTrace();
                	handler.post(new Runnable() {
                		public void run() {
                			progressDialog.dismiss();
                			alert(e.getMessage());
                		}
                	});
                }
            }
        }.start();
    }

}
