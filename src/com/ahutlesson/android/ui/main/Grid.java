package com.ahutlesson.android.ui.main;

import java.io.Serializable;

import com.ahutlesson.android.LessonActivity;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.Timetable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * 绘制日期和网格
 * 
 * */
public class Grid extends ScheduleParent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int NORMALTIME = 0;
	public static final int FREETIME = 1;
	public static final int BUSYTIME = 2;
	public static final int NEXTTIME = 3;

	private Lesson[][] lessons;
	private Timetable timetable;
	private Context context;
	private boolean isLocalDB;
	
	private Canvas canvas;
	
	float top, left;
	float cellWidth, cellHeight;
	float calendarWidth, calendarHeight;
	float textLeft, textTop;
	
	public int markWeek = -1, markTime = -1, markLid = -1;
	
	public Grid(Activity activity, View view, Lesson[][] lessons0, boolean isLocal) {
		super(activity, view);
		context = activity;
		lessons = lessons0;
		timetable = Timetable.getInstance(context);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openLessonActivity();
			}
		});
		isLocalDB = isLocal;
		left = borderMargin;
		top = borderMargin + weekNameSize + weekNameMargin * 2 + 4;
	}

	@Override
	public void draw(Canvas canvas0) {
		canvas = canvas0;

		calendarWidth = view.getMeasuredWidth() - left * 2;
		calendarHeight = view.getMeasuredHeight() - top - borderMargin;
		cellWidth = calendarWidth / 7;
		cellHeight = calendarHeight / 5;
		
		//画线
		drawLines();
		
		//去掉四节课中间的线
		paint.setColor(Color.WHITE);
		for(Lesson[] lessonsOfDay : lessons){
			if(lessonsOfDay!=null){
				for(Lesson lesson:lessonsOfDay){
					if(lesson!=null){
						if(lessonCanAppend(lesson,lessons)){
						canvas.drawLine(left + cellWidth * lesson.week, top + cellHeight * (lesson.time + 1), left + cellWidth * (lesson.week + 1),
								top + cellHeight * (lesson.time + 1), paint);
						}
					}
				}
			}
		}

		//触摸的课程
		drawMarkBackground();
		
		//画背景
		drawBackgrounds();
		
		//课程名和地点
		paint.setAntiAlias(true);
		for(Lesson[] lessonsOfDay : lessons){
			if(lessonsOfDay!=null){
				for(Lesson lesson:lessonsOfDay){
					if(lesson!=null){
						if(lessonCanAppend(lesson,lessons)&&timetable.nowIsAtLessonBreak(lesson.week, lesson.time)){
							drawLesson(lesson, true);
						}else if(lessonIsAppended(lesson,lessons)&&timetable.nowIsAtLessonBreak(lesson.week, lesson.time - 1)){
							//四节课的课间且是后两节课，不用画了
						}else{
							drawLesson(lesson, timetable.isNowHavingLesson(lesson)==0?true:false);
						}
					}
				}
			}
		}
	}

	private void drawBackgrounds() {
		//画背景

		//处理在四节课课间的情况
		Lesson tLesson = lessons[timetable.weekDay][0];
		if(tLesson!=null && lessonCanAppend(tLesson,lessons) && timetable.nowIsAtLessonBreak(timetable.weekDay, 0)){
			drawBackground(tLesson.week, tLesson.time, BUSYTIME, lessonAppendMode(tLesson,lessons));
		}
		tLesson = lessons[timetable.weekDay][2];
		if(tLesson!=null && lessonCanAppend(tLesson,lessons) && timetable.nowIsAtLessonBreak(timetable.weekDay, 2)){
			drawBackground(tLesson.week, tLesson.time, BUSYTIME, lessonAppendMode(tLesson,lessons));
		}
		
		Lesson lesson = getLesson(timetable.weekDay, timetable.getCurrentTimeBlock(Timetable.DelayDefault));
		if(lesson!=null && lesson.isInRange(context)) {
			drawBackground(lesson.week, lesson.time, BUSYTIME, lessonAppendMode(lesson,lessons));
		}else{
			int curTimeBlock = timetable.getCurrentTimeBlock(Timetable.DelayDefault);
			if(curTimeBlock != -1){
				drawBackground(timetable.weekDay, curTimeBlock, FREETIME, 0);
			}
		}

		lesson = timetable.getNextLesson(Timetable.DelayDefault);
		if(lesson!=null){
			drawBackground(lesson.week, lesson.time, NEXTTIME, lessonAppendMode(lesson,lessons));
		}
	}
	
	private void drawMarkBackground(){
		paint.setColor(Color.parseColor("#DDDDDD"));
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		if(markWeek!=-1 && markTime!=-1){
			canvas.drawRect(left + cellWidth * markWeek, top + cellHeight * markTime, left + cellWidth * (markWeek + 1), top + cellHeight * (markTime + 1), paint);
		}
	}
	
	private void drawLines() {
		paint.setColor(Color.LTGRAY);

		//画横线
		for (int i = 0; i <= 5; i++) {
			canvas.drawLine(left, top + (cellHeight) * i, left + calendarWidth,
					top + (cellHeight) * i, paint);
		}
		//画竖线
		for (int i = 0; i <= 7; i++) {
			canvas.drawLine(left + cellWidth * i, top, left + cellWidth * i,
					view.getMeasuredHeight() - borderMargin, paint);
		}

	}
	
	public static final int GREEN = Color.parseColor("#228B22");
	public static final int RED = Color.parseColor("#B22222");
	public static final int GRAY = Color.parseColor("#CDCDCD");
	
	private void drawBackground(int week,int time, int mode, int append) {
		// 画下节课背景
		// append:0 默认 1 扩展下节 -1 扩展上节
		Paint paintbg = new Paint();
		switch(mode){
		case NORMALTIME:
			paintbg.setColor(Color.WHITE);
			break;
		case FREETIME:
			paintbg.setColor(GREEN);
			break;
		case BUSYTIME:
			paintbg.setColor(RED);
			break;
		case NEXTTIME:
			paintbg.setColor(GRAY);
			break;
		}
		switch (append){
		case 0:
			canvas.drawRect(left + cellWidth * (week), top + cellHeight
					* time, left + cellWidth * (week + 1), top
					+ cellHeight * (time + 1), paintbg);
			break;
		case 1:
			canvas.drawRect(left + cellWidth * week, top
					+ cellHeight * time, left + cellWidth
					* (week + 1), top + cellHeight
					* (time + 2), paintbg);
			break;
		case -1:
			canvas.drawRect(left + cellWidth * week, top
					+ cellHeight * (time - 1), left + cellWidth
					* (week + 1), top + cellHeight
					* (time + 1), paintbg);
			break;
		}
	}

	private void drawLesson(Lesson lesson,boolean busytime) {
		// 一般课程
		if(lesson == null) return;
		paint.setTextSize(lessonNameSize);
		int week,time;
		int appendMode = lessonAppendMode(lesson,lessons);
		week = lesson.week;
		time = lesson.time;
		String place;
		LessonName name = (lesson.alias.contentEquals("")) ? new LessonName(lesson.name) : new LessonName(lesson.alias);
		place = lesson.place;
		textLeft = left + cellWidth * week
				+ cellWidth / 2 - paint.getTextSize();
		Lesson mlesson;
		switch(appendMode){
		case 0:
			textTop = top + cellHeight * time
			+ cellHeight / 2 - paint.getTextSize();
			break;
		case 1:
			mlesson = getLesson(week, time + 1);
			if(mlesson==null)return;
			if(timetable.isNowHavingLesson(mlesson) == 0)return;
			textTop = top + cellHeight * time
			+ cellHeight - paint.getTextSize();
			break;
		case -1:
			mlesson = getLesson(week, time - 1);
			if(mlesson==null)return;
			if(timetable.isNowHavingLesson(mlesson) == 0)return;
			textTop = top + cellHeight * (time - 1)
			+ cellHeight - paint.getTextSize();
			break;
		}

		// 画课程名
		if (busytime){
			if(lesson.isInRange(context)){
				paint.setColor(Color.WHITE);
			}else{
				paint.setColor(Color.TRANSPARENT);
			}
		}else if(lesson.hasHomework){
			paint.setColor(Color.parseColor("#CE5600"));
		}else if(!lesson.isInRange(context)){
			paint.setColor(Color.parseColor("#999999"));
		}else{
			paint.setColor(Color.BLACK);
		}
		
		if(appendMode == -1){
			paint.setColor(Color.TRANSPARENT);//后两节课不画
		}
		
		float length = name.length();
		
		if (length <= 2) {
			canvas.drawText(name.toString(), textLeft, textTop, paint);
		} else if (length <= 4) {
			canvas.drawText(name.substring(0, 2), textLeft, textTop, paint);
			canvas.drawText(name.substring(2), textLeft,
					textTop + (paint.getTextSize() + 5), paint);
		} else {
			canvas.drawText(name.substring(0, 2), textLeft, textTop, paint);
			canvas.drawText(name.substring(2, 4), textLeft,
					textTop + (paint.getTextSize() + 5), paint);
		}
		// 画地点
		paint.setTextSize(lessonPlaceSize);
		if (busytime){
			if(lesson.isInRange(context)){
				paint.setColor(Color.WHITE);
			}else{
				paint.setColor(Color.TRANSPARENT);
			}
		}else if(!lesson.isInRange(context)){
			paint.setColor(Color.parseColor("#999999"));
		}else{
			paint.setColor(Color.parseColor("#B22222"));
		}

		if(appendMode == -1){
			paint.setColor(Color.TRANSPARENT);//后两节课不画
		}
		
		switch(appendMode){
		case 0:
			textTop = top + cellHeight * (time + 1) - cellHeight / 5;
			break;
		case 1:
			textTop = top + cellHeight * (time + 2) - cellHeight / 2 - cellHeight / 5;
			break;
		case -1:
			textTop = top + cellHeight * (time) + cellHeight / 2 - cellHeight / 5;
			break;
		}

		if (place.length() <= 4) {
			textLeft = left + cellWidth * week
					+ (cellWidth - paint.measureText(place)) / 2;
			canvas.drawText(place, textLeft, textTop, paint);
		} else if (place.length() <= 8) {
			String roomNumber = place.substring(place.length() - 3,
					place.length());
			if (isInt(roomNumber)) {
				textLeft = left
						+ cellWidth
						* week
						+ (cellWidth - paint.measureText(place.substring(0,
								place.length() - 3))) / 2;
				canvas.drawText(place.substring(0, place.length() - 3),
						textLeft, textTop, paint);
				textLeft = left + cellWidth * week
						+ (cellWidth - paint.measureText(roomNumber)) / 2;
				canvas.drawText(roomNumber, textLeft,
						textTop + paint.getTextSize(), paint);
			} else {
				textLeft = left
						+ cellWidth
						* week
						+ (cellWidth - paint.measureText(place.substring(0, 4)))
						/ 2;
				canvas.drawText(place.substring(0, 4), textLeft, textTop, paint);
				textLeft = left + cellWidth * week
						+ (cellWidth - paint.measureText(place.substring(4)))
						/ 2;
				canvas.drawText(place.substring(4), textLeft,
						textTop + paint.getTextSize(), paint);
			}
		}
	}

	private boolean isInt(String str) {
		for(int i = 0;i < str.length(); i++){
			if(!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	private Lesson getLesson(int week, int time){
		return (!Timetable.isValidWeek(week)||!Timetable.isValidTime(time))? null : lessons[week][time];
	}
	

	public boolean lessonCanAppend(Lesson lesson,Lesson[][] lessons) {
		// 后两节有课
		if (lesson.time == 0 || lesson.time == 2) {
			Lesson appendLesson =  lessons[lesson.week][lesson.time + 1];
			if (appendLesson!=null) {
				if (appendLesson.name.contentEquals(lesson.name)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean lessonIsAppended(Lesson lesson,Lesson[][] lessons) {
		// 前两节有课
		if (lesson.time == 1 || lesson.time == 3) {
			Lesson appendLesson = lessons[lesson.week][lesson.time - 1];
			if (appendLesson!=null) {
				if (appendLesson.name.contentEquals(lesson.name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int lessonAppendMode(Lesson lesson,Lesson[][] lessons){
		if(lessonCanAppend(lesson, lessons))return 1;
		if(lessonIsAppended(lesson, lessons))return -1;
		return 0;
	}

	
	public void markLesson(float x, float y) {
		int gridX = (int) (x / cellWidth);
		int gridY = (int) ((y - top) / cellHeight);
		if(!Timetable.isValidWeekTime(gridX, gridY)) return;
		if(lessons[gridX][gridY] != null) {
			markWeek = gridX;
			markTime = gridY;
			markLid = lessons[gridX][gridY].lid;
		}else{
			markLid = -1;
			markWeek = -1;
			markTime = -1;
		}
	}
	
	public void openLessonActivity() {
		if(!Timetable.isValidWeekTime(markWeek, markTime)) return;
		if(lessons[markWeek][markTime] == null) return;
		Intent i = new Intent(context, LessonActivity.class);
		i.putExtra("lid", markLid);
		i.putExtra("week", markWeek);
		i.putExtra("time", markTime);
		i.putExtra("title", lessons[markWeek][markTime].getTitle());
		i.putExtra("local", isLocalDB);
		context.startActivity(i);
	}
}
