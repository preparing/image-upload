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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import com.qingfeng.admin.utils.DebugUtil;


public class HttpUtils {
	private final String TAG = "HttpUtils";
	
	//服务器地址配置
	public String serverAddress = NetworkConfig.serverAddress;
	public String serverPort = NetworkConfig.serverPort;
	
	private HttpClient httpClient;
	
	private String headerType = "application/x-www-form-urlencoded";
	
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
	
	//设置请求头格式
	public void setHeadType(String type){
		headerType = type;
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
	public InputStream post(String page, String data)
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
	public InputStream postWithPort(String page, String data)
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
	
	/** 用get方式获取网络图片*/
	public InputStream getImageInputStream(String page) throws ClientProtocolException, IOException  {
		String url = "http://" + serverAddress + ":" + serverPort + page;

		DebugUtil.Println("The url is ",url);

		HttpGet httpget = new HttpGet(url);
		HttpResponse response = null;
		response = httpClient.execute(httpget);
		Header contentType = response.getFirstHeader("Content-Type");

		if(contentType != null) {
			System.out.println("the type is: " + contentType.getValue());

			if(contentType.getValue().equals("text/xml")) {
				return null;
			} else {
				InputStream stream = response.getEntity().getContent();
				return stream;
			}
		}
		return null;
	}
	
	/** 向服务器发送Put请求 */
	public InputStream put(String page, String data)
			throws URISyntaxException, ClientProtocolException, IOException {
		HttpPut httpPut = new HttpPut();
		httpPut.setHeader("Content-Type", headerType);

		String url = "http://" + serverAddress + page;

		// just for test;
		System.out.println("The url is " + url);

		httpPut.setURI(new URI(url));

//		addCookieToHeader(httpPost);

		StringEntity entity = new StringEntity(data);
		httpPut.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPut);

		Header hdr = response.getFirstHeader("Set-Cookie");

//		getCookieFromHeader(hdr);
		InputStream ins = response.getEntity().getContent();
		return ins;
	}
	
}
