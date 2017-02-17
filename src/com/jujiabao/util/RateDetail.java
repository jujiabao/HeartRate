package com.jujiabao.util;
/**
 * 存放heart_rate_main中的所有字段
 * @author Hello.Ju
 *
 */
public class RateDetail {
	private String user_id;
	private String name;
	private String create_time;
	private String rate;
	private String is_average;
	
	public RateDetail() {

	}

	public RateDetail(String user_id, String name, String create_time,
			String rate, String is_average) {
		super();
		this.user_id = user_id;
		this.name = name;
		this.create_time = create_time;
		this.rate = rate;
		this.is_average = is_average;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getIs_average() {
		return is_average;
	}

	public void setIs_average(String is_average) {
		this.is_average = is_average;
	}
	@Override
	public String toString() {
		return user_id + "," + name + "," + create_time + "," + rate + ","
				+ is_average;
	}
}
