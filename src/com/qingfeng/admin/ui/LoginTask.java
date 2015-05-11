package com.qingfeng.admin.ui;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.os.AsyncTask;

import com.qingfeng.admin.bean.AdminInfo;
import com.qingfeng.admin.network.HttpUtils;
import com.qingfeng.admin.utils.DataUtil;
import com.qingfeng.admin.utils.DebugUtil;
import com.qingfeng.admin.utils.JSONUtil;

public class LoginTask extends AsyncTask<String, String, AdminInfo> {
	private final String TAG = "LoginTask";

	private Networkresult mResult;
	private int mType;

	public LoginTask(Networkresult result, int type) {
		this.mResult = result;
		this.mType = type;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected AdminInfo doInBackground(String... params) {
		String page = params[0];
		String user = params[1];
		String pass = params[2];
		String verif = params[3];
		InputStream stream = null;
		AdminInfo admin = new AdminInfo();
		try {
			HttpUtils http = new HttpUtils();
			String postData = null;

			postData = DataUtil.fomatSimplePostData("name", user, "password",
					pass, "verifyCode", verif);

			stream = http.post(page, postData);
			/* DebugUtil.Println(stream); */
			
			JSONObject object = JSONUtil.ParseJSON(stream);
			if (object != null) {
				try {
					if(object.has("Code")){
						admin.setCode(object.getInt("Code"));
					}
					if(object.has("Msg")){
						admin.setMessage(object.getString("Msg"));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return admin;
	}

	@Override
	protected void onPostExecute(AdminInfo admin) {
		UIDataContainer.setAdmin(admin);
		// 执行Post结束后相关的操作
		mResult.getPostSuccess(mType);

	}
}
