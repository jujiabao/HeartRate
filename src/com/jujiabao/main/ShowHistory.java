package com.jujiabao.main;

import java.util.List;



import com.jujiabao.heartrate.R;
import com.jujiabao.util.DBUtil;
import com.jujiabao.util.StorageFileUtil;
import com.jujiabao.util.TimeUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ShowHistory extends Activity {

	private int position;
	//适配器
    private ArrayAdapter<String> mConversationArrayAdapter;
    private ListView mConversationView;
	
    private String nowAccount;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_show_history);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("历史记录");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(action);
		
		//接收History的position、nowAccount的数据
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		position = bundle.getInt("position");
		nowAccount = bundle.getString("nowAccount");
		
		//ListView
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.histroy_show);
        mConversationView = (ListView) findViewById(R.id.show_history);
        mConversationView.setAdapter(mConversationArrayAdapter);
		
		switch (position) {
		case 0:
			title1_name.setText("当天记录");
			System.out.println("TimeUtil.getNowDate()="+TimeUtil.getNowDate());
			List<String> list = DBUtil.getRateData(nowAccount, TimeUtil.getNowDate());
			if (list.isEmpty() == false) {
				for (String str : list) {
					mConversationArrayAdapter.add(str);
				}
			}else {
				mConversationArrayAdapter.add("数据暂无");
			}
			break;
		case 1:
			title1_name.setText("两天内记录");
			System.out.println("TimeUtil.getAfterDate(2)"+TimeUtil.getBeforeDate(2));
			List<String> list2 = DBUtil.getRateData(nowAccount, TimeUtil.getBeforeDate(2));
			if (list2.isEmpty() == false) {
				for (String str : list2) {
					mConversationArrayAdapter.add(str);
				}
			}else {
				mConversationArrayAdapter.add("数据暂无");
			}
			break;
		case 2:
			title1_name.setText("四天内记录");
			List<String> list4 = DBUtil.getRateData(nowAccount, TimeUtil.getBeforeDate(4));
			if (list4.isEmpty() == false) {
				for (String str : list4) {
					mConversationArrayAdapter.add(str);
				}
			}else {
				mConversationArrayAdapter.add("数据暂无");
			}
			break;
		case 3:
			title1_name.setText("一星期内记录");
			List<String> list7 = DBUtil.getRateData(nowAccount, TimeUtil.getBeforeDate(7));
			if (list7.isEmpty() == false) {
				for (String str : list7) {
					mConversationArrayAdapter.add(str);
				}
			}else {
				mConversationArrayAdapter.add("数据暂无");
			}
			break;
		case 4:
			title1_name.setText("一个月内记录");
			List<String> list30 = DBUtil.getRateData(nowAccount, TimeUtil.getBeforeDate(31));
			if (list30.isEmpty() == false) {
				for (String str : list30) {
					mConversationArrayAdapter.add(str);
				}
			}else {
				mConversationArrayAdapter.add("数据暂无");
			}
			break;
		case 5:
			title1_name.setText("全部记录");
//			DBUtil.getRateData(nowAccount, "");
			List<String> listAll = DBUtil.getRateData(nowAccount, "");
			if (listAll.isEmpty() == false) {
				for (String str : listAll) {
					mConversationArrayAdapter.add(str);
				}
			}else {
				mConversationArrayAdapter.add("数据暂无");
			}
			break;
		default:
			break;
		}
	}
	
	//按钮事件
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_left_two_title_btn_left:
				//创建日志文件
				StorageFileUtil.createLogFile("log.txt");
				finish();
				overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
				break;

			default:
				break;
			}
		}
	};
	
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
