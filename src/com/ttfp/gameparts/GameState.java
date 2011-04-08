package com.ttfp.gameparts;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

import com.ttfp.views.Game;

public class GameState {
	private Game mGame;
	private static GameState mGameState;

	private Bitmap mImage;
	private Canvas mCanvas;
	private Paint mPaint;

	private GameState(Game game) {
		mGame = game;
		mIsRun = true;// 将游戏状态设置成开
		mY = 300;
		mImage = Bitmap.createBitmap(Game.WIDTH, 100, Config.ARGB_8888);
		mCanvas = new Canvas(mImage);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.RED);
	}

	public int mX;
	public int mY;
	public boolean mIsRun;

	public static GameState createGameState(Game game) {
		if (null == mGameState) {
			mGameState = new GameState(game);
		}
		return mGameState;
	}

	public Bitmap getImage() {
		return mImage;
	}

	public void update() {
		if (mGame.mUpdateFrameId % 10 != 0) {
			return;
		}

		mCanvas.drawColor(Color.GREEN, Mode.CLEAR);
		mPaint.setColor(Color.argb(200, 255, 20, 20));
		float tX = 0;
		float tY = 0;
		float tW = 5;
		float tH = 0;
		for (int i = 0; i < 100; i++) {
			tX = 20 + i * tW;
			tY = 70 - (i / 2);
			tH = 15 + (i / 2);
			if (mGame.mRightPerson.getCurrentPi() < i) {
				mPaint.setColor(Color.argb(150, 100, 100, 100));
			}
			mCanvas.drawRect(tX, tY, tX + tW, tY + tH, mPaint);
		}
		mPaint.setTextSize(24f);
		mPaint.setColor(Color.BLACK);
		int tPrintPi = mGame.mRightPerson.getCurrentPi();
		tPrintPi = tPrintPi >= 100 ? 100 : tPrintPi;
		tPrintPi = tPrintPi <= 0 ? 0 : tPrintPi;
		String showText = String.format("你已经憋了 %s毫升的屁，再不放就憋死了", tPrintPi);
		mCanvas.drawText(showText, 55, 70, mPaint);
	}
}