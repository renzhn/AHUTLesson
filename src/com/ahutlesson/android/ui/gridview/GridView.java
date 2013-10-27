package com.ahutlesson.android.ui.gridview;

import com.ahutlesson.android.LessonActivity;
import com.ahutlesson.android.R;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

public class GridView extends View {
	
	private Context context;
	private Lesson[][] lessons;
	private Timetable timetable;
	private boolean isLocal = true;
	
	private int markWeek = -1, markTime = -1, markLid = -1;

	public static final int NORMALTIME = 0;
	public static final int BUSYTIME = 1;
	public static final int NEXTTIME = 2;

	public static final int BLUE = Color.parseColor("#3F92D2");
	public static final int RED = Color.parseColor("#B22222");
	
	//if lessons null, load local
	public GridView(Context _context, Lesson[][] _lessons) {
		super(_context);
		context = _context;
		lessons = _lessons;
		if (lessons == null) {
			isLocal = false;
			lessons = LessonManager.getInstance(context).getLessons();
		}
		timetable = Timetable.getInstance(context);
	}

	private Paint paint = new Paint();
	private Canvas canvas;
	private float left, top;
	private float borderMargin, weekNameMargin, weekNameSize, lessonNameSize, lessonPlaceSize;
	private float calendarWidth, calendarHeight, cellWidth, cellHeight;
	@Override
	protected void onDraw(Canvas _canvas) {
		canvas = _canvas;
		paint.setAntiAlias(true);

		borderMargin = context.getResources().getDimension(R.dimen.calendar_border_margin);
		
		weekNameMargin = context.getResources().getDimension(R.dimen.weekname_margin);
		weekNameSize = context.getResources().getDimension(R.dimen.weekname_size);
		lessonNameSize = context.getResources().getDimension(R.dimen.lessonname_size);
		lessonPlaceSize = context.getResources().getDimension(R.dimen.lessonplace_size);

		left = borderMargin;
		top = borderMargin;
		String[] weekNames = context.getResources().getStringArray(R.array.week_name);

		paint.setTextSize(weekNameSize);
		
		paint.setColor(BLUE);
		float endYOfWeek = borderMargin * 2 + weekNameSize + weekNameMargin * 2 + 4;
		canvas.drawRect(0, 0, canvas.getWidth(), endYOfWeek, paint);
		
		float everyWeekWidth = (this.getMeasuredWidth() - borderMargin * 2) / 7;
		paint.setColor(Color.WHITE);
		for (int i = 0; i < 7; i++) {

			left = borderMargin + everyWeekWidth * i
					+ (everyWeekWidth - paint.measureText(weekNames[i])) / 2;
			canvas.drawText(weekNames[i], left, top + paint.getTextSize()
					+ weekNameMargin, paint);
		}

		//开始画课表
        left = 0;
        top = endYOfWeek;
        
		calendarWidth = this.getMeasuredWidth();
		calendarHeight = this.getMeasuredHeight() - top;
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

		//画背景
		drawBackgrounds();
		
		//课程名和地点
		paint.setAntiAlias(true);
		for(Lesson[] lessonsOfDay : lessons){
			if(lessonsOfDay!=null){
				for(Lesson lesson:lessonsOfDay){
					if(lesson!=null){
						if(lessonCanAppend(lesson,lessons) && timetable.nowIsAtLessonBreak(lesson.week, lesson.time)){
							drawLesson(lesson, true);
						}else if(lessonIsAppended(lesson,lessons) && timetable.nowIsAtLessonBreak(lesson.week, lesson.time - 1)){
							//四节课的课间且是后两节课，不用画了
						}else{
							drawLesson(lesson, timetable.isNowHavingLesson(lesson)==0?true:false);
						}
					}
				}
			}
		}

		this.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openLessonActivity();
			}
		});
	}

	private void drawBackgrounds() {
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
		}

		lesson = timetable.getNextLesson(Timetable.DelayDefault);
		if(lesson!=null){
			drawBackground(lesson.week, lesson.time, NEXTTIME, lessonAppendMode(lesson,lessons));
		}
	}
	
	private void drawLines() {
		Paint linePaint = new Paint();
		linePaint.setARGB(80, 0, 0, 0);
		linePaint.setStyle(Style.STROKE);
		linePaint.setPathEffect(new DashPathEffect(new float[] {5,10}, 0));
		
		//画横线
		for (int i = 1; i < 5; i++) {
			canvas.drawLine(left, top + (cellHeight) * i, left + calendarWidth,
					top + (cellHeight) * i, linePaint);
		}
		//画竖线
		for (int i = 1; i < 7; i++) {
			canvas.drawLine(left + cellWidth * i, top, left + cellWidth * i,
					this.getMeasuredHeight(), linePaint);
		}
	}

	private void drawBackground(int week,int time, int mode, int append) {
		// 画下节课背景
		// append:0 默认 1 扩展下节 -1 扩展上节
		Paint paintbg = new Paint();
		switch(mode){
		case NORMALTIME:
			paintbg.setColor(Color.WHITE);
			break;
		case BUSYTIME:
			paintbg.setColor(RED);
			break;
		case NEXTTIME:
			paintbg.setARGB(30, 0, 0, 0);
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

	private float textLeft, textTop;
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
			paint.setARGB(30, 0, 0, 0);
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
			paint.setARGB(30, 0, 0, 0);
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
		i.putExtra("local", isLocal);
		context.startActivity(i);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		markLesson(event.getX(), event.getY());
		return super.onTouchEvent(event);
	}

}
