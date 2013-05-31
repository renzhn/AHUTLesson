package com.ahutlesson.android.api;

import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.ahutlesson.android.LessonActivity;
import com.ahutlesson.android.MessageActivity;
import com.ahutlesson.android.NoticeActivity;
import com.ahutlesson.android.ThreadActivity;
import com.ahutlesson.android.model.ForumThread;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.LessonListInfo;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Lessonmate;
import com.ahutlesson.android.model.LessonsInfo;
import com.ahutlesson.android.model.Message;
import com.ahutlesson.android.model.Notice;
import com.ahutlesson.android.model.Post;
import com.ahutlesson.android.model.Timetable;
import com.ahutlesson.android.model.TimetableSetting;
import com.ahutlesson.android.model.User;
import com.ahutlesson.android.model.UserInfo;
import com.ahutlesson.android.model.UserManager;
import com.ahutlesson.android.service.UnreadInfo;
import com.ahutlesson.android.utils.ChangeLog;
import com.ahutlesson.android.utils.Util;

/**
 * @author OHRZ
 *
 */
public class AHUTAccessor {

	private static AHUTAccessor accessor;
	private static Context context;

	private static final int TIMEOUT_CONNECTION = 5000;
	private static final int TIMEOUT_SOCKET = 10000;
	private HttpParams httpParameters = new BasicHttpParams();;

	//public static final String SERVER_URL = "http://ahutlesson.sinaapp.com/";

	public static final String SERVER_URL = "http://192.168.1.2/lesson/";

	public AHUTAccessor(Context context0) {
		context = context0;

		HttpConnectionParams.setConnectionTimeout(httpParameters,
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
	}

	public static AHUTAccessor getInstance(Context context) {
		if (accessor == null) {
			accessor = new AHUTAccessor(context);
		}
		return accessor;
	}

	public JSONObject getURL(String URL) throws Exception {
		HttpGet request = new HttpGet(URL);
		String cookie = UserManager.getInstance(context).getCookie();
		if (cookie != null) {
			request.addHeader("Cookie", "ck=" + cookie);
		}
		String strResult = null;
		try {
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient(
					httpParameters);
			HttpResponse httpResponse = defaultHttpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = new String(strResult.getBytes("ISO-8859-1"),
						"UTF-8");
			}
			defaultHttpClient.getConnectionManager().shutdown();
			Util.log(URL);
			Util.log(strResult);
		} catch (SocketTimeoutException e) {
			throw new Exception("连接超时，请稍候重试");
		} catch (Exception e) {
			throw new Exception("连接服务器失败，请检查网络设置");
		}
		try {
			JSONTokener jsonParser = new JSONTokener(strResult);
			JSONObject ret = (JSONObject) jsonParser.nextValue();
			int retCode = ret.getInt("code");
			if(retCode == 1) {
				String msg = ret.getString("msg");
				throw new Exception(msg);
			}else if(retCode == 0) {
				return ret;
			} 
		} catch (JSONException e) {  
			throw new Exception("解析数据出错");
		}
		throw new Exception("服务器返回了错误的数据");
	}

	public JSONObject postURL(String URL, List<NameValuePair> params)
			throws Exception {
		HttpPost request = new HttpPost(URL);
		String cookie = UserManager.getInstance(context).getCookie();
		if (cookie != null) {
			request.addHeader("Cookie", "ck=" + cookie);
		}
		String strResult = null;
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient(
					httpParameters);
			HttpResponse httpResponse = defaultHttpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = new String(strResult.getBytes("ISO-8859-1"),
						"UTF-8");
			}
			defaultHttpClient.getConnectionManager().shutdown();
			Util.log(URL);
			Util.log(strResult);
		} catch (SocketTimeoutException e) {
			throw new Exception("连接超时，请稍候重试");
		} catch (Exception e) {
			throw new Exception("连接服务器失败，请检查网络设置");
		}
		JSONTokener jsonParser = new JSONTokener(strResult);
		try {
			JSONObject ret = (JSONObject) jsonParser.nextValue();
			int retCode = ret.getInt("code");
			if(retCode == 1) {
				String msg = ret.getString("msg");
				throw new Exception(msg);
			}else if(retCode == 0) {
				return ret;
			} 
		} catch (JSONException e) {  
			throw new Exception("解析数据出错");
		}
		throw new Exception("服务器返回了错误的数据");
	}

	public String regsterUser(String uxh, String password) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("x", uxh));
		params.add(new BasicNameValuePair("p", password));
		JSONObject ret = postURL(SERVER_URL + "api/user.handler.php?act=register", params);
		String cookie = ret.getString("data");
		return cookie;
	}

	public String validateUser(String uxh, String password) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("x", uxh));
		params.add(new BasicNameValuePair("p", password));
		JSONObject ret = postURL(SERVER_URL + "api/user.handler.php?act=login", params);
		String cookie = ret.getString("data");
		return cookie;
	}

	public User getLoginUserInfo() throws Exception {
		User user = new User();
		JSONObject ret = getURL(SERVER_URL
				+ "api/user.handler.php?act=getloginuserinfo");
		JSONObject userInfo = ret.getJSONObject("data");
		try {
			user.uxh = userInfo.getString("uxh");
			user.uname = userInfo.getString("uname");
			user.bj = userInfo.getString("bj");
			user.password = userInfo.getString("password");
			user.signature = userInfo.getString("signature");
			user.isAdmin = (userInfo.getInt("is_admin") == 1);
		} catch (Exception e) {
			throw new Exception("解析用户登陆信息数据出错");
		}
		return user;
	}

	public LessonListInfo getLessonList(String uxh) throws Exception {
		JSONObject ret = getURL(SERVER_URL + "api/getlessonlist.php?xh=" + uxh);
		LessonListInfo info = new LessonListInfo();
		ArrayList<Lesson> lessonList = new ArrayList<Lesson>();
		try {
			JSONArray lessons = ret.getJSONArray("data");
			JSONObject lesson;
			for (int i = 0; i < lessons.length(); i++) {
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
				lessonList.add(new Lesson(lid, lessonName, lessonAlias,
						lessonPlace, teacherName, startweek, endweek, null,
						week, time));
			}
			if(lessonList.size() == 0)	throw new Exception("该学号课表为空，请检查学号是否有误或者反馈");
			info.lessonList = lessonList;
			JSONObject metadata = ret.getJSONObject("metadata");
			info.xm = metadata.getString("xm");
			info.build = metadata.getString("build");
			return info;
		} catch (Exception ex) {
			throw new Exception("解析课表数据出错");
		}
	}

	public LessonsInfo getLessons(String uxh) throws Exception {
		LessonListInfo lessonListInfo = getLessonList(uxh);
		LessonsInfo lessonsInfo = new LessonsInfo();
		Lesson[][] lessons = new Lesson[7][5];
		for (Lesson lesson : lessonListInfo.lessonList) {
			lessons[lesson.week][lesson.time] = lesson;
		}
		lessonsInfo.lessons = lessons;
		lessonsInfo.xm = lessonListInfo.xm;
		return lessonsInfo;
	}

	public ArrayList<ForumThread> getForumThreadList(int lid, int page)
			throws Exception {
		JSONObject ret = getURL(SERVER_URL + "api/thread.handler.php?act=get&lid="
				+ lid + "&page=" + page);
		ArrayList<ForumThread> threadList = new ArrayList<ForumThread>();
		try {
			JSONArray threads = ret.getJSONArray("data");
			JSONObject thread;
			for (int i = 0; i < threads.length(); i++) {
				thread = threads.getJSONObject(i);
				ForumThread t = new ForumThread();
				t.tid = thread.getInt("tid");
				t.lid = thread.getInt("lid");
				t.subject = thread.getString("subject");
				t.uxh = thread.getString("uxh");
				t.uname = thread.getString("uname");
				t.view = thread.getInt("view");
				t.reply = thread.getInt("reply");
				t.top = (thread.getInt("top") == 1);
				t.setReplyTime(thread.getString("lastreply_time"));
				threadList.add(t);
			}
			JSONObject metadata = ret.getJSONObject("metadata");
			LessonActivity.totalThreads = metadata.getInt("total");
			LessonActivity.threadsPerPage = metadata.getInt("threadsPerPage");
			LessonActivity.totalThreadPages = (int) Math.floor((LessonActivity.totalThreads - 1) / LessonActivity.threadsPerPage + 1);
			return threadList;
		} catch (Exception ex) {
			throw new Exception("解析数据出错");
		}
	}

	public ArrayList<Post> getPostList(int tid, int page) throws Exception {
		JSONObject ret = getURL(SERVER_URL + "api/post.handler.php?act=get&tid="
				+ tid + "&page=" + page);
		ArrayList<Post> postList = new ArrayList<Post>();
		try {
			JSONArray posts = ret.getJSONArray("data");
			JSONObject post;
			for (int i = 0; i < posts.length(); i++) {
				post = posts.getJSONObject(i);
				Post p = new Post();
				p.pid = post.getInt("pid");
				p.tid = post.getInt("tid");
				p.uxh = post.getString("uxh");
				p.uname = post.getString("uname");
				p.content = post.getString("content");
				p.floor = post.getInt("floor");
				p.hasAvatar = (post.getInt("has_avatar") == 1);
				p.setPostTime(post.getString("post_time"));
				postList.add(p);
			}
			JSONObject metadata = ret.getJSONObject("metadata");
			ThreadActivity.totalPosts = metadata.getInt("total");
			ThreadActivity.postsPerPage = metadata.getInt("postsPerPage");
			ThreadActivity.currentPage = metadata.getInt("currentPage");
			ThreadActivity.totalPages =  (int) Math.floor((ThreadActivity.totalPosts - 1) / ThreadActivity.postsPerPage + 1);
			return postList;
		} catch (Exception ex) {
			throw new Exception("解析帖子列表数据出错");
		}
	}

	public Post getPost(int pid) throws Exception {
		JSONObject ret = getURL(SERVER_URL + "api/post.handler.php?act=getbypid&pid="
				+ pid);
		try {
			Post p = new Post();
			JSONObject post = ret.getJSONObject("data");
			p.pid = post.getInt("pid");
			p.tid = post.getInt("tid");
			p.uxh = post.getString("uxh");
			p.uname = post.getString("uname");
			p.content = post.getString("content");
			p.floor = post.getInt("floor");
			p.hasAvatar = (post.getInt("has_avatar") == 1);
			p.setPostTime(post.getString("post_time"));
			return p;
		} catch (Exception ex) {
			throw new Exception("解析帖子数据出错");
		}
	}
	
	public static String getAvatarURI(String uxh) {
		return SERVER_URL + "api/getavatar.php?uxh=" + uxh;
	}

	public int postThread(int lid, String subject, String content)
			throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("l", String.valueOf(lid)));
		params.add(new BasicNameValuePair("s", subject));
		params.add(new BasicNameValuePair("c", content));
		JSONObject ret =  postURL(SERVER_URL
				+ "api/thread.handler.php?act=new&from=mobile", params);
		int newtid = ret.getInt("data");
		return newtid;
	}

	public void postReply(int tid, String content) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("t", String.valueOf(tid)));
		params.add(new BasicNameValuePair("c", content));
		postURL(SERVER_URL
				+ "api/post.handler.php?act=new&from=mobile", params);
		return;
	}

	public UnreadInfo getUnreadCount() throws Exception {
		UnreadInfo unreadInfo = new UnreadInfo();
		String uxh = UserManager.getInstance(context).getUserXH();
		if (uxh == null)
			throw new Exception("你还没有登录");
		JSONObject ret = getURL(SERVER_URL
				+ "api/notice.handler.php?act=getunreadcount&uxh=" + uxh);
		try {
			JSONObject data;
			data = ret.getJSONObject("data");
			unreadInfo.unreadMessage = data.getInt("m");
			unreadInfo.unreadNotice = data.getInt("n");
			JSONArray lidListHasNew = data.getJSONArray("l");
			for(int i = 0; i < lidListHasNew.length(); i++) {
				unreadInfo.unreadLessonForum.add(lidListHasNew.getInt(i));
			}
		} catch (Exception e) {
			throw new Exception("解析数据出错");
		}
		return unreadInfo;
	}

	public ArrayList<Notice> getNoticeList(int page) throws Exception {
		JSONObject ret = getURL(SERVER_URL
				+ "api/notice.handler.php?act=getnotice&page=" + page);
		ArrayList<Notice> list = new ArrayList<Notice>();
		try {
			JSONArray notices = ret.getJSONArray("data");
			JSONObject notice;
			for (int i = 0; i < notices.length(); i++) {
				notice = notices.getJSONObject(i);
				Notice n = new Notice();
				n.nid = notice.getInt("nid");
				n.tid = notice.getInt("tid");
				n.pid = notice.getInt("pid");
				n.subject = notice.getString("subject");
				n.read = (notice.getInt("read") == 1);
				n.toUxh = notice.getString("to_uxh");
				n.fromUxh = notice.getString("from_uxh");
				n.uname = notice.getString("uname");
				n.hasAvatar = (notice.getInt("has_avatar") == 1);
				n.setPostTime(notice.getString("post_time"));
				list.add(n);
			}
			JSONObject metadata = ret.getJSONObject("metadata");
			NoticeActivity.noticesPerPage = metadata.getInt("noticesPerPage");
			return list;
		} catch (Exception ex) {
			throw new Exception("解析数据出错");
		}
	}

	public ArrayList<Message> getMessageList(int page) throws Exception {
		JSONObject ret = getURL(SERVER_URL
				+ "api/notice.handler.php?act=getmessage&page=" + page);
		ArrayList<Message> list = new ArrayList<Message>();
		try {
			JSONArray messages = ret.getJSONArray("data");
			JSONObject message;
			for (int i = 0; i < messages.length(); i++) {
				message = messages.getJSONObject(i);
				Message m = new Message();
				m.mid = message.getInt("mid");
				m.title = message.getString("title");
				m.content = message.getString("content");
				m.read = (message.getInt("read") == 1);
				m.toUxh = message.getString("to_uxh");
				m.fromUxh = message.getString("from_uxh");
				m.uname = message.getString("uname");
				m.hasAvatar = (message.getInt("has_avatar") == 1);
				m.setPostTime(message.getString("post_time"));
				list.add(m);
			}
			JSONObject metadata = ret.getJSONObject("metadata");
			MessageActivity.messagesPerPage = metadata.getInt("messagesPerPage");
			return list;
		} catch (Exception ex) {
			throw new Exception("解析数据出错");
		}
	}

	public void deleteMessage(int mid) throws Exception {
		getURL(SERVER_URL + "api/notice.handler.php?act=deletemessage&mid="
				+ mid);
	}

	public void sendMessage(String uxh, String title, String content)
			throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("u", uxh));
		params.add(new BasicNameValuePair("t", title));
		params.add(new BasicNameValuePair("c", content));
		postURL(SERVER_URL + "api/notice.handler.php?act=sendmessage", params);
	}

	public ArrayList<Lessonmate> getLessonmateList(int lid, int page)
			throws Exception {
		JSONObject ret = getURL(SERVER_URL + "api/getlessonmates.php?lid=" + lid
				+ "&page=" + page);
		ArrayList<Lessonmate> list = new ArrayList<Lessonmate>();
		try {
			JSONArray lessonmates = ret.getJSONArray("data");
			JSONObject lessonmate;
			for (int i = 0; i < lessonmates.length(); i++) {
				lessonmate = lessonmates.getJSONObject(i);
				Lessonmate l = new Lessonmate();
				l.xh = lessonmate.getString("xh");
				l.xm = lessonmate.getString("xm");
				l.zy = lessonmate.getString("zy");
				l.bj = lessonmate.getString("bj");
				l.registered = (lessonmate.getInt("registered") == 1);
				l.hasAvatar = (lessonmate.getInt("has_avatar") == 1);
				l.signature = lessonmate.getString("signature");
				list.add(l);
			}
			JSONObject metadata = ret.getJSONObject("metadata");
			LessonActivity.lessonmatesPerPage = metadata.getInt("lessonmatesPerPage");
			return list;
		} catch (Exception ex) {
			throw new Exception("解析数据出错");
		}
	}

	public void uploadAvatar(Bitmap bm) throws Exception {
		String strResult = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 90, bos);
		byte[] data = bos.toByteArray();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost(SERVER_URL + "api/uploadavatar.php");
		String cookie = UserManager.getInstance(context).getCookie();
		if (cookie != null) {
			request.addHeader("Cookie", "ck=" + cookie);
		} else
			throw new Exception("尚未登录！");
		try {
			ByteArrayBody bab = new ByteArrayBody(data, "avatar.jpg");
			MultipartEntity reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("avatar_file", bab);
			request.setEntity(reqEntity);
			HttpResponse httpResponse = httpClient.execute(request);
			strResult = EntityUtils.toString(httpResponse.getEntity());
			strResult = new String(strResult.getBytes("ISO-8859-1"), "UTF-8");
			Util.log(strResult);
		} catch (Exception e) {
			throw e;
		}
		if(strResult.contentEquals("0")) {
			return;
		}else if(strResult.startsWith("1")) {
			throw new Exception(strResult.substring(2));
		}
	}

	public void setSignature(String signature) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("s", signature));
		postURL(SERVER_URL
				+ "api/user.handler.php?act=setsignature", params);
		return;
	}

	public UserInfo getUserInfo(String uxh) throws Exception {
		UserInfo userInfo = new UserInfo();
		JSONObject ret = getURL(SERVER_URL
				+ "api/user.handler.php?act=getuserinfo&uxh=" + uxh);
		try {
			JSONObject userInfoObject = ret.getJSONObject("data");
			userInfo.uxh = userInfoObject.getString("uxh");
			userInfo.uname = userInfoObject.getString("uname");
			userInfo.signature = userInfoObject.getString("signature");
			userInfo.hasAvatar = (userInfoObject.getInt("has_avatar") == 1);
			userInfo.isAdmin = (userInfoObject.getInt("is_admin") == 1);
			userInfo.xb = userInfoObject.getString("xb");
			userInfo.bj = userInfoObject.getString("bj");
			userInfo.zy = userInfoObject.getString("zy");
			userInfo.xy = userInfoObject.getString("xy");
			userInfo.rx = userInfoObject.getInt("rx");
			userInfo.registerTime = userInfoObject.getString("register_time");
			userInfo.lastloginTime = userInfoObject.getString("lastlogin_time");
		} catch (Exception e) {
			throw new Exception("解析错误");
		}
		return userInfo;
	}

	public TimetableSetting getTimetableSetting() throws Exception {
		TimetableSetting timetableSetting = new TimetableSetting();
		JSONObject ret = getURL(SERVER_URL + "api/gettimetable.php");
		try {
			JSONObject retObject = ret.getJSONObject("data");
			timetableSetting.year = retObject.getInt("year");
			timetableSetting.month = retObject.getInt("month");
			timetableSetting.day = retObject.getInt("day");
			timetableSetting.setSeason(retObject.getInt("season"));
		} catch (Exception e) {
			throw new Exception("解析时间表设置数据出错");
		}
		return timetableSetting;
	}

	public JSONObject checkUpdate() throws Exception {
		Timetable timetable = Timetable.getInstance(context);
		TimetableSetting timetableSetting = timetable.getTimetableSetting();
		LessonManager lessonManager = LessonManager.getInstance(context);
		ChangeLog cl = new ChangeLog(context);
		String appVer = cl.getThisVersion();
		String lessondbVer = lessonManager.getLessondbVersion();
		String timetableParam = "ty=" + timetableSetting.year + "&tm=" + timetableSetting.month + "&td=" + timetableSetting.day + "&ts=" + timetableSetting.getSeason();
		JSONObject ret = getURL(SERVER_URL + "api/update.handler.php?act=check&a=" + appVer + "&l=" + lessondbVer + "&" + timetableParam);
		return ret.getJSONObject("data");
	}
}
