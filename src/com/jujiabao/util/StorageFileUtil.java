package com.jujiabao.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.content.Context;
import android.util.Log;

/**
 * 创建手机里的一些文件，包括目录
 * 
 * @author Hello.Ju
 * 
 */
public class StorageFileUtil {
	// 定义包名
	public static final String PACKAGE_NAME = "com.jujiabao.HeartRate";
	// 定义文件名
	public static final String FILE = "config.xml";
	// 定义手机的相对路径
	private final static String PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/"
			+ PACKAGE_NAME;
	// 定义文件名称
//	private final static String FILE_NAME = PATH + "/" + FILE;

	/**
	 * 将文件复制到手机内存中，外部内存
	 * @param context
	 */
	public static void copyFile2Storage(Context context, String file, InputStream inputStream) {
		try {
			String FILE_NAME = PATH + "/" + file;
			
			File dir = new File(PATH);
			if (!dir.exists()) {
				dir.mkdir();
				System.out.println(PATH + "创建成功！");
			}
			if (!(new File(FILE_NAME)).exists()) {
				InputStream is = inputStream;
				FileOutputStream fos = new FileOutputStream(FILE_NAME);
				byte[] buffer = new byte[1024];
				int count = 0;
				// 开始复制dictionary.db文件
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				System.out.println("手机文件：" + file + "复制成功");
				// 关闭文件流
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 解析xml的内容
	 * @return
	 */
	public static HashMap<String, String> getXMLDetail(String file){
		String FILE_NAME = PATH + "/" + file;
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File(FILE_NAME));
			Element root = doc.getRootElement();
			List<Element> elements = root.elements();
			for (Element element : elements) {
				map.put(element.attributeValue("title"), element.getTextTrim());
			}
			if (map.isEmpty() == false) {
				Log.i("jujiabao", "手机文件：" + file + "解析成功！");
			} else {
				Log.i("jujiabao", "手机文件：" + file + "解析失败！");
			}
		} catch (Exception e) {
			Log.i("jujiabao", file + "解析失败！");
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * 获取日志文件
	 * @param file
	 */
	public static void createLogFile(String file){
		String FILE_NAME = PATH + "/" + file;
		BufferedReader br = null;
		PrintWriter pw = null;
		try {
			Process process = Runtime.getRuntime().exec("logcat -d");
			br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
					FILE_NAME)));
			String line = null;
			while ((line = br.readLine()) != null) {
				pw.println(line);
			}
			pw.flush();
			System.out.println(TimeUtil.getNowTime()+"创建log日志文件成功！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (pw != null) {
					pw.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
	}
}
