package com.jujiabao.welcome;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.jujiabao.database.DBManager;
import com.jujiabao.heartrate.R;
import com.jujiabao.login.MainLogin;
import com.jujiabao.util.StorageFileUtil;
import com.jujiabao.util.TimeUtil;

/**
 * 欢迎界面
 * @author Hello.Ju
 *
 */
public class Welcome extends Activity {

	//加载数据库
	private DBManager mDbManager;
	
	private static final int PAUSE_TIME_MS = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		//记录登入软件时间
		System.out.println("于"+TimeUtil.getNowTime()+"登入本软件。");
		
		//完整的加载一次数据库，复制到手机中
        mDbManager = new DBManager(this);
        mDbManager.openDatabase();
        
        //建立文件夹，复制文件
		StorageFileUtil.copyFile2Storage(this, "logic_content.xml",
				getResources().openRawResource(R.raw.logic_content));
        
		final Intent intent = new Intent();
		intent.setClass(Welcome.this, MainLogin.class);
		
		Timer timerIntent = new Timer();
		TimerTask timerTaskIntent = new TimerTask() {
			
			@Override
			public void run() {
				mDbManager.closeDatabase();
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				//创建日志文件
				StorageFileUtil.createLogFile("log.txt");
				finish();
			}
		};
		timerIntent.schedule(timerTaskIntent, PAUSE_TIME_MS);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mDbManager.closeDatabase();
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}
}
