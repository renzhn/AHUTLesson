package com.ahutlesson.android.utils;

import android.util.Log;

public class Util {

	public static void log(String i) {
		if(GlobalContext.DEBUG)
			Log.i("AHUTLESSON", i);
	}

}
