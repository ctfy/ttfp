package com.ttfp.gameparts;

import java.util.Random;

import android.graphics.Bitmap;

import com.ttfp.GameAcitivy;

public class Car {
	public Car(JiaoTong jiaoTong) {
		this.mJiaoTong = jiaoTong;

		Random r = new Random(System.currentTimeMillis());
		int i = r.nextInt(453465) % 7;
		switch (i) {
		case 1:
			mImage = mBitmapLib.car1;
			break;
		case 2:
			mImage = mBitmapLib.car2;
			break;
		case 3:
			mImage = mBitmapLib.car3;
			break;
		case 4:
			mImage = mBitmapLib.car4;
			break;
		case 5:
			mImage = mBitmapLib.car5;
			break;
		default:
			mImage = mBitmapLib.car2;
		}
		mSpeed = r.nextInt(10) + 5;
		mX = -300;
		mY = -120;
		mWidth = 200;
		mHeight = 100;
	}

	private JiaoTong mJiaoTong;

	/** 游戏图形库 */
	private BitmapLib mBitmapLib = BitmapLib.createBitmapLib(null);

	/** 这辆车的图片 */
	public Bitmap mImage;
	/** 这辆车形式的速度, 正值表示往右，负值表示往左 */
	public int mSpeed;
	/** 当前在x坐标的位置 */
	public int mX;
	/** 当前在y坐标的位置 */
	public int mY;
	/** 此车在屏幕上占的宽度 */
	public int mWidth;
	/** 此车在屏幕上占得高度 */
	public int mHeight;

	// 计算新位置
	public void update() {
		if (mSpeed > 0 && mX > GameAcitivy.SCREEN_WIDTH) {
			mJiaoTong.getCars().remove(this);
		} else if (mSpeed < 0 && mX < (0 - mWidth)) {
			mJiaoTong.getCars().remove(this);
		} else {
			this.mX += mSpeed;
		}
	}
}
