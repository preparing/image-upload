package com.qingfeng.admin.ui;

import com.qingfeng.admin.bean.NetworkInfo;

/**
 * 用于记录获取网络数据成功后的UI操作
 * Copyright 2015 
 * @author preparing
 * @date 2015-5-9
 */
public interface Networkresult {
	/**只标志一个页面下不同网络请求类型*/
	public abstract void getPostSuccess(int code);
	
	/** 返回网络的请求结果*/
	public abstract void getPostSuccess(int code,NetworkInfo info);
}
