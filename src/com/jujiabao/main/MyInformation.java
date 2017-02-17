package com.jujiabao.main;

import java.util.ArrayList;
import java.util.List;

import com.jujiabao.database.DBManager;
import com.jujiabao.heartrate.R;
import com.jujiabao.util.StorageFileUtil;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MyInformation extends Activity {
	
	private String nowUser;
	
	private TextView name;
	private TextView age;
	private TextView gender;
	private TextView height;
	private TextView weight;
	private TextView create_time;
	
	private final static String TABLE_NAME_DETAIL = "heart_rate_detail";
	private final static String TABLE_NAME_MAIN = "heart_rate_login";
	private static SQLiteDatabase database;
	private List<String> userInfoList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_my_information);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("我的资料");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(action);
		
		//获取所有的TextView控件
		name = (TextView) findViewById(R.id.my_name);
		age = (TextView) findViewById(R.id.my_age);
		gender = (TextView) findViewById(R.id.my_gender);
		weight = (TextView) findViewById(R.id.my_weight);
		height = (TextView) findViewById(R.id.my_height);
		create_time = (TextView) findViewById(R.id.my_create_time);
		
		
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		
		getUserInfo();
		
		//创建日志文件
		StorageFileUtil.createLogFile("log.txt");
	}
	
	//按键事件汇总
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_left_two_title_btn_left:
				database.close();
				//创建日志文件
				StorageFileUtil.createLogFile("log.txt");
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				break;

			default:
				break;
			}
		}
	};
	
	//查找本人信息
	private void getUserInfo(){
		//获取上一界面的参数
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		nowUser = bundle.getString("nowAcount");
		Cursor cur = database.rawQuery("SELECT * from " + TABLE_NAME_DETAIL
				+ " where user_id IN(SELECT user_id from " + TABLE_NAME_MAIN
				+ " where user_name='" + nowUser
				+ "' order by create_time DESC limit 1)", null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    String name = cur.getString(cur.getColumnIndex("user_true_name"));
                    String age = cur.getString(cur.getColumnIndex("user_age"));
                    String gender = cur.getString(cur.getColumnIndex("user_gender"));
                    String height = cur.getString(cur.getColumnIndex("user_height"));
                    String weight = cur.getString(cur.getColumnIndex("user_weight"));
                    String create_time = cur.getString(cur.getColumnIndex("create_time"));
                    userInfoList.add(name);
                    userInfoList.add(age);
                    userInfoList.add(gender);
                    userInfoList.add(height);
                    userInfoList.add(weight);
                    userInfoList.add(create_time);
                    System.out.println(userInfoList);
                } while (cur.moveToNext());
//                database.close();
                if (userInfoList.isEmpty() == false) {
					showUerInfo();
				}
            }
        } else {
        	Log.i("jujiabao", "无最新数据");
        }
	}
	
	//显示详细信息
	private void showUerInfo(){
		Log.i("jujiabao", "userInfoList="+userInfoList);
		if (userInfoList.isEmpty() == true) {
			return;
		}
		name.setText("姓名："+userInfoList.get(0));
		age.setText("年龄："+userInfoList.get(1));
		gender.setText("性别："+userInfoList.get(2));
		height.setText("身高："+userInfoList.get(3)+"cm");
		weight.setText("体重："+userInfoList.get(4)+"KG");
		create_time.setText("创建时间："+userInfoList.get(5));
		
		emptyList(1);//清空userInfoList
	}
	
	//清空容器
	private void emptyList(int listId){
		switch (listId) {
		case 1:
			if (userInfoList.isEmpty() == false) {
				userInfoList.clear();
			}
			break;

		default:
			break;
		}
	}
	//重写返回键方法，为其加上返回动画
	@Override
	public boolean onKeyDown(int keyCode,
			KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			database.close();
			//创建日志文件
			StorageFileUtil.createLogFile("log.txt");
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right); 
		}
		return super.onKeyDown(keyCode,event);
	}
}
