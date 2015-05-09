package com.qingfeng.admin.ui;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.qingfeng.admin.network.HttpUtils;
import com.qingfeng.admin.utils.DataUtil;
import com.qingfeng.admin.utils.DebugUtil;

public class LoginTask extends AsyncTask<String,String,Bitmap> {
	private final String TAG = "LoginTask";

	private Networkresult mResult;
	private int mType;

	public LoginTask(Networkresult result,int type) {
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
		String user = params[1];
		String pass = params[2];
		String verif = params[3];
		InputStream stream  =null;
		Bitmap bitmap = null;
		try {
			HttpUtils http = new HttpUtils();
			String postData = null;

			postData = DataUtil.fomatSimplePostData("name", user,
					"password", pass, "verifyCode", verif);
			
			stream = http.post(page, postData);
			DebugUtil.Println(stream);
			/*bitmap = BitmapFactory.decodeStream(stream);*/
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
