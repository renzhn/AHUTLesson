package com.ahutlesson.android.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.User;
import com.ahutlesson.android.model.UserManager;

public class AHUTAPIAccessor {

	public static final String SERVER_URL = "http://ahutlesson.sinaapp.com/";

	public static String getURL(String u) {
		HttpGet request = new HttpGet(u);
		if(UserManager.userCookie != null) {
			request.addHeader("Cookie", "ck=" + UserManager.userCookie);
		}
		String strResult = null;
		try {
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			HttpResponse httpResponse = defaultHttpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = new String(strResult.getBytes("ISO-8859-1"),"UTF-8");
			}
			defaultHttpClient.getConnectionManager().shutdown();
		}catch(Exception e) {
			return "";
		}
		log(strResult);
		return strResult;
	}

	public static String postURL(String URL, List<NameValuePair> params) {
		HttpPost request = new HttpPost(URL);
		if(UserManager.userCookie != null) {
			request.addHeader("Cookie", UserManager.userCookie);
		}
		String strResult = null;
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			HttpResponse httpResponse = defaultHttpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = new String(strResult.getBytes("ISO-8859-1"),"UTF-8");
			}
			defaultHttpClient.getConnectionManager().shutdown();
		}catch(Exception e) {
			return "";
		}
		log(strResult);
		return strResult;
	}
	
	public static void log(String i) {
		Log.i("AHUTAPI", i);
	}

	public static String regsterUser(String uxh, String password) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("x", uxh));
		params.add(new BasicNameValuePair("p", password));
		return postURL(SERVER_URL + "api/user.handler.php?act=register", params);
	}
	
	public static String validateUser(String uxh, String password) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("x", uxh));
		params.add(new BasicNameValuePair("p", password));
		return postURL(SERVER_URL + "api/user.handler.php?act=login", params);
	}
	
	public static User getUserInfo() {
		User user = new User();
		String ret = getURL(SERVER_URL + "api/user.handler.php?act=getuserinfo");
		JSONTokener jsonParser = new JSONTokener(ret);
		try {
			JSONObject userInfo = (JSONObject) jsonParser.nextValue();
			user.uxh = userInfo.getString("uxh");
			user.uname = userInfo.getString("uname");
			user.bj = userInfo.getString("bj");
			user.password = userInfo.getString("password");
			user.signature = userInfo.getString("signature");
			user.isAdmin = (userInfo.getInt("is_admin") == 1);
		} catch (JSONException e) {
			e.printStackTrace();
			return user;
		}
		return user;
	}
	
	public static ArrayList<Lesson> getLessonList(String uxh) {
		String ret = getURL(SERVER_URL + "api/getlessonlist.php?xh=" + uxh);
		ArrayList<Lesson> lessonlist = new ArrayList<Lesson>();
		try {
			JSONTokener jsonParser = new JSONTokener(ret);
			JSONObject lesson;
			JSONArray lessons;
				lessons = (JSONArray)jsonParser.nextValue();
				if(lessons == null) return null;
				if(lessons.length() == 0) return null;
				for(int i = 0;i < lessons.length(); i++){
					lesson = lessons.getJSONObject(i);
					int lid = lesson.getInt("lid");
					String lessonName = lesson.getString("lessonname");
					String lessonAlias = lesson.getString("lessonalias");
					String teacherName = lesson.getString("teachername");
					int week = lesson.getInt("week");
					int time = lesson.getInt("time");
					int startweek = lesson.getInt("startweek");
					int endweek = lesson.getInt("endweek");
					String lessonPlace = lesson.getString("place");
					lessonlist.add(new Lesson(lid, lessonName, lessonAlias, lessonPlace, teacherName, startweek, endweek, null, week, time));
				}
			return lessonlist;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String getCurrentTimetableSetting() {
		
		return null;
	}
}
