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

	public Grid(Activity activity, View view) {
		super(activity, view);
	}

	float top, left;
	float cellWidth,cellHeight;

	@Override
	public void draw(Canvas canvas) {
		left = borderMargin;
		top = borderMargin + weekNameSize + weekNameMargin * 2 + 4;
		float calendarWidth = view.getMeasuredWidth() - left * 2;
		float calendarHeight = view.getMeasuredHeight() - top - borderMargin;
		cellWidth = calendarWidth / 7;
		cellHeight = calendarHeight / 5;
		
		
		//画当前课背景
		Timetable timetable = new Timetable(context);
		int week = timetable.weekDay;
		int rowOfCurrentTime = timetable.getCurTimeBlock();
		if(rowOfCurrentTime!=-1){
			Paint paintbg = new Paint();
			Lesson lesson = new Lesson(week,rowOfCurrentTime,context);
			if(lesson.exist){
				paintbg.setColor(Color.parseColor("#B22222"));
			}else{
				paintbg.setColor(Color.parseColor("#7CFC00"));
			}
			canvas.drawRect(left + cellWidth * week, 
					top + cellHeight * rowOfCurrentTime,
					left + cellWidth * (week + 1), 
					top + cellHeight * (rowOfCurrentTime + 1), paintbg);	
		}
		//画下节课背景
		Lesson nextLesson = timetable.getNextLesson();
		if(nextLesson != null){
			Paint paintbg = new Paint();
			paintbg.setColor(Color.parseColor("#CDCDCD"));
			canvas.drawRect(left + cellWidth * (nextLesson.week), 
					top + cellHeight * nextLesson.time,
					left + cellWidth * (nextLesson.week + 1), 
					top + cellHeight * (nextLesson.time + 1), paintbg);	
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
		Lesson lesson;
		float textLeft,textTop;
		for(int col = 0; col < 7; col++){
			for(int row = 0; row < 5; row++){
				paint.setTextSize(lessonNameSize);
				lesson = new Lesson(col, row, this.context);
				if(lesson.exist){
					String lessonname = lesson.alias;
					String lessonplace = lesson.place;
					textLeft = left + cellWidth * col
							+ (cellWidth - paint.getTextSize() * 2) / 2;
					textTop = top + cellHeight * row
							+ (cellHeight - paint.getTextSize() * 2 - 20) / 2;
					
					//画课程名
					if(lesson.isNowHaving()==0)
						paint.setColor(Color.WHITE);
					else
						paint.setColor(Color.BLACK);
					if(lessonname.length()<=2){
						canvas.drawText(lessonname, textLeft, textTop, paint);
					}else if(lessonname.length()<=4){
						canvas.drawText(lessonname.substring(0, 2), textLeft, textTop, paint);
						canvas.drawText(lessonname.substring(2), textLeft, textTop + (paint.getTextSize() + 5) , paint);
					}else{
						canvas.drawText(lessonname.substring(0, 2), textLeft, textTop, paint);
						canvas.drawText(lessonname.substring(2, 4), textLeft, textTop + (paint.getTextSize() + 5), paint);
						canvas.drawText("...", textLeft, textTop + (paint.getTextSize() + 5) * 2, paint);
					}
					//画地点
					if(lesson.isNowHaving()==0)
						paint.setColor(Color.WHITE);
					else
						paint.setColor(Color.parseColor("#B22222"));
					paint.setTextSize(lessonPlaceSize);
					
					if(lessonplace.length()<=4){
						textLeft = left + cellWidth * col
								+ (cellWidth - paint.measureText(lessonplace)) / 2;
						textTop = top + cellHeight * (row + 1) - 15;
						canvas.drawText(lessonplace, textLeft, textTop, paint);
					}else if(lessonplace.length()<=8){
						String roomNumber = lessonplace.substring(lessonplace.length() - 3, lessonplace.length());
						if(isInt(roomNumber)){	
							textLeft = left + cellWidth * col
									+ (cellWidth - paint.measureText(lessonplace.substring(0, lessonplace.length() - 3))) / 2;
							textTop = top + cellHeight * (row + 1) - 15 - paint.getTextSize();
							canvas.drawText(lessonplace.substring(0, lessonplace.length() - 3), textLeft, textTop, paint);	
							textLeft = left + cellWidth * col
									+ (cellWidth - paint.measureText(roomNumber)) / 2;
							canvas.drawText(roomNumber, textLeft, textTop + paint.getTextSize(), paint);
						}else{
							textLeft = left + cellWidth * col
									+ (cellWidth - paint.measureText(lessonplace.substring(0, 4))) / 2;
							textTop = top + cellHeight * (row + 1) - 15 - paint.getTextSize();
							canvas.drawText(lessonplace.substring(0, 4), textLeft, textTop, paint);	
							textLeft = left + cellWidth * col
									+ (cellWidth - paint.measureText(lessonplace.substring(4))) / 2;
							canvas.drawText(lessonplace.substring(4), textLeft, textTop + paint.getTextSize(), paint);
						}
					}
				}
			}
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
