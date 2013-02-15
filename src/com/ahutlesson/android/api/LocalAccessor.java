package com.ahutlesson.android.api;

import android.content.Context;

public class LocalAccessor {

	private static LocalAccessor accessor;
	
	public LocalAccessor(Context context) {
		
	}

	public static LocalAccessor getInstance(Context context) {
		if(accessor == null){
			accessor = new LocalAccessor(context);
		}
		return accessor;
	}
	
	public static boolean hasLogin() {
		
		return true;
	}
}
