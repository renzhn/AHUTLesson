package com.ahutpt.lesson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Timetable {
	
	private Context context;
	private SharedPreferences preferences;
	private String[] begintime = {"","","","",""};
	private String[] endtime= {"","","","",""};
	private int beginDate_year,beginDate_month,beginDate_day;
	private Calendar cal;
	public int year,month,dayOfMonth,dayOfYear,weekDay;
	public static String[] weekname,lessontime_name;
	
	private static final boolean DEBUG = true;
	
	public Timetable(Context context0){
		context = context0;
		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		weekDay = getCurrentWeekDay();
		preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		

		weekname = context.getResources().getStringArray(R.array.week_name);
		lessontime_name = context.getResources()
				.getStringArray(R.array.lessontime_name);
		
		loadData();
	}
	
	public void loadData() {
		// 载入/刷新数据
		
		beginDate_year = preferences.getInt("begin_date_year", getYearOfCurrentPeriod());
		beginDate_month = preferences.getInt("begin_date_month", 1);//starting from 0
		beginDate_day = preferences.getInt("begin_date_day", 13);
		
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
	}

	public int getNumOfWeekSincePeriod() {
		//计算开学第几周
		Calendar beginCal = Calendar.getInstance();
		beginCal.set(beginDate_year, beginDate_month, beginDate_day);
		int passDays = calcPassDays(beginCal);
		if(passDays>0)
			return passDays / 7 +1;
		else
			return 0;
	}
	
	public int calcPassDays(Calendar cal_old){
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
	
	public int dayOfYear(int year) {
		//一年中的天数
		if((year%4==0&&year%400!=0)||year%400==0)
			return 365;
		else
			return 366;
	}

	public int getYearOfCurrentPeriod() {
		// 计算当前学期的开学年份
		if(month >= 1 && month <= 2){
			return year - 1;
		}else{
			return year;
		}
	}

	public String getBeginTime(int num){
		return begintime[num];
	}
	public String getEndTime(int num){
		return endtime[num];
	}
	Editor edit = null;
	public boolean setBeginTime(int num,String newtime){
		if(!checkTime(newtime))return false;
		begintime[num] = newtime;
		if(edit == null)
			edit = preferences.edit();
		edit.putString("time_begin" + String.valueOf(num), newtime);
		edit.commit();
		return true;
	}
	public boolean setEndTime(int num,String newtime){
		if(!checkTime(newtime))return false;
		begintime[num] = newtime;
		if(edit == null)
			edit = preferences.edit();
		edit.putString("time_end" + String.valueOf(num), newtime);
		edit.commit();
		return true;
	}

	public int getTimeId(String time) {
		//某一时间对应的时间段
		for(int i = 0;i < 5;i++){
			if(time2minute(time) >= time2minute(begintime[i]) && time2minute(time) <= time2minute(endtime[i]))
				return i;
		}
		return -1;
	}
	
	public int getCurTimeBlock(){
		//返回当前时间段，如果不在上课时间段，返回-1
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("HH:mm");     
		String  time  =  sDateFormat.format(new java.util.Date()); 
		return getTimeId(time);
	}
	
	public int getNextTimeBlock(){
		//返回将要到来的时间段
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("HH:mm");     
		String  time  =  sDateFormat.format(new java.util.Date()); 
		int min = time2minute(time);
		for(int i = 0;i < 5;i++){
			if(min < time2minute(begintime[i]))
				return i;
		}
		return 5;
	}
	
	public int time2minute(String time){
		int hour = Integer.valueOf(time.substring(0, 2));
		int minute = Integer.valueOf(time.substring(3, 5));
		return hour * 60 + minute;
	}
	public boolean checkTime(String time){
		if(time.length()==5){
			if(Integer.valueOf(time.substring(0,2)) >= 0 && Integer.valueOf(time.substring(0,2)) < 24){
				if(Integer.valueOf(time.substring(3,5)) >= 0 && Integer.valueOf(time.substring(3,5)) < 60){
					return true;
				}
			}
		}
		return false;
	}

	public int getBeginDate_year() {
		return beginDate_year;
	}

	public int getBeginDate_month() {
		return beginDate_month + 1;
	}

	public int getBeginDate_day() {
		return beginDate_day;
	}
	public void setBeginDate_year(int beginYear){
		if(beginYear==year || beginYear==year-1){
			if(edit == null)
				edit = preferences.edit();
			edit.putInt("begin_date_year", beginYear);
			edit.commit();
		}
	}
	public void setBeginDate_month(int beginMonth){
		if(beginMonth>=1 && beginMonth<=12){
			if(edit == null)
				edit = preferences.edit();
			edit.putInt("begin_date_month", beginMonth - 1);
			edit.commit();
		}
	}
	public void setBeginDate_day(int beginDay){
		if(beginDay>=1 && beginDay<=31){
			if(edit == null)
				edit = preferences.edit();
			edit.putInt("begin_date_day", beginDay);
			edit.commit();
		}
	}

	public long getNextLessonTime(int week, int time) {
		//某课下次上课时间(毫秒)
		Calendar c = Calendar.getInstance();
		String t = getBeginTime(time);
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
			Log.i("ahutlesson", "下次上课时间： " + this.miliTime2String(c.getTimeInMillis()));
		return c.getTimeInMillis();
	}
	
	public long getNextLessonEndTime(int week,int time){
		Calendar c = Calendar.getInstance();
		String t = getEndTime(time);
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
			Log.i("ahutlesson", "下课时间： " + "week: " + week + ", time: " + time + " : " + this.miliTime2String(c.getTimeInMillis()));
		return c.getTimeInMillis();
	}

	public Lesson getNextLesson() {
		int week;
		int time = getNextTimeBlock();
		Lesson lesson;
		for(week = weekDay;week < 7; week++){
			while(time < 5){
				lesson = new Lesson(week, time, context);
				if(lesson.exist)
					return lesson;
				time++;
			}
			time = 0;
		}
		return null;
	}
	
	public Lesson getCurrentLesson(){
		Lesson lesson = new Lesson(weekDay,getCurTimeBlock(),context);
		return lesson;
	}
	
	public int isNowHavingLesson(int week, int time) {
		//本周本课状态
		//-1还没上，0正在上，1上过了
		Calendar curCal = Calendar.getInstance();
		if(weekDay < week){
			return -1;
		}else if(weekDay > week){
			return 1;
		}else{
			int curMinute = curCal.get(Calendar.HOUR_OF_DAY) * 60 + curCal.get(Calendar.MINUTE);
			int beginMinute = time2minute(getBeginTime(time));
			int endMinute = time2minute(getEndTime(time));
			if(curMinute < beginMinute){
				return -1;
			}else if(curMinute >= beginMinute && curMinute <= endMinute){
				return 0;
			}else{
				return 1;
			}
		}
	}

	public int getCurrentWeekDay() {
		Calendar calendar = Calendar.getInstance();
		return systemWeek2NormalWeek(calendar.get(Calendar.DAY_OF_WEEK));
	}
	
	public int systemWeek2NormalWeek(int sys){
		int week = sys -2;
		if(week==-1)
			week = 6;
		return week;
	}
	
	public int normalWeek2SystemWeek(int week){
		if(week == 6)
			week = -1;
		return week + 2;
	}


	public String miliTime2String(long miliTime){
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("MM月d日 HH:mm"); 
		Date date = new Date();
		date.setTime(miliTime);
		return  sDateFormat.format(date);
	}

}
