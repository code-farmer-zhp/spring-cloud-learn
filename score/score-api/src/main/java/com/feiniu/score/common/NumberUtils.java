package com.feiniu.score.common;

public class NumberUtils {
	
	/*
	 * 获取value的值， 如果为空去默认值
	 */
	public static int getIntValue(Integer value, int defaluteValue) {
		if(value == null){
			return defaluteValue;
		}
		return value;
	}
	
}
