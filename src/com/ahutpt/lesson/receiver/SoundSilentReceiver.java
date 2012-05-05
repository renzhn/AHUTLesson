package com.ahutpt.lesson.receiver;

import com.ahutpt.lesson.lesson.Lesson;
import com.ahutpt.lesson.lesson.LessonManager;
import com.ahutpt.lesson.time.Timetable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SoundSilentReceiver extends BroadcastReceiver {
	
	private SharedPreferences preferences;
	private Context context;
	private int week,time;
	private boolean enableSilent,enableVibrate;

	@Override
	public void onReceive(Context context0, Intent intent) {
		context = context0;
		if(!Timetable.loaded)
			new Timetable(context);
		if(!LessonManager.loaded)
			new LessonManager(context);
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		enableSilent = preferences.getBoolean("SilentMode", true);
		enableVibrate = preferences.getBoolean("VibrateWhenSilentMode", false);
		
		week = intent.getExtras().getInt("week");
		time = intent.getExtras().getInt("time");;

		if(enableSilent){
			AudioManager am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			if(enableVibrate){
				alert("已设为静音（振动）");
			    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}else{
				alert("已设为静音");
			    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
			
			setCancelSilent(week,time);
		}
	}

	private void setCancelSilent(int week,int time) {
		// 恢复正常音量
		Timetable timetable = new Timetable(context);
		Lesson curLesson = timetable.getCurrentLesson(Timetable.DelaySilent);
		if(!curLesson.exist)return;//手动更改时间，执行时并无课
		long alarmTime = curLesson.getCurrentLessonEndTime(Timetable.DelaySilent);
		if(true){
		     Log.i("ahutLesson","将恢复音量: " + Timetable.miliTime2String(alarmTime)); 
		}
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context,SoundNormalReceiver.class);
		intent.putExtra("week", week);
		intent.putExtra("time", time);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
	}
	
	public void alert(String notice) {
		Toast.makeText(context, notice, Toast.LENGTH_LONG).show();
	}
}
