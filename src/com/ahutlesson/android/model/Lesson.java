package com.ahutlesson.android.model;

public class Lesson {

	public String name, place, teacher;
	public int lid, week, time, startweek, endweek;

	public Lesson(int lid0, String name0, String place0,
			String teacher0, int startweek0, int endweek0, int week0, int time0) {
		lid = lid0;
		name = name0;
		place = place0;
		teacher = teacher0;
		startweek = startweek0;
		endweek = endweek0;
		week = week0;
		time = time0;
	}

	public boolean isBeforeEnd(int numOfWeek) {
		return numOfWeek <= endweek;
	}

	public boolean isInRange(int numOfWeek) {
		return numOfWeek <= endweek;
	}

	public String getTitle() {
		return name + "(" + teacher + ")";
	}

	public String getDuration() {
		return "µÚ" + startweek + "~" + endweek + "ÖÜ";
	}
	
	public boolean atPosition(GridPosition grid) {
		return (week == grid.week && time == grid.time);
	}

}
