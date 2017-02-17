package com.jujiabao.client2server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.jujiabao.database.DBManager;
import com.jujiabao.util.DBUtil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class CheckLog {
	private final static String TABLE_NAME = "heart_rate_main";
	private static SQLiteDatabase database;
	
	public static final String PACKAGE_NAME = "com.jujiabao.heartrate";
	public static final String ID_FILE = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME+ "/" + "last_id.txt";
	
	public static int checkId(String name){
		int id = 0;
		try {
			//开启数据库
			database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
			
			Cursor cur = database.rawQuery("SELECT max(id) as MAX FROM " + TABLE_NAME
					+ " where user_id='"+DBUtil.getUserId(name)+"'", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String idStr = cur.getString(cur.getColumnIndex("MAX"));
						id = Integer.parseInt(idStr);
						System.out.println("ID=" + id);
						return id;
					} while (cur.moveToNext());
				}
			}
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	//读取文件的id
	public static int readLastId() throws Exception{
		BufferedReader br = null;
		int id = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					ID_FILE), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String str = line;
				id = Integer.parseInt(str);
			}
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			if (br != null) {
				br.close();
			}
		}
	}
	
}
