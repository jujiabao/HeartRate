package com.jujiabao.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jujiabao.heartrate.R;
import com.jujiabao.util.StorageFileUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class LogicShow extends Activity {

	private TextView m1;//心率测量
	private TextView m2;//个人资料
	private TextView m3;//历史记录
	private TextView m4;//数据分析
	private TextView m5;//设置
	private TextView m6;
	
	private TextView m1_detail;
	private TextView m2_detail;
	private TextView m3_detail;
	private TextView m4_detail;
	private TextView m5_detail;
	private TextView m6_detail;
	
	private HashMap<String, String> map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_logic_show);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("逻辑解释");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//创建日志文件
				StorageFileUtil.createLogFile("log.txt");
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
		});
		//获取TextView对象
		m1 = (TextView) findViewById(R.id.m1);
		m1_detail = (TextView) findViewById(R.id.m1_detail);
		m2 = (TextView) findViewById(R.id.m2);
		m2_detail = (TextView) findViewById(R.id.m2_detail);
		m3 = (TextView) findViewById(R.id.m3);
		m3_detail = (TextView) findViewById(R.id.m3_detail);
		m4 = (TextView) findViewById(R.id.m4);
		m4_detail = (TextView) findViewById(R.id.m4_detail);
		m5 = (TextView) findViewById(R.id.m5);
		m5_detail = (TextView) findViewById(R.id.m5_detail);
		m6 = (TextView) findViewById(R.id.m6);
		m6_detail = (TextView) findViewById(R.id.m6_detail);
		
		// 放置对象数组中
		TextView[] mTitle = { m1, m2, m3, m4, m5, m6 };
		TextView[] mDetail = { m1_detail, m2_detail, m3_detail, m4_detail,
				m5_detail, m6_detail };
		//初始化map集合
		map = new LinkedHashMap<String, String>();
		//获取logic_content.xml文件信息
		map = StorageFileUtil.getXMLDetail("logic_content.xml");
		int i = 0;
		for (Entry<String, String> entry : map.entrySet()) {
			 mTitle[i].setText(entry.getKey());
			 mDetail[i].setText("\t\t"+entry.getValue());
			 i++;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			//创建日志文件
			StorageFileUtil.createLogFile("log.txt");
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right); 
		}
		return super.onKeyDown(keyCode,event);
	}
}
