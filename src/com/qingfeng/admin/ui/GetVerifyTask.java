package com.qingfeng.admin.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.qingfeng.admin.network.HttpUtils;
import com.qingfeng.admin.utils.DebugUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 获取验证码图片的线程
 * 
 * Copyright 2015 
 * @author preparing
 * @date 2015-5-9
 */
public class GetVerifyTask extends AsyncTask<String,String,Bitmap> {
	private final String TAG = "GetVerifyTask";

	private Networkresult mResult;
	private int mType;

	public GetVerifyTask(Networkresult result,int type) {
		this.mResult = result;
		this.mType = type;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		String page = params[0];
		InputStream stream  =null;
		Bitmap bitmap = null;
		try {
			HttpUtils http = new HttpUtils();
			stream = http.getImageInputStream(page);
//			DebugUtil.Println(stream);
			bitmap = BitmapFactory.decodeStream(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap s) {
		Log.d(TAG, "onPostExecute");
		UIDataContainer.setVerify(s);
		// 执行Post结束后相关的操作
		mResult.getPostSuccess(mType);
	}

}

