package com.ttfp.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;

import com.ttfp.GameAcitivy;
import com.ttfp.gameparts.BitmapLib;
import com.ttfp.gameparts.Car;
import com.ttfp.gameparts.GameState;
import com.ttfp.gameparts.JiaoTong;
import com.ttfp.gameparts.LeftPerson;
import com.ttfp.gameparts.RightPerson;
import com.ttfp.util.FrameManager;

public class Game extends View {

	public static int WIDTH;
	public static int HEIGHT;

	public static Game mGame;

	/** 游戏中用到的图片的仓库 */
	private BitmapLib mBitmapLib;

	/** 里层背景，楼房、马路、街道 */
	public Bitmap mBackground;
	/** 游戏画面中向左开的那些车辆 */
	public JiaoTong mLeftJiaoTong;
	/** 游戏画面中向右开的那些车辆 */
	public JiaoTong mRightJiaoTong;
	/** 游戏中外面的那层背景，游戏画面下侧能遮挡车辆的透明背景 */
	public Bitmap mBackground2;
	/** 游戏中左侧的人 */
	public LeftPerson mLeftPerson;
	/** 游戏中右侧的人 */
	public RightPerson mRightPerson;
	public GameState mGameState;
	/** 游戏刷新线程针记录 */
	public int mUpdateFrameId = 0;
	
	private Vibrator mVibrator;

	public Game(Context context, AttributeSet attrs) {
		super(context, attrs);
		mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		WIDTH = 547;
		HEIGHT = 397;

		mViewBmp = Bitmap.createBitmap(WIDTH, HEIGHT, Config.ARGB_8888);
		mViewCanvas = new Canvas(mViewBmp);

		mBitmapLib = BitmapLib.createBitmapLib(context);

		mBackground = mBitmapLib.background;

		mLeftJiaoTong = new JiaoTong();
		mRightJiaoTong = new JiaoTong();

		mBackground2 = mBitmapLib.background2;

		mLeftPerson = new LeftPerson(this);
		mRightPerson = new RightPerson(this);

		mGameState = GameState.createGameState(this);

		// 启动进度更新线程
		new Thread(updateRunnable).start();
		// 启动界面重绘线程
		new Thread(drawRunnable).start();
	}

	public void gameOver() {
		mGameState.mIsRun = false;
		mLeftPerson.setImage(mBitmapLib.leftPerson3);
		mRightPerson.setImage(mBitmapLib.rightPerson5);
	}

	public void keyDown() {
		mRightPerson.setPiGoing(true);
		try {
			mVibrator.vibrate(1000);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void keyUp() {
		mVibrator.cancel();
		try {
			mRightPerson.setPiGoing(false);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}

	public Runnable updateRunnable = new Runnable() {
		@Override
		public void run() {
			FrameManager fm = FrameManager.getInstance();
			FrameManager.setRefreshRate(40);
			int oldFrameId = fm.getFrameIndex();
			int frameId = oldFrameId;
			while (mGameState.mIsRun) {
				frameId = fm.getFrameIndex();
				if (frameId > oldFrameId) {
					updateFrame();
					oldFrameId = frameId;
				}

				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void updateFrame() {
			// 更新更新线程针
			Game.this.mUpdateFrameId++;

			// mLeftJiaoTong.update();
			mRightJiaoTong.update();
			// mLeftPerson.update();
			mRightPerson.update();
			mGameState.update();
		}
	};

	public Runnable drawRunnable = new Runnable() {
		@Override
		public void run() {
			while (mGameState.mIsRun) {
				postInvalidate();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	Bitmap mViewBmp;
	Canvas mViewCanvas;

	public void onDraw(Canvas canvas2) {

		mViewCanvas.drawColor(Color.BLACK);
		// 画背景
		mViewCanvas.drawBitmap(mBackground, 0f, 0f, null);
		// 第一排车
		ArrayList<Car> cars = mLeftJiaoTong.getCars();

		Car tmpCar = null;
		for (int i = 0; i < cars.size(); i++) {
			tmpCar = cars.get(i);
			mViewCanvas.drawBitmap(tmpCar.mImage, tmpCar.mX, tmpCar.mY, null);
		}
		// 第二排车
		cars = mRightJiaoTong.getCars();
		for (int i = 0; i < cars.size(); i++) {
			tmpCar = cars.get(i);
			mViewCanvas.drawBitmap(tmpCar.mImage, tmpCar.mX, tmpCar.mY, null);
		}
		// 画第二层背景
		mViewCanvas.drawBitmap(mBackground2, 0f, 0f, null);
		// 画人物1
		mViewCanvas.drawBitmap(mLeftPerson.getImage(), mLeftPerson.mX,
				mLeftPerson.mY, null);
		// 画人物2
		mViewCanvas.drawBitmap(mRightPerson.getImage(), mRightPerson.mX,
				mRightPerson.mY, null);
		// 画游戏状态
		mViewCanvas.drawBitmap(mGameState.getImage(), mGameState.mX,
				mGameState.mY, null);

		Rect tSrc = new Rect(0, 0, WIDTH, HEIGHT);
		Rect tDst = new Rect(0, 0, GameAcitivy.SCREEN_WIDTH,
				GameAcitivy.SCREEN_HEIGHT);
		canvas2.drawBitmap(mViewBmp, tSrc, tDst, null);
	}
}