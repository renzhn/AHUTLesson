package com.ahutlesson.android.model;

import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.utils.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserManager {

	private static UserManager userManager;

	private static String userCookie;
	
	private Context context;
	private SharedPreferences prefs;
	private User user;
	
	public UserManager(Context context0) {
		context = context0;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		userCookie = getCookie();
		loadUser();
	}
	
	public static UserManager getInstance(Context context) {
		if(userManager == null) {
			userManager = new UserManager(context);
		}
		return userManager;
	}
	
	public boolean hasLocalUser() {
		return getUserXH() != null;
	}

	public String getUserXH() {
		return prefs.getString("uxh", null);
	}
	
	public void loadUser() {
		User ret = new User();
		ret.uxh = getUserXH();
		ret.uname = prefs.getString("uname", null);
		ret.bj = prefs.getString("bj", null);
		ret.password = prefs.getString("password", null);
		ret.signature = prefs.getString("signature", null);
		ret.isAdmin = prefs.getBoolean("isAdmin", false);
		user = ret;
	}
	
	public void setUser(User user) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("uxh", user.uxh);
		editor.putString("uname", user.uname);
		editor.putString("bj", user.bj);
		editor.putString("password", user.password);
		editor.putString("signature", user.signature);
		editor.putBoolean("isAdmin", user.isAdmin);
		editor.commit();
	}
	
	public User getUser() {
		if(user == null) loadUser();
		return user;
	}
	
	public void registerUser(String uxh, String password) throws Exception {
		String cookie = AHUTAccessor.getInstance(context).regsterUser(uxh, password);
		setCookie(cookie);
		user = AHUTAccessor.getInstance(context).getLoginUserInfo();
		setUser(user);
	}
	
	public void verifyUser(String uxh, String password) throws Exception {
		String cookie = AHUTAccessor.getInstance(context).validateUser(uxh, password);
		setCookie(cookie);
		user = AHUTAccessor.getInstance(context).getLoginUserInfo();
		setUser(user);
	}

	public void setCookie(String ck) {
		UserManager.userCookie = ck;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("ck", ck);
		editor.commit();
	}
	
	public String getCookie() {
		if(userCookie == null) {
			loadCookie();
		}
		return userCookie;
	}
	
	public void loadCookie() {
		userCookie = prefs.getString("ck", null);
	}

	public void updateLessonDB() throws Exception {
		String uxh = getUserXH();
		if(uxh == null) throw new Exception("Î´µÇÂ¼");
		LessonListInfo lessonListInfo = AHUTAccessor.getInstance(context).getLessonList(uxh);
		LessonManager lessonManager = LessonManager.getInstance(context);
		lessonManager.lessonlistToDB(lessonListInfo.lessonList);
		lessonManager.setLessondbVersion(lessonListInfo.build);
		Util.log("New Lessondb Build: " + lessonListInfo.build);
	}

	public void updateUserInfo() throws Exception {
		user = AHUTAccessor.getInstance(context).getLoginUserInfo();
		setUser(user);
	}
}
