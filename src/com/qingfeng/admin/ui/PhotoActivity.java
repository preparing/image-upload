package com.qingfeng.admin.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ViewSwitcher.ViewFactory;

import com.qingfeng.admin.R;
import com.qingfeng.admin.bean.AdminInfo;
import com.qingfeng.admin.bean.NetworkInfo;
import com.qingfeng.admin.network.NetworkConfig;
import com.qingfeng.admin.utils.DebugUtil;
import com.qingfeng.admin.widget.ClearEditText;

/**
 * 上传图片的界面，主要是可以拍摄多张图片一起上传
 * 
 * Copyright 2015
 * 
 * @author preparing
 * @date 2015-5-9
 */
public class PhotoActivity extends BaseActivity implements Networkresult,
OnItemSelectedListener, ViewFactory, OnItemClickListener {
	public Button save;
	public LinearLayout exit;
	private ClearEditText imageCode;
	private TextView user;
	private TextView gallery_number;

	private Gallery gallery;
	private ImageAdapter imageAdapter;
	private int mCurrentPos = -1;// 当前的item

	
	private Bitmap[] resIds = new Bitmap[5];
	//0表示为拍照，1表示拍照完成，大于2表示需要修改或者覆盖
	private int[] isphoto = new int[resIds.length];
	private boolean[] isupload = new boolean[resIds.length];
	private int current_photo = -1;
	private int upload_photo = -1;

	//网络请求类型
	private final int TYPE_UPLOAD = 0x01;
	private final int TYPE_SAVE = 0x02;
	
	//弹出对话框类型
	private final static int UploadDialog = 0x20;
	private final static int AddDialog = 0x21;
	
	//拍摄图片与裁剪图片
	public final String IMAGE_UNSPECIFIED = "image/*";
	public final int TAKE = 0x10; // 读取图片
	public final int PHOTO = 0x11; // 读取图片
	public final int PHOTOZOOM = 0x12;// 缩放图片
	public Uri currentUri = null;
	public Uri currentCrop = null;
	
	private ProgressDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		setTitle("清风管理员系统");

		InitView();
		
		InitData();

	}

	public void InitView() {
		imageCode = (ClearEditText) findViewById(R.id.codeEdit);
		user = (TextView)findViewById(R.id.user);
		gallery_number = (TextView) findViewById(R.id.gallery_number);
		
		save = (Button) findViewById(R.id.button);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				/** 保存图片*/
				SaveImgTask task = new SaveImgTask(PhotoActivity.this,TYPE_SAVE);
				task.execute(NetworkConfig.savePath);
			}

		});
		
		exit = (LinearLayout)findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				/** 退出*/
				exitConfirm();
			}

		});
		
		gallery = (Gallery)findViewById(R.id.gallery);
		imageAdapter = new ImageAdapter(PhotoActivity.this, resIds);
		gallery.setAdapter(imageAdapter);
		gallery.setOnItemSelectedListener(this);
		gallery.setSelection(0);// 设置一加载Activity就显示的图片后面
		gallery.setOnItemClickListener(this);
	}

	
	public void InitData(){
		for(int i = 0; i<resIds.length; i++){
			isphoto[i] = 0;
			isupload[i] = false;
			Bitmap bitmap = BitmapFactory.decodeResource (getResources(), R.drawable.add);
			resIds[i] = bitmap;
			imageAdapter.notifyDataSetChanged();
		}
		
		if(UIDataContainer.getAdmin()!= null){
			user.setText(UIDataContainer.getAdmin().getMessage());
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
	}

	// 设置点击Gallery的时候才切换到该图片
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 第一次点击，直接进入添加图片界面
		if (isphoto[position] == 0) {
			gallery_number.setText("正在添加第"+(position+1)+"张图片");
			current_photo = position;
			DialogFragment newFragment = MyDialogFragment
					.newInstance("添加第"+(position+1)+"张图片",AddDialog);
			newFragment.show(getFragmentManager(), "dialog");
		} else {
			// 如果在显示当前图片，再点击，就进入修改图片界面。
			/*if (mCurrentPos == position) {*/
				current_photo = position;
				upload_photo = position;
				DialogFragment newFragment = MyDialogFragment
						.newInstance("修改或上传第"+(position+1)+"张图片",UploadDialog);
				newFragment.show(getFragmentManager(), "dialog");
				
			/*}*/
			mCurrentPos = position;
//			gallery_number.setText("这是第"+(position+1)+"张图片,再点一次修改或上传");
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public View makeView() {
		ImageView imageView = new ImageView(PhotoActivity.this);
		imageView.setBackgroundColor(0xFF000000);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return imageView;
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 读取图片的返回结果
		if (requestCode == TAKE) {
			if (resultCode == RESULT_OK) {
			    try {
			        if(currentUri != null){
			        	startPhotoZoom(currentUri);
			        }else{
			        	Toast.makeText(PhotoActivity.this, "从相机中获取图片失败=====",
								Toast.LENGTH_LONG).show();
			        }
			    } catch (Exception e) {
			    	Toast.makeText(PhotoActivity.this, "从相机中获取图片失败=====",
							Toast.LENGTH_LONG).show();
			        e.printStackTrace();
			    }
			}else{
				Toast.makeText(PhotoActivity.this, "操作已经取消",
						Toast.LENGTH_LONG).show();
			}
		}

		// 读取图片的返回结果
		if (requestCode == PHOTO) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				startPhotoZoom(uri);
			}else{
				Toast.makeText(PhotoActivity.this, "操作已经取消",
						Toast.LENGTH_LONG).show();
			}
		}
		// 处理图片返回
		else if (requestCode == PHOTOZOOM) {
			if (resultCode == RESULT_OK) {

				Bitmap bitmap = null;
				try {
					if (currentCrop != null) {
						bitmap = BitmapFactory
								.decodeStream(getContentResolver()
										.openInputStream(currentCrop));
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				if (bitmap != null) {
					if (current_photo >= 0) {
						if (resIds[current_photo] != null) {
							try {
								resIds[current_photo].recycle();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						resIds[current_photo] = bitmap;
						imageAdapter.notifyDataSetChanged();
						if(isphoto[current_photo] == 0){
							isphoto[current_photo] = 1;
						}
						isupload[current_photo] = false;
						bitmapToBase64(bitmap);
					}
				} else {
					Toast.makeText(PhotoActivity.this, "没有图片",
							Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(PhotoActivity.this, "操作已经取消",
						Toast.LENGTH_LONG).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/** 缩放并裁剪图片 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 3);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 800);
		intent.putExtra("outputY", 600);
		intent.putExtra("scale", true);
		
		File file = FileInformation.picturefilecreate("image_", ".jpg");
		currentCrop = Uri.fromFile(file);
		
		intent.putExtra(MediaStore.EXTRA_OUTPUT, currentCrop);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, PHOTOZOOM);
	}
	
	/**
	 * 改成base64为编码
	 */
	public String bitmapToBase64(Bitmap bitmap) {
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	
	/**
	 * dialog class
	 * 弹出对话框判断选择哪种图片添加方式
	 * */
	public static class MyDialogFragment extends DialogFragment {
		public static MyDialogFragment newInstance(String title,int type) {
			MyDialogFragment frag = new MyDialogFragment();
			Bundle args = new Bundle();
			args.putString("title", title);
			args.putInt("type", type);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String title = getArguments().getString("title");
			int type = getArguments().getInt("type");
			if(type == AddDialog){
				Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(title);
				String[] list = { "拍照", "从相册中选择" };
				builder.setSingleChoiceItems(list, -1, avatarListener);
				return builder.create();
			}else if(type == UploadDialog){
				Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(title);
				String[] list = {"上传图片", "重新拍照", "重新选择"};
				builder.setSingleChoiceItems(list, -1, uploadListener);
				return builder.create();
			}
			return null;
			
		}

		// avatarDialog click
		private DialogInterface.OnClickListener avatarListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((PhotoActivity) getActivity()).avatarClick(which);
				dialog.dismiss();
			}
		};
		
		// avatarDialog click
		private DialogInterface.OnClickListener uploadListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which > 0){
					((PhotoActivity) getActivity()).avatarClick(which-1);
				}else if(which == 0){
					((PhotoActivity) getActivity()).uploadClick();
				}
				dialog.dismiss();
			}
		};
	}
	
	/**点击拍照和从相册中选取分别对应的图片*/
	public void avatarClick(int position) {
		// Toast.makeText(this,"位置为："+position, Toast.LENGTH_SHORT).show();
		if (position == 0) {
			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			File file = FileInformation.picturefilecreate("image_", ".jpg");
			Uri mUri = Uri.fromFile(file);
			currentUri = mUri;
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, TAKE);
		} else if (position == 1) {
			Intent intent = new Intent();
			intent.setType("image/*");
			/* 使用Intent.ACTION_GET_CONTENT这个Action */
			intent.setAction(Intent.ACTION_GET_CONTENT);
			/* 取得相片后返回本画面 */
			startActivityForResult(intent, PHOTO);
		}
	}
	
	/** 点击了弹出界面的上传按钮图片*/
	public void uploadClick(){
		if(upload_photo <0){
			Toast.makeText(getApplicationContext(), "请选选择图片", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(resIds[upload_photo] == null){
			Toast.makeText(getApplicationContext(), "请选选择图片", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(isupload[upload_photo]){
			Toast.makeText(getApplicationContext(), "改图片已经上传过了，不需要再次上传", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String mid_id = imageCode.getText().toString().trim();
		if(mid_id.equals("")){
			Toast.makeText(getApplicationContext(), "请先填写中间码", Toast.LENGTH_SHORT).show();
			return;
		}
		
		
		String img = bitmapToBase64(resIds[upload_photo]);
		
		UploadImgTask task = new UploadImgTask(PhotoActivity.this,TYPE_UPLOAD);
		
		if(isphoto[upload_photo] == 1){
			task.execute(NetworkConfig.uploadPath,mid_id,String.valueOf(0),img);
			mDialog = new ProgressDialog(getApplicationContext());
			mDialog.setCancelable(true);
			mDialog = ProgressDialog.show(PhotoActivity.this, "上传中...", "上传第"+(upload_photo+1)+"张图片中，请等待...", true);
		}else{
			task.execute(NetworkConfig.uploadPath,mid_id,String.valueOf(upload_photo),img);
			mDialog = new ProgressDialog(getApplicationContext());
			mDialog.setCancelable(true);
			mDialog = ProgressDialog.show(PhotoActivity.this, "上传中...", "覆盖第"+(upload_photo+1)+"张图片中，请等待...", true);
		}
	}
	
	/** 获取网络结束后最好都调用这个函数 */
	@Override
	public void getPostSuccess(int code) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getPostSuccess(int code, NetworkInfo info) {
		if (code == TYPE_UPLOAD) {
			mDialog.dismiss();
			if(info == null){
				if(isphoto[upload_photo] == 1){
					Toast.makeText(getApplicationContext(), "上传第"+(upload_photo+1)+"张图片失败", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), "覆盖第"+(upload_photo+1)+"张图片失败", Toast.LENGTH_SHORT).show();
				}
			}else if(info.getCode() == 0){
				if(isphoto[upload_photo] == 1){
					Toast.makeText(getApplicationContext(), "上传第"+(upload_photo+1)+"张图片成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), "覆盖第"+(upload_photo+1)+"张图片成功", Toast.LENGTH_SHORT).show();
				}
				isphoto[upload_photo] = 2;
				isupload[upload_photo] = true;
			}else{
				if(isphoto[upload_photo] == 1){
					Toast.makeText(getApplicationContext(), "上传第"+(upload_photo+1)+"张图片失败："+info.Message, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), "覆盖第"+(upload_photo+1)+"张图片失败："+info.Message, Toast.LENGTH_SHORT).show();
				}
			}
		} else if (code == TYPE_SAVE) {
			if(info == null){
				Toast.makeText(getApplicationContext(), "保存图片失败", Toast.LENGTH_SHORT).show();
			}else if(info.getCode() == 0){
				Toast.makeText(getApplicationContext(), "保存图片成功", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), "保存图片失败:"+info.Message, Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	// 退出时弹出确定对话款
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("In onKeyDown", "Key Down");
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			System.out.println("ExitDialog");
			exitConfirm();
		}
		return false;
	}
	
	public void exitConfirm(){
		AlertDialog.Builder builder = new Builder(PhotoActivity.this);
		builder.setMessage("确定退出？");
		builder.setTitle("提醒");
		builder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						System.exit(0);
					}
				});
		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}