package com.ahutlesson.android;

import java.io.IOException;

import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class AlarmActivity extends BaseActivity {

	private MediaPlayer player;
	private Lesson lesson;
	private AlertDialog ad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Timetable timetable = Timetable.getInstance(this);
		LessonManager lessonManager = LessonManager.getInstance(this);
		
		int week = getIntent().getExtras().getInt("week");
		int time = getIntent().getExtras().getInt("time");
		
		lesson = lessonManager.getLessonAt(week, time);
		if(lesson==null){
			this.finish();
		}
		
		int curTimeBlock = timetable.getCurrentTimeBlock(Timetable.DelayDefault);
		if(curTimeBlock != -1 && lessonManager.lessons[lesson.week][curTimeBlock] != null){
			this.finish();//如果现在正在上课则不提醒
			return;
		}
		
		playMusic();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String alertMessage = preferences.getString("MessageWhenAlarm", "{TIME}有{LESSON}课，该上课了！！");
		alertMessage = alertMessage.replace("{TIME}", timetable.lessontime_name[time]);
		alertMessage = alertMessage.replace("{LESSON}", lesson.alias);
		
		ad = new AlertDialog.Builder(AlarmActivity.this)

				.setTitle("上课提醒")
				.setMessage(alertMessage)
				.setPositiveButton("关闭闹钟",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								ad.dismiss();
								AlarmActivity.this.finish();
								System.exit(0);
							}
						}).show();
	}
	
	public void playMusic() {
		if (player == null) {
			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			try {
				player = new MediaPlayer();
				player.setDataSource(this, uri);
				final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
					player.setAudioStreamType(AudioManager.STREAM_ALARM);
					player.setLooping(true);
					player.prepare();
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!player.isPlaying()) {
			player.start();
		}
	}

}
