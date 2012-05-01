package com.ahutpt.lesson;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Notification;

public class AlarmReceiver extends BroadcastReceiver {
	
	private SharedPreferences preferences;
	private Context context;
	private int week,time;
	private boolean enableAlert,enableNotification,enableSound;
	
	@Override
	public void onReceive(Context context0, Intent intent) {
		//接到上课广播
		context = context0;
		
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		enableAlert = preferences.getBoolean("NoticeBeforeLesson", true);
		if(!enableAlert)
			return;

		enableNotification = preferences.getBoolean("SendNotificationWhenNotice", true);
		enableSound = preferences.getBoolean("PlaySoundWhenNotice", true);
		
		week = intent.getExtras().getInt("week");
		time = intent.getExtras().getInt("time");

        String info = intent.getStringExtra("LessonInfo");  
        Log.i("ahutLesson","LessonInfo: " + info); 
	    
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

	private void pushNotification() {
		Lesson lesson = new Lesson(week, time, context);
		new Timetable(context);
		String notice = Timetable.lessontime_name[time] + "有" + lesson.alias + "课";
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);               
        Notification n = new Notification(R.drawable.calendar_small, notice, System.currentTimeMillis());             
        n.flags = Notification.FLAG_AUTO_CANCEL;                
        Intent i = new Intent(context, LessonActivity.class);
		i.putExtra("week", week);
		i.putExtra("time", time);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
        PendingIntent pendingIntent = PendingIntent.getActivity(context, R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
                         
        n.setLatestEventInfo(context, "上课提醒", notice,  pendingIntent);
        nm.notify(R.string.app_name, n);
	}
}
