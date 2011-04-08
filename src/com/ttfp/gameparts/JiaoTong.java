package com.ttfp.gameparts;

import java.util.ArrayList;
import java.util.Random;

public class JiaoTong {
	ArrayList<Car> mCars = new ArrayList<Car>();

	public ArrayList<Car> getCars() {
		return mCars;
	}

	private static int mNanDu = 40;// 该值与游戏的困难程度有关，随时间推移此值增大
	private static int mNanDuTmp = 4000;

	public void update() {
		mNanDu = (mNanDuTmp++) / 100;// 改变游戏难度，算法是没执行100次该方法难度+1
		for (int i = 0; i < mCars.size(); i++) {
			mCars.get(i).update();
		}

		/** 下面步骤保证随机添加向车道中随机车辆 */
		if (mCars.size() < 3) {
			Random r = new Random(System.currentTimeMillis());
			/** 下面的i是一个根据游戏难度生成的随机数，该算法使得难度越高i>-1的概率就越小，i>-1时才添加车辆 */
			int i = r.nextInt(mNanDu) - mNanDu + 1; // 负值的时候不添加车辆
			if (i > -1) {
				Car c = new Car(this);
				mCars.add(c);
			}
		}
	}
}