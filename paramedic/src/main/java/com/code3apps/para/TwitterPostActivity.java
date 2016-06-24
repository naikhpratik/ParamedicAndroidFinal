package com.code3apps.para;

import com.code3apps.para.ScoreHistory.MainHandler;

import twitter4j.TwitterException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterPostActivity extends Activity implements OnClickListener{

	private EditText mSharedContent;
	private TextView mWordsNum;
	private String mShareContent = "";
	private int mTotalNum = 140;
	private Handler mHandler = new MainHandler();
	private ProgressDialog mProgressdialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share);
		Intent in = this.getIntent();
		if(in != null){
			mShareContent = in.getStringExtra("content");
		}
		mSharedContent = (EditText) findViewById(R.id.publish_content);
		mWordsNum = (TextView) findViewById(R.id.wordscount_label);
		findViewById(R.id.cancel_btn).setOnClickListener(this);
		findViewById(R.id.sure_btn).setOnClickListener(this);
		mSharedContent.setText(mShareContent);
		mWordsNum.setText(""+(140 - mShareContent.length()));
		mSharedContent.addTextChangedListener(new EditTextWatcher());
	}

	private class EditTextWatcher implements TextWatcher{
		private CharSequence temp;
		private int selectionStart;
		private int selectionEnd;
		@Override
		public void afterTextChanged(Editable s) {
			int number = mTotalNum - s.length();
			mWordsNum.setText("" + number);
			selectionStart = mSharedContent.getSelectionStart();
			selectionEnd = mSharedContent.getSelectionEnd();
			if(s.length() <= mTotalNum)
				mSharedContent.setSelection(s.length());
			if (temp.length() > mTotalNum) {
				s.delete(selectionStart - 1, selectionEnd);
				int tempSelection = selectionStart;
				mSharedContent.setText(s);
				mSharedContent.setSelection(tempSelection);// set selection to end
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			//do nothing
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			temp = s;
		}
	}
	class MainHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 100:
				if(mProgressdialog != null){
					mProgressdialog.dismiss();
				}
				Toast.makeText(TwitterPostActivity.this, "Post Success!", 0).show();
				onBackPressed();
				break;
			case 200:
				break;
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.sure_btn:
			mProgressdialog = new ProgressDialog(TwitterPostActivity.this);
			mProgressdialog.show();
			final String con = mSharedContent.getText().toString().trim();
			if(con!=null && con.length()>0){
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							ScoreHistory.twitter.updateStatus(con);
							Message msg = new Message();
							msg.what = 100;
							mHandler.sendMessage(msg);
						} catch (TwitterException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}else{
				if(mProgressdialog != null){
					mProgressdialog.dismiss();
				}
				Toast.makeText(TwitterPostActivity.this, "post content can not be null", 0).show();
			}

			break;
		case R.id.cancel_btn:
			onBackPressed();
			break;
		}
	}

}
