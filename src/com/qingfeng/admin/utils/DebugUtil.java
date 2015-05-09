package com.qingfeng.admin.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.text.TextUtils;

/**
 * 
 * Copyright 2015
 * 
 * @author preparing
 * @date 2015-5-9
 * 
 * 主要是输出一些字符，用于测试，和log相同
 */
public class DebugUtil {
	public static String customTagPrefix = "";

	/**生成Tag,参考自xUtils源代码*/
	private static String generateTag(StackTraceElement caller) {
		String tag = "%s.%s(L:%d)";
		String callerClazzName = caller.getClassName();
		callerClazzName = callerClazzName.substring(callerClazzName
				.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(),
				caller.getLineNumber());
		tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":"
				+ tag;
		return tag;
	}

	/**输出基本的字符流*/
	public static void Println(String content) {
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);

		Println(tag, content);
	}

	public static void Println(String tag, String content) {
		System.out.println(tag + ": " + content);
	}
	
	/**输出网络流*/
	public static void Println(InputStream stream) {
		StackTraceElement caller = getCallerStackTraceElement();
		String tag = generateTag(caller);

		Println(tag, stream);
	}
	
	public static void Println(String tag, InputStream stream) {
		System.out.println(tag + ":");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			StringBuffer str = new StringBuffer();
			int c;
			while ((c = reader.read()) != -1)
				str.append((char) c);
			System.out.println(str.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**	其他函数*/
	public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }
}
