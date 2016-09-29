package cn.eoe.leigo.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @{# SplashActivity.java Create on 2013-5-2 涓嬪崍9:10:01
 * 
 *     class desc: 鍚姩鐢婚潰 (1)鍒ゆ柇鏄惁鏄娆″姞杞藉簲鐢�--閲囧彇璇诲彇SharedPreferences鐨勬柟娉�
 *     (2)鏄紝鍒欒繘鍏uideActivity锛涘惁锛屽垯杩涘叆MainActivity (3)3s鍚庢墽琛�(2)鎿嶄綔
 * 
 *     <p>
 *     Copyright: Copyright(c) 2013
 *     </p>
 * @Version 1.0
 * @Author <a href="mailto:gaolei_xj@163.com">Leo</a>
 * 
 * 
 */
public class SplashActivity extends Activity {
	boolean isFirstIn = false;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 寤惰繜3绉�
	private static final long SPLASH_DELAY_MILLIS = 3000;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	/**
	 * Handler:璺宠浆鍒颁笉鍚岀晫闈�
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		init();
	}

	private void init() {
		// 璇诲彇SharedPreferences涓渶瑕佺殑鏁版嵁
		// 浣跨敤SharedPreferences鏉ヨ褰曠▼搴忕殑浣跨敤娆℃暟
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);

		// 鍙栧緱鐩稿簲鐨勫�硷紝濡傛灉娌℃湁璇ュ�硷紝璇存槑杩樻湭鍐欏叆锛岀敤true浣滀负榛樿鍊�
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		// 鍒ゆ柇绋嬪簭涓庣鍑犳杩愯锛屽鏋滄槸绗竴娆¤繍琛屽垯璺宠浆鍒板紩瀵肩晫闈紝鍚﹀垯璺宠浆鍒颁富鐣岄潰
		if (!isFirstIn) {
			// 浣跨敤Handler鐨刾ostDelayed鏂规硶锛�3绉掑悗鎵ц璺宠浆鍒癕ainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}

	}

	private void goHome() {
		Intent intent = new Intent(SplashActivity.this,com.esri.FiveBigRoad.FiveBigRoadActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	private void goGuide() {
		Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}
}
