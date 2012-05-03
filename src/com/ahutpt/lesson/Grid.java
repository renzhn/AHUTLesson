package com.ahutpt.lesson;

import java.io.Serializable;

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
	private static Timetable timetable;

	public Grid(Activity activity, View view) {
		super(activity, view);
	}

	float top, left;
	float cellWidth,cellHeight;

	@Override
	public void draw(Canvas canvas) {
		if(timetable == null){
				timetable = new Timetable(context);
		}
		left = borderMargin;
		top = borderMargin + weekNameSize + weekNameMargin * 2 + 4;
		float calendarWidth = view.getMeasuredWidth() - left * 2;
		float calendarHeight = view.getMeasuredHeight() - top - borderMargin;
		cellWidth = calendarWidth / 7;
		cellHeight = calendarHeight / 5;
		
		
		//画当前时间背景
		int colOfCurrentTime = timetable.weekDay;
		int rowOfCurrentTime = timetable.getCurTimeBlock();
		if(rowOfCurrentTime!=-1){
			Paint paintbg = new Paint();
			Lesson lesson = new Lesson(colOfCurrentTime,rowOfCurrentTime,context);
			if(lesson.exist){
				paintbg.setColor(Color.parseColor("#B22222"));
			}else{
				paintbg.setColor(Color.parseColor("#7CFC00"));
			}
			canvas.drawRect(left + cellWidth * colOfCurrentTime, 
					top + cellHeight * rowOfCurrentTime,
					left + cellWidth * (colOfCurrentTime + 1), 
					top + cellHeight * (rowOfCurrentTime + 1), paintbg);	
		}
		//画下节课背景
		Lesson nextLesson = timetable.getNextLesson(Timetable.NextDefault);
		if(nextLesson != null){
			boolean flag = false;
			if(nextLesson.time==0||nextLesson.time==2){
				Lesson appendLesson = new Lesson(nextLesson.week, nextLesson.time + 1, context);
				if(appendLesson.exist){
					if(appendLesson.name.contentEquals(nextLesson.name)){
						Paint paintbg = new Paint();
						paintbg.setColor(Color.parseColor("#CDCDCD"));
						canvas.drawRect(left + cellWidth * (nextLesson.week), 
								top + cellHeight * nextLesson.time,
								left + cellWidth * (nextLesson.week + 1), 
								top + cellHeight * (nextLesson.time + 2), paintbg);	
						flag = true;
					}
				}
			}
			if(!flag){
				Paint paintbg = new Paint();
				paintbg.setColor(Color.parseColor("#CDCDCD"));
				canvas.drawRect(left + cellWidth * (nextLesson.week), 
						top + cellHeight * nextLesson.time,
						left + cellWidth * (nextLesson.week + 1), 
						top + cellHeight * (nextLesson.time + 1), paintbg);	
			}
		}
		
		paint.setColor(Color.LTGRAY);
		
		// 画横线
		for (int i = 0; i <= 5; i++) {
			canvas.drawLine(left, top + (cellHeight) * i, left + calendarWidth,
					top + (cellHeight) * i, paint);
		}
		// 画竖线
		for (int i = 0; i <= 7; i++) {
			canvas.drawLine(left + cellWidth * i, top, left + cellWidth * i,
					view.getMeasuredHeight() - borderMargin, paint);
		}
		
		paint.setAntiAlias(true);
		
		// 开始绘制日期文本
		
		LessonManager mLessonManager = new LessonManager(context);
		Lesson[] lessons = mLessonManager.getAllLessons();

		if(lessons==null)
			return;
		
		float textLeft,textTop;
		String name,place;
		int i = 0, week, time;
		while(lessons[i]!=null){
			paint.setTextSize(lessonNameSize);
			week = lessons[i].week;
			time = lessons[i].time;
			name = lessons[i].alias;
			place = lessons[i].place;
			textLeft = left + cellWidth * week
					+ (cellWidth - paint.getTextSize() * 2) / 2;
			textTop = top + cellHeight * time
					+ (cellHeight - paint.getTextSize() * 2 - 20) / 2;
			
			//画课程名
			if(lessons[i].isNowHaving()==0)
				paint.setColor(Color.WHITE);
			else
				paint.setColor(Color.BLACK);
			if(name.length()<=2){
				canvas.drawText(name, textLeft, textTop, paint);
			}else if(name.length()<=4){
				canvas.drawText(name.substring(0, 2), textLeft, textTop, paint);
				canvas.drawText(name.substring(2), textLeft, textTop + (paint.getTextSize() + 5) , paint);
			}else{
				canvas.drawText(name.substring(0, 2), textLeft, textTop, paint);
				canvas.drawText(name.substring(2, 4), textLeft, textTop + (paint.getTextSize() + 5), paint);
				canvas.drawText("...", textLeft, textTop + (paint.getTextSize() + 5) * 2, paint);
			}
			//画地点
			if(lessons[i].isNowHaving()==0)
				paint.setColor(Color.WHITE);
			else
				paint.setColor(Color.parseColor("#B22222"));
			
			paint.setTextSize(lessonPlaceSize);
			
			if(place.length()<=4){
				textLeft = left + cellWidth * week
						+ (cellWidth - paint.measureText(place)) / 2;
				textTop = top + cellHeight * (time + 1) - 15;
				canvas.drawText(place, textLeft, textTop, paint);
			}else if(place.length()<=8){
				String roomNumber = place.substring(place.length() - 3, place.length());
				if(isInt(roomNumber)){	
					textLeft = left + cellWidth * week
							+ (cellWidth - paint.measureText(place.substring(0, place.length() - 3))) / 2;
					textTop = top + cellHeight * (time + 1) - 15 - paint.getTextSize();
					canvas.drawText(place.substring(0, place.length() - 3), textLeft, textTop, paint);	
					textLeft = left + cellWidth * week
							+ (cellWidth - paint.measureText(roomNumber)) / 2;
					canvas.drawText(roomNumber, textLeft, textTop + paint.getTextSize(), paint);
				}else{
					textLeft = left + cellWidth * week
							+ (cellWidth - paint.measureText(place.substring(0, 4))) / 2;
					textTop = top + cellHeight * (time + 1) - 15 - paint.getTextSize();
					canvas.drawText(place.substring(0, 4), textLeft, textTop, paint);	
					textLeft = left + cellWidth * week
							+ (cellWidth - paint.measureText(place.substring(4))) / 2;
					canvas.drawText(place.substring(4), textLeft, textTop + paint.getTextSize(), paint);
				}
			}
			i++;
		}
	}

	private boolean isInt(String substring) {
		return substring.matches("\\d*");
	}

	public void openLessonDetail(float x, float y) {
		// 打开课程详情
		int week = (int) (x / cellWidth);
		int time = (int) ((y - top) / cellHeight);
		Intent i = new Intent(context,LessonActivity.class);
		i.putExtra("week", week);
		i.putExtra("time", time);
		context.startActivity(i);
	}
}
