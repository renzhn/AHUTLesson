package com.ahutpt.lesson.receiver;

import com.ahutpt.lesson.AlarmAlertActivity;
import com.ahutpt.lesson.LessonActivity;
import com.ahutpt.lesson.R;
import com.ahutpt.lesson.lesson.Lesson;
import com.ahutpt.lesson.lesson.LessonManager;
import com.ahutpt.lesson.time.Timetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Notification;

public class AlarmReceiver extends BroadcastReceiver {
	
	private SharedPreferences preferences;
	private Context context;
	private int week,time;
	private boolean enableAlert,enableNotification,enableSound,enableNotificationSound,enableVibrate,enableLED;
	
	@Override
	public void onReceive(Context context0, Intent intent0) {
		//接到上课广播
		context = context0;
		
		if(!Timetable.loaded)
			new Timetable(context);
		if(!LessonManager.loaded)
			new LessonManager(context);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		enableAlert = preferences.getBoolean("NoticeBeforeLesson", true);
		if(!enableAlert)
			return;

		enableNotification = preferences.getBoolean("SendNotificationWhenNotice", false);
		enableSound = preferences.getBoolean("PlaySoundWhenNotice", true);
		enableNotificationSound = preferences.getBoolean("NotificationSoundWhenNotice", false);
		enableVibrate = preferences.getBoolean("VibrateWhenNotice", true);
		enableLED = preferences.getBoolean("BlinkWhenNotice", true);
		
		week = intent0.getExtras().getInt("week");
		time = intent0.getExtras().getInt("time");
		
		addNextLessonAlarm();
	    
		if(enableNotification){
			pushNotification();
		}
        
		if(enableSound){
	        Intent i = new Intent(context, AlarmAlertActivity.class);
	        i.putExtra("week", week);
	        i.putExtra("time", time);
	        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(i);
		}
		
	}

	private void addNextLessonAlarm() {
		//设置下节课闹钟
		Lesson nextLesson = Timetable.getNextLesson(Timetable.DelayAlarm);
		if(nextLesson!=null){
			AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			long alarmTime = nextLesson.getNextTime(Timetable.DelayAlarm);
			Intent intent = new Intent(context,AlarmReceiver.class);
			intent.putExtra("week", nextLesson.week);
			intent.putExtra("time", nextLesson.time);
			intent.putExtra("LessonInfo", nextLesson.toString());  
			PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
		}
	}

	private void pushNotification() {
		Lesson lesson = LessonManager.getLessonAt(week, time, context);
		if(lesson==null)return;
		String notice = Timetable.lessontime_name[time] + "有" + lesson.alias + "课";
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);               
        Notification n = new Notification(R.drawable.calendar_small, notice, System.currentTimeMillis());             
        n.flags = Notification.FLAG_AUTO_CANCEL;
        if(enableNotificationSound){
        	n.defaults |= Notification.DEFAULT_SOUND;
        }
        if(enableVibrate){
            n.defaults |= Notification.DEFAULT_VIBRATE;//VIBRATE
        }
        if(enableLED){
        	n.flags |= Notification.FLAG_SHOW_LIGHTS;
            n.ledARGB = 0xff00ff00;
            n.ledOnMS = 300;
            n.ledOffMS = 1000;//LED
        }
        Intent i = new Intent(context, LessonActivity.class);
		i.putExtra("week", week);
		i.putExtra("time", time);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
        PendingIntent pendingIntent = PendingIntent.getActivity(context, R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
                         
        n.setLatestEventInfo(context, "上课提醒", notice,  pendingIntent);
        nm.notify(R.string.app_name, n);
	}
}
