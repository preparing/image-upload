package com.qingfeng.admin.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * JSON解析方面的相关函数，暂时没有使用GSON
 * 
 * Copyright 2015 
 * @author preparing
 * @date 2015-5-9
 */
public class JSONUtil {
	/**从InputStrem中解析出一个JsonObject*/
	public static JSONObject ParseJSON(InputStream stream){
		JSONObject json = null;
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					stream));
			StringBuilder str = new StringBuilder();
			for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
				str.append(s);
			}
			buffer.close();
			json = new JSONObject(str.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return json;
	}
	
	/**从InputStrem中解析出一个JSONArray*/
	public static JSONArray ParseJSONArray(InputStream stream){
		JSONArray json = null;
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					stream));
			StringBuilder str = new StringBuilder();
			for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
				str.append(s);
			}
			buffer.close();
			json = new JSONArray(str.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return json;
	}
}
