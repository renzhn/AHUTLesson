package com.ahutpt.lesson.time;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ahutpt.lesson.R;
import com.ahutpt.lesson.lesson.Lesson;
import com.ahutpt.lesson.lesson.LessonManager;

public class Timetable {
	
	private static Context context;
	private static Calendar cal;
	private static SharedPreferences preferences;
	public static String[] begintime = new String[5];
	public static String[] endtime = new String[5];
	private static int beginDate_year,beginDate_month,beginDate_day;
	public static int year,month,dayOfMonth,dayOfYear,weekDay;
	public static int begintimemin[] = new int[5];
	public static int endtimemin[] = new int[5];
	public static String[] weekname = new String[7],lessontime_name = new String[5];
	private static Editor edit;
	private static final boolean DEBUG = false;
	public static boolean loaded = false;
	
	public Timetable(Context context0){
		context = context0;
		preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		weekname = context.getResources().getStringArray(R.array.week_name);
		lessontime_name = context.getResources()
				.getStringArray(R.array.lessontime_name);
		initTime();
		loadData();
		loaded = true;
	}
	
	public static void loadData() {
		// 载入/刷新数据
		
		beginDate_year = preferences.getInt("begin_date_year", getYearOfCurrentPeriod());
		beginDate_month = preferences.getInt("begin_date_month", 8);//important! 从0开始 实际9月
		beginDate_day = preferences.getInt("begin_date_day", 2);
		
		begintime[0] = preferences.getString("time_begin0", "08:00");
		begintime[1] = preferences.getString("time_begin1", "10:00");
		begintime[2] = preferences.getString("time_begin2", "14:30");
		begintime[3] = preferences.getString("time_begin3", "16:30");
		begintime[4] = preferences.getString("time_begin4", "19:00");
		
		endtime[0] = preferences.getString("time_end0", "09:35");
		endtime[1] = preferences.getString("time_end1", "11:35");
		endtime[2] = preferences.getString("time_end2", "16:05");
		endtime[3] = preferences.getString("time_end3", "18:05");
		endtime[4] = preferences.getString("time_end4", "21:30");
		
		for(int i = 0;i < 5; i++){
			begintimemin[i] = time2minute(begintime[i]);
		}
		
		for(int i = 0;i < 5; i++){
			endtimemin[i] = time2minute(endtime[i]);
		}
		
	}
	
	public static void initTime(){
		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		weekDay = getCurrentWeekDay();
	}
	
	public static void loadLesson(){
		if(!LessonManager.loaded)
			new LessonManager(context);
	}

	public static int getNumOfWeekSincePeriod() {
		//计算开学第几周
		Calendar beginCal = Calendar.getInstance();
		beginCal.set(beginDate_year, beginDate_month, beginDate_day);
		int passDays = calcPassDays(beginCal);
		if(passDays>0)
			return passDays / 7 +1;
		else
			return 0;
	}
	
	public static int calcPassDays(Calendar cal_old){
		int passYear = ((year - cal_old.get(Calendar.YEAR))==1)? 1 : 0;
		int oldday = cal_old.get(Calendar.DAY_OF_YEAR);
		if(passYear == 0 && dayOfYear>=oldday){
				return dayOfYear - oldday;
		}
		if(passYear == 1 && dayOfYear<=oldday){
			return dayOfYear + dayOfYear(cal_old.get(Calendar.YEAR)) - oldday;
		}
		return 0;
	}
	
	public static int dayOfYear(int year) {
		//一年中的天数
		if((year%4==0&&year%400!=0)||year%400==0)
			return 365;
		else
			return 366;
	}

	public static int getYearOfCurrentPeriod() {
		// 计算当前学期的开学年份
		if(month >= 1 && month <= 2){
			return year - 1;
		}else{
			return year;
		}
	}

	public static boolean setBeginTime(int num,String newtime){
		if(!checkTime(newtime))return false;
		begintime[num] = newtime;
		if(edit == null)
			edit = preferences.edit();
		edit.putString("time_begin" + String.valueOf(num), newtime);
		edit.commit();
		return true;
	}
	public static boolean setEndTime(int num,String newtime){
		if(!checkTime(newtime))return false;
		begintime[num] = newtime;
		if(edit == null)
			edit = preferences.edit();
		edit.putString("time_end" + String.valueOf(num), newtime);
		edit.commit();
		return true;
	}

	public static int getTimeId(String time,int advanceMode) {
		//某一时间对应的时间段
		int min = time2minute(time);
		for(int i = 0;i < 5;i++){
			if(min >= (begintimemin[i] - getTimeDelay(advanceMode)) && min <= (endtimemin[i] + getTimeDelay(advanceMode))){
				return i;
			}
		}
		return -1;
	}
	
	public static int getCurrentTimeBlock(int advanceMode){
		//返回当前时间段，如果不在上课时间段，返回-1
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("HH:mm");     
		String  time  =  sDateFormat.format(new java.util.Date()); 
		return getTimeId(time, advanceMode);
	}
	
	public static final int DelayDefault = 0;
	public static final int DelayAlarm = 1;
	public static final int DelaySilent = 2;
	
	public static int getNextTimeBlock(int advanceMode){
		//返回将要到来的时间段
		//advanceMode:0默认 1闹钟提前时间 2静音提前时间
		int timeInAdvance = 0;
		switch (advanceMode){
		case DelayAlarm:
			timeInAdvance = Integer.valueOf(preferences.getString("NoticeTimeBeforeLesson", "20"));
			break;
		case DelaySilent:
			timeInAdvance = Integer.valueOf(preferences.getString("SilentDelay", "10"));
			break;
		}
		int min = getCurrentMinute();
		for(int i = 0;i < 5;i++){
			if(min < (begintimemin[i] - timeInAdvance ))
				return i;
		}
		return 5;
	}
	
	public static int time2minute(String time){
		int hour = Integer.valueOf(time.substring(0, 2));
		int minute = Integer.valueOf(time.substring(3, 5));
		return hour * 60 + minute;
	}
	public static boolean checkTime(String time){
		if(time.length()==5){
			if(Integer.valueOf(time.substring(0,2)) >= 0 && Integer.valueOf(time.substring(0,2)) < 24){
				if(Integer.valueOf(time.substring(3,5)) >= 0 && Integer.valueOf(time.substring(3,5)) < 60){
					return true;
				}
			}
		}
		return false;
	}

	public static int getBeginDate_year() {
		return beginDate_year;
	}

	public static int getBeginDate_month() {
		return beginDate_month + 1;
	}

	public static int getBeginDate_day() {
		return beginDate_day;
	}
	public static void setBeginDate_year(int beginYear){
		if(beginYear==year || beginYear==year-1){
			if(edit == null)
				edit = preferences.edit();
			edit.putInt("begin_date_year", beginYear);
			edit.commit();
		}
	}
	public static void setBeginDate_month(int beginMonth){
		if(beginMonth>=1 && beginMonth<=12){
			if(edit == null)
				edit = preferences.edit();
			edit.putInt("begin_date_month", beginMonth - 1);
			edit.commit();
		}
	}
	public static void setBeginDate_day(int beginDay){
		if(beginDay>=1 && beginDay<=31){
			if(edit == null)
				edit = preferences.edit();
			edit.putInt("begin_date_day", beginDay);
			edit.commit();
		}
	}
	
	public Lesson getCurrentLesson(int advanceMode) {
		// 当前时间的课
		return LessonManager.getLessonAt(weekDay, getCurrentTimeBlock(advanceMode), context);
	}

	public long getCurrentLessonEndTime(int time, int advanceMode) {
		// 当前时间的课结束时间
		Calendar c = Calendar.getInstance();
		String t = endtime[time];
		int hour = Integer.valueOf(t.substring(0, 2));
		int min = Integer.valueOf(t.substring(3));
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis() + getTimeDelay(advanceMode) * 60 * 1000;
	}

	public static Lesson getNextLesson(int advanceMode) {
		int week;
		int time = getNextTimeBlock(advanceMode);
		loadLesson();
		Lesson lesson;
		for(week = getCurrentWeekDay();week < 7; week++){
			while(time < 5){
				lesson =  LessonManager.getLessonAt(week, time, context);
				if(lesson != null&&lesson.isNowHaving()==-1&&!lesson.isAppended())
					return lesson;
				time++;
			}
			time = 0;
		}
		return null;
	}
	
	public static long getNextLessonBeginTime(int week, int time,int advanceMode) {
		//某课上课时间(毫秒)，若已上则下周
		
		Calendar c = Calendar.getInstance();
		String t = begintime[time];
		int weekOfMonth = c.get(Calendar.WEEK_OF_MONTH);
		int hour = Integer.valueOf(t.substring(0, 2));
		int min = Integer.valueOf(t.substring(3));
		c.setTimeInMillis(System.currentTimeMillis());
		if(isNowHavingLesson(week,time)!=-1){
			c.set(Calendar.WEEK_OF_MONTH, weekOfMonth + 1);
		}
		int sysWeek = normalWeek2SystemWeek(week);
		if(sysWeek == 1){
			//系统的星期日是上周的星期日
			c.set(Calendar.WEEK_OF_MONTH, weekOfMonth + 1);
		}
		
		c.set(Calendar.DAY_OF_WEEK, sysWeek);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if(DEBUG)
			Log.i("ahutlesson", "下次上课时间： " + Timetable.miliTime2String(c.getTimeInMillis()));
		return c.getTimeInMillis() - getTimeDelay(advanceMode) * 60 * 1000;
	}
	
	public static long getNextLessonEndTime(int week,int time,int advanceMode){
		Calendar c = Calendar.getInstance();
		String t = endtime[time];
		int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
		int hour = Integer.valueOf(t.substring(0, 2));
		int min = Integer.valueOf(t.substring(3));
		c.setTimeInMillis(System.currentTimeMillis());
		if(isNowHavingLesson(week,time)!=-1){
			c.set(Calendar.DAY_OF_YEAR,dayOfYear + 7);
		}
		c.set(Calendar.DAY_OF_WEEK, normalWeek2SystemWeek(week));
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if(DEBUG)
			Log.i("ahutlesson", "下课时间： " + "week: " + week + ", time: " + time + " : " + Timetable.miliTime2String(c.getTimeInMillis()));
		return c.getTimeInMillis() + getTimeDelay(advanceMode) * 60 * 1000;
	}

	public static int isNowHavingLesson(int week, int time) {
		//本周本课状态
		//-1还没上，0正在上，1上过了
		weekDay = getCurrentWeekDay();
		Calendar curCal = Calendar.getInstance();
		if(weekDay < week){
			return -1;
		}else if(weekDay > week){
			return 1;
		}else{
			int curMinute = curCal.get(Calendar.HOUR_OF_DAY) * 60 + curCal.get(Calendar.MINUTE);
			if(curMinute < begintimemin[time]){
				return -1;
			}else if(curMinute >= begintimemin[time] && curMinute <= endtimemin[time]){
				return 0;
			}else{
				return 1;
			}
		}
	}

	
	public static int getTimeDelay(int advanceMode){
		switch (advanceMode){
		case DelayAlarm:
			return Integer.valueOf(preferences.getString("NoticeTimeBeforeLesson", "20"));
		case DelaySilent:
			return  Integer.valueOf(preferences.getString("SilentDelay", "10"));
		}
		return 0;
	}
	
	public static int systemWeek2NormalWeek(int sys){
		int week = sys -2;
		if(week==-1)
			return 6;
		return week;
	}
	
	public static int normalWeek2SystemWeek(int week){
		if(week == 6)
			return 1;
		return week + 2;
	}


	public static String miliTime2String(long miliTime){
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("MM月d日 HH:mm"); 
		Date date = new Date();
		date.setTime(miliTime);
		return  sDateFormat.format(date);
	}

	public static int getCurrentWeekDay() {
		Calendar calendar = Calendar.getInstance();
		return systemWeek2NormalWeek(calendar.get(Calendar.DAY_OF_WEEK));
	}
	
	public static int getCurrentMinute(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
	}
	
	public static boolean isValidWeek(int week){
		return (week >= 0&&week <= 6)?true:false;
	}
	
	public static boolean isValidTime(int time){
		return (time >= 0&&time <= 4)?true:false;
	}
	
	public static boolean nowIsAtLessonBreak(int week, int i) {
		// 0 早上四节课的课间， 2 下午四节课的课间
		if(week != weekDay) return false;
		int min = getCurrentMinute();
		switch(i){
		case 0:
			return (min >= endtimemin[0] && min <= begintimemin[1])? true:false;
		case 2:
			return (min >= endtimemin[2] && min <= begintimemin[3])? true:false;
		}
		return false;
	}

}
