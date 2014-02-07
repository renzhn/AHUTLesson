package com.ahutlesson.android.alarm;

import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.Timetable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SoundSilentReceiver extends BroadcastReceiver {
	
	private Context context;
	private int week,time;
	private boolean enableSilent,enableVibrate;

	@Override
	public void onReceive(Context context0, Intent intent) {
		context = context0;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		enableSilent = preferences.getBoolean("SilentMode", true);
		enableVibrate = preferences.getBoolean("VibrateWhenSilentMode", true);
		
		week = intent.getExtras().getInt("week");
		time = intent.getExtras().getInt("time");;

		if (enableSilent) {
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			int ringerMode = am.getRingerMode();
			if (enableVibrate) {
				Toast.makeText(context, "已设为静音（振动）", Toast.LENGTH_LONG).show();
				saveRingerMode(ringerMode);
			    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			} else {
				Toast.makeText(context, "已设为静音", Toast.LENGTH_LONG).show();
				saveRingerMode(ringerMode);
			    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
			
			setCancelSilent(week,time);
		}
	}
	
	private void saveRingerMode(int ringerMode) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putInt("savedRingerMode", ringerMode);
		editor.commit();
	}

	private void setCancelSilent(int week,int time) {
		// 恢复正常音量
		Timetable timetable = Timetable.getInstance(context);
		Lesson curLesson = timetable.getCurrentLesson(Timetable.DelaySilent);
		if(curLesson == null) return; //手动更改时间，执行时并无课
		long alarmTime = timetable.getCurrentLessonEndTime(curLesson, Timetable.DelaySilent);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SoundNormalReceiver.class);
		intent.putExtra("week", week);
		intent.putExtra("time", time);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
	}
}
