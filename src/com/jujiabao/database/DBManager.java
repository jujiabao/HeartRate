package com.jujiabao.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jujiabao.heartrate.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
/**
 * 先将程序中的数据库拷贝至手机内存中
 * @author Hello.Ju
 *
 */
public class DBManager {

	private final int BUFFER_SIZE = 1024;
	public static final String DB_NAME = "heartrate.db"; // 保存的数据库文件名
	public static final String PACKAGE_NAME = "com.jujiabao.heartrate";
	public static final String DB_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME; // 在手机里存放数据库的位置
	
	public static final String FILE_NAME = "config.xml";
	public static final String PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME+ "/" + FILE_NAME; // 在手机里存放的位置

	private SQLiteDatabase database;
	private Context context;

	public DBManager(Context context) {
		this.context = context;
	}

	public void openDatabase() {
		this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
	}

	private SQLiteDatabase openDatabase(String dbfile) {
		try {
			try {
				copyXML();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!(new File(dbfile).exists())) {// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
				InputStream is = this.context.getResources().openRawResource(
						R.raw.heartrate); // 将数据库添加至流中
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			} else {
				Log.i("jujiabao", "数据库在手机内存中已存在，不需要重新拷贝！");
			}
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,
					null);
			return db;
		} catch (FileNotFoundException e) {
			Log.e("Database", "文件没有找到");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("Database", "IO Exception");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将config.xml拷贝至手机目录中去
	 * @throws Exception 
	 */
	public void copyXML() throws Exception{
		try {
			if (!(new File(PATH).exists())) {
				InputStream is = this.context.getResources().openRawResource(
						R.raw.config); // 将数据库添加至流中
				FileOutputStream fos = new FileOutputStream(PATH);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}else {
					Log.i("jujiabao", "xml文件在手机内存中已存在，不需要重新拷贝！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void closeDatabase() {
		this.database.close();
	}
}
