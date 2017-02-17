package com.jujiabao.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 获取时间的工具类
 * @author Hello.Ju
 *
 */
public class TimeUtil {
	//获取当前系统时间，包括日期
	public static String getNowTime(){
		String dateformate = "yyyy-MM-dd HH:mm:ss";
		//获取当前系统时间毫秒数
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(dateformate);
        String now_time = sdf.format(date);
		return now_time;
	}
	
	//获取当前日期
	public static String getNowDate(){
		String dateformate = "yyyy-MM-dd";
		//获取当前系统时间毫秒数
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(dateformate);
        String now_date = sdf.format(date);
		return now_date;
	}
	
	//获取的只是时间
	public static String getJustNowTime(){
		String dateformate = "HH:mm:ss";
		//获取当前系统时间毫秒数
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(dateformate);
        String just_now_time = sdf.format(date);
		return just_now_time;
	}
	
	//获取几天前的日期
	public static String getBeforeDate(int offsetDays){
		String date_formate = "yyyy-MM-dd";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -offsetDays);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(date_formate);
		String beforeDays = sdf.format(date);
		System.out.println("beforeDays="+beforeDays);
		return beforeDays;
	}
	
	//获取几天前的日期
	public static String getAfterDate(int offsetDays){
		String date_formate = "yyyy-MM-dd";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, offsetDays);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(date_formate);
		String afterDays = sdf.format(date);
		System.out.println("afterDays="+afterDays);
		return afterDays;
	}
}
