package com.jujiabao.login;

import java.util.Random;
/**
 * 随机生成20位用户编码，区分用户账号
 * @author Hello.Ju
 *
 */
public class UserID {

	public static StringBuffer user_id = null;
	private static StringBuffer stringBuffer = new StringBuffer();
	
	public static StringBuffer user_id() {
		
		Random random = new Random();
		
		for (int i = 0; i < 19; i++) {
			String temp = null;
			temp = ""+random.nextInt(10);//强制转化为String类型，每次生成1个随机数，范围0~9。
			user_id = stringBuffer.append(temp);//随机往后增加
			if (i == 0 && "0".equals(user_id.toString())) {
				i = -1;
				user_id.setLength(0);;
			}
		}
		return user_id;
	}
}
