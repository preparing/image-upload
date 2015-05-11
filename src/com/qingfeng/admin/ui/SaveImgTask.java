package com.qingfeng.admin.ui;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.os.AsyncTask;

import com.qingfeng.admin.bean.NetworkInfo;
import com.qingfeng.admin.network.HttpUtils;
import com.qingfeng.admin.utils.DataUtil;
import com.qingfeng.admin.utils.DebugUtil;
import com.qingfeng.admin.utils.JSONUtil;

public class SaveImgTask extends AsyncTask<String, String, NetworkInfo> {
	private final String TAG = "SaveImgTask ";

	private Networkresult mResult;
	private int mType;

	public SaveImgTask(Networkresult result, int type) {
		this.mResult = result;
		this.mType = type;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected NetworkInfo doInBackground(String... params) {
		String page = params[0];
		InputStream stream = null;
		NetworkInfo info = new NetworkInfo();
		try {
			HttpUtils http = new HttpUtils();
			String postData = null;

			postData = "";

			stream = http.put(page, postData);
			/*DebugUtil.Println(stream); */
			
			JSONObject object = JSONUtil.ParseJSON(stream);
			if (object != null) {
				try {
					if(object.has("Code")){
						info.setCode(object.getInt("Code"));
					}
					if(object.has("Msg")){
						info.setMessage(object.getString("Msg"));
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
		return info;
	}

	@Override
	protected void onPostExecute(NetworkInfo info) {
		// 执行Post结束后相关的操作
		mResult.getPostSuccess(mType,info);

	}
}