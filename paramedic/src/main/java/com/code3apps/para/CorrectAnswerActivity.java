package com.code3apps.para;

import java.io.IOException;

import com.code3apps.para.beans.ExplanationSettingBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class CorrectAnswerActivity extends Activity implements OnClickListener {
    private static final String TAG = "CorrectAnswerActivity";
    private static final boolean DEBUG_ENABLED = true;
    
    private ImageView mBookmarkIcon;
    private TextView mBookmarkLabel;
    private boolean mBookmarked;
    private String mChapterId = "";
    
    private ImageView imgNextQuestion = null;
    private String mExplanation = "";
    private String mIfLastQuestion = "";
    private StringBuilder mStringBulder = null;
	private String mStrHtmlTemplate = "";
	private ExplanationSettingBean eSetting = new ExplanationSettingBean();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        log("CorrectAnswerActivity.onCreate");
        Intent intent = getIntent();

        // load view
//        setContentView(R.layout.custom_dialog_activity);
        setContentView(R.layout.result);

        // Set data
        WebView explanation = (WebView) findViewById(R.id.explanation);
        explanation.setBackgroundColor(0);
        mExplanation = intent.getStringExtra(Const.KEY_EXPLANATION);
        mIfLastQuestion = intent.getStringExtra(Const.KEY_IF_LAST);
        log("mExplanation = " + mExplanation);
        log("mIfLastQuestion = " + mIfLastQuestion);
        
        try {
			mStringBulder = Util.readFileByLines(getResources().getAssets()
					.open("template/explanation.html"));
			mStrHtmlTemplate = mStringBulder.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eSetting.setExplanation_content(mExplanation);
		float f = getResources().getDimension(R.dimen.answer_font_size);
		eSetting.setFont_size(f+"px");
		
        explanation.loadDataWithBaseURL("file:///android_asset/template/",
        		Util.getExplanation(mStrHtmlTemplate, eSetting), "text/html",
                "UTF-8", null);
        
        mBookmarked = intent.getBooleanExtra(Const.KEY_BOOKMARK, false);
        mChapterId = intent.getStringExtra(Const.KEY_CHAPTER_ID);
        mBookmarkIcon = (ImageView) findViewById(R.id.bookmarkIcon);
        mBookmarkLabel = (TextView) findViewById(R.id.bookmarkLabel);
        if (mBookmarked) {
            mBookmarkIcon.setImageResource(R.drawable.bookmarked_icon);
            mBookmarkLabel.setText(R.string.bookmarked);
        } else {
            mBookmarkIcon.setImageResource(R.drawable.bookmark_icon);
            mBookmarkLabel.setText(R.string.addToBookmarks);
        }
        
        mBookmarkIcon.setOnClickListener(this);
        mBookmarkLabel.setOnClickListener(this);
        findViewById(R.id.reloadQuestion).setOnClickListener(this);
        imgNextQuestion = (ImageView) findViewById(R.id.nextQuestion);
        imgNextQuestion.setOnClickListener(this);
        
    }

    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id) {
        case R.id.bookmarkIcon:
        case R.id.bookmarkLabel:
            if (mBookmarked) {
                setQuizBookmark(false);
                mBookmarked = false;
            } else {
                setQuizBookmark(true);
                mBookmarked = true;
            }
            break;
        case R.id.reloadQuestion:
            intent = new Intent();
            intent.setAction(Const.ACTION_RELOAD);
            intent.putExtra(Const.KEY_BOOKMARK, mBookmarked);
            setResult(RESULT_OK, intent);
            finish();
            break;
        case R.id.nextQuestion:
            intent = new Intent();
            intent.setAction(Const.ACTION_NEXT);
            intent.putExtra(Const.KEY_BOOKMARK, mBookmarked);
            setResult(RESULT_OK, intent);
            finish();
            break;
        default:
            break;
        }
    }

    private void setQuizBookmark(boolean bookmark) {
        if (bookmark) {
            mBookmarkIcon.setImageResource(R.drawable.bookmarked_icon);
            mBookmarkLabel.setText(R.string.bookmarked);
        } else {
            mBookmarkIcon.setImageResource(R.drawable.bookmark_icon);
            mBookmarkLabel.setText(R.string.addToBookmarks);
        }
        AllQuiz.setQuizBookmark(mChapterId, bookmark);
    }
    
    @SuppressWarnings("unused")
	private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }

}