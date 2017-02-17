package com.jujiabao.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jujiabao.util.DBUtil;
import com.jujiabao.util.ExportExcel;
import com.jujiabao.util.RateDetail;
/**
 * 写数据进Excel
 * @author Hello.Ju
 *
 */
public class WriteData2Excel {
	
	// 定义包名
	public static final String PACKAGE_NAME = "com.jujiabao.HeartRate";
	// 定义手机的相对路径
	private final static String PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/"
			+ PACKAGE_NAME;
	private final static String FILE = PATH + "/" + "心率数据.xls";
	
	public static boolean write(String nowAccount){
		// 默认测试数据
		File file = new File(FILE);
		String title = "心率数据";
		List<String> header = new ArrayList<String>();
		//导入原型数据-表头
	    header.add("用户ID");
	    header.add("姓名");
	    header.add("测量时间");
	    header.add("心率(次/min)");
	    header.add("均值");
		List<List<String>> data = new ArrayList<List<String>>();
		
		List<RateDetail> rateDetails = new ArrayList<RateDetail>();
		rateDetails = DBUtil.getAllRateExcel(nowAccount);
		for (RateDetail r : rateDetails) {
			List<String> inlist = new ArrayList<String>();
			inlist.add(r.getUser_id());
			inlist.add(r.getName());
			inlist.add(r.getCreate_time());
			inlist.add(r.getRate());
			inlist.add(("0".equals(r.getIs_average()))?"否":"是");
			
			data.add(inlist);
		}
		
		ExportExcel ef = new ExportExcel(title, header, data);
		try {
		    ef.save(file);
		    return true;
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return false;
	}
}
