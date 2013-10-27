package com.ahutlesson.android.ui.gridview;

public class LessonName {
	//used in lessonName drawing
	
	private String name;
	
	public LessonName(String name0) {
		name = name0;
	}
	
	public String substring(int start, int end) {
		float count = 0;
		String ret = "";
		for(int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if(count >= end) break;
			if(count >= start) {
				ret += c;
			}
			count += ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '.') ?  0.5 : 1;
		}
		return ret;
	}

	public String substring(int start) {
		float count = 0;
		int i;
		for(i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			count += ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '.') ?  0.5 : 1;
			if(count >= start) break;
		}
		return name.substring(i + 1);
	}
	
	public float length() {
		float count = 0;
		for(int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			count += ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '.') ?  0.5 : 1;
		}
		return count;
	}
	
	public String toString() {
		return name;
	}
	
}
