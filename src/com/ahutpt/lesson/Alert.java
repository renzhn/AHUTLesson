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
		int timeInAdvance = Integer.valueOf(preferences.getString("NoticeTimeBeforeLesson", "20"));
		Lesson nextLesson = timetable.getNextLesson();
		if(nextLesson!=null){
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			long alarmTime = nextLesson.getNextTime();
			if(enableAlert){
				Intent intent = new Intent(context,AlarmReceiver.class);
				intent.putExtra("week", nextLesson.week);
				intent.putExtra("time", nextLesson.time);
				intent.putExtra("LessonInfo", nextLesson.toString());  
				PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, alarmTime - timeInAdvance * 60 * 1000 , sender);
			}
			if(enableSilent){
				int silentDelay = Integer.valueOf(preferences.getString("NoticeTimeBeforeLesson", "10"));
				Intent intent1 = new Intent(context,SoundSilentReceiver.class);
				intent1.putExtra("week", nextLesson.week);
				intent1.putExtra("time", nextLesson.time);
				PendingIntent sender1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, alarmTime - silentDelay * 60 * 1000 , sender1);
			}
			
		}
	}
}
