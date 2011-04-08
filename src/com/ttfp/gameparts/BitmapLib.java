package com.ttfp.gameparts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.ttfp.R;

public class BitmapLib extends View {
	private static BitmapLib mBitmapLib;

	private BitmapLib(Context context) {
		super(context);
	}

	public static BitmapLib createBitmapLib(Context context) {
		if (null == mBitmapLib) {
			mBitmapLib = new BitmapLib(context);
		}
		return mBitmapLib;
	}

	/** 背景1 */
	public Bitmap background = BitmapFactory.decodeResource(
			this.getResources(), R.drawable.background);
	/** 背景2 */
	public Bitmap background2 = BitmapFactory.decodeResource(this
			.getResources(), R.drawable.background2);

	/** 左侧人物第一个状态 */
	public Bitmap leftPerson1 = BitmapFactory.decodeResource(this
			.getResources(), R.drawable.left_person_1);
	/** 左侧人物第二个状态 */
	public Bitmap leftPerson2 = BitmapFactory.decodeResource(this
			.getResources(), R.drawable.left_person_2);
	/** 左侧人物第三个状态 */
	public Bitmap leftPerson3 = BitmapFactory.decodeResource(this
			.getResources(), R.drawable.left_person_3);

	/** 右侧人物第一个状态 */
	public Bitmap rightPerson1 = BitmapFactory.decodeResource(this
			.getResources(), R.drawable.right_person_1);
	/** 右侧人物第二个状态 */
	public Bitmap rightPerson2 = BitmapFactory.decodeResource(this
			.getResources(), R.drawable.right_person_2);
	/** 右侧人物第三个状态 */
	public Bitmap rightPerson3 = BitmapFactory.decodeResource(this
			.getResources(), R.drawable.right_person_3);
	/** 右侧人物第四个状态 */
	public Bitmap rightPerson4 = BitmapFactory.decodeResource(this
			.getResources(), R.drawable.right_person_4);
	/** 右侧人物第五个状态 */
	public Bitmap rightPerson5 = BitmapFactory.decodeResource(this
			.getResources(), R.drawable.right_person_5);

	/** 第一种汽车 */
	public Bitmap car1 = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.car1);
	/** 第二种汽车 */
	public Bitmap car2 = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.car2);
	/** 第三种汽车 */
	public Bitmap car3 = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.car3);
	/** 第四种汽车 */
	public Bitmap car4 = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.car4);
	/** 第五种汽车 */
	public Bitmap car5 = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.car5);
}