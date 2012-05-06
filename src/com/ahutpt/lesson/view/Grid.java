package com.ahutpt.lesson.view;

import java.io.Serializable;

import com.ahutpt.lesson.LessonActivity;
import com.ahutpt.lesson.lesson.Lesson;
import com.ahutpt.lesson.lesson.LessonManager;
import com.ahutpt.lesson.time.Timetable;

import android.app.Activity;
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
	private Canvas canvas;

	float top, left;
	float cellWidth, cellHeight;
	float calendarWidth, calendarHeight;
	float textLeft, textTop;
	
	private static int markWeek = -1,markTime = -1;
	
	public static final int NORMALTIME = 0;
	public static final int FREETIME = 1;
	public static final int BUSYTIME = 2;
	public static final int NEXTTIME = 3;
	
	public Grid(Activity activity, View view) {
		super(activity, view);
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
		for(Lesson[] lessonsOfDay:LessonManager.lessons){
			if(lessonsOfDay!=null){
				for(Lesson lesson:lessonsOfDay){
					if(lesson!=null){
						if(lesson.canAppend()){
						canvas.drawLine(left + cellWidth * lesson.week, top + cellHeight * (lesson.time + 1), left + cellWidth * (lesson.week + 1),
								top + cellHeight * (lesson.time + 1), paint);
						}
					}
				}
			}
		}
		
		//画背景
		drawBackgrounds();
		
		//载入课程
		if(!LessonManager.loaded)
			new LessonManager(context);
		if(!Timetable.loaded)
			new Timetable(context);
		
		Timetable.initTime();
		
		//课程名和地点
		paint.setAntiAlias(true);
		for(Lesson[] lessonsOfDay:LessonManager.lessons){
			if(lessonsOfDay!=null){
				for(Lesson lesson:lessonsOfDay){
					if(lesson!=null){
						if(lesson.canAppend()&&Timetable.nowIsAtLessonBreak(lesson.week, lesson.time)){
							drawLesson(lesson, true, lesson.appendMode());
						}else if(lesson.isAppended()&&Timetable.nowIsAtLessonBreak(lesson.week, lesson.time - 1)){
							//四节课的课间且是后两节课，不用画了
						}else{
							drawLesson(lesson, lesson.isNowHaving()==0?true:false, lesson.appendMode());
						}
					}
				}
			}
		}
		
		drawMarkBorder();
	}

	private void drawBackgrounds() {
		//画背景

		//处理在四节课课间的情况
		Lesson tLesson = LessonManager.getLessonAt(Timetable.weekDay, 0, context);
		if(tLesson!=null&&tLesson.canAppend()&&Timetable.nowIsAtLessonBreak(Timetable.weekDay, 0)){
			drawBackground(tLesson.week, tLesson.time, BUSYTIME, tLesson.appendMode());
		}
		tLesson = LessonManager.getLessonAt(Timetable.weekDay, 2, context);
		if(tLesson!=null&&tLesson.canAppend()&&Timetable.nowIsAtLessonBreak(Timetable.weekDay, 2)){
			drawBackground(tLesson.week, tLesson.time, BUSYTIME, tLesson.appendMode());
		}
		
		Lesson lesson = LessonManager.getLessonAt(Timetable.weekDay,
				Timetable.getCurrentTimeBlock(Timetable.DelayDefault), context);
		if(lesson!=null) {
			drawBackground(lesson.week, lesson.time, BUSYTIME, lesson.appendMode());
		}else{
			int curTimeBlock = Timetable.getCurrentTimeBlock(Timetable.DelayDefault);
			if(curTimeBlock != -1){
				drawBackground(Timetable.weekDay, curTimeBlock, FREETIME, 0);
			}
		}

		lesson = Timetable.getNextLesson(Timetable.DelayDefault);
		if(lesson!=null){
			drawBackground(lesson.week, lesson.time, NEXTTIME, lesson.appendMode());
		}
	}

	private void drawMarkBorder(){
		paint.setColor(RED);
		paint.setStyle(Paint.Style.STROKE);
		if(markWeek!=-1&&markTime!=-1){
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

	private void drawLesson(Lesson lesson,boolean busytime,
			int appendMode) {
		// 一般课程
		if(lesson == null) return;
		paint.setTextSize(lessonNameSize);
		int week,time;
		week = lesson.week;
		time = lesson.time;
		String name, place;
		name = lesson.alias;
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
			mlesson = LessonManager.getLessonAt(week, time + 1, context);
			if(mlesson==null)return;
			if(mlesson.isNowHaving()==0)return;
			textTop = top + cellHeight * time
			+ cellHeight - paint.getTextSize();
			break;
		case -1:
			mlesson = LessonManager.getLessonAt(week, time - 1, context);
			if(mlesson==null)return;
			if(mlesson.isNowHaving()==0)return;
			textTop = top + cellHeight * (time - 1)
			+ cellHeight - paint.getTextSize();
			break;
		}

		// 画课程名
		if (busytime)
			paint.setColor(Color.WHITE);
		else
			paint.setColor(Color.BLACK);
		
		if (name.length() <= 2) {
			canvas.drawText(name, textLeft, textTop, paint);
		} else if (name.length() <= 4) {
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
		if (busytime)
			paint.setColor(Color.WHITE);
		else
			paint.setColor(Color.parseColor("#B22222"));

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

	public void openLessonDetail(float x, float y) {
		// 打开课程详情
		int week = (int) (x / cellWidth);
		int time = (int) ((y - top) / cellHeight);
		Intent i = new Intent(context, LessonActivity.class);
		i.putExtra("week", week);
		i.putExtra("time", time);
		context.startActivity(i);
	}
	
	public void markLesson(float x, float y) {
		markWeek = (int) (x / cellWidth);
		markTime = (int) ((y - top) / cellHeight);
		
	}
}
