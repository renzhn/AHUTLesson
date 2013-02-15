package com.ahutlesson.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		disableHomeButton();
		setContentView(R.layout.register);
		
		Button btnToLogin = (Button) findViewById(R.id.btnToLogin);
		btnToLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(i);
				RegisterActivity.this.finish();
			}
		});
	}
}
