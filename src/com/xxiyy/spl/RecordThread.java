package com.xxiyy.spl;

import java.sql.Date;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class RecordThread extends Thread {
	
	private final String TAG = RecordThread.class.getSimpleName();

	private AudioRecord mAudioRecord;

	/* 声音取样缓冲区大小 */
	private int BUFFER_SIZE;
	/* 声音取样时每次跳过的字节书 */
	private final int READ_SOUND_SKIP_SIZE = 50;
	
	private static int SAMPLE_RATE_IN_HZ = 8000;
	private boolean isRun = false;

	public RecordThread() {
		super();
		BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
	}

	public void run() {
		super.run();
		mAudioRecord.startRecording();

		// 用于读取的 buffer
		byte[] buffer = new byte[BUFFER_SIZE];
		isRun = true;
		while (true) {
			if (!isRun) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
			long total = 0;
			// 将 buffer 内容取出，进行平方和运算
			for (int i = 0;  i < buffer.length; i = i + READ_SOUND_SKIP_SIZE) {
				// 这里没有做运算的优化，为了更加清晰的展示代码
				total += buffer[i];
			}
			
			int soundSize = (int) (total / (BUFFER_SIZE / READ_SOUND_SKIP_SIZE));
			if (null != mISoundSizeChanged) {
				mISoundSizeChanged.onSoundSizeChanged(soundSize);
			}
			
			// 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
			// 如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在 Handler 里进行处理。
			Log.d(TAG, "SOUND_SIZE: " + soundSize);
		}
	}

	public void pause() {
		// 在调用本线程的 Activity 的 onPause 里调用，以便 Activity 暂停时释放麦克风
		isRun = false;
	}

	public void start() {
		// 在调用本线程的 Activity 的 onResume 里调用，以便 Activity 恢复后继续获取麦克风输入音量
		isRun = true;
		super.start();
	}
	
	private ISoundSizeChanged mISoundSizeChanged;
	public void setSoundSizeChanged(ISoundSizeChanged  i) {
		mISoundSizeChanged = i;
	}

	public interface ISoundSizeChanged {
		public void onSoundSizeChanged(int soundSize);
	}
	
	@Override
	public void destroy() {
		try {
			isRun = false;
			mISoundSizeChanged = null;
			mAudioRecord.stop();
			mAudioRecord.release();
			mAudioRecord = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}