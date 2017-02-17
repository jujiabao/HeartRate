package com.jujiabao.main;

import com.jujiabao.dialog.ExcelDialog;
import com.jujiabao.heartrate.R;
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

public class History extends Activity {

	//适配器
    private ArrayAdapter<String> mConversationArrayAdapter;
    private ListView mConversationView;
	
    private String nowAccount;
    
    private ExcelDialog mExcelDialog;
    public static boolean isSuccess;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_history);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("历史记录");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(action);
		//获取上一次参数的信息
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		nowAccount = bundle.getString("nowAcount");
		
		//ListView
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.time_pick);
        mConversationView = (ListView) findViewById(R.id.data_pick);
        mConversationView.setAdapter(mConversationArrayAdapter);
        mConversationArrayAdapter.add("当天数据");
        mConversationArrayAdapter.add("两天内数据");
        mConversationArrayAdapter.add("四天内数据");
        mConversationArrayAdapter.add("一星期内数据");
        mConversationArrayAdapter.add("一个月内数据");
        mConversationArrayAdapter.add("全部数据");
        mConversationArrayAdapter.add("一键导出数据至Excel");
        
        mConversationView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position <= 5) {
					System.out.println("position="+position+",id="+id);
					Intent intent = new Intent();
					intent.setClass(History.this, ShowHistory.class);
					Bundle bundle = new Bundle();
					bundle.putInt("position", position);
					bundle.putCharSequence("nowAccount", nowAccount);
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				} else {
//					Toast.makeText(History.this, "position="+position, Toast.LENGTH_SHORT).show();
					isSuccess = WriteData2Excel.write(nowAccount);
					System.out.println(isSuccess?"导出Excel文件成功！":"导出Excel文件失败！");
					initInstance();
					mExcelDialog.show();
				}
				
			}
		});
	}
	
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_left_two_title_btn_left:
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
	// 初始化dialog对话框
	private void initInstance() {
		ExcelDialog.Builder builder = new ExcelDialog.Builder(this);
		mExcelDialog = builder.create();
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
