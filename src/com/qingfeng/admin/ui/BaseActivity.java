package com.qingfeng.admin.ui;

import com.qingfeng.admin.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BaseActivity extends Activity{
	/** 主要用来判断当前Activity是否在前端 */
	public static boolean isFront = false;

	public static synchronized void setisFront(boolean bool) {
		isFront = bool;
	}

	public static synchronized boolean getidFront() {
		return isFront;
	}

	/** 标题栏相关信息 */
	private ActionBar actionBar;
	private TextView titleText;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		View addView = getLayoutInflater().inflate(R.layout.titlebar_custom,
				null);
		actionBar.setCustomView(addView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		titleText = (TextView) findViewById(R.id.titlebar_text_title);
	}

	public ActionBar getMyActionBar() {
		return this.actionBar;
	}
	
	/**获取XML数据管理实例*/
	/*public Preference getPreference(){
		return ((SmartApplication)getApplication()).getPrefence();
	}*/



	/** 设置标题文字 */
	public void setTitle(String title) {
		if (titleText != null) {
			titleText.setText(title);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		setisFront(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		setisFront(false);
	}

	public String getResourceString(int id) {
		return getResources().getString(id);
	}

	public Drawable getResourceDrawable(int id) {
		return getResources().getDrawable(id);
	}
}
