package com.feiniu.member.dto;

/**
 * 返回结果
 *
 */
public class Result {

	/**
	 * 状态码
	 */
	private int code;

	/**
	 * 信息
	 */
	private String msg;

	/**
	 * 数据
	 */
	private Object data;
	
	
	public Result() {
		
	}

	public Result(Integer code) {
		this.code = code;
	}

	public Result(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Result(Integer code, Object data, String msg) {
		this.code = code;
		this.data = data;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String toString() {
		return "code:" + code + "\tmsg:" + msg + "\tdata:" + data;
	}
}
