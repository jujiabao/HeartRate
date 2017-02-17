package com.jujiabao.client2server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.os.Environment;
import android.util.Log;

public class Client2Server {
	private static Socket socket;
	private String serverHost = "192.168.155.1";
	private int serverPort = 8088;

	private static final String FILE_NAME = "log.txt";
	public static final String PACKAGE_NAME = "com.jujiabao.heartrate";
	public static final String FILE = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME + "/" + FILE_NAME; // 在手机里存放数据库的位置

	public static final String ID_FILE = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME + "/" + "last_id.txt";

	private File logFile = new File(FILE);
	private File idFile = new File(ID_FILE);

	public Client2Server() {
		try {
			Log.i("jujiabao", "正在连接服务端...");
			socket = new Socket(serverHost, serverPort);// localhost:连接本机的8088端口
			InetAddress address = socket.getInetAddress();
			String ha = address.getHostAddress();
			int port = socket.getPort();
			System.out.println("服务器地址：" + ha + ":" + port);
			Log.i("jujiabao", "连接服务端成功！");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送日志给服务器
	 */
	public boolean sendLog() {
		BufferedReader br = null;
		try {
			if (!logFile.exists() || logFile.length() == 0) {
				System.out.println("logs文件不存在");
				return false;
			}
			// 读取日志文件到集合中
			List<String> logList = LogUtil.readLog();

			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF-8"));

			// 发送字符串给服务端
			for (String log : logList) {
				pw.println(log);
			}
			// 客户端over
			pw.println("OVER");
			pw.flush();

			// 读取服务器发送过来的信息
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			String response = br.readLine();
			if ("OK".equals(response)) {
				// file.delete();
				br.close();
				return true;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Finally");
		}
		return true;
	}
}
