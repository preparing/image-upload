package com.qingfeng.admin.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;


public class HttpUtils {
	private final String TAG = "HttpUtils";
	
	//服务器地址配置
	public static String serverAddress = "10.60.136.39";
	public static String serverPort = "80";
	
	private HttpClient httpClient;
	
	//参数配置
    private final static int DEFAULT_RETRY_TIMES = 3;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
	
	
//	设置服务器请求地址和端口
	public void setHostAddress(String address){
		setHost(address, null);
	}
	
	public void setHostPort(String port){
		setHost(null,port);
	}
	
	public void setHost(String address, String port) {
		if (address != null) {
			serverAddress = address;
		}
		if (port != null) {
			serverPort = port;
		}
	}
	
	public HttpUtils(){
		httpClient = CustomerHttpClient.getHttpClient();
		((DefaultHttpClient)httpClient).setHttpRequestRetryHandler(new RetryHandler(DEFAULT_RETRY_TIMES));

		//以下两个设置正常情况下用不到的，主要是GZIP压缩
		((DefaultHttpClient)httpClient).addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(org.apache.http.HttpRequest httpRequest, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
                if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

		((DefaultHttpClient)httpClient).addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase("gzip")) {
                            return;
                        }
                    }
                }
            }
        });
	}
	
	/** 向服务器发送Post请求 */
	public InputStream post(String page, String data, String headerType)
			throws URISyntaxException, ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost();
		httpPost.setHeader("Content-Type", headerType);

		String url = "http://" + serverAddress + page;

		// just for test;
		System.out.println("The url is " + url);

		httpPost.setURI(new URI(url));

//		addCookieToHeader(httpPost);

		StringEntity entity = new StringEntity(data);
		httpPost.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPost);

		Header hdr = response.getFirstHeader("Set-Cookie");

//		getCookieFromHeader(hdr);
		InputStream ins = response.getEntity().getContent();
		return ins;
	}
	
	/**Post的路径有端口号的情况*/
	public InputStream postWithPort(String page, String data, String headerType)
			throws URISyntaxException, ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost();
		httpPost.setHeader("Content-Type", headerType);

		String url = "http://" + serverAddress + ":" + serverPort + page;

		// just for test;
		System.out.println("The url is " + url);

		httpPost.setURI(new URI(url));

		/*addCookieToHeader(httpPost);*/

		StringEntity entity = new StringEntity(data);
		httpPost.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPost);

		Header hdr = response.getFirstHeader("Set-Cookie");

		/*getCookieFromHeader(hdr);*/
		InputStream ins = response.getEntity().getContent();
		return ins;
	}
}
