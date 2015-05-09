package com.qingfeng.admin.network;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * 
 * Copyright 2015 
 * @author preparing
 * @date 2015-5-9
 * 
 * 基本的网络连接httplient,用于维护固定的一个连接
 * 
 */
public class CustomerHttpClient {
private final static String TAG = "CustomerHttpClient";
	
	private final static String Charset = HTTP.UTF_8;
	private static HttpClient httpclient;
	 /* 从连接池中取连接的超时时间 */
	private static int manager_timeout = 1000;
	/* 连接超时 */
	private static int connection_timeout = 2000;
	/* 设置Socket超时   */
	private static int  so_timeout = 4000;
	
	public static synchronized HttpClient getHttpClient(){
		if(httpclient == null){
			
			Log.d(TAG,"httpclient is create");
			
			HttpParams params =new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,
            		Charset);
            HttpProtocolParams.setUseExpectContinue(params, true);
           /* HttpProtocolParams
                    .setUserAgent(
                            params,
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                    +"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");*/
            // 超时设置
            ConnManagerParams.setTimeout(params, manager_timeout);
            HttpConnectionParams.setConnectionTimeout(params, connection_timeout);
            HttpConnectionParams.setSoTimeout(params, so_timeout);
            
            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg =new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(
                    params, schReg);
            httpclient =new DefaultHttpClient(conMgr, params);
		}
		
		return httpclient;
	}
	
	/*设置超时时间*/
	public static synchronized void setConnectionTimeout(int con_timeout){
		setTimeout(con_timeout,manager_timeout,so_timeout);
	}
	
	public static synchronized void setManagerTimeout(int man_timeout){
		setTimeout(connection_timeout,man_timeout,so_timeout);
	}
	
	public static synchronized void setSoTimeout(int s_timeout){
		setTimeout(connection_timeout,manager_timeout,s_timeout);
	}
	
	public static synchronized void setTimeout(int con_timeout, int man_timeout, int s_timeout){
		if(con_timeout > 0){
			connection_timeout = con_timeout;
		}
		if(man_timeout > 0){
			manager_timeout = man_timeout;
		}
		if(s_timeout > 0){
			so_timeout = s_timeout;
		}
	}
}
