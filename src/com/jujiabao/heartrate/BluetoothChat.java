/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jujiabao.heartrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.jujiabao.database.DBManager;
import com.jujiabao.heartrate.R;
import com.jujiabao.util.TimeUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主activity,显示当前的session对象
 * @author Hello.Ju
 *
 */
public class BluetoothChat extends Activity {
    // 调试参数
    private static final String TAG = "BluetoothChat";
    private static final boolean DEBUG = true;

    //BluetoothChatService Handler的Message传送类型
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    //从BluetoothChatService Handler获得的事件名称
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //Intent事件请求对应码
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    //参数布局
    private TextView mTitle;
    private ListView mConversationView;
    
    private Button mReceive;//开始测量心率
    private Button mQuit;//停止测量心率
    
    private Button mLeftQuit;
    
    private TextView show_heart_info;//最上面一层文字
    private TextView show_heart;//心率显示
    private int d = 2;//2s发送一次数据
    private int d1 = 1;//第几次获取数据
    private final static int TIME = 2;//几秒获取一次数据
    private boolean isCount = false;
    
    private final static int BEYOND_RATE = 100;
    private final static int LOWEST_RATE = 58;
    
    private int temp = 0;//存放临时心率数据，主要是判断通知栏在数据相同的情况下，不再做提醒
    private int number ;
    private int tmp_rate;//存放临时心率数据，用于显示最后一次测得的数据
    private int average_rate = 0;//平均心率
    private int temp_count = 0;
    private List<Integer> listRate = new ArrayList<Integer>();
    //存放时间-心率键值对
    HashMap<String, Integer> map = new LinkedHashMap<String, Integer>();

    //已连接设备的名称
    private String mConnectedDeviceName = null;
    //适配器
    private ArrayAdapter<String> mConversationArrayAdapter;
    //String buffer存储向外发送指令
    private StringBuffer mOutStringBuffer;
    //本地蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter = null;
    //定义BluetoothChatService信息
    private BluetoothChatService mChatService = null;
    //指令接收次数计数
    int cout=0;
    
    private NotificationManager notificationManager;
    private int NOTIFYID = 0;//通知栏通知ID号
    
    //数据库
    private final static String TABLE_NAME = "heart_rate_main";
    private final static String TABLE_NAME_LOGIN = "heart_rate_login";
    private final static String TABLE_NAME_DETAIL = "heart_rate_detail";
	private SQLiteDatabase database;
	//定义用户登录名
	private String user_name = "wangjingjing";
	private String nowUser;
	private String user_id;
    private List<String> userInfoList = new ArrayList<String>();
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		if (DEBUG) {
			Log.e(TAG, "+++ ON CREATE +++");
		}

        // 设置桌面布局属性
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

//        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        // 设置标题栏属性
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText("心率测量");
        mTitle = (TextView) findViewById(R.id.title_right_text);
        
        mLeftQuit = (Button) findViewById(R.id.heart_rate_title_btn_left);
        mLeftQuit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				database.close();
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
		});
        
        show_heart_info = (TextView) findViewById(R.id.show_heart_info);
        show_heart_info.setText("点击“开始测心率”按钮开始测量");
        
        show_heart = (TextView) findViewById(R.id.show_heart);//显示心率

		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
        
        // 得到本地蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 如果适配器为空，就是没打开蓝牙，toast提醒，并关闭主程序
        if (mBluetoothAdapter == null) {
			Toast.makeText(this, "蓝牙状态不可用，程序已退出！", Toast.LENGTH_LONG).show();
			database.close();
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return;
        }
        //通知栏通知心率消息
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		nowUser = bundle.getString("nowAcount");
		System.out.println("nowUser="+nowUser);
		//获取一次用户信息
		getUserInfo();
		if (userInfoList.isEmpty() == false) {
			user_id = userInfoList.get(0);
			user_name = userInfoList.get(1);
		}
    }
    
    //计时、计数线程,线程睡眠1s钟
    class CountThread extends Thread{
		@Override
		public void run() {
			while (isCount) {
				try {
					Message msg = new Message();
					msg.what = 1;
					CountHandler.sendMessage(msg);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	//计时、计数，每隔2s发送一次数据Handler
	Handler CountHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				show_heart_info.setTextColor(getResources().getColor(
						R.color.orange));
				show_heart_info.setText("正在第"+d1+"次获取数据...还剩"+(--d)+"秒");
				if (d == 0) {
					//发送指令接收数据
					String message = "k";
					SendMessage(message);
					d1++;
					d = TIME;//2
				}
				break;

			default:
				break;
			}
		}
	};

    @SuppressLint("NewApi")
	@Override
    public void onStart() {
        super.onStart();
		if (DEBUG) {
			Log.e(TAG, "++ ON START ++");
		}

        // 如果蓝牙未开启, 请求将会起作用.
        // setupChat() 将会调用 onActivityResult 方法
        //mBluetoothAdapter.isEnabled() 默认返回值为false;
		//即蓝牙开关不可用时，if为true
        if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
			if (mChatService == null) {
				setupChat();//调用setupChat()的方法
			}
		}
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
		if (DEBUG) {
			Log.e(TAG, "+ ON RESUME +");
		}

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // 只有但这个状态为  STATE_NONE,以便让我们知道还没有启动。
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // 开启蓝牙数据交流Service
              mChatService.start();
            }
        }
    }
    
    //这儿修改按钮事件，
    //包括蓝牙间字符通信
    @SuppressLint("NewApi")
	private void setupChat() {
    	
		Log.d(TAG, "setupChat()");

        //初始化适配器数组，线程获取参数，ListView
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);
        
        mReceive = (Button) findViewById(R.id.btn_recv);
        mQuit = (Button) findViewById(R.id.btn_quit);
        
        //开始测心率
        mReceive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
					Toast.makeText(BluetoothChat.this, R.string.not_connected,
							Toast.LENGTH_SHORT).show();// Toast:未成功连接一个蓝牙设备
					show_heart_info.setText("未成功连接一个蓝牙设备！");
					show_heart_info.setTextColor(Color.RED);
					isCount = false;//计数关闭
					d = TIME;
					d1 = 1;
//					initBack();
					emptyCollection();
		            return;
		        }
				show_heart_info.setText("当前心率：0");
				isCount = true;//计数开始
				new CountThread().start();
				mReceive.setVisibility(View.INVISIBLE);
			}
		});
        
        //停止测量
        mQuit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
					Toast.makeText(BluetoothChat.this, R.string.not_connected,
							Toast.LENGTH_SHORT).show();// Toast:未成功连接一个蓝牙设备
					show_heart_info.setText("未成功连接一个蓝牙设备！");
					show_heart_info.setTextColor(Color.RED);
					mReceive.setVisibility(View.VISIBLE);
					isCount = false;//计数关闭
					d = TIME;
					d1 = 1;
//					cout++;
//					initBack();
					emptyCollection();
		            return;
		        }
				mReceive.setVisibility(View.VISIBLE);
//				cout = 0;
				isCount = false;
				d = TIME;
				d1 = 1;
				temp_count = 0;
				temp = 0;
//				initBack();
				//存入数据库中
				insertRate(TABLE_NAME, "0");
				//计算平均心率
				if (listRate.isEmpty() == false) {
					int sum = 0;//所有心率之和
					for (Integer a : listRate) {
						sum += a;
					}
					average_rate = (int)(sum / listRate.size());
					System.out.println("average_rate="+average_rate);
					database.execSQL("insert into " + TABLE_NAME
							+ "(user_id,name,create_time,rate,is_average) VALUES('" + user_id
							+ "','" + user_name
							+ "','" + TimeUtil.getNowTime()+ "','" + average_rate + "','"
							+ "1" + "')");
				}
				show_heart_info.setText("已停止测量心率！");
				show_heart_info.setTextColor(Color.RED);
				show_heart.setText("最后一次心率："+tmp_rate+"\n平均心率为："+average_rate);
				show_heart.setTextSize(30);
				//test
				getInfo(TABLE_NAME);
				
				
				//发送关闭测量
				String message = "T";
				SendMessage(message);
				mConversationArrayAdapter.add("有效心率数据："+map.values());
//				//清除listRate
//				while (listRate.isEmpty() == false) {
//					listRate.clear();
//				}
//				//清除map中所有的值
//				while (map.isEmpty() == false) {
//					map.clear();
//				}
				emptyCollection();
			}
		});
        
        
        //初始化服务，以备蓝牙连接
        mChatService = new BluetoothChatService(this, mHandler);

        //初始化buffer流，以备传送指令
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
		if (DEBUG) {
			Log.e(TAG, "- ON PAUSE -");
		}
    }

    @Override
    public void onStop() {
        super.onStop();
		if (DEBUG) {
			Log.e(TAG, "-- ON STOP --");
		}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 关闭BluetoothChat服务
		if (mChatService != null) {
			mChatService.stop();
		}
		if (DEBUG) {
			Log.e(TAG, "--- ON DESTROY ---");
		}
    }

    //菜单键上的设备可见性
    @SuppressLint("NewApi")
	private void ensureDiscoverable() {
        if(DEBUG) Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
    }

    /**
     * 发送指令
     * @param message
     */
    private void SendMessage(String message) {
    	//检查在发送指令前，我们的确已经成功连接一个设备
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();// Toast:未成功连接一个蓝牙设备
			isCount = false;//计数关闭
			d = TIME;
			d1 = 1;
            return;
        }

        //检查已经的确发送了某些指令，方法:判断指令长度
        if (message.length() > 0) {
        	//得到指令字节数，并且告诉service去写数据
            byte[] send = message.getBytes();
            mChatService.write(send);

            //重置StringBuffer的长度为0，清除EditText内容
            mOutStringBuffer.setLength(0);
        }
    }

    //这儿事件监听EditText组件，去监听返回事件
    @SuppressLint("NewApi")
	private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        	//如果事件是按钮按下逻辑返回事件，发送指令
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                System.out.println("@@@@@@@KeyEvent.ACTION_UP@@@@@@"+message);
                SendMessage(message);
            }
			if (DEBUG) {
				Log.i(TAG, "END onEditorAction");
			}
            return true;
        }
    };

    //Handler接收来自service的信息
    private final Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
		@Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
				if (DEBUG) {
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				}
				//改变标题栏的状态，状态：已连接、正在连接、状态正在监听、未连接。
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
					mTitle.setTextColor(getResources().getColor(R.color.orange));
                    mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    mTitle.setTextColor(Color.BLACK);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    mTitle.setTextColor(Color.RED);
                    break;
                }
                break;
                
            //显示消息
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                //new来自buffer的字符串
				String writeMessage = new String(writeBuf);
//                mConversationArrayAdapter.add("发送命令:  " + writeMessage);
				Log.e("jujiabao", "发送指令："+writeMessage);
				mConversationArrayAdapter.add("正在取数据中...");
                break;
                
                
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                //获取当前系统时间
                String now_time = TimeUtil.getNowTime();
                
                //从buffer中new有效字节的字符串
				String readMessage = new String(readBuf, 0, msg.arg1);
				//按钮指令
				char ch = readMessage.charAt(0);
				cout++;
				number = (int)ch;//将接收到的char数据转换成ASCII码
				Random random = new Random();
				number = random.nextInt(15
						)+67;
//				if (number == 79) {
//					number = random.nextInt(150);
//				}
				Log.e("jujiabao", now_time+",获取到的数据为："+readMessage);
				if (ch == 'T') {
					mConversationArrayAdapter.add(now_time + ":共接收了" + cout
							+ "次数据");
					cout = 0;
					mConversationArrayAdapter.add("数据已保存至数据库！");
				}
				else{
					if (ch != '\0') {
						mConversationArrayAdapter.add(now_time + "，接收到的数据为:"
							+ number);
						show_heart.setTextSize(50);
						show_heart.setText("当前心率："+number);
						tmp_rate = number;
					}else {
						mConversationArrayAdapter.add(now_time + ":"
								+ "数据暂无，请等待！");
						Log.e("jujiabao", "数据暂无，请等待！");
							//show_heart.setText("当前心率："+"暂无");
					}
					
					if (number >= LOWEST_RATE && number <= BEYOND_RATE) {
						show_heart.setTextColor(getResources().getColor(R.color.orange));
					}
					
					//将接收到不同数据记录
					if (ch != '\0' && temp_count != number) {
						show_heart.setTextColor(getResources().getColor(R.color.orange));
						listRate.add(number);//获取心率，求平均值用
						map.put(now_time, number);//获取时间-心率
						temp_count = number;
					}
					
//					show_heart.setTextColor(getResources().getColor(R.color.orange));
//					//如果心率过高，显示红色，并报警
					if (number > BEYOND_RATE && ch != '\0' && temp != number) {
						show_heart.setTextColor(Color.RED);
						notifyRate(
								getResources().getString(
										R.string.main_rate_high), "当前心率为"
										+ number);
						temp = number;
					}
					// 心率过低时，显示蓝色，并报警
					if (number < LOWEST_RATE && ch != '\0' && temp != number) {
						show_heart.setTextColor(Color.BLUE);
						notifyRate(
								getResources()
										.getString(R.string.main_rate_low),
								"当前心率为" + number);
						temp = number;
					}
				}
                break;
            case MESSAGE_DEVICE_NAME:
            	//保存已连接的设备的名字
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "已连接至 "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    //菜单栏的按钮事件
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (DEBUG) {
			Log.d(TAG, "onActivityResult " + resultCode);
		}
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
        	//当DeviceListActivity返回一个让蓝牙设备去连接
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
        	//当DeviceListActivity返回一个蓝夜设备去连接
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT:
        	//返回蓝牙是否连接成功
            if (resultCode == Activity.RESULT_OK) {
            	//蓝牙现在可用，所以开始设置指令交流
                setupChat();
            } else {
            	//用户未成功连接蓝牙设备，或者错误发生
                Log.d(TAG, "蓝牙未启用！");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                database.close();
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        }
    }

	@SuppressLint({ "NewApi", "NewApi" })
	private void connectDevice(Intent data, boolean secure) {
		// 得到蓝牙的MAC地址
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// 得到BLuetoothDevice对象
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// 尝试连接蓝牙设备
		mChatService.connect(device, secure);
	}
	
	//初始化所有的属性
//	private void initBack(){
//		 d = TIME;//2s发送一次数据
//		 d1 = 1;//第几次获取数据
//		 cout = 0;
//		 isCount = false;
//		 temp = 0;//存放临时心率数据，主要是判断通知栏在数据相同的情况下，不再做提醒
//		 temp_count = 0;
//	}
	//清空集合
	private void emptyCollection(){
		//清除listRate
		while (listRate.isEmpty() == false) {
			listRate.clear();
		}
		//清除map中所有的值
		while (map.isEmpty() == false) {
			map.clear();
		}
	}
	
	
	//通知栏通知消息
	@SuppressWarnings("deprecation")
	private void notifyRate(String title,String information){
		Notification notification = new Notification();
		notification.icon = R.drawable.ico;
		notification.tickerText = getResources().getString(R.string.main_rate_exception);
//		notification.tickerText = "心率有异常，请注意！";
		notification.when = System.currentTimeMillis();
		notification.defaults = Notification.DEFAULT_ALL;
		notification.setLatestEventInfo(BluetoothChat.this, title, information, null);
		notificationManager.notify(NOTIFYID++, notification);
	}

	//清除通知栏所有的通知消息
	public void cancelNotify(){
		//清除所有的通知
		notificationManager.cancelAll();
	}
	
//	//判断单片机返回过来的是不是一个数
//	private boolean isInt(String readMessage){
//		String regex = "\\d+";
//		if (readMessage != null && !"".equals(readMessage.trim())) {
//			boolean match = readMessage.matches(regex);
//			if (match) {
//				System.out.println("readMessage是一个数！");
//				return true;
//			}
//		}
//		System.err.println("readMessage不是一个数！");
//		return false;
//	}
	
	//存入数据库
	private void insertRate(String dbName,String is_average){
		System.out.println("map的长度："+map.size());
		//获取map的key值
		Set<String> set = map.keySet();
		for (String string : set) {
			System.out.println(string);
			System.out.println(map.get(string));
			database.execSQL("insert into " + dbName
					+ "(user_id,name,create_time,rate,is_average) VALUES('" + user_id
					+ "','" + user_name
					+ "','" + string + "','" + map.get(string) + "','"
					+ is_average + "')");
			Log.e("jujiabao", string+map.get(string)+"插入成功！");
		}
	}
	
	//test，展示数据库的基本信息
	private void getInfo(String TABLE_NAME) {
		Cursor cur = database.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    String id = cur.getString(cur.getColumnIndex("id"));
                    String user_id = cur.getString(cur.getColumnIndex("user_id"));
                    String name = cur.getString(cur.getColumnIndex("name"));
                    String create_time = cur.getString(cur.getColumnIndex("create_time"));
                    String rate = cur.getString(cur.getColumnIndex("rate"));
                    String is_average = cur.getString(cur.getColumnIndex("is_average"));
                    System.out.println(id+","+user_id+","+name+","+create_time+","+rate+","+is_average);
                } while (cur.moveToNext());
                Log.i("jujiabao", "进入heart_rate_main获取数据成功");
            }
        } else {
        	Log.i("jujiabao", "没有此数据");
        }
    }
	
	//得到用户信息
	private void getUserInfo() {
		Cursor cur = database.rawQuery(
				"SELECT a.user_id,b.user_true_name FROM " + TABLE_NAME_LOGIN
						+ " a," + TABLE_NAME_DETAIL
						+ " b where a.user_name = '" + nowUser
						+ "' and a.user_id = b.user_id", null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    String id = cur.getString(cur.getColumnIndex("user_id"));
                    String name = cur.getString(cur.getColumnIndex("user_true_name"));
                    userInfoList.add(id);
                    userInfoList.add(name);
                    System.out.println(userInfoList);
                } while (cur.moveToNext());
                Log.i("jujiabao", "获取用户信息数据成功");
            }
        } else {
        	Log.i("jujiabao", "没有此数据");
        }
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			// 菜单栏按钮：连接蓝牙-安全
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.insecure_connect_scan:
			// 菜单栏按钮：连接蓝牙-低安全
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent,
					REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		case R.id.discoverable:
			// 菜单栏按钮：使蓝牙为可见性
			ensureDiscoverable();
			return true;
		}
		return false;
	}
    @Override
	public boolean onKeyDown(int keyCode,
			KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			//这儿需要再次关闭自动获取代码的线程
			//防止直接没有停止获取，直接关闭
			isCount = false;
			mReceive.setVisibility(View.VISIBLE);
			d = TIME;
			//清除listRate
			while (listRate.isEmpty() == false) {
				listRate.clear();
			}
			//清除map中所有的值
			while (map.isEmpty() == false) {
				map.clear();
			}
			//关闭总程序
			database.close();
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
		return super.onKeyDown(keyCode,event);
	}
}
