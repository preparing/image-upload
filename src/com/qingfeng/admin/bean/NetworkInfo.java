package com.qingfeng.admin.bean;

import java.io.Serializable;
/**
 * 所有网络连接的父类，所有用于存储网络获取到的信息全部从这边继承
 * 
 * Copyright 2015 
 * @author preparing
 * @date 2015-5-9
 */
public class NetworkInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	public int Code = -1;
	public String Message;
	
	public NetworkInfo() {
		super();
	}
	
	public NetworkInfo(int code, String message) {
		super();
		Code = code;
		Message = message;
	}
	
	public int getCode() {
		return Code;
	}
	public void setCode(int code) {
		Code = code;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
}
