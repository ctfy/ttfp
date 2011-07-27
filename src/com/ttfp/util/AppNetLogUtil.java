package com.ttfp.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import com.ttfp.TtfpGameApplication;
import com.ttfp.netlog.NetLogInfo;

public class AppNetLogUtil extends Thread {
	private static AppNetLogUtil mInstance;
	public static AppNetLogUtil getInstance() {
		if (null == mInstance || !mInstance.isAlive()) {
			mInstance = new AppNetLogUtil();
		}
		return mInstance;
	}
	private AppNetLogUtil() {
		reread();
	}
	
	@Override
	protected void finalize() throws Throwable {
		back();
		super.finalize();
	}

	@Override
	public void run() {
		int retry = 20;
		while (--retry > 0) {
			syncLog();
			try {
				Thread.sleep(1000 * 30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public final String API_URL = "http://applog.sinaapp.com/index.php";

	private List<NetLogInfo> list = new ArrayList<NetLogInfo>();
	
	public static void log(String log) {
		getInstance().addLog(log);
	}
	
	public synchronized void addLog(String log) {
		NetLogInfo nlog = new NetLogInfo(TtfpGameApplication.getContext(), log);
		list.add(nlog);
		syncLog();
	}

	public synchronized void syncLog() {
		int retry = 10;
		while (list.size() > 0 && (--retry) > 0) {
			NetLogInfo t = list.get(0);
			if (t.hasSync) {
				list.remove(t);
			}
			try {
				updateInfo(t);
				t.hasSync = true;
				list.remove(t);
			} catch (Exception e) {
				try {
					Thread.sleep(1000 * 30);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	// 上传信息到server
	private void updateInfo(NetLogInfo info) throws JSONException,
			ClientProtocolException, IOException {
		HttpPost post = new HttpPost(API_URL);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("content", info
				.toJSONString()));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpClient hc = new DefaultHttpClient();
		HttpResponse httpResp = hc.execute(post);
		String res = httpResp.getEntity().toString();
		System.out.println(res);
	}

	public void back() {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = TtfpGameApplication.getContext().openFileOutput("applog", 0);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
			oos.flush();
		} catch (Exception e) {
			try {
				oos.close();
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void reread() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = TtfpGameApplication.getContext().openFileInput("applog");
			ois = new ObjectInputStream(fis);
			List<NetLogInfo> tmpList = (List<NetLogInfo>) ois.readObject();
			if (null != tmpList) {
				list = tmpList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
