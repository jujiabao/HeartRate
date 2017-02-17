package com.jujiabao.client2server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jujiabao.database.DBManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class SaveLog {
	public static final String PACKAGE_NAME = "com.jujiabao.heartrate";
	public static final String FILE = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME+ "/" + "log.txt";
	public static final String ID_FILE = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME+ "/" + "last_id.txt";

	
	private final static String TABLE_NAME = "heart_rate_main";
	private final static String TABLE_NAME_LOGIN = "heart_rate_login";
	private static SQLiteDatabase database;
	
	private static String getUserId(String user_name){
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
	
	//得到心率数据，只是hash值，不得到平均值
	private static HashMap<String, Integer> getAllRate(String name){
		HashMap<String, Integer> map = null;
		try {
			map = new LinkedHashMap<String, Integer>();
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			Cursor cur = database.rawQuery("SELECT * FROM " + TABLE_NAME
					+ " where user_id = '" + getUserId(name)
					+ "' and id >= "+CheckLog.readLastId()+" ORDER BY create_time ASC", null);
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
					return map;
				}
			}
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void SaveLog(String name){
		HashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
		map = getAllRate(name);
		PrintWriter pw = null;
		try {
			FileOutputStream fos = new FileOutputStream(new File(FILE));//不要追加写进去
			OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
			pw = new PrintWriter(osw);
			for (Entry<String, Integer> e : map.entrySet()) {
				pw.println("用户："+name+"，在"+e.getKey()+"测得心率："+e.getValue()+"次/min");
			}
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (pw != null) {
				System.out.println("写入log文件成功！");
				pw.close();
			}
		}
	}
	//写入id文件
	public static void SaveLastId(String name){
		int id = CheckLog.checkId(name);
		PrintWriter pw = null;
		try {
			FileOutputStream fos = new FileOutputStream(new File(ID_FILE));//不要追加写进去
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			pw = new PrintWriter(osw);
			pw.println(id);
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (pw != null) {
				System.out.println("写入id文件成功！");
				pw.close();
			}
		}
	}
}
