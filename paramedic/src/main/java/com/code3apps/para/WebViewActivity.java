package com.code3apps.para;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.Util;

public class WebViewActivity extends Activity {
	
	private WebView webView;
	private String mFrom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);    //设置Activity显示进度条 
		setContentView(R.layout.oauth_webview);
		webView = (WebView)this.findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				WebViewActivity.this.setProgress(newProgress * 100);
			}
		});
		Intent in = this.getIntent();
		mFrom = in.getStringExtra("from");
		Bundle extras = in.getExtras();
		String url = "";
		if (extras != null) {
			if (extras.containsKey("url")) {
				url = extras.getString("url");
				if (Util.isConnectInternet(this)) {
					System.out.println("oe url:"+url);
					webView.loadUrl(url);
				} else {
					Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
					return;
				}
			}
		}
		webView.addJavascriptInterface(new JavaScriptInterface(), "Methods");
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
//				System.out.println("url start:" + url);
			}
			
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			
			@Override
			public void onLoadResource(WebView view, String url) {
//				System.out.println("webview url:"+url);
		        Uri uri = Uri.parse(url);
		        
		        //callbackurlhost
		        if (uri.getHost().equals(Const.CALLBACKURLHOST)) {
		        	String verifier = uri.getQueryParameter("oauth_verifier");
			        String token = uri.getQueryParameter("oauth_token");
		            if (null != token) {
		            	if("score".equals(mFrom)){
		            		System.out.println("from score");
		            		Intent intent = new Intent();
							intent.setAction("oauth_verifier_score");
							Bundle extras = new Bundle();
							extras.putString("oauth_token", token);
							extras.putString("oauth_verifier", verifier);
							intent.putExtras(extras);
							sendBroadcast(intent);
		            	}else{
		            		System.out.println("from allquiz");
			            	Intent intent = new Intent();
							intent.setAction("oauth_verifier");
							Bundle extras = new Bundle();
							extras.putString("oauth_token", token);
							extras.putString("oauth_verifier", verifier);
							intent.putExtras(extras);
							sendBroadcast(intent);
		            	}
		                finish();
		            } else {
		                // TODO tell user to try again 
		            }
		        } else {
		            super.onLoadResource(view, url);
		        }
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				view.loadUrl("javascript:window.Methods.getHTML('<div>'+document.getElementById('oauth_pin').innerHTML+'</div>');"); 
				super.onPageFinished(view, url);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack(); 
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	class JavaScriptInterface {
		public void getHTML(String html) {
			System.out.println("JavaScriptInterface");
			System.out.println(html);
			String pin = getPin(html);
			// 这里就获取到了我们想要的pin码
			// 这个pin码就是oauth_verifier值,用来进一步获取Access Token和Access Secret用
			System.out.println("pin:" + pin);
			Intent intent = new Intent();
			intent.setAction("oauth_verifier");
			Bundle extras = new Bundle();
			extras.putString("pin", pin);
			intent.putExtras(extras);
			sendBroadcast(intent);
			WebViewActivity.this.finish();
		}
	}
	
	public String getPin(String html) {
		String ret = "";
		String regEx = "[0-9]{7}";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		boolean result = m.find();
		if (result) {
			ret = m.group(0);
		}
		return ret;
	}
}
