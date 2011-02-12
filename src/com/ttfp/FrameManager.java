package com.ttfp;

/*
 * 游戏时间管理类（单例）
 */
public class FrameManager {

	private static FrameManager mFrameManager;
	private static int mRefreshRate = 12;
	private long mStartTime;

	private FrameManager() {
		mStartTime = System.currentTimeMillis();
	}

	public static FrameManager getInstance() {
		if (null == mFrameManager) {
			mFrameManager = new FrameManager();
		}
		return mFrameManager;
	}

	/*
	 * 设置每秒钟的刷新次数
	 */
	public static void setRefreshRate(int refreshRate) {
		mRefreshRate = refreshRate;
	}

	/*
	 * 返回当前游戏所在的帧
	 */
	public int getFrameIndex() {
		long game_time = System.currentTimeMillis() - mStartTime;
		int temp = 1000 / mRefreshRate;//每多少毫秒增加一帧
		return (int)(game_time / temp);
	}
}