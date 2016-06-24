package com.code3apps.para;

import com.code3apps.para.utils.Const;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class FollowActivity extends Activity implements OnClickListener {
	private static final String TAG = "HomeActivity";
	private static final boolean DEBUG_ENABLED = false;
	private static final String URL_FB = "http://www.facebook.com/pages/Code-3-Apps/180948875248863";
	private static final String URL_TW = "http://twitter.com/#!/Code3Apps";
	// private static final String URL_APP = "http://code3apps.blogspot.com/";
	private static final String URL_APP = "http://code3-apps.com/";
	// for GM
	private static final String FF_APP_URL = "http://market.android.com/details?id=com.code3apps.FireFighter";

	// for Amazon
	// private static final String FF_APP_URL =
	// "http://amazon.com/gp/mas/dl/android?p=com.code3apps.FireFighter";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		log("onCreate");
		// disable default title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// load view
		setContentView(R.layout.follow);

		findViewById(R.id.fb).setOnClickListener(this);
		findViewById(R.id.ff).setOnClickListener(this);
		findViewById(R.id.twitter).setOnClickListener(this);
		findViewById(R.id.code3banner).setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;
		switch (id) {
		case R.id.fb:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(URL_FB));
			startActivity(intent);
			break;
		case R.id.twitter:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(URL_TW));
			startActivity(intent);
			break;
		case R.id.ff:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(FF_APP_URL));
			startActivity(intent);
			break;
		case R.id.code3banner:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(URL_APP));
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private static void log(String msg) {
		if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
			Log.d(TAG, Const.TAG_PREFIX + msg);
		}
	}

}