package com.qingfeng.admin.ui;

import com.qingfeng.admin.bean.AdminInfo;

import android.graphics.Bitmap;

public class UIDataContainer {
	
	/**验证码图片*/
	private static Bitmap verifyCode = null;
	
	public static synchronized Bitmap getVerify(){
		return verifyCode;
	}
	public static synchronized void setVerify(Bitmap verify){
		verifyCode = verify;
	}
	
	/**管理员登陆信息*/
	private static AdminInfo admin;

	public static synchronized AdminInfo getAdmin() {
		return admin;
	}
	public static synchronized void setAdmin(AdminInfo admin) {
		UIDataContainer.admin = admin;
	}
	
}
