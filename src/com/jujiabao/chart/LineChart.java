package com.jujiabao.chart;

/**
 * @author Hello.Ju
 * 
 * 内容：此区域为折线图作图区域
 * 
 * 主要方法：ChartFactory.getLineChartIntent(this, getDataset(),getRenderer());
 * getLineChartIntent用于折线图的画法。
 * 
 */

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.jujiabao.main.ChartChoose;
import com.jujiabao.main.ChartShow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

public class LineChart extends Activity {

	public static Intent intent;

	private static final int BEYOND_RATE = 100;
	private static final int LOWEST_RATE = 60;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intent = ChartFactory.getLineChartIntent(this, getDataset(),
				getRenderer());

		startActivity(intent);
	}

	public static XYMultipleSeriesDataset getDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries series = new XYSeries("心率");
		XYSeries series1 = new XYSeries("超高警戒");
		XYSeries series2 = new XYSeries("超低警戒");
		int i = 0;
		for (String date : ChartChoose.mapKey) {
			i++;
			series.add(i, ChartChoose.map.get(date));
			// series1.add(i, 100);
			// series2.add(i, 58);
		}
		// 心率最高临界值
		series1.add(0, BEYOND_RATE);
		series1.add(ChartChoose.map.size(), BEYOND_RATE);
		// 心率最低临界值
		series2.add(0, LOWEST_RATE);
		series2.add(ChartChoose.map.size(), LOWEST_RATE);

		dataset.addSeries(series);
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		return dataset;
	}

	@SuppressWarnings("deprecation")
	public static XYMultipleSeriesRenderer getRenderer() {

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		renderer.setXTitle("时间");
		renderer.setYTitle("心率");
		renderer.setChartTitle("全部心率数据进行分析");
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.WHITE);

		renderer.setXLabels(10);// 设置X轴显示10个刻度
		renderer.setYLabels(10);// 设置Y轴显示10个刻度

		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);

		renderer.setLabelsColor(Color.BLACK);

		renderer.setAxisTitleTextSize(16);// 坐标轴文本
		renderer.setChartTitleTextSize(40);// 设置图标标题文本大小40的值跟renderer.setMargins(new
											// int[] {40中40走，效果正好
		renderer.setLabelsTextSize(20);// 设置轴标签文本大小
		renderer.setLegendTextSize(20);// 设置图例文本大小
		renderer.setYAxisMax(120);
		renderer.setYAxisMin(30);
		renderer.setPointSize(5f);

		renderer.setMargins(new int[] { 40, 50, 35, 50 });

		renderer.setShowGrid(true);
		renderer.setXLabelsAngle(-25);
		renderer.setXLabels(0); // 设置 X 轴不显示数字（改用我们手动添加的文字标签）
		int i = 0;
		if (ChartShow.position != 2) {
			for (String date : ChartChoose.mapKey) {
				i++;
				renderer.addTextLabel(i, date);
			}
		}
		// 设置系列的颜色
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.BLACK);// 设置一个系列为
		if (ChartShow.position != 2) {
			r.setDisplayChartValues(true);
			r.setDisplayChartValuesDistance(30);
			r.setChartValuesTextSize(20f);
			r.setPointStyle(PointStyle.CIRCLE);
			r.setFillPoints(true);
		} else {
			r.setDisplayChartValues(false);

		}
		renderer.addSeriesRenderer(r);// 往XYMultipleSeriesRenderer添加一个系列

		r = new XYSeriesRenderer();
		r.setColor(Color.RED);
		renderer.addSeriesRenderer(r);

		r = new XYSeriesRenderer();
		r.setColor(Color.BLUE);
		renderer.addSeriesRenderer(r);

		return renderer;
	}
}
