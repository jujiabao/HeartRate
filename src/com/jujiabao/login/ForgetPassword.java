package com.jujiabao.login;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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

public class ForgetPassword extends Activity {
	
	
	private EditText returnAccount;
	private EditText returnTrueName;
	private EditText returnIndenty;
	
	private Button find;
	
	private final static String TABLE_NAME_DETAIL = "heart_rate_detail";
	private final static String TABLE_NAME_LOGIN = "heart_rate_login";
	private SQLiteDatabase database;
	
	private String findIndenty;
	private List<String> verifyList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_forget_password);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("找回密码");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(action);
		
		//获取EditText的id文件
		returnAccount = (EditText) findViewById(R.id.return_count);
		returnAccount.addTextChangedListener(mTextWatcher);
		returnTrueName = (EditText) findViewById(R.id.return_true_name);
		returnTrueName.addTextChangedListener(mTextWatcher);
		returnIndenty = (EditText) findViewById(R.id.return_Indenty);
		returnIndenty.addTextChangedListener(mTextWatcher);
		
		//找回密码按钮事件
		find = (Button) findViewById(R.id.find_pwd);
		find.setOnClickListener(action);
		find.setVisibility(View.INVISIBLE);
		//开启数据库
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		
	}
	
	//汇总按钮事件
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			//标题栏按钮返回事件
			case R.id.head_left_two_title_btn_left:
				database.close();
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				break;
			
			case R.id.find_pwd:
				VerifyInfo();
				if (verifyList.isEmpty() == false && verifyList.size() == 3) {
					if (returnAccount.getText().toString().equals(verifyList.get(0))
							&& returnTrueName.getText().toString().equals(verifyList.get(1))
							&& findIndenty.equals(verifyList.get(2))) {
						Intent intent = new Intent();
						intent.setClass(ForgetPassword.this, ChangePassword.class);
						Bundle bundle = new Bundle();
						bundle.putCharSequence("findAcount", verifyList.get(0));
						bundle.putCharSequence("findTrueName", verifyList.get(1));
						bundle.putCharSequence("findIndenty", verifyList.get(2));
						intent.putExtras(bundle);
						startActivity(intent);
						database.close();
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						finish();
					}else {
						Log.i("jujiabao", "出现未知错误！可能信息填写有误！");
					}
				}else {
					Toast.makeText(ForgetPassword.this, "用户信息填写错误！", Toast.LENGTH_SHORT).show();
					Log.i("jujiabao", "找回密码--数据未找到！可能数据填写错误！");
				}
				
				//清除verifyList容器的内容
				if (verifyList.isEmpty() == false) {
					do {
						verifyList.clear();
					} while (verifyList.isEmpty() == false);
				
				}
				break;

			default:
				break;
			}
		}
	};
	
	//find按钮显现变化
	TextWatcher mTextWatcher  = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			if(returnAccount.getText().toString() != null && !returnAccount.getText().toString().equals("")
					&& returnIndenty.getText().toString() != null && !returnIndenty.getText().toString().equals("")
					&& returnTrueName.getText().toString() != null && !returnTrueName.getText().toString().equals("")
					){
				find.setVisibility(View.VISIBLE);
				}else{
					find.setVisibility(View.INVISIBLE);
				}
		}
	};
	
	//验证四项信息是否正确
	private List<String> VerifyInfo(){
		MD5 getMD5 = new MD5();
		String findAcount = returnAccount.getText().toString();
		String findTrueName = returnTrueName.getText().toString();
		findIndenty = getMD5.GetMD5Code(returnIndenty.getText().toString());
		
		Cursor cur = database.rawQuery(
				"SELECT a.user_name,a.user_id,b.user_true_name,b.user_identy,b.user_id FROM "
						+ TABLE_NAME_LOGIN + " a," + TABLE_NAME_DETAIL
						+ " b where a.user_name='" + findAcount
						+ "' and a.user_id=b.user_id and b.user_identy='"
						+ findIndenty + "'", null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    String user_name = cur.getString(cur.getColumnIndex("user_name"));
                    String user_true_name = cur.getString(cur.getColumnIndex("user_true_name"));
                    String user_identy = cur.getString(cur.getColumnIndex("user_identy"));
                    verifyList.add(user_name);
                    
                    if (user_true_name.equals(findTrueName)) {
						verifyList.add(user_true_name);
					}else {
						Log.i("jujiabao", "找回密码--真实姓名填写错误");
					}
                    
                    if (user_identy.equals(findIndenty)) {
						verifyList.add(user_identy);
					}else {
						Log.i("jujiabao", "找回密码--身份证号填写错误");
					}
                    if (verifyList.size() == 3) {
						Log.i("jujiabao", "2项数据验证成功，verifyList="+verifyList);
					}
		           	
                } while (cur.moveToNext());
            }
            
            return verifyList;
    		
        } else {
        	Log.i("jujiabao", "没有此数据来进行四项验证");
            return null;
        }
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
