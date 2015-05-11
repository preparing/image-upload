package com.qingfeng.admin.ui;

import com.qingfeng.admin.R;
import com.qingfeng.admin.bean.AdminInfo;
import com.qingfeng.admin.bean.NetworkInfo;
import com.qingfeng.admin.network.NetworkConfig;
import com.qingfeng.admin.utils.DebugUtil;
import com.qingfeng.admin.widget.ClearEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity  implements Networkresult{
	public Button login;
	private ClearEditText username;
	private ClearEditText password;
	private ClearEditText verifyCode;
	private ImageView verify;
	
	private final int YTPE_VERIFY = 0x01;
	private final int YTPE_LOGIN = 0x02;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle("清风管理员系统");
		
		InitView();
		
		getVerifyCode();
	}
	
	public void InitView() {
		username = (ClearEditText) findViewById(R.id.usernameEdit);
		password = (ClearEditText) findViewById(R.id.passwordEdit);
		verifyCode = (ClearEditText) findViewById(R.id.verifycode);
		verify = (ImageView)findViewById(R.id.verify);
		password.setPassword(true);
		login = (Button) findViewById(R.id.login_button);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String user = username.getText().toString().trim();
				String pass = password.getText().toString().trim();
				String verif = verifyCode.getText().toString().trim();
				if (user.equals("") || pass.equals("") || verif.equals("")) {
					Toast.makeText(LoginActivity.this, "请填写完整信息",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				LoginTask task = new LoginTask(LoginActivity.this,YTPE_LOGIN);
				task.execute(NetworkConfig.loginPath, user,pass,verif);
			}

		});
		
		verify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getVerifyCode();
			}
		});
	}
	
	/**获取验证码图片*/
	public void getVerifyCode(){
		GetVerifyTask task = new GetVerifyTask(LoginActivity.this,YTPE_VERIFY);
		task.execute(NetworkConfig.verifyPath);
	}
	
	/**获取网络结束后最好都调用这个函数*/
	@Override
	public void getPostSuccess(int code) {
		if(code == YTPE_VERIFY){
			if(UIDataContainer.getVerify() != null){
				verify.setImageBitmap(UIDataContainer.getVerify());
			}else{
				DebugUtil.Println("get image","null");
			}
		}else if(code == YTPE_LOGIN){
			if(UIDataContainer.getAdmin() != null){
				AdminInfo admin = UIDataContainer.getAdmin();
				if(admin.getCode() == 0){
					Toast.makeText(getApplicationContext(), "登陆成功:"+admin.getMessage(), Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(LoginActivity.this,PhotoActivity.class);
					startActivity(intent);
					LoginActivity.this.finish();
				}else{
					Toast.makeText(getApplicationContext(), "用户名或者密码错误", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void getPostSuccess(int code, NetworkInfo info) {
		// TODO Auto-generated method stub
		
	}
}
