package com.jujiabao.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import android.os.Environment;
import android.util.Log;

/**
 * 主要作用是向手机中写入数据
 * @author Hello.Ju
 *
 */
public class IOUtil {
	private static final String FILE_NAME = "config.xml";
	public static final String PACKAGE_NAME = "com.jujiabao.heartrate";
	public static final String PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME+ "/" + FILE_NAME; // 在手机里存放数据库的位置
	/**
	 * 读取xml文件
	 * @throws Exception
	 */
	public static List<String> parseXML() throws Exception{
		try {
			SAXReader reader = new SAXReader();
			
			Document document = reader.read(new File(PATH));//这步比较耗时
			
			Element root = document.getRootElement();
			
			List<String> configList = new ArrayList<String>();
			
			List<Element> list = root.elements();//获取所有的子标签，存放在List集合中
			//遍历获取每一个标签
			for (Element element : list) {
				//解析名字
				String account = element.elementTextTrim("account");
				String password = element.elementTextTrim("pwd");
				String clear = element.elementTextTrim("clear");
				configList.add(account);
				configList.add(password);
				configList.add(clear);
			}
//			System.out.println(configList);
			Log.i("jujiabao", "config.xml读取完毕！");
			return configList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 新建xml文件
	 */
	public static boolean writeXML(String account,String password,String clear) throws Exception{
		try {
			List<String> mList = new ArrayList<String>();
			mList.add(account);
			mList.add(password);
			mList.add(clear);
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("config");
			Element element = root.addElement("t1");
			for (String string : mList) {
				Element ele1 = element.addElement("account");
				ele1.addText(mList.get(0));
				Element ele2 = element.addElement("pwd");
				ele2.addText(mList.get(1));
				Element ele3 = element.addElement("clear");
				ele3.addText(mList.get(2));
			}	
			
			XMLWriter writer = new XMLWriter();
			writer.setOutputStream(new FileOutputStream(PATH));
			//将文档对象写出
			writer.write(document);
			writer.close();
			Log.i("jujiabao", "config.xml输出完毕！");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	} 
}
