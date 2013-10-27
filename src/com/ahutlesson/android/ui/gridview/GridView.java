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
	private static final int ORIENTATION_LANDSCAPE = 2;
	private boolean isLandscapeMode = false;

	public static final float WEEKNAME_SIZE = 28;
	public static final float WEEKNAME_MARGIN = 10;
	public static final float LESSONNAME_SIZE = 29;
	public static final float LESSONPLACE_SIZE = 24;
	public static final float LESSONNAME_PLACE_GAP = 24;
	public static final float LANDSCAPEMODE_LESSONNAME_PLACE_GAP = 5;
	public static final int LANDSCAPEMODE_LESSONNAME_MAX_LENGTH = 6;

	public static final float GAPHEIGHT = 28;
	
	private float markX = -1, markY = -1;
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
	private float viewWidth, viewHeight, calendarWidth, calendarHeight, cellWidth, cellHeight;
	private float leftBorder[] = new float[8];
	private float topBorder[] = new float[6];
	private float bottomBorder[] = new float[6];

	@Override
	protected void onDraw(Canvas _canvas) {
		canvas = _canvas;
		isLandscapeMode = (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE);
		paint.setAntiAlias(true);

        viewWidth = this.getMeasuredWidth();
        viewHeight = this.getMeasuredHeight();

		calendarWidth = viewWidth;
		cellWidth = calendarWidth / 7;
        
		left = 0;
		top = 0;
		String[] weekNames = context.getResources().getStringArray(R.array.week_name);
		paint.setTextSize(WEEKNAME_SIZE);
		paint.setColor(BLUE);
		float endYOfWeek = WEEKNAME_SIZE + WEEKNAME_MARGIN * 2 + 4;
		canvas.drawRect(0, 0, canvas.getWidth(), endYOfWeek, paint);
		
		paint.setColor(Color.WHITE);
		for (int i = 0; i < 7; i++) {
			left = cellWidth * i
					+ (cellWidth - paint.measureText(weekNames[i])) / 2;
			canvas.drawText(weekNames[i], left, top + paint.getTextSize()
					+ WEEKNAME_MARGIN, paint);
		}

		//开始画课表
        left = 0;
        top = endYOfWeek;
        
		calendarHeight = viewHeight - top;
		cellHeight = (calendarHeight - GAPHEIGHT * 2) / 5;
		
		for (int i = 0; i < 8; i++) {
			leftBorder[i] = cellWidth * i;
		}
		for (int i = 0; i < 6; i++) {
			topBorder[i] = top + cellHeight * i;
			if (i > 1) topBorder[i] += GAPHEIGHT;
			if (i > 3) topBorder[i] += GAPHEIGHT;
		}
		for (int i = 0; i < 6; i++) {
			bottomBorder[i] = top + cellHeight * (i + 1);
			if (i > 1) bottomBorder[i] += GAPHEIGHT;
			if (i > 3) bottomBorder[i] += GAPHEIGHT;
		}
		
		//画线
		drawLines();

		//画背景
		drawBackgrounds();
		
		//课程名和地点
		for(Lesson[] lessonsOfDay : lessons){
			if(lessonsOfDay != null){
				for(Lesson lesson:lessonsOfDay){
					if(lesson != null){
						if(lessonCanAppend(lesson,lessons) && timetable.nowIsAtLessonBreak(lesson.week, lesson.time)){
							drawLesson(lesson, true);
						}else if(lessonIsAppended(lesson,lessons) && timetable.nowIsAtLessonBreak(lesson.week, lesson.time - 1)){
							//四节课的课间且是后两节课，不用画了
						}else{
							drawLesson(lesson, timetable.isNowHavingLesson(lesson) == 0 ? true : false);
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
			canvas.drawLine(0, topBorder[i], calendarWidth, topBorder[i], linePaint);
		}
		canvas.drawLine(0, bottomBorder[1], calendarWidth, bottomBorder[1], linePaint);
		canvas.drawLine(0, bottomBorder[3], calendarWidth, bottomBorder[3], linePaint);

		//画竖线
		for (int i = 1; i < 7; i++) {
			canvas.drawLine(leftBorder[i], top, leftBorder[i], bottomBorder[1], linePaint);
		}
		for (int i = 1; i < 7; i++) {
			canvas.drawLine(leftBorder[i], topBorder[2], leftBorder[i], bottomBorder[3], linePaint);
		}
		for (int i = 1; i < 7; i++) {
			canvas.drawLine(leftBorder[i], topBorder[4], leftBorder[i], bottomBorder[4], linePaint);
		}
	}

	private void drawBackground(int week, int time, int mode, int append) {
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
			canvas.drawRect(leftBorder[week], topBorder[time], leftBorder[week + 1], topBorder[time + 1], paintbg);
			break;
		case 1:
			canvas.drawRect(leftBorder[week], topBorder[time], leftBorder[week + 1], topBorder[time + 2], paintbg);
			break;
		case -1:
			break;
		}
	}

	private float textLeft, textTop;
	private void drawLesson(Lesson lesson,boolean busytime) {
		if (isLandscapeMode) {
			drawLessonInLandscapeMode(lesson, busytime);
			return;
		}
		
		// 一般课程
		if(lesson == null) return;
		paint.setTextSize(LESSONNAME_SIZE);
		int week,time;
		int appendMode = lessonAppendMode(lesson,lessons);
		if(appendMode == -1){
			return;//后两节课不画
		}
		
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
			textTop = topBorder[time] + cellHeight / 2 - LESSONNAME_SIZE;
			break;
		case 1:
			mlesson = getLesson(week, time + 1);
			if(mlesson == null) return;
			if(timetable.isNowHavingLesson(mlesson) == 0) return;
			textTop = topBorder[time] + cellHeight - LESSONNAME_SIZE;
			break;
		case -1:
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
		paint.setTextSize(LESSONPLACE_SIZE);
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

		switch(appendMode){
		case 0:
			textTop = topBorder[time] + cellHeight / 2 + LESSONPLACE_SIZE + LESSONNAME_PLACE_GAP;
			break;
		case 1:
			textTop = topBorder[time] + cellHeight + LESSONPLACE_SIZE + LESSONNAME_PLACE_GAP;
			break;
		}

		if (place.length() <= 4) {
			textLeft = leftBorder[week] + (cellWidth - paint.measureText(place)) / 2;
			canvas.drawText(place, textLeft, textTop, paint);
		} else if (place.length() <= 8) {
			String roomNumber = place.substring(place.length() - 3,
					place.length());
			if (isInt(roomNumber)) {
				textLeft = leftBorder[week] + (cellWidth - paint.measureText(place.substring(0,
								place.length() - 3))) / 2;
				canvas.drawText(place.substring(0, place.length() - 3),
						textLeft, textTop, paint);
				textLeft = leftBorder[week] + (cellWidth - paint.measureText(roomNumber)) / 2;
				canvas.drawText(roomNumber, textLeft,
						textTop + LESSONPLACE_SIZE, paint);
			} else {
				textLeft = leftBorder[week] + (cellWidth - paint.measureText(place.substring(0, 4)))
						/ 2;
				canvas.drawText(place.substring(0, 4), textLeft, textTop, paint);
				textLeft = leftBorder[week] + (cellWidth - paint.measureText(place.substring(4)))
						/ 2;
				canvas.drawText(place.substring(4), textLeft,
						textTop + LESSONPLACE_SIZE, paint);
			}
		}
	}

	private void drawLessonInLandscapeMode(Lesson lesson,boolean busytime) {
		// 一般课程
		if(lesson == null) return;
		paint.setTextSize(LESSONNAME_SIZE);
		int week,time;
		int appendMode = lessonAppendMode(lesson,lessons);
		if(appendMode == -1){
			return;//后两节课不画
		}
		
		week = lesson.week;
		time = lesson.time;
		String place;
		LessonName name = (lesson.alias.contentEquals("")) ? new LessonName(lesson.name) : new LessonName(lesson.alias);
		String drawName = (name.length() > LANDSCAPEMODE_LESSONNAME_MAX_LENGTH) ? name.substring(0, LANDSCAPEMODE_LESSONNAME_MAX_LENGTH).toString() : name.toString();
		place = lesson.place;
		Lesson mlesson;
		switch(appendMode){
		case 0:
			textTop = topBorder[time] + cellHeight / 2;
			break;
		case 1:
			mlesson = getLesson(week, time + 1);
			if(mlesson == null) return;
			if(timetable.isNowHavingLesson(mlesson) == 0) return;
			textTop = topBorder[time] + cellHeight;
			break;
		case -1:
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

		textLeft = leftBorder[week] +  (cellWidth - paint.measureText(drawName)) / 2;
		canvas.drawText(drawName, textLeft, textTop, paint);

		// 画地点
		paint.setTextSize(LESSONPLACE_SIZE);
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

		switch(appendMode){
		case 0:
			textTop = topBorder[time] + cellHeight / 2 + LESSONPLACE_SIZE + LANDSCAPEMODE_LESSONNAME_PLACE_GAP;
			break;
		case 1:
			textTop = topBorder[time] + cellHeight + LESSONPLACE_SIZE + LANDSCAPEMODE_LESSONNAME_PLACE_GAP;
			break;
		}
		
		textLeft = leftBorder[week] + (cellWidth - paint.measureText(place)) / 2;
		canvas.drawText(place, textLeft, textTop, paint);

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
		if(lessonCanAppend(lesson, lessons)) return 1;
		if(lessonIsAppended(lesson, lessons)) return -1;
		return 0;
	}

	public void openLessonActivity() {
		int gridX = (int) (markX / cellWidth);
		int gridY = -1;
		for (int i = 0; i < 5; i++) {
			if (markY > topBorder[i] && markY < bottomBorder[i])
				gridY = i;
		}
		if (!Timetable.isValidWeekTime(gridX, gridY)) return;
		if (lessons[gridX][gridY] == null) return;
		markWeek = gridX;
		markTime = gridY;
		markLid = lessons[gridX][gridY].lid;
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
		markX = event.getX();
		markY = event.getY();
		return super.onTouchEvent(event);
	}

}
