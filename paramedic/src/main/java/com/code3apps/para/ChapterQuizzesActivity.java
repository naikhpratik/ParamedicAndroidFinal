package com.code3apps.para;

import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;

public class ChapterQuizzesActivity extends Activity {
    private static final String TAG = "ChapterQuizzesActivity";
    private static final boolean DEBUG_ENABLED = true;

    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private QuizChapterAdapter mAdapter;
    private ListView mListView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("onCreate");

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.chapter_quizzes);

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

        mAdapter = new QuizChapterAdapter(this, QuizDatabaseHelper.getQuizChapterCursor(mDb), mDb);
        if (mListView != null)
            mListView.setAdapter(mAdapter);
        else
            log("mListView is null!");
    }

    private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }

}