package com.jujiabao.login;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jujiabao.database.DBManager;
import com.jujiabao.dialog.AllowInternetDialog;
import com.jujiabao.dialog.AllowInternetDialog.Builder;
import com.jujiabao.dialog.DialogLoginTips;
import com.jujiabao.heartrate.R;
import com.jujiabao.main.AboutAuthor;
import com.jujiabao.main.MainChoose;
import com.jujiabao.util.IOUtil;

public class MainLogin extends Activity {

	private Button btn_login;

	private final static String TABLE_NAME = "heart_rate_login";
	private SQLiteDatabase database;

	private List<String> userInfo = new ArrayList<String>();
	private String getName;
	private String getPassword;

	private EditText name;
	private EditText password;
	private String pwd2MD5;

	private int isPassed;

	private Button btn_delete1;
	private Button btn_delete2;

	private Button btn_tips;
	
	private CheckBox checkBox;
	
	private CheckBox allowInternet;

	private DialogLoginTips mDialogLoginTips;
	private AllowInternetDialog mAllowInternetDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main_login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title1);

		// 设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title1_name);
		title1_name.setText("登\t录");

		// 注册一个账号、忘记密码？
		Button register = (Button) findViewById(R.id.register);
		Button forget_pwd = (Button) findViewById(R.id.forget_pwd);
		register.setOnClickListener(action);
		forget_pwd.setOnClickListener(action);

		// 定义文本文件
		name = (EditText) findViewById(R.id.user_name);
		password = (EditText) findViewById(R.id.user_pwd);

		// 登录按钮
		btn_login = (Button) findViewById(R.id.login_confirm);
		btn_login.setOnClickListener(action);

		// 一键清除编辑框内的内容
		btn_delete1 = (Button) findViewById(R.id.delete1);
		btn_delete2 = (Button) findViewById(R.id.delete2);
		btn_delete1.setOnClickListener(action);
		btn_delete2.setOnClickListener(action);
		name.addTextChangedListener(mTextWatcher);
		password.addTextChangedListener(mTextWatcher);
		btn_delete1.setVisibility(View.INVISIBLE);
		btn_delete2.setVisibility(View.INVISIBLE);
		btn_login.setVisibility(View.INVISIBLE);

		btn_tips = (Button) findViewById(R.id.tips);
		btn_tips.setOnClickListener(action);

		checkBox = (CheckBox) findViewById(R.id.checkBox);
		allowInternet = (CheckBox) findViewById(R.id.checkBox_inter);
		
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
		
		//使用SAXReader读取xml文件
		List<String> list = null;
		try {
			list = IOUtil.parseXML();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (list.get(2).equals("0")) {
			checkBox.setChecked(true);
			name.setText(list.get(0));
			password.setText(list.get(1));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_tip) {
			// 说明入口
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.content_explain)
					.setCancelable(false)
					.setPositiveButton("嗯！",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}
		
		if (id == R.id.action_about) {
			// 关于
			Intent intent = new Intent();
			intent.setClass(MainLogin.this, AboutAuthor.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_top, R.anim.out_to_bottom);
			return true;
		}
		
		if (id == R.id.action_exit) {
			// 登录窗口，一键关闭应用
			System.exit(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// 按钮事件
	private OnClickListener action = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 登录按钮事件
			case R.id.login_confirm:
				// 获取通过权限信息
				getName = name.getText().toString();
				getPassword = password.getText().toString();
				getPass();
				Log.i("jujiabao", "名字：" + getName + "," + "密码：" + "*******"
						+ ",isPassed=" + isPassed);
				// OurUniversity.class
				if (isPassed == 1) {
					//如果记住密码被按下
					if (checkBox.isChecked()) {
						try {
							IOUtil.writeXML(getName, getPassword, "0");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else {
						try {
							IOUtil.writeXML(getName, getPassword, "1");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					//如果允许联网,暂未开通，不给用
					//TODO 联网的CheckBox，暂不设计
					if (allowInternet.isChecked()) {
						//目前只弹出对话框显示未开通
						initInstance();
						mAllowInternetDialog.show();
						allowInternet.setChecked(false);
						break;
					}
					//界面跳转
					Intent intent = new Intent();
					intent.setClass(MainLogin.this, MainChoose.class);
					Bundle bundle = new Bundle();
					bundle.putCharSequence("now_name", getName);
					bundle.putCharSequence("now_pwd", getPassword);
					intent.putExtras(bundle);
					startActivity(intent);
					database.close();//关闭数据库
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					MainLogin.this.finish();
				}
				break;
			// 注册一个账号
			case R.id.register:
				Intent intent_register = new Intent();
				intent_register.setClass(MainLogin.this, Register.class);
				startActivity(intent_register);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				break;
			// 忘记密码？
			case R.id.forget_pwd:
				Intent intent_forget_pwd = new Intent();
				intent_forget_pwd
						.setClass(MainLogin.this, ForgetPassword.class);
				startActivity(intent_forget_pwd);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				break;
			// 打开提示
			case R.id.tips:
				initInstance();
				overridePendingTransition(R.anim.in_from_bottom,
						R.anim.out_to_top);
				mDialogLoginTips.show();
				break;

			case R.id.delete1:
				name.setText("");
				break;

			case R.id.delete2:
				password.setText("");
				break;

			default:
				break;
			}
		}
	};

	// 获取密码信息
	private String getUserPwd(String TABLE_NAME) {
		String mPassword;
		MD5 getMD5 = new MD5();
		this.pwd2MD5 = getMD5.GetMD5Code(getPassword);
		System.out.println(this.pwd2MD5);

		Cursor cur = database.rawQuery("SELECT user_pwd FROM " + TABLE_NAME
				+ " where user_name='" + getName + "'", null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					mPassword = cur.getString(cur.getColumnIndex("user_pwd"));
					userInfo.add(mPassword);
					Log.i("jujiabao", "获取数据成功");
					System.out.println("mPassword=" + mPassword);
					return mPassword;
				} while (cur.moveToNext());
				// database.close();
			}
			return null;
		} else {
			Log.i("jujiabao", "没有此数据");
			return null;
		}
	}

	// 获取通行权限
	private int getPass() {
		String now_pwd = getUserPwd(TABLE_NAME);
		if (userInfo.isEmpty() == false) {
			if (pwd2MD5.equals(now_pwd)) {
				Log.i("jujiabao", "通过");
				isPassed = 1;
				while (userInfo.isEmpty() == false) {
					userInfo.remove(0);
				}
			} else {
				Toast.makeText(MainLogin.this,
						"密码错误！用户名：" + getName + ",密码：" + getPassword + "",
						Toast.LENGTH_SHORT).show();
				Log.i("jujiabao", "密码错误！user=" + getName + ",password="
						+ getPassword + "");
				isPassed = 0;
				while (userInfo.isEmpty() == false) {
					userInfo.remove(0);
				}
			}
		} else {
			Toast.makeText(MainLogin.this, "用户名输入有误，请重新输入！", Toast.LENGTH_SHORT)
					.show();
			Log.i("jujiabao", "用户名输入有误，请重新输入！");
		}
		return 1;
	}

	// 一键删除按钮的动态显示
	TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (name.getText().toString() != null
					&& !name.getText().toString().equals("")) {
				btn_delete1.setVisibility(View.VISIBLE);
			} else {
				btn_delete1.setVisibility(View.INVISIBLE);
			}
			if (password.getText().toString() != null
					&& !password.getText().toString().equals("")) {
				btn_delete2.setVisibility(View.VISIBLE);
			} else {
				btn_delete2.setVisibility(View.INVISIBLE);
			}
			if (!name.getText().toString().equals("")
					&& !password.getText().toString().equals("")) {
				btn_login.setVisibility(View.VISIBLE);
			} else {
				btn_login.setVisibility(View.INVISIBLE);
			}
		}
	};

	// 初始化dialog对话框
	private void initInstance() {
		//初始化提示框
		DialogLoginTips.Builder builder = new DialogLoginTips.Builder(this);
		mDialogLoginTips = builder.create();
		//初始化允许联网的提示框
		Builder builder2 = new AllowInternetDialog.Builder(this);
		mAllowInternetDialog = builder2.create();
	}
}
