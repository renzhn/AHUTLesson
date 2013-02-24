package com.ahutlesson.android.service;

import java.util.Timer;
import java.util.TimerTask;

import com.ahutlesson.android.MessageActivity;
import com.ahutlesson.android.NoticeActivity;
import com.ahutlesson.android.R;
import com.ahutlesson.android.api.AHUTAccessor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class CheckUnreadService extends Service{
	
	private static final int ONEMIN = 60000;
	private static final int MESSAGENOTIFYID = 1, NOTICENOTIFYID = 2;
	private Timer timer = new Timer();
	private boolean enableSound, enableVibrate;
	private int checkFreq, interval = 5 * ONEMIN;
	
    @Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		enableSound = preferences.getBoolean("CheckUnreadSound", true);
		enableVibrate = preferences.getBoolean("CheckUnreadVibrate", true);
		checkFreq = Integer.valueOf(preferences.getString("CheckUnreadFreq", "5"));
		System.out.println("check freq:" + checkFreq);
		switch(checkFreq) {
		case 1:
			interval = ONEMIN;
			break;
		case 5:
			interval = 5 * ONEMIN;
			break;
		case 15:
			interval = 10 * ONEMIN;
			break;
		}
		if(checkFreq == 100) {
			this.stopservice();
			return;
		}
		startservice();
	}

	private void startservice() {
		timer.scheduleAtFixedRate( new TimerTask() {
			public void run() {
				checkUnread();
			}
		}, 0, interval);
	}
	
	private void checkUnread() {
		int[] unreadCount;
		try {
			unreadCount = AHUTAccessor.getInstance(this).getUnreadCount();
			if(unreadCount[0] > 0) {
				showMessageNotification(unreadCount[0]);
			}
			if(unreadCount[1] > 0) {
				showNoticeNotification(unreadCount[1]);
			}
		} catch (Exception e) {
			System.out.println("checkUnreadError");
		}
	}

	private void stopservice() {
		if (timer != null){
			timer.cancel();
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopservice();
	}
	
    private void showMessageNotification(int count) {
    	Intent i = new Intent(this, MessageActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager nm = (NotificationManager) this	.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification.Builder(this)
	        .setContentTitle("新消息")
	        .setContentText("您有" + count + "条新消息")
	        .setContentIntent(pendingIntent)
	        .setSmallIcon(R.drawable.ahutlesson)
	        .setAutoCancel(true)
	        .setOnlyAlertOnce(true)
	        .setLights(0xff00ff00, 300, 1000)
			.build();
		if(enableSound) n.defaults |= Notification.DEFAULT_SOUND;
		if(enableVibrate) n.defaults |= Notification.DEFAULT_VIBRATE;
		nm.notify(MESSAGENOTIFYID, n);
    }
    
    private void showNoticeNotification(int count) {
    	Intent i = new Intent(this, NoticeActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager nm = (NotificationManager) this	.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification.Builder(this)
	        .setContentTitle("新提醒")
	        .setContentText("您有" + count + "条新提醒")
	        .setContentIntent(pendingIntent)
	        .setSmallIcon(R.drawable.ahutlesson)
	        .setAutoCancel(true)
	        .setOnlyAlertOnce(true)
	        .setLights(0xff00ff00, 300, 1000)
			.build();
		if(enableSound) n.defaults |= Notification.DEFAULT_SOUND;
		if(enableVibrate) n.defaults |= Notification.DEFAULT_VIBRATE;
		nm.notify(NOTICENOTIFYID, n);
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
