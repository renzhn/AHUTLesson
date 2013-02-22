package com.ahutlesson.android.ui.lesson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ahutlesson.android.utils.DateHelper;

public class ForumThread {
	
	public int tid, lid, view, reply;
	public String subject, uxh, uname;
	public Date replyTime;
	
	public void setReplyTime(String replyTime0) {
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		try {  
		    replyTime = format.parse(replyTime0);  
		} catch (ParseException e) {
		    e.printStackTrace();
		}
	}
	
	public String getReplyTime() {
		return DateHelper.toSmartDateString(replyTime);
	}
}
