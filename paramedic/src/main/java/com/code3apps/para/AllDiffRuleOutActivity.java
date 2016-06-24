package com.code3apps.para;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.Util;

public class AllDiffRuleOutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.all_diff_rule_out);
		WebView webView = (WebView) this.findViewById(R.id.webview_content);
		//webView.loadUrl("file:///android_asset/Differential Rule Outs.html");

		StringBuilder data = new StringBuilder();
		try {
			data = Util.readFileByLines(getResources().getAssets().open(
					"Differential Rule Outs.html"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.loadDataWithBaseURL("./", data.toString(), Const.MIME_TYPE,
				Const.ENCODING_UTF8, null);
		webView.addJavascriptInterface(new Object() {
			public void showPopUp(String name) {
				Toast.makeText(AllDiffRuleOutActivity.this, "click items:"+name, 0).show();
			}
		}, "htmltest");
	}

}
