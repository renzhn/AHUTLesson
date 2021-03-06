package com.ahutlesson.android.alarm;

import com.ahutlesson.android.AlarmActivity;
import com.ahutlesson.android.MainActivity;
import com.ahutlesson.android.R;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;

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

	private Context context;
	private int week, time;
	private boolean enableAlarm, enableNotification, enableSound,
			enableNotificationSound, enableNotificationVibrate;

	@Override
	public void onReceive(Context context0, Intent intent0) {
		// 接到上课广播
		context = context0;

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		enableAlarm = preferences.getBoolean("AlarmBeforeLesson", true);
		if (!enableAlarm)
			return;

		enableNotification = preferences.getBoolean("SendNotificationWhenAlarm", true);
		enableSound = preferences.getBoolean("PlaySoundWhenAlarm", false);
		enableNotificationSound = preferences.getBoolean("NotificationSoundWhenAlarm", true);
		enableNotificationVibrate = preferences.getBoolean("NotificationVibrateWhenAlarm", true);
		
		week = intent0.getExtras().getInt("week");
		time = intent0.getExtras().getInt("time");

		setNextLessonAlarm();

		if (enableNotification) {
			pushNotification();
		}

		if (enableSound) {
			Intent i = new Intent(context, AlarmActivity.class);
			i.putExtra("week", week);
			i.putExtra("time", time);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}

	}

	private void setNextLessonAlarm() {
		// 设置下节课闹钟
		Lesson nextLesson = Timetable.getInstance(context).getNextLesson(Timetable.DelayAlarm);
		if (nextLesson != null) {
			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			long alarmTime = Timetable.getInstance(context).getNextTime(nextLesson, Timetable.DelayAlarm);
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra("week", nextLesson.week);
			intent.putExtra("time", nextLesson.time);
			PendingIntent sender = PendingIntent.getBroadcast(context, 0,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void pushNotification() {
		Lesson lesson = LessonManager.getInstance(context).getLessonAt(week, time);
		if (lesson == null)
			return;
		
		Intent i = new Intent(context, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		String message = Timetable.getInstance(context).lessontimeName[time] + "有" + lesson.name
				+ "课，地点：" + lesson.place;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.ahutlesson, message,
				System.currentTimeMillis());
		if (enableNotificationSound) {
			n.defaults |= Notification.DEFAULT_SOUND;
		}
		if (enableNotificationVibrate) {
			n.defaults |= Notification.DEFAULT_VIBRATE;// VIBRATE
		}
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		n.ledARGB = 0xff00ff00;
		n.ledOnMS = 300;
		n.ledOffMS = 1000;// LED
		n.setLatestEventInfo(context, "上课提醒", message, pendingIntent);
		nm.notify(0, n);
	}
}
