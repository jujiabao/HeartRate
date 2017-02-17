package com.jujiabao.login;

import com.jujiabao.database.DBManager;
import com.jujiabao.heartrate.R;
import com.jujiabao.util.TimeUtil;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePassword extends Activity {
	
	private TextView change_pwd_count;
	private EditText change_pwd_input_pwd;
	private EditText change_pwd_conf_pwd;
	private Button confirm;
	private Button cancel;
	private Button delete1;
	private Button delete2;
	
	private final static String TABLE_NAME = "heart_rate_login";
	
	private SQLiteDatabase database;
	
	private String acount;
	private String true_name;
	private String indenty;
	private String studyID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_change_password);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("修改密码");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(action);
		
		change_pwd_count = (TextView) findViewById(R.id.change_pwd_count);
		change_pwd_count.addTextChangedListener(mTextWatcher);
		change_pwd_input_pwd = (EditText) findViewById(R.id.change_pwd_input_pwd);
		change_pwd_input_pwd.addTextChangedListener(mTextWatcher);
		change_pwd_conf_pwd = (EditText) findViewById(R.id.change_pwd_conf_pwd);
		change_pwd_conf_pwd.addTextChangedListener(mTextWatcher);
		confirm = (Button) findViewById(R.id.change_pwd_confirm);
		confirm.setOnClickListener(action);
		confirm.setVisibility(View.INVISIBLE);
		cancel = (Button) findViewById(R.id.change_pwd_cancel);
		cancel.setOnClickListener(action);
		
		delete1 = (Button) findViewById(R.id.pwd_no_same1);
		delete1.setOnClickListener(action);
		delete2 = (Button) findViewById(R.id.pwd_no_same2);
		delete2.setOnClickListener(action);
		
		//获得上一界面的操作数据信息
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		change_pwd_count.setText(bundle.getString("findAcount"));
		acount = bundle.getString("findAcount");
		true_name = bundle.getString("findTrueName");
		indenty = bundle.getString("findIndenty");
		Log.i("jujiabao", "change_password--{acount,true_name,indenty,studyID}={"+acount+","+true_name+","+indenty+","+studyID+"}");
		//开启数据库
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		
	}
	
	//按钮事件汇总
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_left_two_title_btn_left:
				database.close();
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				break;
				
			case R.id.change_pwd_confirm:
				if (!"".equals(acount) && !"".equals(true_name) && !"".equals(indenty)
						&& change_pwd_input_pwd.getText().toString().trim().equals(change_pwd_conf_pwd.getText().toString().toString())
						) {
					updatePwd(TABLE_NAME);
					Toast.makeText(ChangePassword.this, "修改成功！", Toast.LENGTH_SHORT).show();
					database.close();
					finish();
					overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				} else {
					Toast.makeText(ChangePassword.this, "未知错误！关闭后重试。", Toast.LENGTH_SHORT).show();
				}
				break;

			case R.id.change_pwd_cancel:
				database.close();
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				break;
				
			case R.id.pwd_no_same1:
				change_pwd_input_pwd.setText("");
				break;
				
			case R.id.pwd_no_same2:
				change_pwd_conf_pwd.setText("");
				break;
				
			default:
				break;
			}
		}
	};
	
	//确认按钮按钮显现变化
	TextWatcher mTextWatcher  = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			if(change_pwd_input_pwd.getText().toString() != null && !change_pwd_input_pwd.getText().toString().equals("")
					&& change_pwd_conf_pwd.getText().toString() != null && !change_pwd_conf_pwd.getText().toString().equals("")
					&& change_pwd_input_pwd.getText().toString().trim().equals(change_pwd_conf_pwd.getText().toString().toString())
					){
				confirm.setVisibility(View.VISIBLE);
				}else{
					confirm.setVisibility(View.INVISIBLE);
				}
			if (!change_pwd_conf_pwd.getText().toString().equals(change_pwd_input_pwd.getText().toString())
					&& !change_pwd_input_pwd.getText().toString().equals("")
					&& !change_pwd_conf_pwd.getText().toString().equals("")) {
				delete1.setVisibility(View.VISIBLE);
				delete2.setVisibility(View.VISIBLE);
			}else {
				delete1.setVisibility(View.INVISIBLE);
				delete2.setVisibility(View.INVISIBLE);
			}
		}
	};
	
	//更新数据库的内容
	private void updatePwd(String dbName){
		MD5 getMD5 = new MD5();
		String password = getMD5.GetMD5Code(change_pwd_conf_pwd.getText().toString());
		String nowTime = TimeUtil.getNowTime();
		database.execSQL("update " + dbName + " set user_pwd='" + password
				+ "',create_time='" + nowTime + "' where user_name='"
				+ change_pwd_count.getText().toString() + "'");
		Log.i("jujiabao", "密码修改成功，当前密码为："+password);
	}
	
	//重写返回键方法，为其加上返回动画
	@Override
	public boolean onKeyDown(int keyCode,
			KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			database.close();
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right); 
		}
		return super.onKeyDown(keyCode,event);
	}
}
