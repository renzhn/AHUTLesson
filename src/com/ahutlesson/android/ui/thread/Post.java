package com.ahutlesson.android.ui.thread;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ahutlesson.android.utils.DateHelper;

public class Post {

	public int pid;
	public int tid;
	public String uxh;
	public String uname;
	public String content;
	public int floor;
	public Date postTime;
	public boolean hasAvatar;

	public void setPostTime(String postTime0) {
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		try {  
		    postTime = format.parse(postTime0);  
		} catch (ParseException e) {
		    e.printStackTrace();
		}
	}

	public String getPostTime() {
		return DateHelper.toSmartTimeString(postTime);
	}

}
