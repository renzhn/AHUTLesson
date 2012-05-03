package com.ahutpt.lesson;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Alert {

	private Lesson lessons[][] = new Lesson[7][5];
	private Context context;
	private Timetable timetable;
	private SharedPreferences preferences;
	private AlarmManager am;
	private boolean enableAlert,enableSilent;

	public Alert(Context context0){
		context = context0;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		enableAlert = preferences.getBoolean("NoticeBeforeLesson", true);
		enableSilent = preferences.getBoolean("SilentMode", true);
		loadLesson();
		timetable = new Timetable(context);
	}
	
	private void loadLesson(){
		Lesson lesson;
		for(int week = 0; week < 7; week++){
			for(int time = 0;time < 5; time++){
				lesson = new Lesson(week,time,context);
				if(lesson.exist){
					lessons[week][time] = lesson;
				}else{
					lessons[week][time] = null;
				}
			}
		}
	}
	
	public void setAlarm(){
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Lesson nextLesson = timetable.getNextLesson(Timetable.DelayAlarm);
		Intent intent;
		if(nextLesson!=null&&enableAlert){
				long alarmTime = nextLesson.getNextTime(Timetable.DelayAlarm);
				intent = new Intent(context,AlarmReceiver.class);
				intent.putExtra("week", nextLesson.week);
				intent.putExtra("time", nextLesson.time);
				intent.putExtra("LessonInfo", nextLesson.toString());  
				PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
		}
		nextLesson = timetable.getNextLesson(Timetable.DelaySilent);
		if(nextLesson!=null&&enableSilent){
				long alarmTime = nextLesson.getNextTime(Timetable.DelaySilent);
				intent = new Intent(context,SoundSilentReceiver.class);
				intent.putExtra("week", nextLesson.week);
				intent.putExtra("time", nextLesson.time);
				PendingIntent sender1 = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, alarmTime, sender1);
		}
	}
}
