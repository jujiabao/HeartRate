package com.jujiabao.main;

import com.jujiabao.heartrate.R;
import com.jujiabao.util.DBUtil;
import com.jujiabao.util.KillActivity;
import com.jujiabao.util.StorageFileUtil;
import com.jujiabao.util.TimeUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends Activity {

	private String nowAccount;
	
	//适配器
    private ArrayAdapter<String> mConversationArrayAdapter;
    private ListView mConversationView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("设\t置");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(action);
		
		//接收History的position、nowAccount的数据
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		nowAccount = bundle.getString("nowAcount");
		
		//ListView
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.setting_show);
        mConversationView = (ListView) findViewById(R.id.setting);
        mConversationView.setAdapter(mConversationArrayAdapter);
        mConversationArrayAdapter.add("删除当天数据");
        mConversationArrayAdapter.add("删除两天前数据");
        mConversationArrayAdapter.add("删除四天前数据");
        mConversationArrayAdapter.add("删除一星期前数据");
        mConversationArrayAdapter.add("删除一个月前数据");
        mConversationArrayAdapter.add("删除全部数据");
        mConversationArrayAdapter.add("退出程序");
        
        mConversationView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					DBUtil.deleteRateData(nowAccount);
					Toast.makeText(Setting.this, "当天数据删除成功", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					DBUtil.deleteRateData(nowAccount, TimeUtil.getBeforeDate(2));
					Toast.makeText(Setting.this, "两天前数据删除成功", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					DBUtil.deleteRateData(nowAccount, TimeUtil.getBeforeDate(4));
					Toast.makeText(Setting.this, "四天前数据删除成功", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					DBUtil.deleteRateData(nowAccount, TimeUtil.getBeforeDate(7));
					Toast.makeText(Setting.this, "一星期前数据删除成功", Toast.LENGTH_SHORT).show();
					break;
				case 4:
					DBUtil.deleteRateData(nowAccount, TimeUtil.getBeforeDate(31));
					Toast.makeText(Setting.this, "一个月前数据删除成功", Toast.LENGTH_SHORT).show();
					break;
				case 5:
					DBUtil.deleteRateData(nowAccount, TimeUtil.getAfterDate(1));
					Toast.makeText(Setting.this, "全部数据删除成功", Toast.LENGTH_SHORT).show();
					break;

				default:
					KillActivity.getInstance().exit();
					break;
				}
			}
		});
	}

	//按钮事件
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//创建日志文件
			StorageFileUtil.createLogFile("log.txt");
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
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
