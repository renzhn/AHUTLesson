package com.ahutlesson.android.ui.notice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ahutlesson.android.utils.DateHelper;

public class Notice {

	public int nid;
	public int pid;
	public int tid;
	public String subject;
	public String type;
	public boolean read;
	public String toUxh;
	public String fromUxh;
	public String uname;
	public boolean hasAvatar;
	public Date postTime;

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
