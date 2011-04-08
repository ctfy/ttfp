package com.ttfp.gameparts;

import android.graphics.Bitmap;

public 
abstract class Person {
	private Bitmap mImage;
	public int mX;
	public int mY;
	public int mWidth;
	public int mHeight;
	
	public abstract void update ();

	public void setImage(Bitmap mImage) {
		this.mImage = mImage;
	}

	public Bitmap getImage() {
		return mImage;
	}
}