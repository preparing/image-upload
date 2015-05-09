package com.qingfeng.admin.ui;

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
}
