package com.jujiabao.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.jujiabao.database.DBManager;
import com.jujiabao.heartrate.R.raw;

import android.R.integer;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库一系列的操作
 * @author Hello.Ju
 *
 */
public class DBUtil {
	//定义数据库
	private final static String TABLE_NAME = "heart_rate_main";
	private final static String TABLE_NAME_LOGIN = "heart_rate_login";
	private static SQLiteDatabase database;
	
	private static List<String> list = new ArrayList<String>();
	//哈希集合，存放--时间：心率值
	private static HashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
	
	private static HashMap<String, Integer> countMap = new LinkedHashMap<String, Integer>();
	
	private static List<String> minOrMaxList = new ArrayList<String>();
	
	public static String getUserId(String user_name){
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			String user_id;
			
			Cursor cur = database.rawQuery("SELECT user_id FROM " + TABLE_NAME_LOGIN
					+ " where user_name='"+user_name+"'", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						user_id = cur.getString(cur.getColumnIndex("user_id"));
						System.out.println("user_id=" + user_id);
						return user_id;
					} while (cur.moveToNext());
				}
			}
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//获取详细资料
	public static List<String> getRateData(String name,String offsetDate){
		try {
			//使用一次list时，清空一次
			if (list.isEmpty() == false) {
				list.clear();
			}
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			int count = 0;
			Cursor cur = database.rawQuery("SELECT * FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(name)
					+ "' and create_time between '" + offsetDate+" 00:00:00" + "' and '"
					+ TimeUtil.getNowDate()+" 23:59:59"  + "' ORDER BY create_time ASC", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String date = cur.getString(cur.getColumnIndex("create_time"));
						String rate = cur.getString(cur.getColumnIndex("rate"));
						String is_average = cur.getString(cur.getColumnIndex("is_average"));
						list.add((is_average.equals("0"))
								?((++count)+"、"+date+"，\t"+rate+"次/min")
										:((++count)+"、"+date+"，\t"+rate+"次/min\t平均值"));
					} while (cur.moveToNext());
					// database.close();
					Log.i("jujiabao", "在DBtil方法中的历史记录获取数据成功！");
					return list;
				}
			}
//			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//得到心率数据，只是hash值，不得到平均值
	public static HashMap<String, Integer> getAllRate(String name){
		if (map.isEmpty() == false) {
			map.clear();
		}
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			Cursor cur = database.rawQuery("SELECT * FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(name)
					+ "' ORDER BY create_time ASC", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String date = cur.getString(cur.getColumnIndex("create_time"));
						String rateStr = cur.getString(cur.getColumnIndex("rate"));
						double rateDo = Double.parseDouble(rateStr);
						int rate = (int)rateDo;
						String is_average = cur.getString(cur.getColumnIndex("is_average"));
						if (is_average.equals("0")) {
							map.put(date, rate);
						}
					} while (cur.moveToNext());
					Log.i("jujiabao", "在DBtil方法中的全部心率获取数据成功！");
					return map;
				}
			}
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	//得到最大值或者最小值得数据,不包括平均值
	public static List<String> getMinOrMax(String name,String minOrMaxName){
		if (minOrMaxList.isEmpty() == false) {
			minOrMaxList.clear();
		}
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			Cursor cur = database.rawQuery(
					"SELECT max(" + minOrMaxName + ") AS MAX,min("
							+ minOrMaxName + ") AS MIN FROM " + TABLE_NAME
							+ " where user_id = '" + getUserId(name) + "' and is_average='0'",
					null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String max = cur.getString(cur.getColumnIndex("MAX"));
						String min = cur.getString(cur.getColumnIndex("MIN"));
						minOrMaxList.add(min);
						minOrMaxList.add(max);
					} while (cur.moveToNext());
					Log.i("jujiabao", "在DBtil方法中的最大最小值获取数据成功！");
					return minOrMaxList;
				}
			}
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return minOrMaxList;
	}
	
	//得到某组心率数据
	public static List<String> getKeyRate(String name,Integer rate){
		if (list.isEmpty() == false) {
			list.clear();
		}
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			Cursor cur = database.rawQuery("SELECT * FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(name)
					+ "' and rate="+rate, null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String date = cur.getString(cur.getColumnIndex("create_time"));
						String rateStr = cur.getString(cur.getColumnIndex("rate"));
//						int rate1 = Integer.parseInt(rateStr);
						String is_average = cur.getString(cur.getColumnIndex("is_average"));
						if (is_average.equals("0")) {
							list.add(date);
							list.add(rateStr);
						}
					} while (cur.moveToNext());
					Log.i("jujiabao", "在DBtil方法中的某组数据获取成功！");
					return list;
				}
			}
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	//按标签排序心率
	public static HashMap<String, Integer> classifyRate(String name){
		if (countMap.isEmpty() == false) {
			countMap.clear();
		}
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			//计数器,分别对应0~59、60~70
			int c1=0,c2=0,c3=0,c4=0,c5=0,c6=0;
			
			Cursor cur = database.rawQuery("SELECT * FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(name)
					+ "' and is_average='0'", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String rateStr = cur.getString(cur.getColumnIndex("rate"));
						int rate = Integer.parseInt(rateStr);
						if (rate < 60) {
							c1++;
						}else if (rate < 70) {
							c2++;
						}else if (rate < 80) {
							c3++;
						}else if (rate < 90) {
							c4++;
						}else if (rate < 100) {
							c5++;
						}else {
							c6++;
						}
					} while (cur.moveToNext());
					System.out.println(c1+","+c2+","+c3+","+c4+","+c5+","+c6);
					countMap.put("c1", c1);
					countMap.put("c2", c2);
					countMap.put("c3", c3);
					countMap.put("c4", c4);
					countMap.put("c5", c5);
					countMap.put("c6", c6);
					System.out.println("countMap="+countMap);
					Log.i("jujiabao", "在DBtil方法中的按标签排序心率值获取成功！");
					return countMap;
				}
			}
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return countMap;
	}
	
	//得到总共心率数
	public static int getRateNum(String name){
		int count = 0;
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			Cursor cur = database.rawQuery("SELECT * FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(name)
					+ "' and is_average='0'", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String rateStr = cur.getString(cur.getColumnIndex("rate"));
						int rate = Integer.parseInt(rateStr);
						count++;
					} while (cur.moveToNext());
					Log.i("jujiabao", "在DBtil方法中的心率数量获取成功！");
					return count;
				}
			}
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	//得到总共心率数
	public static List<String> getErrorRate(String name){
		int count = 0;//记录总数据
		int c1 = 0;//操作100
		int c2 = 0;//低于60
		double average = 0.0;
		int sum = 0;
		if (list.isEmpty() == false) {
			list.clear();
		}
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			Cursor cur = database.rawQuery("SELECT * FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(name)
					+ "' and is_average='0'", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String rateStr = cur.getString(cur.getColumnIndex("rate"));
						int rate = Integer.parseInt(rateStr);
						sum += rate;
						count++;
						if (rate < 60) {
							c2++;
						}else if (rate > 100) {
							c1++;
						}
					} while (cur.moveToNext());
					Log.i("jujiabao", "在DBtil方法中的获取详细信息成功！");
					list.add(c2+"");//低于60次
					list.add(c1+"");//高于100次
					average = sum/count;
					list.add(average+"");
					return list;
				}
			}
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	//删除offsetDate天前数据
	public static void deleteRateData(String name,String offsetDate){
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			database.execSQL("DELETE FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(name)
					+ "' and create_time < '"+offsetDate+" 00:00:00'");
			
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//删除当天数据
	public static void deleteRateData(String name){
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			database.execSQL("DELETE FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(name)
					+ "' and create_time between '"+TimeUtil.getNowDate()+" 00:00:00' and '"+TimeUtil.getNowDate()+" 23:59:59'");
			
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//获得要导入Excel表中的数据，获取heart_rate_main中的所有字段
	public static List<RateDetail> getAllRateExcel(String nowAccount) {
		List<RateDetail> list = new ArrayList<RateDetail>();
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			Cursor cur = database.rawQuery("SELECT * FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(nowAccount)
					+ "' ORDER BY create_time ASC", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String user_id = cur.getString(cur.getColumnIndex("user_id"));
						String name = cur.getString(cur.getColumnIndex("name"));
						String create_time = cur.getString(cur.getColumnIndex("create_time"));
						String rate = cur.getString(cur.getColumnIndex("rate"));
						String is_average = cur.getString(cur.getColumnIndex("is_average"));
						RateDetail rateDetail = new RateDetail(user_id, name, create_time, rate, is_average);
						list.add(rateDetail);
					} while (cur.moveToNext());
					Log.i("jujiabao", "在DBtil方法中的获取要导入Excel的数据成功！");
					return list;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
		return list;
	}
}
