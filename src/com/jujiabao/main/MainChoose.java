package com.jujiabao.main;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.jujiabao.client2server.CheckLog;
import com.jujiabao.client2server.Client2Server;
import com.jujiabao.client2server.LogUtil;
import com.jujiabao.client2server.SaveLog;
import com.jujiabao.database.DBManager;
import com.jujiabao.heartrate.BluetoothChat;
import com.jujiabao.heartrate.R;
import com.jujiabao.util.KillActivity;
import com.jujiabao.util.StorageFileUtil;

import android.app.Activity;
import android.app.Fragment.SavedState;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainChoose extends Activity {

	private Button btn_start_rate;
	private Button btn_personal_info;
	private Button btn_history;
	private Button btn_chart;
	private Button btn_free;
	private Button btn_setting;
	private TextView now_acount_label;
	
	private final static String TABLE_NAME_LOGIN = "heart_rate_login";
	private final static String TABLE_NAME_DETAIL = "heart_rate_detail";
	private SQLiteDatabase database;
	
	private HashMap<String, String> acount_pwdMap = new LinkedHashMap<String, String>();
	private String nowAcount;
	private String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main_choose);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title1);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title1_name);
		title1_name.setText("功能选择");
		
		now_acount_label = (TextView) findViewById(R.id.now_acount);
		
		KillActivity.getInstance().addActivity(this);
		
		Button[] btn = { btn_start_rate, btn_personal_info, btn_history,
				btn_chart, btn_free, btn_setting };
		btn[0] = (Button) findViewById(R.id.start_rate);
		btn[1] = (Button) findViewById(R.id.personal_info);
		btn[2] = (Button) findViewById(R.id.history);
		btn[3] = (Button) findViewById(R.id.chart);
		btn[4] = (Button) findViewById(R.id.free);
		btn[5] = (Button) findViewById(R.id.setting);
		//遍历所有按钮，并为其添加事件
		for (Button button : btn) {
			button.setOnClickListener(action);
		}
		
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
		
		//获取当前账户信息，acount_pwdMap保存当前的账户，密码值。
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		nowAcount = bundle.getString("now_name");
		Log.i("jujiabao", "nowAcount="+nowAcount);
		
//		SaveLog.SaveLog(nowAcount);
//		int last_id = CheckLog.checkId(nowAcount);
//		int read_id = 0;
//		try {
//			read_id = CheckLog.readLastId();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (read_id != last_id) {
//			//登入后发送数据给服务器
//			Thread thread = new Thread(){
//	
//				@Override
//				public void run() {
//					System.out.println("开启服务器成功");
//					Client2Server client2Server = new Client2Server();
//					client2Server.sendLog();
//					SaveLog.SaveLastId(nowAcount);//保存id
//				}
//	        };
//	        thread.start();
//		}else {
//			System.out.println("经过检查，id一样，不需要开启服务器");
//		}
		
		if (!bundle.getString("now_name").equals("")) {
			this.name = getTrueName();
			now_acount_label.setText("尊敬的"+this.name+"，欢迎您！");
			if (acount_pwdMap.isEmpty() == true) {
				acount_pwdMap.put(bundle.getString("now_name"), bundle.getString("now_pwd"));
			}
			Log.i("jujiabao", "主界面--当前用户名登录正常");
//			Log.i("jujiabao", "主界面--"+acount_pwdMap);
		}else {
			Log.i("jujiabao", "主界面--当前用户名登录不正常，上一界面未成功传值");
			return;
		}
	}
	
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//创建日志文件
			StorageFileUtil.createLogFile("log.txt");
			switch (v.getId()) {
			case R.id.start_rate:
//				Toast.makeText(MainChoose.this, "心率测量", Toast.LENGTH_SHORT).show();
				Intent intent_rate = new Intent();
				intent_rate.setClass(MainChoose.this, BluetoothChat.class);
				Bundle bundle = new Bundle();
				bundle.putCharSequence("nowAcount", nowAcount);
				intent_rate.putExtras(bundle);
				startActivity(intent_rate);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				break;
			case R.id.personal_info:
//				Toast.makeText(MainChoose.this, "个人资料", Toast.LENGTH_SHORT).show();
				Intent intent_info = new Intent();
				intent_info.setClass(MainChoose.this, MyInformation.class);
				Bundle bundle_info = new Bundle();
				bundle_info.putCharSequence("nowAcount", nowAcount);
				intent_info.putExtras(bundle_info);
				startActivity(intent_info);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				break;
			case R.id.history:
//				Toast.makeText(MainChoose.this, "历史记录", Toast.LENGTH_SHORT).show();
				Intent intent_his = new Intent();
				intent_his.setClass(MainChoose.this, History.class);
				Bundle bundle_history = new Bundle();
				bundle_history.putCharSequence("nowAcount", nowAcount);
				intent_his.putExtras(bundle_history);
				startActivity(intent_his);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				break;
			case R.id.chart:
//				Toast.makeText(MainChoose.this, "数据分析", Toast.LENGTH_SHORT).show();
				Intent intent_data = new Intent();
				intent_data.setClass(MainChoose.this, ChartChoose.class);
				Bundle bundle_data = new Bundle();
				bundle_data.putCharSequence("nowAcount", nowAcount);
				intent_data.putExtras(bundle_data);
				startActivity(intent_data);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				break;
			case R.id.free:
//				Toast.makeText(MainChoose.this, "预留", Toast.LENGTH_SHORT).show();
				Intent intent_free = new Intent();
				intent_free.setClass(MainChoose.this, LogicShow.class);
				startActivity(intent_free);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				break;
			case R.id.setting:
//				Toast.makeText(MainChoose.this, "设置", Toast.LENGTH_SHORT).show();
				Intent intent_setting = new Intent();
				intent_setting.setClass(MainChoose.this, Setting.class);
				Bundle bundle_setting = new Bundle();
				bundle_setting.putCharSequence("nowAcount", nowAcount);
				intent_setting.putExtras(bundle_setting);
				startActivity(intent_setting);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				
				break;

			default:
				break;
			}
		}
	};
	
	//获取真实名字信息
	private String getTrueName(){
		String name = null;
		Cursor cur = database.rawQuery("SELECT user_true_name from "+TABLE_NAME_DETAIL+" where user_id IN(SELECT user_id from "+TABLE_NAME_LOGIN+" where user_name='"+nowAcount+"' order by create_time DESC limit 1)", null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                     name = cur.getString(cur.getColumnIndex("user_true_name"));
                     return name;
                } while (cur.moveToNext());
            }
        } else {
        	Log.i("jujiabao", "无最新数据");
        	return null;
        }
		return name;
	}
	
	@Override
	public boolean onKeyDown(int keyCode,
			KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			//创建日志文件
			StorageFileUtil.createLogFile("log.txt");
			overridePendingTransition(R.anim.in_from_top, R.anim.out_to_bottom);
			finish();
		}
		return super.onKeyDown(keyCode,event);
	}
}
