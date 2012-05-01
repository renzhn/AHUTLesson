package com.ahutpt.lesson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class FoundUpdateActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new AlertDialog.Builder(this).setTitle("发现新版本")
		.setMessage("发现新版本\n是否更新？")
		.setCancelable(false)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri
						.parse("http://ahut2011.sinaapp.com/app/lesson");
				intent.setData(content_url);
				startActivity(intent);
				FoundUpdateActivity.this.finish();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				FoundUpdateActivity.this.finish();
			}
		}).show();
	}

}
