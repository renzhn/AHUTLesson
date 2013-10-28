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

	private float weekNameSize;
	private float weekNameMargin;
	private float lessonNameSize;
	private float lessonNameMargin;
	private float lessonPlaceSize;
	private float lessonNamePlaceGap;
	private float timePartitionGap;
	
	private int lessonNameMaxLines = 3; 
	private int lessonNameMaxLength = 2;// 每行
	private int lessonPlaceMaxLength = 4;
	private int lessonPlaceMaxLines = 2; 
	
	private float markX = -1, markY = -1;
	private int markWeek = -1, markTime = -1, markLid = -1;
	
	public static final int BLUE = Color.parseColor("#3F92D2");
	public static final int RED = Color.parseColor("#B22222");
	public static final int BLACK = Color.parseColor("#333333");
	
	//if lessons is null, load local
	public GridView(Context _context, Lesson[][] _lessons) {
		super(_context);
		context = _context;
		lessons = _lessons;
		if (lessons == null) {
			isLocal = true;
			lessons = LessonManager.getInstance(context).getLessons();
		}
		timetable = Timetable.getInstance(context);
	}

	private Paint paint = new Paint();
	private Canvas canvas;
	private float top, weekNameHeight;
	private float viewWidth, viewHeight, calendarWidth, calendarHeight, cellWidth, cellHeight;
	private float leftBorder[] = new float[8];
	private float topBorder[] = new float[6];
	private float bottomBorder[] = new float[6];
	private Paint weekNamePaint, linePaint;

	@Override
	protected void onDraw(Canvas _canvas) {
		canvas = _canvas;
		paint.setAntiAlias(true);
		
		weekNameSize = context.getResources().getDimension(R.dimen.weekname_size);
		weekNameMargin = context.getResources().getDimension(R.dimen.weekname_margin);
		lessonNameSize = context.getResources().getDimension(R.dimen.lessonname_size);
		lessonPlaceSize = context.getResources().getDimension(R.dimen.lessonplace_size);
		
        viewWidth = this.getMeasuredWidth();
        viewHeight = this.getMeasuredHeight();

		calendarWidth = viewWidth;
		cellWidth = calendarWidth / 7;
		lessonNameMargin = cellWidth / 15;
		weekNameHeight = weekNameSize + weekNameMargin * 2 + 5;

        top = weekNameHeight;
        
		calendarHeight = viewHeight - weekNameHeight;
		timePartitionGap = calendarHeight / 40;
		cellHeight = (calendarHeight - timePartitionGap * 2) / 5;
		lessonNamePlaceGap = cellHeight / 20;
		
		for (int i = 0; i < 8; i++) {
			leftBorder[i] = cellWidth * i;
		}
		for (int i = 0; i < 6; i++) {
			topBorder[i] = top + cellHeight * i;
			if (i > 1) topBorder[i] += timePartitionGap;
			if (i > 3) topBorder[i] += timePartitionGap;
		}
		for (int i = 0; i < 6; i++) {
			bottomBorder[i] = top + cellHeight * (i + 1);
			if (i > 1) bottomBorder[i] += timePartitionGap;
			if (i > 3) bottomBorder[i] += timePartitionGap;
		}

		weekNamePaint = new Paint();
		weekNamePaint.setTextSize(weekNameSize);
		weekNamePaint.setColor(BLUE);
		
		linePaint = new Paint();
		linePaint.setARGB(80, 0, 0, 0);
		linePaint.setStyle(Style.STROKE);
		linePaint.setPathEffect(new DashPathEffect(new float[] {5,10}, 0));
		
		//画周几
		paint.setColor(Color.WHITE);
		paint.setTextSize(weekNameSize);
		drawWeekNames();

		//画线
		drawLines();
		
		//课程名和地点
		lessonNameMaxLength = (int) ((cellWidth - lessonNameMargin) / (float) lessonNameSize);
		lessonPlaceMaxLength = (int) (cellWidth / (float) lessonPlaceSize);
		lessonNameMaxLines = (int) ((cellHeight - lessonPlaceMaxLength * 2 - lessonNamePlaceGap) / lessonNameSize);
		for (Lesson[] lessonsOfDay : lessons) {
			if (lessonsOfDay != null) {
				for (Lesson lesson:lessonsOfDay) {
					if (lesson != null) {
						drawLesson(lesson);
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

	private void drawWeekNames() {
		String[] weekNames = context.getResources().getStringArray(R.array.week_name);
		canvas.drawRect(0, 0, canvas.getWidth(), weekNameHeight, weekNamePaint);
		
		float weekNameXOffset = (cellWidth - paint.measureText(weekNames[0])) / 2; //都是两个字
		float weekNameYOffset = weekNameSize + weekNameMargin;
		for (int i = 0; i < 7; i++) {
			canvas.drawText(weekNames[i], leftBorder[i] + weekNameXOffset, weekNameYOffset, paint);
		}
	}

	private void drawLines() {
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

	private float textLeft, textTop;
	private void drawLesson(Lesson lesson) {
		// 一般课程
		if(lesson == null) return;
		paint.setTextSize(lessonNameSize);
		int week,time;
		int appendMode = lessonAppendMode(lesson, lessons);
		if(appendMode == -1){
			return;//后两节课不画
		}
		
		week = lesson.week;
		time = lesson.time;
		String place;
		LessonName name = (lesson.alias.contentEquals("")) ? new LessonName(lesson.name) : new LessonName(lesson.alias);
		place = lesson.place;
		float cellHeight = this.cellHeight;
		int lessonNameMaxLines = this.lessonNameMaxLines;

		if (appendMode == 1) {
			Lesson nextlesson = getLesson(week, time + 1);
			if(nextlesson == null) return;
			if(timetable.isNowHavingLesson(nextlesson) == 0) return;
			cellHeight *= 2;
			lessonNameMaxLines = (int) ((cellHeight - lessonPlaceMaxLength * 2 - lessonNamePlaceGap) / lessonNameSize);
			Paint bgPaint = new Paint();
			bgPaint.setColor(Color.WHITE);
			canvas.drawLine(leftBorder[week], bottomBorder[time], leftBorder[week + 1], bottomBorder[time], bgPaint);
		}
		
		float length = name.length();
		int lines = (int) ((length - 1) / (float) lessonNameMaxLength) + 1;
		if (lines > lessonNameMaxLines) lines = lessonNameMaxLines;
		int lessonNameMaxLength = this.lessonNameMaxLength;
		if (lessonNameMaxLength == 3 && length >= 4 && length <= 5) {
			lines = 2;
			lessonNameMaxLength = 2;
		}

		int placeLines = 2;
		boolean isRoomNumber = false;
		if (place.length() <= lessonPlaceMaxLength) {
			placeLines = 1;
		} else if (place.length() <= lessonPlaceMaxLength * lessonPlaceMaxLines) {
			if (isInt(place.substring(place.length() - 3, place.length()))) {
				isRoomNumber = true;
				placeLines = 2;

			} else {
				placeLines = lessonPlaceMaxLines;
			}
		}
		
		//画课程名
		textTop = topBorder[time] + (cellHeight - lessonPlaceSize * placeLines - lessonNamePlaceGap + lessonNameSize * lines) / 2; //课程名最后一行的底部Y
		
		if (!lesson.isInRange(context)) {
			paint.setARGB(30, 0, 0, 0);
		} else {
			paint.setColor(BLACK);
		}
		
		String text;
		for (int i = 0; i < lines; i++) {
			if (name.length() <= lessonNameMaxLength) {
				text = name.toString();
			} else {
				text = name.substring(0, lessonNameMaxLength);
				name = new LessonName(name.substring(lessonNameMaxLength));
			}
			textLeft = leftBorder[week] + (cellWidth - paint.measureText(text)) / 2;
			canvas.drawText(text, textLeft, textTop - lessonNameSize * (lines - i - 1), paint);
		}


		// 画地点
		paint.setTextSize(lessonPlaceSize);
		if(!lesson.isInRange(context)){
			paint.setARGB(30, 0, 0, 0);
		}else{
			paint.setColor(Color.parseColor("#B22222"));
		}

		textTop += lessonNamePlaceGap + lessonPlaceSize;
		
		if (place.length() <= lessonPlaceMaxLength) {
			textLeft = leftBorder[week] + (cellWidth - paint.measureText(place)) / 2;
			canvas.drawText(place, textLeft, textTop, paint);
		} else if (place.length() <= lessonPlaceMaxLength * lessonPlaceMaxLines) {
			if (isRoomNumber) {
				String building = place.substring(0, place.length() - 3);
				String roomNumber = place.substring(place.length() - 3, place.length());
				if (building.length() > lessonPlaceMaxLength)
					building = building.substring(0, lessonPlaceMaxLength);
				textLeft = leftBorder[week] + (cellWidth - paint.measureText(building)) / 2;
				canvas.drawText(building, textLeft, textTop, paint);
				textLeft = leftBorder[week] + (cellWidth - paint.measureText(roomNumber)) / 2;
				canvas.drawText(roomNumber, textLeft, textTop + lessonPlaceSize, paint);
			} else {
				text = place.substring(0, lessonPlaceMaxLength);
				textLeft = leftBorder[week] + (cellWidth - paint.measureText(text)) / 2;
				canvas.drawText(text, textLeft, textTop, paint);
				text = place.substring(lessonPlaceMaxLength);
				if (text.length() > lessonPlaceMaxLength)
					text = text.substring(0, lessonPlaceMaxLength);
				textLeft = leftBorder[week] + (cellWidth - paint.measureText(text)) / 2;
				canvas.drawText(text, textLeft, textTop + lessonPlaceSize, paint);
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
		return (!Timetable.isValidWeekTime(week, time))? null : lessons[week][time];
	}
	

	public boolean lessonCanAppend(Lesson lesson,Lesson[][] lessons) {
		// 后两节有课
		if (lesson.time == 0 || lesson.time == 2) {
			Lesson appendLesson =  lessons[lesson.week][lesson.time + 1];
			if (appendLesson != null) {
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
