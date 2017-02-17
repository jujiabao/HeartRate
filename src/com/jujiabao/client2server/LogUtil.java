package com.jujiabao.client2server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class LogUtil {
	public static final String PACKAGE_NAME = "com.jujiabao.heartrate";
	public static final String FILE = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME + "/" + "log.txt";

	public static List<String> readLog() throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					FILE), "UTF-8"));
			List<String> list = new ArrayList<String>();
			String line = null;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

}
