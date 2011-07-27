package com.ttfp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.ttfp.views.Game;
import com.xxiyy.spl.RecordThread;
import com.xxiyy.spl.RecordThread.ISoundSizeChanged;

public class GameAcitivy extends Activity {
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	public static final int MENU_NEW_GAME = 0;// 新游戏
	public static final int MENU_QUIT = 1;// 退出
	public static final int MENU_ENABLE_VIBRATE = 2;// 启用震动
	public static final int MENU_ENABLE_SOUND = 3;// 启用声音控制
	protected static final String TAG = GameAcitivy.class.getSimpleName();
	private TextView mSoundControlTips;
	
	
	private boolean mEnableSound = true;
	private Game game;

	public void onCreate(Bundle savedInstanceState) {
		setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		super.onCreate(savedInstanceState);
		SCREEN_WIDTH = this.getWindowManager().getDefaultDisplay().getWidth();
		SCREEN_HEIGHT = this.getWindowManager().getDefaultDisplay().getHeight();
		setContentView(R.layout.main);
		game = (Game) findViewById(R.id.game);
		

		SharedPreferences sp = getSharedPreferences(GameAcitivy.class.getName(), MODE_PRIVATE);
		
		if (null != sp && sp.getBoolean("has_init_setting", false)) {
			game.mEnableVibrate = sp.getBoolean("EnableVibrate", false);
			mEnableSound = sp.getBoolean("EnableSound", true);
		}
		setSoundControlEnable(mEnableSound);

		mSoundControlTips = (TextView)findViewById(R.id.sound_control_tips);
		if (mEnableSound) {
			showTips(true);
		} else {
			game.start();
		}
	}
	
	private void showTips(final boolean startGame) {
		mSoundControlTips.postDelayed(new Runnable() {
			@Override
			public void run() {
				AlphaAnimation aa = new AlphaAnimation(1f, 0f);
				aa.setDuration(2000);
				mSoundControlTips.startAnimation(aa);
				if (startGame) {
					game.start();
				}
				mSoundControlTips.post(new Runnable() {
					public void run() {
						mSoundControlTips.setVisibility(View.GONE);
					}
				});
			}
		}, 1000);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setSoundControlEnable(mEnableSound);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		setSoundControlEnable(mEnableSound);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v("a", String.valueOf(event.getAction()));
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			game.keyDown();
		}
		if (event.getAction() == KeyEvent.ACTION_UP) {
			game.keyUp();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW_GAME:
			game.restart();
//			Intent i = new Intent(this, GameAcitivy.class);
//			startActivity(i);
//			finish();
			break;
		case MENU_QUIT:
			finish();
			break;
		case MENU_ENABLE_SOUND:
			mEnableSound = !mEnableSound;
			setSoundControlEnable(mEnableSound);
			if (mEnableSound) {
				showTips(false);
			}
			break;
		case MENU_ENABLE_VIBRATE:
			game.mEnableVibrate = !game.mEnableVibrate;
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(MENU_ENABLE_SOUND).setTitle(mEnableSound ? "关闭声控" : "开启声控");
		menu.findItem(MENU_ENABLE_VIBRATE).setTitle(game.mEnableVibrate ? "关闭震动" : "打开震动");
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ENABLE_SOUND, 1, mEnableSound ? "关闭声控" : "开启声控");
		menu.add(0, MENU_ENABLE_VIBRATE, 1, game.mEnableVibrate ? "关闭震动" : "打开震动");
		menu.add(0, MENU_NEW_GAME, 1, "重新开始");
		menu.add(0, MENU_QUIT, 1, "退出");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onPause() {
		SharedPreferences.Editor editor = getSharedPreferences(GameAcitivy.class.getName(), MODE_PRIVATE).edit();
		editor.putBoolean("has_init_setting", true);
		editor.putBoolean("EnableVibrate", game.mEnableVibrate);
		editor.putBoolean("EnableSound", mEnableSound);
		editor.commit();
	}
	
	@Override
	public void finish() {
		if (null != mRecordThread)
			mRecordThread.destroy();
		super.finish();
//		android.os.Process.killProcess(android.os.Process.myPid());
	}

	////////////////////////////////////////////////////////////////////////////
	//////////////////////////// 开始: 声音控制相关 ///////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	private RecordThread mRecordThread;
	private void setSoundControlEnable(boolean b) {
		if (b) {
			if (null == mRecordThread) {
				mRecordThread = new RecordThread();
				mRecordThread.start();
			}
			mRecordThread.setSoundSizeChanged(new ISoundSizeChanged() {
				
				int oldSoundSize = Integer.MIN_VALUE;
				@Override
				public void onSoundSizeChanged(int soundSize) {
					// oldSoundSize尚未赋值则复制后直接
					if (oldSoundSize == Integer.MIN_VALUE) {
						oldSoundSize = soundSize;
						return;
					}
				
					if (soundSize - oldSoundSize > 70) { // 如果与上次音量大于一定值
						Log.v(TAG, "声控方式按下");
						game.keyDown();
					} else {
						Log.v(TAG, "声控方式谈起");
						game.keyUp();
					}
					oldSoundSize = soundSize;
				}
			});
		} else { // 关闭声音控制
			if (null != mRecordThread) {
				mRecordThread.pause();
			}
		}
		Toast.makeText(this, "声控已" + (mEnableSound ? "开启" : "关闭"), Toast.LENGTH_SHORT).show();
	}
	
	////////////////////////////////////////////////////////////////////////////
	//////////////////////////// 结束: 声音控制相关 ///////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
}
