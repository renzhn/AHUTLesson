package com.ahutpt.lesson.utils;

public class ValidateHelper {
	
	public static boolean isXH(String str) {
		if(str.length() != 9) return false;
		for(int i = 0;i < str.length(); i++){
			if(!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
}
