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

import android.content.Context;
import android.util.Log;

import com.ahutlesson.android.model.ForumThread;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.Post;
import com.ahutlesson.android.model.User;
import com.ahutlesson.android.model.UserManager;

public class AHUTAccessor {
	
	private static AHUTAccessor accessor;
	private static Context context;
	
	//public static final String SERVER_URL = "http://ahutlesson.sinaapp.com/";
	public static final String SERVER_URL = "http://192.168.150.100/lesson/";
	
	public AHUTAccessor(Context context0) {
		context = context0;
	}
	
	public static AHUTAccessor getInstance(Context context) {
		if(accessor == null){
			accessor = new AHUTAccessor(context);
		}
		return accessor;
	}
	
	public String getURL(String URL) {
		HttpGet request = new HttpGet(URL);
		String cookie = UserManager.getInstance(context).getCookie();
		if(cookie != null) {
			request.addHeader("Cookie", "ck=" + cookie);
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
		log(URL);
		log(strResult);
		return strResult;
	}

	public String postURL(String URL, List<NameValuePair> params) {
		HttpPost request = new HttpPost(URL);
		String cookie = UserManager.getInstance(context).getCookie();
		if(cookie != null) {
			request.addHeader("Cookie", "ck=" + cookie);
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
		log(URL);
		log(strResult);
		return strResult;
	}
	
	public static void log(String i) {
		Log.i("AHUTAPI", i);
	}

	public static String getCurrentTimetableSetting() {
		
		return null;
	}

	public String regsterUser(String uxh, String password) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("x", uxh));
		params.add(new BasicNameValuePair("p", password));
		return postURL(SERVER_URL + "api/user.handler.php?act=register", params);
	}
	
	public String validateUser(String uxh, String password) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("x", uxh));
		params.add(new BasicNameValuePair("p", password));
		return postURL(SERVER_URL + "api/user.handler.php?act=login", params);
	}
	
	public User getUserInfo() {
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
	
	public ArrayList<Lesson> getLessonList(String uxh) {
		String ret = getURL(SERVER_URL + "api/getlessonlist.php?xh=" + uxh);
		ArrayList<Lesson> lessonList = new ArrayList<Lesson>();
		try {
			JSONTokener jsonParser = new JSONTokener(ret);
			JSONArray lessons = (JSONArray)jsonParser.nextValue();
			JSONObject lesson;
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
				lessonList.add(new Lesson(lid, lessonName, lessonAlias, lessonPlace, teacherName, startweek, endweek, null, week, time));
			}
			return lessonList;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public ArrayList<ForumThread> getForumThreadList(int lid, int page) {
		String ret = getURL(SERVER_URL + "api/thread.handler.php?act=get&lid=" + lid + "&page=" + page);
		ArrayList<ForumThread> threadList = new ArrayList<ForumThread>();
		try {
			JSONTokener jsonParser = new JSONTokener(ret);
			JSONArray retArray = (JSONArray)jsonParser.nextValue();
			JSONArray threads = (JSONArray)retArray.getJSONArray(1);
			JSONObject thread;
			for(int i = 0; i < threads.length(); i++) {
				thread = threads.getJSONObject(i);
				ForumThread t = new ForumThread();
				t.tid = thread.getInt("tid");
				t.lid = thread.getInt("lid");
				t.subject = thread.getString("subject");
				t.uxh = thread.getString("uxh");
				t.uname = thread.getString("uname");
				t.view = thread.getInt("view");
				t.reply = thread.getInt("reply");
				t.setReplyTime(thread.getString("lastreply_time"));
				threadList.add(t);
			}
			return threadList;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public ArrayList<Post> getPostList(int tid, int page) {
		String ret = getURL(SERVER_URL + "api/post.handler.php?act=get&tid=" + tid + "&page=" + page);
		ArrayList<Post> postList = new ArrayList<Post>();
		try {
			JSONTokener jsonParser = new JSONTokener(ret);
			JSONArray retArray = (JSONArray)jsonParser.nextValue();
			JSONArray posts = (JSONArray)retArray.getJSONArray(1);
			JSONObject post;
			for(int i = 0; i < posts.length(); i++) {
				post = posts.getJSONObject(i);
				Post p = new Post();
				p.pid = post.getInt("pid");
				p.tid = post.getInt("tid");
				p.uxh = post.getString("uxh");
				p.uname = post.getString("uname");
				p.content = post.getString("content");
				p.floor = post.getInt("floor");
				p.setPostTime(post.getString("post_time"));
				postList.add(p);
			}
			return postList;
		} catch (Exception ex) {
			return null;
		}
	}

	public static String getAvatarURI(String uxh) {
		return SERVER_URL + "api/getavatar.php?uxh=" + uxh;
	}

	public String postNewThread(int lid, String subject, String content) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("l", String.valueOf(lid)));
		params.add(new BasicNameValuePair("s", subject));
		params.add(new BasicNameValuePair("c", content));
		return postURL(SERVER_URL + "api/thread.handler.php?act=new&from=mobile", params);
	}
	
	public String postNewReply(int tid, String content) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("t", String.valueOf(tid)));
		params.add(new BasicNameValuePair("c", content));
		return postURL(SERVER_URL + "api/post.handler.php?act=new&from=mobile", params);
	}

}
