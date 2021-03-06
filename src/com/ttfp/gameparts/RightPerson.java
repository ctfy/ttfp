package com.ttfp.gameparts;

import java.util.ArrayList;
import java.util.List;

import com.ttfp.views.Game;

public class RightPerson extends Person {
	private Game mGame;
	/** 最大屁量 */
	private int mMaxPi;

	public int getMaxPi() {
		return mMaxPi;
	}

	/** 当前屁量 */
	private int mCurrentPi;

	public int getCurrentPi() {
		return mCurrentPi;
	}

	/** 游戏状态单例 */
	private GameState mGameState;
	/** 是否正在放屁 */
	private boolean mPiGoing = false;

	public boolean getPiGoding() {
		return mPiGoing;
	}

	public void setPiGoing(boolean b) {
		mPiGoing = b;
	}

	private BitmapLib mBitmapLib = BitmapLib.createBitmapLib(null);

	public RightPerson(Game game) {
		mGame = game;
		mGameState = GameState.createGameState(game);

		this.setImage(mBitmapLib.rightPerson1);
		this.mMaxPi = 100;
		this.mCurrentPi = 0;
		this.mX = 270;
		this.mY = 183;
	}

	@Override
	public void update() {
		int oldPi = mCurrentPi; // 记录旧的屁量
		if (mPiGoing && mGame.mRightJiaoTong.getCars().size() < 1) {
			mGame.gameOver();
		}

		if (!mPiGoing) {// 不是正在放屁的状态
			if (0 == mGame.mUpdateFrameId % 4) {
				mCurrentPi += 1;// 屁量增长
			}

			/** 如果当前屁量超过了最大屁量则将游戏状态设置为false */
			if (mCurrentPi > mMaxPi) {
				mGameState.mIsRun = false;
				mGame.gameOver();
				return;
			}

			if (this.getImage() == mBitmapLib.rightPerson1) {
				this.setImage(mBitmapLib.rightPerson2);
			} else if (this.getImage() == mBitmapLib.rightPerson2) {
				this.setImage(mBitmapLib.rightPerson3);
			} else if (this.getImage() == mBitmapLib.rightPerson3) {
				this.setImage(mBitmapLib.rightPerson1);
			}
		} else {// 是放屁状态, 则将批量减少
			mCurrentPi -= 1;
			mCurrentPi = mCurrentPi < 0 ? 0 : mCurrentPi;
		}

		// 计算新的屁量的差值并发广播
		int changedCount = mCurrentPi - oldPi;
		if (changedCount > 0) { //屁量增加 
			for (IPiChanged t : mAllIPiChanged) {
				t.morePi(changedCount);
			}
			
		} else if (changedCount < 0) { // 屁量减少
			for (IPiChanged t : mAllIPiChanged) {
				t.lessPi(-1 * changedCount);
			}
		}
	}
	
	/** 重置人物状态 */
	public void reset() {
		mCurrentPi = 0;
		this.setImage(mBitmapLib.rightPerson1);
	}
	
	/////////////////////////////////////////////////////////////////////
	////////////////////////// 开始: 屁量改变的监听 ///////////////////////
	/////////////////////////////////////////////////////////////////////
	private List<IPiChanged> mAllIPiChanged = new ArrayList<IPiChanged>();
	public void addPiChangedEvent(IPiChanged i) {
		mAllIPiChanged.add(i);
	}
	public void removeChangedEvent(IPiChanged i) {
		mAllIPiChanged.remove(i);
	}
	public interface IPiChanged{
		/** 屁增加 */
		public void morePi(int moreCount);
		/** 屁减少 */
		public void lessPi(int lessCount);
	}
	/////////////////////////////////////////////////////////////////////
	////////////////////////// 结束: 屁量改变的监听 ///////////////////////
	/////////////////////////////////////////////////////////////////////
}