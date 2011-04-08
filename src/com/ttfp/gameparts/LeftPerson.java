package com.ttfp.gameparts;

import com.ttfp.views.Game;

public class LeftPerson extends Person {

	/** 游戏状态单例 */
	@SuppressWarnings("unused")
	private GameState mGameState;
	private BitmapLib mBitmapLib = BitmapLib.createBitmapLib(null);

	public LeftPerson(Game game) {
		mGameState = GameState.createGameState(game);

		this.setImage(mBitmapLib.leftPerson1);
		this.mX = 155;
		this.mY = 173;
	}

	@Override
	public void update() {
	}
}
