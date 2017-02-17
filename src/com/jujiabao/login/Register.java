package com.jujiabao.login;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
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

import com.jujiabao.database.DBManager;
import com.jujiabao.heartrate.R;
import com.jujiabao.util.TimeUtil;

public class Register extends Activity {

	private final static String TABLE_NAME_LOGIN = "heart_rate_login";
	private final static String TABLE_NAME_DETAIL = "heart_rate_detail";
	private SQLiteDatabase database;
	
	private EditText acount;//登录账号
	private EditText input_pwd;//输入密码
	private EditText confirm_pwd;//再次输入密码
	private EditText true_name;//您的姓名
	private EditText identy_id;//身份证号
	private EditText age;//您的年龄
	private EditText gender;//您的性别
	private EditText height;//您的身高
	private EditText weight;//您的体重
	private Button register;//注册
	private Button have_acount;//已有账号
	
	private Button imageView1;
	private Button imageView2;
	
	private String user_id;
	
	private List<String> acount_same = new ArrayList<String>(); //ID=1
	private String is_acount_same;
	@SuppressWarnings("unused")
	private int isPassAcount;//0表示不通过，1表示通过：同户名相同验证
	
	private String acount2MD5;
	private String password2MD5;
	private String identy2MD5;
	
	private List<String> isAllPassedList = new ArrayList<String>();//ID=2
	
	private List<String> userInfoList = new ArrayList<String>();//ID=3
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_register);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("注\t册");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(action);
		
		//数据库开启
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		//获取空间信息
		acount = (EditText) findViewById(R.id.count);
		acount.addTextChangedListener(mTextWatcher);
		
		input_pwd = (EditText) findViewById(R.id.input_pwd);
		input_pwd.addTextChangedListener(mTextWatcher);
		
		confirm_pwd = (EditText) findViewById(R.id.confirm_pwd);
		confirm_pwd.addTextChangedListener(mTextWatcher);
		
		true_name = (EditText) findViewById(R.id.true_name);
		true_name.addTextChangedListener(mTextWatcher);
		
		identy_id = (EditText) findViewById(R.id.Indenty);
		identy_id.addTextChangedListener(mTextWatcher);
		
		age = (EditText) findViewById(R.id.input_age);
		age.addTextChangedListener(mTextWatcher);
		
		gender = (EditText) findViewById(R.id.gender);
		gender.addTextChangedListener(mTextWatcher);
		
		height = (EditText) findViewById(R.id.height);
		height.addTextChangedListener(mTextWatcher);
		
		weight = (EditText) findViewById(R.id.weight);
		weight.addTextChangedListener(mTextWatcher);
		
		imageView1 = (Button) findViewById(R.id.imageView1);
		imageView1.addTextChangedListener(mTextWatcher);
		imageView1.setOnClickListener(action);
		
		imageView2 = (Button) findViewById(R.id.imageView2);
		imageView2.addTextChangedListener(mTextWatcher);
		imageView2.setOnClickListener(action);
		
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(action);
		
		register.setVisibility(View.INVISIBLE);
		
		have_acount = (Button) findViewById(R.id.have_count);
		have_acount.setOnClickListener(action);
		
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
	
	//所有按键事件汇总
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_left_two_title_btn_left:
				database.close();
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				break;
			
			case R.id.have_count:
				database.close();
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				break;
			
			case R.id.register:
				if(!"".equals(acount.getText().toString())
						&& !"".equals(input_pwd.getText().toString())
						&& !"".equals(confirm_pwd.getText().toString())
						&& !"".equals(true_name.getText().toString())
						&& !"".equals(identy_id.getText().toString())
						&& !"".equals(age.getText().toString())
						&& !"".equals(gender.getText().toString())
						&& !"".equals(height.getText().toString())
						&& !"".equals(weight.getText().toString())){
					isAcountSame(TABLE_NAME_LOGIN);
				}else {
					Toast.makeText(Register.this, "每项信息必填！", Toast.LENGTH_SHORT).show();
				}
				
				break;
				
			case R.id.imageView1:
				input_pwd.setText("");
				break;
				
			case R.id.imageView2:
				confirm_pwd.setText("");
				break;
			default:
				break;
			}
		}
	};
	
	//一键删除按钮的动态显示
	TextWatcher mTextWatcher  = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			if(acount.getText().toString() != null && !acount.getText().toString().equals("")
					&& input_pwd.getText().toString() != null && !input_pwd.getText().toString().equals("")
						&& confirm_pwd.getText().toString() != null && !confirm_pwd.getText().toString().equals("")
							&& true_name.getText().toString() != null && !true_name.getText().toString().equals("")
								&& identy_id.getText().toString() != null && !identy_id.getText().toString().equals("")
									&& age.getText().toString() != null && !age.getText().toString().equals("")
										&& input_pwd.getText().toString().equals(confirm_pwd.getText().toString())
											&& gender.getText().toString() != null && !gender.getText().toString().equals("")
												&& height.getText().toString() != null && !height.getText().toString().equals("")
													&& weight.getText().toString() != null && !weight.getText().toString().equals("")
					){
				register.setVisibility(View.VISIBLE);
				}else{
					register.setVisibility(View.INVISIBLE);
				}
			if (!input_pwd.getText().toString().equals(confirm_pwd.getText().toString()) 
					&& !input_pwd.getText().toString().equals("")
					&& !confirm_pwd.getText().toString().equals("")
					&& !true_name.getText().toString().equals("")) {
				imageView1.setVisibility(View.VISIBLE);
				imageView2.setVisibility(View.VISIBLE);
			}else {
				imageView1.setVisibility(View.INVISIBLE);
				imageView2.setVisibility(View.INVISIBLE);
			}
			}
		};
		
	//设置UserID信息
	private String setUserID(){
		UserID.user_id();
		this.user_id = UserID.user_id.toString();
		UserID.user_id.setLength(0);
		Log.i("jujiabao", "账号编码："+user_id);
		
		return user_id;
	}
	
	//获取数据库用户名相同
	private String isAcountSame(String TABLE_NAME) {
		Cursor cur = database.rawQuery("SELECT user_name FROM " + TABLE_NAME
				+ " where user_name='" + acount.getText().toString() + "'",
				null);
		isPassAcount = 1;//默认为1，表示允许通过
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    is_acount_same = cur.getString(cur.getColumnIndex("user_name"));
                    acount_same.add(is_acount_same);
                    Log.i("jujiabao", "校验用户名相同获取数据成功");
                    Toast.makeText(Register.this, "用户名称已存在，请更改重试！", Toast.LENGTH_SHORT).show();
                    Log.i("jujiabao", "相同名称："+acount_same);
                } while (cur.moveToNext());
//	                database.close();
            }
            
            if (acount_same.isEmpty() == true) {
				isPassAcount = 1;
				Log.i("jujiabao", "校验用户名确认无相同,isAllPassed()方法奏效");
				UpdateUserInfo();
			}else {
				isPassAcount = 0;
				EmptyList(1);
				Log.i("jujiabao", "校验用户名确认有相同");
			}
            
            return is_acount_same;
        } else {
        	Log.i("jujiabao", "校验用户名相同没有此数据");
            return null;
        }
    }
	
	//调用方法，清空容器内的数据
	private void EmptyList(int deleteID){
		switch (deleteID) {
		case 1:
			do {
				acount_same.clear();
			} while (acount_same.isEmpty() == false);
			break;

		case 2:
			do {
				isAllPassedList.clear();
			} while (isAllPassedList.isEmpty() == false);
			break;
			
		case 3:
			do {
				userInfoList.clear();
			} while (userInfoList.isEmpty() == false);
			break;
			
		default:
			break;
		}
	}
	
	//得到MD5值
	private String getMD5Code(String needColumn){
		MD5 getMD5 = new MD5();
		
		if ("acount2MD5".equals(needColumn)) {
			this.acount2MD5 = getMD5.GetMD5Code(acount.getText().toString());
			Log.i("jujiabao", "账户编码加密值："+this.acount2MD5);
		}
		
		if ("password2MD5".equals(needColumn)) {
			this.password2MD5 = getMD5.GetMD5Code(input_pwd.getText().toString());
			Log.i("jujiabao", "密码加密值："+this.password2MD5);
		}
		
		if ("identy2MD5".equals(needColumn)) {
			this.identy2MD5 = getMD5.GetMD5Code(identy_id.getText().toString());
			Log.i("jujiabao", "身份证号加密值："+this.identy2MD5);
		}
		return needColumn;
	}
	
	//开始向数据库更新数据
	private void UpdateUserInfo(){
		//user_id
		userInfoList.add(setUserID());//0
		//user_name
		userInfoList.add(acount.getText().toString().trim());//1
		//user_pwd
		getMD5Code("password2MD5");
		userInfoList.add(password2MD5);//2
		//create_time
		userInfoList.add(TimeUtil.getNowTime());//3
		//user_true_name
		userInfoList.add(true_name.getText().toString().trim());//4
		//
		getMD5Code("identy2MD5");
		userInfoList.add(identy2MD5);//5
		//
		userInfoList.add(age.getText().toString().trim());//6
		//
		userInfoList.add(gender.getText().toString().trim());//7
		//
		userInfoList.add(height.getText().toString().trim());//8
		//
		userInfoList.add(weight.getText().toString().trim());//9
//		Log.i("jujiabao", "用户信息容器userInfoList="+userInfoList);
		//密码等信息插入heart_rate_login表中
		database.execSQL("insert into "+TABLE_NAME_LOGIN+"(user_id,user_name,user_pwd,create_time) VALUES('"
				+ userInfoList.get(0)
				+ "','"
				+ userInfoList.get(1)
				+ "','"
				+ userInfoList.get(2) + "','" + userInfoList.get(3) + "')");
		Log.i("jujiabao", "heart_rate_login插入成功！");
		//个人等信息插入heart_rate_detail表中
		database.execSQL("insert into "+TABLE_NAME_DETAIL+"(create_time,user_id,user_true_name,user_identy,user_age,user_gender,user_height,user_weight) VALUES('"
				+ userInfoList.get(3)
				+ "','"
				+ userInfoList.get(0)
				+ "','"
				+ userInfoList.get(4)
				+ "','"
				+ userInfoList.get(5)
				+ "','"
				+ userInfoList.get(6)
				+ "','"
				+ userInfoList.get(7)
				+ "','"
				+ userInfoList.get(8)
				+ "','"
				+ userInfoList.get(9) + "')");
		Log.i("jujiabao", "heart_rate_detail插入成功！");
		Toast.makeText(Register.this, "注册成功！", Toast.LENGTH_SHORT).show();
		Log.i("jujiabao", "已注册成功！");
		EmptyList(3);//最后清空userInfoList
		//注册成功后，返回登录界面
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
}
