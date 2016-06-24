package com.code3apps.para;

import java.io.IOException;

import com.code3apps.para.beans.HtmlFileBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;
import com.code3apps.para.utils.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class AllHtmlFileActivity extends Activity implements OnClickListener {
	private static final String TAG = "AllQuiz";
	private static final boolean DEBUG_ENABLED = true;
	private static final int DIALOG_CONFIRM_BOOKMARK = 0;
	private static final int DIALOG_CONFIRM_UNBOOKMARK = 1;
	private static final int ANIMATION_DURATION = 500;

	private String mType = "";
	private String mId = "";
	private String mHtmlName = "";
	private String mHtmlFile = "";
	private boolean mBookmarked;
	private TextView mHeader;
	private WebView mWebView;
	private ImageView mPrev;
	private ImageView mNext;
	private ImageView mBookmark;
	private QuizDatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private int[] mIds = null;
	private int mTotalCount = 0;
	private int[] mIndexUsedArray;
	private int mCurHtmlCount;
	private int mCurHtmlIndex;
	private int mHtmlId;

	private final int TOOLBOX_SCREEN = 0;
	private final int SCENARIOS_SCREEN = 1;
	private int CURRENT_SCREEN = -1;

	// added for v1.4
	private boolean mIsBooked = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mType = intent.getStringExtra(Const.KEY_BOOKMARK_TYPE);
		mId = intent.getStringExtra(Const.KEY_ID);
		mIds = intent.getIntArrayExtra(Const.KEY_IDS);
		// added for v1.4
		mIsBooked = intent.getBooleanExtra(Const.KEY_IS_BOOKMARK, true);

		log("mId is " + mId);
		if (mIds != null) {
			// Come from bookmark
			mTotalCount = mIds.length;
		} else {
			mTotalCount = intent.getIntExtra(Const.KEY_HTML_COUNT, 0);
		}
		log("mTotalCount is " + mTotalCount);

		// no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (mType.equals(Const.VALUE_TYPE_SKILL_SHEET)) {
			setContentView(R.layout.all_skill_sheets_html);
			CURRENT_SCREEN = TOOLBOX_SCREEN;
		} else {
			setContentView(R.layout.all_scenarios_html);
			CURRENT_SCREEN = SCENARIOS_SCREEN;
		}

		prepareViews();
		prepareData();
		loadNextHtmlFile(true, true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		log("onDestroy");

		if (mDb != null)
			mDb.close();
		if (mDbHelper != null)
			mDbHelper.close();
	}

//	@Override
//	public void onBackPressed() {
//		Intent intent;
//
//		if (mIsBooked) {
//			// Come from bookmark
//			intent = new Intent(this, BookmarksActivity.class);
//			intent.putExtra(Const.KEY_BOOKMARK_TYPE, mType);
//		} else {
//			intent = new Intent(
//					this,
//					CURRENT_SCREEN == TOOLBOX_SCREEN ? SkillSheetsActivity.class
//							: ScenariosActivity.class);
//		}
//		try {
//			startActivity(intent);
//			finish();
//		} catch (ActivityNotFoundException e) {
//			Log.e(TAG, "No activity found for this intent!");
//			e.printStackTrace();
//		}
//	}

	private void prepareViews() {
		mHeader = (TextView) findViewById(R.id.header);
		mPrev = (ImageView) findViewById(R.id.prev);
		mNext = (ImageView) findViewById(R.id.next);
		mBookmark = (ImageView) findViewById(R.id.bookmark);
		mWebView = (WebView) findViewById(R.id.webview_content);

		mPrev.setOnClickListener(this);
		mNext.setOnClickListener(this);
		mBookmark.setOnClickListener(this);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
	}

	private void prepareData() {
		log("prepareData begin");
		mDbHelper = QuizContentProvider.createDatabaseHelper(this);
		mDb = mDbHelper.getWritableDatabase();
		if (mIds != null) {
			// Come from bookmark
			mIndexUsedArray = new int[mTotalCount];
			for (int i = 0; i < mTotalCount; i++) {
				mIndexUsedArray[i] = mIds[i];
				if (Integer.valueOf(mId) == mIds[i])
					mCurHtmlCount = i;
			}
		} else {
			mIndexUsedArray = new int[mTotalCount];
			for (int i = 0; i < mTotalCount; i++) {
				mIndexUsedArray[i] = i + 1;
			}
			mCurHtmlCount = Integer.valueOf(mId) - 1;
		}
		log("prepareData end");
	}

	public void onClick(View v) {
		log("onClick");
		Animation slide;
		int id = v.getId();
		switch (id) {
		case R.id.prev:
			log("prev");
			loadNextHtmlFile(false, false);
			slide = AnimationUtils.loadAnimation(this, R.anim.slide_right);
			slide.setDuration(ANIMATION_DURATION);
			slide.setInterpolator(new AccelerateInterpolator());
			mWebView.startAnimation(slide);
			break;
		case R.id.next:
			log("next");
			loadNextHtmlFile(false, true);
			slide = AnimationUtils.loadAnimation(this, R.anim.slide_left);
			slide.setDuration(ANIMATION_DURATION);
			slide.setInterpolator(new AccelerateInterpolator());
			mWebView.startAnimation(slide);
			break;
		case R.id.bookmark:
			log("bookmark");
			if (mBookmarked)
				showDialog(DIALOG_CONFIRM_UNBOOKMARK);
			else
				showDialog(DIALOG_CONFIRM_BOOKMARK);
			break;
		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CONFIRM_BOOKMARK:
			return new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.bookmark)
					.setMessage(
							CURRENT_SCREEN == TOOLBOX_SCREEN ? R.string.bookmark_confirm_skill_sheet
									: R.string.bookmark_confirm_scenario)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked OK so do some stuff */
									setHtmlBookmark(true);
									mBookmarked = true;
								}
							}).setNegativeButton(android.R.string.cancel, null)
					.create();
		case DIALOG_CONFIRM_UNBOOKMARK:
			return new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.bookmark)
					.setMessage(
							CURRENT_SCREEN == TOOLBOX_SCREEN ? R.string.unbookmark_confirm_skill_sheet
									: R.string.umbookmark_confirm_scenario)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked OK so do some stuff */
									setHtmlBookmark(false);
									mBookmarked = false;
								}
							}).setNegativeButton(android.R.string.cancel, null)
					.create();
		}
		return null;
	}

	private void loadNextHtmlFile(boolean bFirsttime, boolean bNext) {
		log("loadNextHtmlFile, bFirsttime is " + bFirsttime + ", bNext is "
				+ bNext);
		log("mCurHtmlCount is " + mCurHtmlCount);
		if (bNext) {
			// Get Next
			if (mCurHtmlCount >= mTotalCount)
				mCurHtmlCount = 0;
			mCurHtmlIndex = mIndexUsedArray[mCurHtmlCount];
			mCurHtmlCount++;
		} else {
			// Get Previous
			mCurHtmlCount = mCurHtmlCount - 2;
			if (mCurHtmlCount < 0)
				mCurHtmlCount = mTotalCount - 1;
			mCurHtmlIndex = mIndexUsedArray[mCurHtmlCount];
			mCurHtmlCount++;
		}
		HtmlFileBean html = QuizDatabaseHelper.getHtmlFile(mDb, mCurHtmlIndex,
				mType);

		mHtmlId = html.getId();
		mHtmlName = html.getName();
		mHtmlFile = html.getFile();
		mBookmarked = html.isBook();
		// mHtmlName = intent.getStringExtra(Const.KEY_HTML_NAME);
		// mHtmlFile = intent.getStringExtra(Const.KEY_HTML_FILE);
		// mBookmarked = intent.getBooleanExtra(Const.KEY_BOOKMARK, false);

		mHeader.setText(mHtmlName);
		// updated for support 3.x Android OS
		// mWebView.loadUrl("file:///android_asset/" + mHtmlFile + ".html");
		String fileName = mHtmlFile + ".html";
		StringBuilder data = new StringBuilder();
		try {
			data = Util.readFileByLines(getResources().getAssets().open(
					fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mWebView.loadDataWithBaseURL("./", data.toString(), Const.MIME_TYPE,
				Const.ENCODING_UTF8, null);
	}

	private void setHtmlBookmark(boolean bookmark) {
		QuizDatabaseHelper.setHtmlFileBookmark(mDb, mType, mHtmlId, bookmark);
	}

	private static void log(String msg) {
		if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
			Log.d(TAG, Const.TAG_PREFIX + msg);
		}
	}

}
