package com.qingfeng.admin.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
/**
 * 主要是在本地创建存储图片的文件
 * 
 * Copyright 2015 
 * @author preparing
 * @date 2015-5-10
 */

public class FileInformation  {

	private static String path = Environment.getExternalStorageDirectory().getPath();
	public static final String picturepath = path+"/qingfeng/picture";
	public static File file;
	public static String current_picturepath = null;
	
	// 创建一个存储图片的文件
	public static File picturefilecreate(String title, String attribute) {
		File file = null;
		// 没有SD卡，使用默认的
		try {
			if (!hasSdcard())
				file = File.createTempFile(title, attribute);
			else if (isFileExit(picturepath)) {
				file = File.createTempFile(title, attribute,
						FileInformation.file);
			} else {
				file = File.createTempFile(title, attribute);
			}
		} catch (IOException e) {
			System.out.println("In Create File:");
			e.printStackTrace();
		}
		return file;
	}

	// 存储bitmap型的图片文件到本地
	public static void SaveBitmap(Bitmap bitmap)
			throws IOException {
		System.out.println("开始创建图片文件");
		//第一个参数为开头部分，后面为格式，比如：image_212367.jpg
		File file = picturefilecreate("image_", ".jpg");
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			//图片压缩
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
			try {
				fOut.flush();
				// 如果写入成功，就记住文件名
				current_picturepath = file.getAbsolutePath();
				//InsertBitmap(current_picturepath, context);
			} catch (IOException e) {
				e.printStackTrace();
				current_picturepath = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			current_picturepath = null;
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap getBitmap(String path) {
		Bitmap bitmap = null;
		if (path == null) {
			return null;
		}
		try {
			bitmap = BitmapFactory.decodeFile(path);
			System.out.println("获取到的图片的长度为:" + bitmap.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	
	// 判断SD卡是否存在
	public static boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	// 判断文件是否存在
	public static boolean isFileExit(String filepath) {
		try {
			file = new File(filepath);
			if (!file.exists()) {
				file.mkdirs();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void clear() {
		current_picturepath = null;
	}

}

