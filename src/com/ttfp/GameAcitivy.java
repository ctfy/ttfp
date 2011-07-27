package com.ttfp;

import java.security.spec.MGF1ParameterSpec;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.ttfp.views.Game;

public class GameAcitivy extends Activity {
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	public static final int MENU_NEW_GAME = 0;// 新游戏
	public static final int MENU_QUIT = 1;// 退出
	public static final int MENU_ENABLE_VIBRATE = 2;// 启用震动
	public static final int MENU_ENABLE_SOUND = 3;// 启用声音控制
	
	private Game game;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		SCREEN_WIDTH = this.getWindowManager().getDefaultDisplay().getWidth();
		SCREEN_HEIGHT = this.getWindowManager().getDefaultDisplay().getHeight();
		setContentView(R.layout.main);
		game = (Game) findViewById(R.id.game);
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
			Intent i = new Intent(this, GameAcitivy.class);
			startActivity(i);
			android.os.Process.killProcess(android.os.Process.myPid());
			break;
		case MENU_QUIT:
			android.os.Process.killProcess(android.os.Process.myPid());
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
		menu.findItem(MENU_ENABLE_VIBRATE).setTitle(game.mEnableVibrate ? "关闭震动" : "打开震动");
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_NEW_GAME, 1, "重新开始");
		menu.add(0, MENU_QUIT, 1, "退出");
		menu.add(0, MENU_ENABLE_SOUND, 1, "关闭声控");
		menu.add(0, MENU_ENABLE_VIBRATE, 1, game.mEnableVibrate ? "关闭震动" : "打开震动");
		return super.onCreateOptionsMenu(menu);
	}
}
