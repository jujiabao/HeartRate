package com.jujiabao.main;

import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.CubicLineChart;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jujiabao.chart.LineChart;
import com.jujiabao.chart.PieChart;
import com.jujiabao.heartrate.R;
import com.jujiabao.util.DBUtil;
import com.jujiabao.util.StorageFileUtil;

public class ChartShow extends Activity {
	
    public static int position;
    private GraphicalView chartView ;
    
    //适配器
    private ArrayAdapter<String> mConversationArrayAdapter;
    private ListView mConversationView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_chart_show);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);
		
		//设置标题栏标题
		TextView title1_name = (TextView) findViewById(R.id.title2_name);
		title1_name.setText("数据统计图");
		
		//标题栏的左键
		Button btn_left = (Button) findViewById(R.id.head_left_two_title_btn_left);
		btn_left.setOnClickListener(action);

		//获取上一次参数的信息
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		position = bundle.getInt("position");
		
//	    mLayout = (LinearLayout) findViewById(R.id.chart_show);
	    
	    //加载这个布局
	    LinearLayout linearView=(LinearLayout)findViewById(R.id.chart_show);
	    
	    switch (position) {
		case 0:
			title1_name.setText("数据柱状统计");
			chartView = getCombinedXYChartGraphicalView(this);
			break;
		case 1:
			title1_name.setText("数据折线统计");
			chartView = getLineChartGraphicalView(this);
			break;
		case 2:
			title1_name.setText("数据曲线趋势");
			chartView = getCubicChartGraphicalView(this);
			break;
		case 3:
			title1_name.setText("大数据分析");
			chartView = getPieChartGraphicalView(this);
		    linearView.setLayoutParams(new RelativeLayout.LayoutParams(1000, 970));
		    
		    //ListView
	        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.chart_list);
	        mConversationView = (ListView) findViewById(R.id.data_handle);
	        mConversationView.setAdapter(mConversationArrayAdapter);
	        BigDataHandle bigDataHandle = new BigDataHandle();
	        mConversationArrayAdapter.add(bigDataHandle.l9);
	        mConversationArrayAdapter.add(bigDataHandle.l1);
	        mConversationArrayAdapter.add(bigDataHandle.l2);
	        mConversationArrayAdapter.add(bigDataHandle.l3);
	        mConversationArrayAdapter.add(bigDataHandle.l4);
	        mConversationArrayAdapter.add(bigDataHandle.l5);
	        mConversationArrayAdapter.add(bigDataHandle.l6);
	        mConversationArrayAdapter.add(bigDataHandle.l7);
	        mConversationArrayAdapter.add(bigDataHandle.l8);
		    
			break;

		default:
			break;
		}
	    
	    linearView.addView(chartView,
	                new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	//创建折线图
	public GraphicalView getLineChartGraphicalView(Context context){
		return ChartFactory.getLineChartView(context,
				LineChart.getDataset(), LineChart.getRenderer());
	}
	
	//创建曲线图
	public GraphicalView getCubicChartGraphicalView(Context context){
		return ChartFactory.getCubeLineChartView(context,
				LineChart.getDataset(), LineChart.getRenderer(), 0.33f);
	}
	
	//创建柱状图
	public GraphicalView getCombinedXYChartGraphicalView(Context context){
		 String[] types = new String[] { BarChart.TYPE, CubicLineChart.TYPE, CubicLineChart.TYPE};
		return ChartFactory.getCombinedXYChartView(context,
				LineChart.getDataset(), LineChart.getRenderer(),types);
	}
	
	//创建饼图
	public GraphicalView getPieChartGraphicalView(Context context){
		HashMap<String, Integer> countMap = DBUtil.classifyRate(ChartChoose.nowAccount);
	double[] values = { countMap.get("c1"), countMap.get("c2"),
			countMap.get("c3"), countMap.get("c4"), countMap.get("c5"),
			countMap.get("c6") };
		int[] colors={Color.BLUE,Color.CYAN,Color.GREEN,Color.YELLOW,Color.MAGENTA,Color.RED};
		return ChartFactory
				.getPieChartView(context, PieChart.getDataset("测试", values),
						PieChart.getRenderer(colors));
	}
	//按钮事件
	private OnClickListener action = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
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
