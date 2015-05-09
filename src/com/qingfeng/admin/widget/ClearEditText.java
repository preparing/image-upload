package com.qingfeng.admin.widget;

import com.qingfeng.admin.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


/**
 * 可以清除全部输入的数值，本质上还是一个EditText
 * 
 * Copyright 2015 
 * @author preparing
 * @date 2015-5-9
 */
public class ClearEditText extends LinearLayout implements TextWatcher{
	private final static String TAG = "ClearEditText";
	private Context mContext;
	private Button mDelete;
	private EditText mEditText;
	
	public  ClearEditText(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public ClearEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init() {
		View mView = LayoutInflater.from(mContext).inflate(
				R.layout.widget_clearedittext, this, true);
		mView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				System.out.println("mView click");
			}
		});
		mDelete = (Button)mView.findViewById(R.id.clear_delete);
		mEditText = (EditText)mView.findViewById(R.id.clear_edittext);
		mEditText.addTextChangedListener(this); 
		if(mEditText.getText().toString().trim().equals("")){
			mDelete.setVisibility(View.GONE);
		}else{
			mDelete.setVisibility(View.VISIBLE);
		}
		mDelete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(mEditText != null){
					mEditText.setText("");
				}
			}
		});
	}

	public String getText(){
		if(mEditText!= null){
			return mEditText.getText().toString().trim();
		}else{
			return "";
		}
	}
	
	public void setText(String args){
		if(mEditText!= null){
			mEditText.setText(args);
		}
	}
	
	/**设置明文显示还是密文显示*/
	public void setPassword(boolean bool){
		if(mEditText != null){
			if(bool)
				mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
			else{
				mEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			}
		}
	}
	
	/**一下三个函数为TextWatcher中继承的函数*/
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(mEditText.getText().toString().trim().length()>0){
			mDelete.setVisibility(View.VISIBLE);
		}else{
			mDelete.setVisibility(View.GONE);
		}
	}
}	
