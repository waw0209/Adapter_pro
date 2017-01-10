package com.example.dohee.adapter_pro;

import android.graphics.drawable.Drawable;

public class TextItem {

	private String[] mData;

	public TextItem(String obj01, String obj02) {
		
		mData = new String[2];
		mData[0] = obj01;
		mData[1] = obj02;
	}

	public TextItem(String obj01, String obj02, String obj03, String obj04) {

		mData = new String[4];
		mData[0] = obj01;
		mData[1] = obj02;
		mData[2] = obj03;
		mData[3] = obj04;
	}

	public String[] getData() {
		return mData;
	}

	public String getData(int index) {
		if (mData == null || index >= mData.length) {
			return null;
		}
		
		return mData[index];
	}

	public void setData(String[] obj) {
		mData = obj;
	}

}
