package com.ahutpt.lesson;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class SoundNormalReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		AudioManager am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		Log.i("ahutlesson", "已恢复正常音量");		
	}
}
