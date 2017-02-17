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

import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;

public class PieChart extends Activity {

	public static CategorySeries getDataset(String title, double[] values) {
		CategorySeries series = new CategorySeries(title);
		series.add("0~59", values[0]);
		series.add("60~70", values[1]);
		series.add("70~80", values[2]);
		series.add("80~90", values[3]);
		series.add("90~100", values[4]);
		series.add(">100", values[5]);
		return series;
	}

	public static DefaultRenderer getRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(30);
		renderer.setLegendTextSize(30);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
}
