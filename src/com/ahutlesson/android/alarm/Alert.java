package com.ahutlesson.android.alarm;

import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.Timetable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Alert {

	public static void setAlarm(Context context){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enableAlert = preferences.getBoolean("NoticeBeforeLesson", true);
		boolean enableSilent = preferences.getBoolean("SilentMode", true);
		Timetable timetable = Timetable.getInstance(context);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Lesson nextLesson = timetable.getNextLesson(Timetable.DelayAlarm);
		Intent intent;
		if(nextLesson != null && enableAlert){
				long alarmTime = timetable.getNextTime(nextLesson, Timetable.DelayAlarm);
				intent = new Intent(context,AlarmReceiver.class);
				intent.putExtra("week", nextLesson.week);
				intent.putExtra("time", nextLesson.time);
				PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
		}
		nextLesson = timetable.getNextLesson(Timetable.DelaySilent);
		if(nextLesson != null && enableSilent){
				long alarmTime = timetable.getNextTime(nextLesson, Timetable.DelaySilent);
				intent = new Intent(context,SoundSilentReceiver.class);
				intent.putExtra("week", nextLesson.week);
				intent.putExtra("time", nextLesson.time);
				PendingIntent sender1 = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, alarmTime, sender1);
		}
	}
}
