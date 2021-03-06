package com.ahutlesson.android;

import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;
import com.ahutlesson.android.model.TimetableSetting;
import com.ahutlesson.android.utils.ChangeLog;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.about);
		
		final ChangeLog cl = new ChangeLog(AboutActivity.this);
		
		TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
		
		tvVersion.setText("版本: " + cl.getThisVersion());

		TextView tvLessonDBVer = (TextView) findViewById(R.id.tvLessonDBVer);
		TextView tvBeginDate = (TextView) findViewById(R.id.tvBeginDate);
		TextView tvSeason = (TextView) findViewById(R.id.tvSeason);
		
		TimetableSetting timetableSetting = Timetable.getInstance(this).getTimetableSetting();
		String lessondbVer = LessonManager.getInstance(this).getLessondbVersion();
		tvLessonDBVer.setText(lessondbVer);
		tvBeginDate.setText(timetableSetting.year + "年" + timetableSetting.month + "月" + timetableSetting.day + "日");
		tvSeason.setText(timetableSetting.seasonWinter ? "冬季" : "夏季");
		
		Button btnCheckUpdate = (Button) findViewById(R.id.btnCheckUpdate);
		Button btnChangelog = (Button) findViewById(R.id.btnChangelog);
		
		btnCheckUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				makeToast("开始检查更新...");
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
					@Override
					public void onUpdateReturned(int updateStatus,
							UpdateResponse updateInfo) {
						switch (updateStatus) {
						case 0: // has update
							UmengUpdateAgent.showUpdateDialog(AboutActivity.this, updateInfo);
							break;
						case 1: // has no update
							alert("您正在使用最新版");
							break;
						case 3: // time out
							alert("网络连接超时");
							break;
						}
					}
				});
				UmengUpdateAgent.update(AboutActivity.this);
			}
		});
		
		btnChangelog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				cl.getFullLogDialog().show();
			}
		});
		
	}
}
