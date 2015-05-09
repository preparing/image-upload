package com.qingfeng.admin.utils;

import java.net.URLEncoder;

/**
 * 对数据格式的处理，比如对发送的数据的处理
 * 
 * Copyright 2015 
 * @author preparing
 * @date 2015-5-9
 */
public class DataUtil {

	/** 将数据封装成可以发送的格式 */
	public static String fomatSimplePostData(String... args) {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < args.length; i += 2) {
			try {
				str.append(URLEncoder.encode(args[i], "utf-8"));
				str.append('=');
				str.append(URLEncoder.encode(args[i + 1], "utf-8"));
				str.append('&');
			} catch (Exception e) {
				DebugUtil.Println("error", "URL Encode error.");
			}
		}
		str.deleteCharAt(str.length() - 1);

		DebugUtil.Println("PostData", str.toString());

		return str.toString();
	}

	/** 字符格式转换 */
	public static String toUtf8String(String str) {
		String s = null;
		try {
			s = new String(str.getBytes("UTF-8"), "UTF-8");
		} catch (Exception e) {
			DebugUtil.Println("In toUtf8String...", e.toString());

			return str;
		}
		return s;
	}

}