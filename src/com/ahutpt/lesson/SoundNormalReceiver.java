package com.ahutpt.lesson;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SoundNormalReceiver extends BroadcastReceiver {
	
	private Timetable timetable;
	private SharedPreferences preferences;
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		timetable = new Timetable(context);
		AudioManager am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Toast.makeText(context, "已恢复正常音量", Toast.LENGTH_LONG).show();
		
		//设置下一次
		boolean enableSilent = preferences.getBoolean("SilentMode", true);
		Lesson nextLesson = timetable.getNextLesson(Timetable.NextSilent);
		if(nextLesson!=null&&enableSilent){
			long alarmTime = nextLesson.getNextTime(Timetable.NextSilent);
			Intent intent1 = new Intent(context,SoundSilentReceiver.class);
			intent1.putExtra("week", nextLesson.week);
			intent1.putExtra("time", nextLesson.time);
			PendingIntent sender1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.set(AlarmManager.RTC_WAKEUP, alarmTime, sender1);
		}
	}
}
