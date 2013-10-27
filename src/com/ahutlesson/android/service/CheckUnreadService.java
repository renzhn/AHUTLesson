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

public class CheckUnreadService extends Service {
	
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
		//System.out.println("check freq:" + checkFreq);
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
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				checkUnread();
			}
		}, 0, interval);
	}
	
	private void checkUnread() {
		UnreadInfo unreadInfo;
		try {
			unreadInfo = AHUTAccessor.getInstance(this).getUnreadCount();
			if(unreadInfo.unreadMessage > 0) {
				showMessageNotification(unreadInfo.unreadMessage);
			}
			if(unreadInfo.unreadNotice > 0) {
				showNoticeNotification(unreadInfo.unreadNotice);
			}
		} catch (Exception e) {
			System.out.println("checkUnreadError: " + e.getMessage());
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
	
    @SuppressWarnings("deprecation")
	private void showMessageNotification(int count) {
    	Intent i = new Intent(this, MessageActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		String message = "您有" + count + "条新消息";
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.ahutlesson, message, System.currentTimeMillis());
		if (enableSound) {
			n.defaults |= Notification.DEFAULT_SOUND;
		}
		if (enableVibrate) {
			n.defaults |= Notification.DEFAULT_VIBRATE;// VIBRATE
		}
		n.flags = Notification.FLAG_ONLY_ALERT_ONCE;
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		n.ledARGB = 0xff00ff00;
		n.ledOnMS = 300;
		n.ledOffMS = 1000;// LED
		n.setLatestEventInfo(this, "新消息", message, pendingIntent);
		nm.notify(MESSAGENOTIFYID, n);
		
    }
    
    @SuppressWarnings("deprecation")
	private void showNoticeNotification(int count) {
    	Intent i = new Intent(this, NoticeActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		String message = "您有" + count + "条新提醒";
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.ahutlesson, message, System.currentTimeMillis());
		if (enableSound) {
			n.defaults |= Notification.DEFAULT_SOUND;
		}
		if (enableVibrate) {
			n.defaults |= Notification.DEFAULT_VIBRATE;// VIBRATE
		}
		n.flags = Notification.FLAG_ONLY_ALERT_ONCE;
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		n.ledARGB = 0xff00ff00;
		n.ledOnMS = 300;
		n.ledOffMS = 1000;// LED
		n.setLatestEventInfo(this, "新提醒", message, pendingIntent);
		nm.notify(NOTICENOTIFYID, n);
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
