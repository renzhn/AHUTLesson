package com.ahutlesson.android.model;

public class LessonmateSimilarity {
	
	public String xh;
	public double similarity;
	
	public String getSimilarity() {
		return Math.round(similarity * 100) + "%";
	}
	
}
