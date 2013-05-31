package com.ahutlesson.android.model;

public class TimetableSetting {
	public int year, month, day;
	public boolean seasonWinter = false;
	public void setSeason(int seasonIsWinter) {
		if(seasonIsWinter == 1)
			seasonWinter = true;
	}
	
	public String getSeason() {
		return seasonWinter ? "1" : "0";
	}
}
