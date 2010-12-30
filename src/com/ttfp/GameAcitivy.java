package com.ttfp;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GameAcitivy extends Activity {
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	public static final int MENU_NEW_GAME = 0;//新游戏
	public static final int MENU_QUIT = 1;//退出
	
    private Game game;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        SCREEN_WIDTH = this.getWindowManager().getDefaultDisplay().getWidth();
        SCREEN_HEIGHT = this.getWindowManager().getDefaultDisplay().getHeight();
        game = new Game(this);
        setContentView(game);
    }
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v("a", String.valueOf(event.getAction()));
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    		game.keyDown();
    	}
    	if(event.getAction() == KeyEvent.ACTION_UP) {
    		game.keyUp();
    	}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW_GAME:
			Intent i = new Intent(this, GameAcitivy.class);
			startActivity(i);
			android.os.Process.killProcess(android.os.Process.myPid());
			break;
		case MENU_QUIT:
			android.os.Process.killProcess(android.os.Process.myPid());
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_NEW_GAME, 1, "重新开始");
		menu.add(0, MENU_QUIT, 1, "退出");
		return super.onCreateOptionsMenu(menu);
	}
}

class Game extends View {

	
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
	
	public Game(Context context) {
		super(context);
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
	}
	public void keyUp() {
		mRightPerson.setPiGoing(false);
	}
	
	public Runnable updateRunnable = new Runnable() {
		@Override
		public void run() {
			while(mGameState.mIsRun) {
				//更新更新线程针
				Game.this.mUpdateFrameId++;
				
//				mLeftJiaoTong.update();
				mRightJiaoTong.update();
//				mLeftPerson.update();
				mRightPerson.update();
				mGameState.update();
				
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
		//画背景
		mViewCanvas.drawBitmap(mBackground, 0f, 0f, null);
		//第一排车
		ArrayList<Car> cars = mLeftJiaoTong.getCars();
		
		Car tmpCar = null;
		for (int i = 0; i < cars.size(); i++) {
			tmpCar = cars.get(i);
			mViewCanvas.drawBitmap(tmpCar.mImage, tmpCar.mX, tmpCar.mY, null);
		}
		//第二排车
		cars = mRightJiaoTong.getCars();
		for (int i = 0; i < cars.size(); i++) {
			tmpCar = cars.get(i);
			mViewCanvas.drawBitmap(tmpCar.mImage, tmpCar.mX, tmpCar.mY, null);
		}
		//画第二层背景
		mViewCanvas.drawBitmap(mBackground2, 0f, 0f, null);
		//画人物1
		mViewCanvas.drawBitmap(mLeftPerson.getImage(), mLeftPerson.mX, mLeftPerson.mY, null);
		//画人物2
		mViewCanvas.drawBitmap(mRightPerson.getImage(), mRightPerson.mX, mRightPerson.mY, null);
		//画游戏状态		
		mViewCanvas.drawBitmap(mGameState.getImage(), mGameState.mX, mGameState.mY, null);

		Rect tSrc = new Rect(0, 0, WIDTH, HEIGHT);
		Rect tDst = new Rect(0, 0, GameAcitivy.SCREEN_WIDTH, GameAcitivy.SCREEN_HEIGHT);
		canvas2.drawBitmap(mViewBmp,tSrc, tDst, null);
	}
}

class JiaoTong {
	ArrayList<Car> mCars = new ArrayList<Car>();
	
	public ArrayList<Car> getCars() {
		return mCars;
	}
	
	public void update() {
		for (int i = 0; i < mCars.size(); i++) {
			mCars.get(i).update();
		}

		/** 下面步骤保证随机添加向车道中随机车辆 */
		if (mCars.size() < 3) {
			Random r = new Random(System.currentTimeMillis());
			int i = r.nextInt(200) - 199; // 负值的时候不添加车辆
			if (i > -1) {
				Car c = new Car(this);
				mCars.add(c);
			}
		}
	}
}

class Car {
	public Car(JiaoTong jiaoTong){
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
	
	
	//计算新位置
	public void update() {
		if (mSpeed > 0 && mX > GameAcitivy.SCREEN_WIDTH) {
			mJiaoTong.getCars().remove(this);
		}
		else if (mSpeed < 0 && mX < (0 - mWidth)) {
			mJiaoTong.getCars().remove(this);
		}
		else {
			this.mX += mSpeed;
		}
	}
}

abstract class Person {
	private Bitmap mImage;
	public int mX;
	public int mY;
	public int mWidth;
	public int mHeight;
	
	public abstract void update ();

	protected void setImage(Bitmap mImage) {
		this.mImage = mImage;
	}

	public Bitmap getImage() {
		return mImage;
	}
}

class LeftPerson extends Person {

	/** 游戏状态单例 */
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

class RightPerson extends Person {
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
		if (mPiGoing && mGame.mRightJiaoTong.getCars().size() < 1) {
			mGame.gameOver();
		}
		
		if (!mPiGoing) {//不是正在放屁的状态
			if (0 == mGame.mUpdateFrameId % 4) {
				mCurrentPi += 1;//屁量增长
			}
			
			/** 如果当前屁量超过了最大屁量则将游戏状态设置为false */
			if (mCurrentPi > mMaxPi) {
				mGameState.mIsRun = false;
				mGame.gameOver();
				return;
			}
			
			if (this.getImage() == mBitmapLib.rightPerson1) {
				this.setImage(mBitmapLib.rightPerson2);
			}
			else if (this.getImage() == mBitmapLib.rightPerson2) {
				this.setImage(mBitmapLib.rightPerson3);
			}
			else if (this.getImage() == mBitmapLib.rightPerson3) {
				this.setImage(mBitmapLib.rightPerson1);
			}
		}
		else {//是放屁状态, 则将批量减少
			mCurrentPi -= 1;
			mCurrentPi = mCurrentPi < 0 ? 0 : mCurrentPi;
		}
		
	}
}

class GameState {
	private Game mGame;
	private static GameState mGameState;
	
	private BitmapLib mBitmapLib = BitmapLib.createBitmapLib(null);
	private Bitmap mImage;
	private Canvas mCanvas;
	private Paint mPaint;
	private GameState(Game game) {
		mGame = game;
		mIsRun = true;//将游戏状态设置成开
		mY = 300;
		mImage = Bitmap.createBitmap(Game.WIDTH, 100, Config.ARGB_8888);
		mCanvas = new Canvas(mImage);
		mPaint = new Paint();
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
			tY = 70 - (i/2);
			tH = 15 + (i / 2);
			if (mGame.mRightPerson.getCurrentPi() < i) {
				mPaint.setColor(Color.argb(150, 100, 100, 100));
			}
			mCanvas.drawRect(tX, tY, tX + tW, tY + tH, mPaint);
		}
		mPaint.setTextSize(24f);
		mPaint.setColor(Color.BLACK);
		String showText = String.format("你已经憋了 %s毫升的屁，再不放就憋死了", mGame.mRightPerson.getCurrentPi() - 1);
		mCanvas.drawText(showText, 55, 70, mPaint);
	}
}

class BitmapLib extends View{
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
	public Bitmap background = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
	/** 背景2 */
	public Bitmap background2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.background2);
	

	/** 左侧人物第一个状态 */
	public Bitmap leftPerson1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.left_person_1);
	/** 左侧人物第二个状态 */
	public Bitmap leftPerson2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.left_person_2);
	/** 左侧人物第三个状态 */
	public Bitmap leftPerson3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.left_person_3);
	
	
	/** 右侧人物第一个状态 */
	public Bitmap rightPerson1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_1);
	/** 右侧人物第二个状态 */
	public Bitmap rightPerson2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_2);
	/** 右侧人物第三个状态 */
	public Bitmap rightPerson3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_3);
	/** 右侧人物第四个状态 */
	public Bitmap rightPerson4 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_4);
	/** 右侧人物第五个状态 */
	public Bitmap rightPerson5 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_5);
	
	
	/** 第一种汽车 */
	public Bitmap car1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car1);
	/** 第二种汽车 */
	public Bitmap car2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car2);
	/** 第三种汽车 */
	public Bitmap car3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car3);
	/** 第四种汽车 */
	public Bitmap car4 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car4);
	/** 第五种汽车 */
	public Bitmap car5 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car5);
}