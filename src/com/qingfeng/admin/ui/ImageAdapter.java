package com.qingfeng.admin.ui;

import java.util.HashMap;

import com.qingfeng.admin.R;
import com.qingfeng.admin.utils.DebugUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;


/**
 * 用于装载Gallery的图片
 * 
 * Copyright 2015 
 * @author preparing
 * @date 2015-5-9
 */
public class ImageAdapter extends BaseAdapter {
	private HashMap<Integer, ImageView> mViewMap;
	private Context mContext;
	private int mCount;// 一共多少个item
	private Bitmap[] resIds ;

	public ImageAdapter(Context context, Bitmap[] res) {
		mContext = context;
		resIds = res;
		mCount = resIds.length;
		mViewMap = new HashMap<Integer, ImageView>(mCount);
	}

	public int getCount() {
		return mCount;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageview = mViewMap.get(position % mCount);
		if (imageview == null) {
			imageview = new ImageView(mContext);
			imageview.setImageBitmap(resIds[position % resIds.length]);
			imageview.setScaleType(ImageView.ScaleType.FIT_XY);
			imageview.setLayoutParams(new Gallery.LayoutParams(400, 300));
			imageview.setBackgroundResource(R.drawable.rectangle_border);
		}
		return imageview;
	}
}