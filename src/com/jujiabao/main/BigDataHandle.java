package com.jujiabao.main;

import java.util.List;

import com.jujiabao.util.DBUtil;
/**
 * 显示大数据处理的结果
 * @author Hello.Ju
 *
 */
public class BigDataHandle {
	public static String l1;
	public static String l2;
	public static String l3;
	public static String l4;
	public static String l5;
	public static String l6;
	public static String l7;
	public static String l8;
	public static String l9;
	private String name = ChartChoose.nowAccount;
	
	public BigDataHandle() {
		List<String> startToEndTimeList = DBUtil.getMinOrMax(name, "create_time");
		l1 = "1、从" + startToEndTimeList.get(0) + "到" + startToEndTimeList.get(1)
				+ "，共测得数据有：" + DBUtil.getRateNum(name) + "条；";
		
		List<String> minOrMaxList = DBUtil.getMinOrMax(name, "rate");
		int minRate = Integer.parseInt(minOrMaxList.get(0));
		int maxRate = Integer.parseInt(minOrMaxList.get(1));
		int differ = maxRate - minRate;
		l2 = "2、其中,于"+DBUtil.getKeyRate(name, minRate).get(0)+"测得心率最小值为:"+minRate+"次/min；";
		l3 = "\t\t于"+DBUtil.getKeyRate(name, maxRate).get(0)+"测得心率最大值为:"+maxRate+"次/min；";
		l4 = "3、其中最大偏差为:"+differ+"次/min；";
		List<String> lbaList = DBUtil.getErrorRate(name);
		String l_rate = lbaList.get(0);
		String b_rate = lbaList.get(1);
		String average = lbaList.get(2);
		
		l5 = "4、在这组数据中，其中，心率次数低于60的共有："+l_rate+"次，心率次数高于100的有："+b_rate+"次；";
		l6 = "5、最终的平均值为："+average+"次/min。";
		l7 = "-------小贴士-------";
		l8 = "\t\t心率是指正常人安静状态下每分钟心跳的次数，也叫安静心率，一般为60～100次/分，可因年龄、性别或其他生理因素产生个体差异。一般来说，年龄越小，心率越快，老年人心跳比年轻人慢，女性的心率比同龄男性快，这些都是正常的生理现象。安静状态下，成人正常心率为60～100次/分钟，理想心率应为55～70次/分钟（运动员的心率较普通成人偏慢，一般为50次/分钟左右）。";
		l9 = "-------大数据处理结果-------";
	}
}
