package com.ahutlesson.android.utils;

import com.ahutlesson.android.MainActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GlobalContext {
	
	public static final boolean DEBUG = true;
	
	public static MainActivity mainActivity;
	
	private static boolean imageLoadedInited = false;
	
	public static void initImageLoader () {
		if (!imageLoadedInited) {
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true)
	        .cacheInMemory(true)
			.build();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mainActivity)
			.defaultDisplayImageOptions(defaultOptions)
			.build();
			ImageLoader.getInstance().init(config); 
		}
	}
}
