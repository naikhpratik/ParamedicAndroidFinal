package com.code3apps.para;

import com.code3apps.para.beans.QuizRecorderBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class QuizzesActivity extends Activity implements OnClickListener {
    private static final String TAG = "QuizzesActivity";
    private static final boolean DEBUG_ENABLED = true;
    private QuizDatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("onCreate");

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.quizzes);

        findViewById(R.id.bookmarks).setOnClickListener(this);
        findViewById(R.id.comprehensive_quiz).setOnClickListener(this);
        findViewById(R.id.chapter_quizzes).setOnClickListener(this);
        findViewById(R.id.score_history).setOnClickListener(this);
        mDbHelper = QuizContentProvider.createDatabaseHelper(this);
		mDb = mDbHelper.getWritableDatabase();
    }

    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id) {
        case R.id.bookmarks:
            intent = new Intent(this, BookmarksActivity.class);
            intent.putExtra(Const.KEY_BOOKMARK_TYPE, Const.VALUE_TYPE_QUIZ);
            startActivity(intent);
            break;
        case R.id.comprehensive_quiz:
        	final QuizRecorderBean recordbean = QuizDatabaseHelper.getChaperHistoryRecorder(mDb, 0);
        	if(recordbean!=null){
            	new AlertDialog.Builder(this)
            	.setIcon(android.R.drawable.ic_dialog_alert)
            	.setTitle(R.string.continue_quiz)
            	.setMessage(R.string.continue_test)
            	.setPositiveButton(R.string.continue_btn, new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					
    					Intent intent = new Intent(QuizzesActivity.this, AllQuiz.class);
    			        intent.putExtra(Const.KEY_CHAPTER_ID, "0");
//    			        intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
    			        Bundle b = new Bundle();
    			        b.putSerializable("rb", recordbean);
    			        intent.putExtra("bundle", b);
    			        QuizzesActivity.this.startActivity(intent);
    				}
    			})
    			.setNegativeButton(R.string.delete_btn, new DialogInterface.OnClickListener() {
    				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// delete history record
    					QuizDatabaseHelper.deletRecorder(mDb, ""+0);
    					// update last quizScore
    					QuizDatabaseHelper.setQuizScoreDone(mDb, "Comprehensive Quiz");
    					
    					Intent intent = new Intent(QuizzesActivity.this, AllQuiz.class);
    		        	intent.putExtra(Const.KEY_CHAPTER_ID, "0");
//    		        	intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
    		        	QuizzesActivity.this.startActivity(intent);
    				}
    			})
    			.create().show();
            }else{
            	intent = new Intent(this, AllQuiz.class);
            	intent.putExtra(Const.KEY_CHAPTER_ID, "0");
            	startActivity(intent);
            }
            break;
        case R.id.chapter_quizzes:
            intent = new Intent(this, ChapterQuizzesActivity.class);
            startActivity(intent);
            break;
        case R.id.score_history:
            intent = new Intent(this, ScoreHistory.class);
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