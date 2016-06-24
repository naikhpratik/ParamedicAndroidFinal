package com.code3apps.para;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);

		WebView webView = (WebView) this.findViewById(R.id.webview_about);
		//for GM
		webView.loadUrl("file:///android_asset/About_gm.html");
		
		//for AM
//		webView.loadUrl("file:///android_asset/About_am.html");
	}
}
