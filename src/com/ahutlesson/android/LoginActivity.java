package com.ahutlesson.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		disableHomeButton();
		setContentView(R.layout.login);

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
}
