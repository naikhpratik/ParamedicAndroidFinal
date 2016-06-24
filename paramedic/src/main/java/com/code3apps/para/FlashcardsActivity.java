package com.code3apps.para;

import com.code3apps.para.beans.FlashcardRecorderBean;
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
import android.widget.ListView;

public class FlashcardsActivity extends Activity implements OnClickListener     {
    private static final String TAG = "FlashcardsActivity";
    private static final boolean DEBUG_ENABLED = true;

    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private FlashcardChapterAdapter mAdapter;
    private ListView mListView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("onCreate");

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.flashcards);

        findViewById(R.id.bookmarks).setOnClickListener(this);
        findViewById(R.id.comprehensive_fcards).setOnClickListener(this);
        findViewById(R.id.chapter_fcards).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.chapter_list);
        prepareData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");

        if (mDb != null)
            mDb.close();
        if (mDbHelper != null)
            mDbHelper.close();
        if (mAdapter != null)
            mAdapter.close();
    }

    private void prepareData() {
        mDbHelper = QuizContentProvider.createDatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();

        mAdapter = new FlashcardChapterAdapter(this,
                QuizDatabaseHelper.getFlashcardChapterCursor(mDb));
        if (mListView != null)
            mListView.setAdapter(mAdapter);
        else
            log("mListView is null!");
    }

    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id) {
        case R.id.bookmarks:
            intent = new Intent(this, BookmarksActivity.class);
            intent.putExtra(Const.KEY_BOOKMARK_TYPE, Const.VALUE_TYPE_FLASHCARD);
            startActivity(intent);
            break;
        case R.id.comprehensive_fcards:
        	final FlashcardRecorderBean fr = QuizDatabaseHelper.getFlashChaperHistoryRecorder(mDb, 0);
        	if(fr!=null){
    			new AlertDialog.Builder(FlashcardsActivity.this)
    			.setIcon(android.R.drawable.ic_dialog_alert)
    			.setTitle(R.string.continue_cards)
    			.setMessage(R.string.continue_test)
    			.setPositiveButton(R.string.continue_btn, new DialogInterface.OnClickListener() {

    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					Intent intent = new Intent(FlashcardsActivity.this, AllFlashcard.class);
    					intent.putExtra(Const.KEY_CHAPTER_ID, "0");
//    					intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
    					intent.putExtra(Const.KEY_IDS, QuizDatabaseHelper.getFlashCardIdByChapter(mDb,0));
    					Bundle b = new Bundle();
    					b.putSerializable("fr", fr);
    					intent.putExtra("bundle", b);
    					FlashcardsActivity.this.startActivity(intent);
    				}
    			})
    			.setNegativeButton(R.string.delete_btn, new DialogInterface.OnClickListener() {

    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// delete history record
    					QuizDatabaseHelper.deletFlashRecorder(mDb, "0");

    					Intent intent = new Intent(FlashcardsActivity.this, AllFlashcard.class);
    					intent.putExtra(Const.KEY_CHAPTER_ID, "0");
//    					intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
    					intent.putExtra(Const.KEY_IDS, QuizDatabaseHelper.getFlashCardIdByChapter(mDb,0));
    					FlashcardsActivity.this.startActivity(intent);
    				}
    			})
    			.create().show();
    		}else{
            	intent = new Intent(this, AllFlashcard.class);
    			intent.putExtra(Const.KEY_CHAPTER_ID, "0");
    			intent.putExtra(Const.KEY_CHAPTER_NAME, "All Chapters");
    			intent.putExtra(Const.KEY_IDS, QuizDatabaseHelper.getFlashCardIdByChapter(mDb,0));
    			this.startActivity(intent);
    		}

        	break;
        case R.id.chapter_fcards:
        	intent = new Intent(this, ChapterFcardsActivity.class);
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