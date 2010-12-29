package com.ttfp;

import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class GameAcitivy extends Activity {
	public static final int MENU_NEW_GAME = 0;//����Ϸ
	public static final int MENU_QUIT = 1;//�˳�
	
    private Game game;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
		menu.add(0, MENU_NEW_GAME, 1, "���¿�ʼ");
		menu.add(0, MENU_QUIT, 1, "�˳�");
		return super.onCreateOptionsMenu(menu);
	}
}

class Game extends View {

	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static Game mGame;
	
	/** ��Ϸ���õ���ͼƬ�Ĳֿ� */
	private BitmapLib mBitmapLib;
	
	/** ��㱳����¥������·���ֵ� */
	public Bitmap mBackground;
	/** ��Ϸ���������󿪵���Щ���� */
	public JiaoTong mLeftJiaoTong;
	/** ��Ϸ���������ҿ�����Щ���� */
	public JiaoTong mRightJiaoTong;
	/** ��Ϸ��������ǲ㱳������Ϸ�����²����ڵ�������͸������ */
	public Bitmap mBackground2;
	/** ��Ϸ�������� */
	public LeftPerson mLeftPerson;
	/** ��Ϸ���Ҳ���� */
	public RightPerson mRightPerson;
	public GameState mGameState;
	/** ��Ϸˢ���߳����¼ */
	public int mUpdateFrameId = 0;
	
	public Game(Context context) {
		super(context);
        WIDTH = 547;
        HEIGHT = 397;
        
        mBitmapLib = BitmapLib.createBitmapLib(context);
        
        mBackground = mBitmapLib.background;
        
        mLeftJiaoTong = new JiaoTong();
        mRightJiaoTong = new JiaoTong();

        mBackground2 = mBitmapLib.background2;
        
        mLeftPerson = new LeftPerson(this);
        mRightPerson = new RightPerson(this);
        
        mGameState = GameState.createGameState(this);
        
        // �������ȸ����߳�
        new Thread(updateRunnable).start();
        // ���������ػ��߳�
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
				//���¸����߳���
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
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		//������
		canvas.drawBitmap(mBackground, 0f, 0f, null);
		//��һ�ų�
		ArrayList<Car> cars = mLeftJiaoTong.getCars();
		
		Car tmpCar = null;
		for (int i = 0; i < cars.size(); i++) {
			tmpCar = cars.get(i);
			canvas.drawBitmap(tmpCar.mImage, tmpCar.mX, tmpCar.mY, null);
		}
		//�ڶ��ų�
		cars = mRightJiaoTong.getCars();
		for (int i = 0; i < cars.size(); i++) {
			tmpCar = cars.get(i);
			canvas.drawBitmap(tmpCar.mImage, tmpCar.mX, tmpCar.mY, null);
		}
		//���ڶ��㱳��
		canvas.drawBitmap(mBackground2, 0f, 0f, null);
		//������1
		canvas.drawBitmap(mLeftPerson.getImage(), mLeftPerson.mX, mLeftPerson.mY, null);
		//������2
		canvas.drawBitmap(mRightPerson.getImage(), mRightPerson.mX, mRightPerson.mY, null);
		//����Ϸ״̬		
		canvas.drawBitmap(mGameState.getImage(), mGameState.mX, mGameState.mY, null);
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

		/** ���沽�豣֤�������򳵵���������� */
		if (mCars.size() < 3) {
			Random r = new Random(System.currentTimeMillis());
			int i = r.nextInt(200) - 199; // ��ֵ��ʱ����ӳ���
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
	
	/** ��Ϸͼ�ο� */
	private BitmapLib mBitmapLib = BitmapLib.createBitmapLib(null);
	
	/** ��������ͼƬ */
	public Bitmap mImage;
	/** ��������ʽ���ٶ�, ��ֵ��ʾ���ң���ֵ��ʾ���� */
	public int mSpeed;
	/** ��ǰ��x�����λ�� */
	public int mX;
	/** ��ǰ��y�����λ�� */
	public int mY;
	/** �˳�����Ļ��ռ�Ŀ�� */
	public int mWidth;
	/** �˳�����Ļ��ռ�ø߶� */
	public int mHeight;
	
	
	//������λ��
	public void update() {
		if (mSpeed > 0 && mX > Game.WIDTH) {
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

	/** ��Ϸ״̬���� */
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
	/** ���ƨ�� */
	private int mMaxPi;
	public int getMaxPi() {
		return mMaxPi;
	}

	/** ��ǰƨ�� */
	private int mCurrentPi;
	public int getCurrentPi() {
		return mCurrentPi;
	}
	/** ��Ϸ״̬���� */
	private GameState mGameState;
	/** �Ƿ����ڷ�ƨ */
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
		
		if (!mPiGoing) {//�������ڷ�ƨ��״̬
			if (0 == mGame.mUpdateFrameId % 4) {
				mCurrentPi += 1;//ƨ������
			}
			
			/** �����ǰƨ�����������ƨ������Ϸ״̬����Ϊfalse */
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
		else {//�Ƿ�ƨ״̬, ����������
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
		mIsRun = true;//����Ϸ״̬���óɿ�
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
		String showText = String.format("���Ѿ����� %s������ƨ���ٲ��žͱ�����", mGame.mRightPerson.getCurrentPi() - 1);
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

	/** ����1 */
	public Bitmap background = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
	/** ����2 */
	public Bitmap background2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.background2);
	

	/** ��������һ��״̬ */
	public Bitmap leftPerson1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.left_person_1);
	/** �������ڶ���״̬ */
	public Bitmap leftPerson2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.left_person_2);
	/** ������������״̬ */
	public Bitmap leftPerson3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.left_person_3);
	
	
	/** �Ҳ������һ��״̬ */
	public Bitmap rightPerson1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_1);
	/** �Ҳ�����ڶ���״̬ */
	public Bitmap rightPerson2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_2);
	/** �Ҳ����������״̬ */
	public Bitmap rightPerson3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_3);
	/** �Ҳ�������ĸ�״̬ */
	public Bitmap rightPerson4 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_4);
	/** �Ҳ���������״̬ */
	public Bitmap rightPerson5 = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_person_5);
	
	
	/** ��һ������ */
	public Bitmap car1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car1);
	/** �ڶ������� */
	public Bitmap car2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car2);
	/** ���������� */
	public Bitmap car3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car3);
	/** ���������� */
	public Bitmap car4 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car4);
	/** ���������� */
	public Bitmap car5 = BitmapFactory.decodeResource(this.getResources(), R.drawable.car5);
}