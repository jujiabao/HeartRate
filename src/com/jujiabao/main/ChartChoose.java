package com.jujiabao.main;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.jujiabao.heartrate.R;
import com.jujiabao.util.DBUtil;
import com.jujiabao.util.StorageFileUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChartChoose extends Activity {

	//适配器
    private ArrayAdapter<String> mConversationArrayAdapter;
    private ListView mConversationView;
	
    public static String nowAccount;
    
    public static HashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
    
    public static Set<String> mapKey;
    public static Collection<Integer> mapValue;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_chart_choose);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("数据分析");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(action);
		
		//获取上一次参数的信息
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		nowAccount = bundle.getString("nowAcount");
		System.out.println("nowAccount="+nowAccount);
		
		//ListView
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.chart_choose);
        mConversationView = (ListView) findViewById(R.id.choose_chart);
        mConversationView.setAdapter(mConversationArrayAdapter);
        mConversationArrayAdapter.add("数据柱状分析");
        mConversationArrayAdapter.add("数据折线分析");
        mConversationArrayAdapter.add("曲线趋势");
        mConversationArrayAdapter.add("大数据分析处理");
        
        map = DBUtil.getAllRate(nowAccount);
        mapKey = map.keySet();
        mConversationView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//用之前先判断一下数据库有没有信息
		        if (map.isEmpty() == true) {
		        	Toast.makeText(ChartChoose.this, "数据库暂无数据！", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent();
				intent.setClass(ChartChoose.this, ChartShow.class);
				Bundle bundle = new Bundle();
				bundle.putInt("position", position);
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
	}
	//按钮事件
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_left_two_title_btn_left:
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
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
