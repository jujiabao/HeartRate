package com.jujiabao.util;

import android.app.Application;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

/**
 * * 退出程序，关闭所有Activity类 *
 * 
 * @author Hello.Ju *
 * */
public class KillActivity extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();

	private static KillActivity instance;

	private KillActivity() {
	}

	// 单例模式中获取唯一的MyApplication实例
	public static KillActivity getInstance() {
		if (null == instance) {
			instance = new KillActivity();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activityList) {
			System.out.println("activity=" + activity + "list=" + activityList);
			activity.finish();
		}
		System.exit(0);

	}

	// 单独关闭CallBack.java
	public void exitCallBack() {
		for (Activity activity : activityList) {
			activityList.get(activityList.size() - 1).finish();
		}

	}
}
