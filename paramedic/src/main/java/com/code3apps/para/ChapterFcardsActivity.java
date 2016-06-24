package com.code3apps.para;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

public class ChapterFcardsActivity extends Activity {

	private static final String TAG = "FlashcardsActivity";

	private QuizDatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private FlashcardChapterAdapter mAdapter;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// disable default title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// load view
		setContentView(R.layout.chapter_fcards);

		mListView = (ListView) findViewById(R.id.chapter_list);
		prepareData();
	}

	private void prepareData() {
		mDbHelper = QuizContentProvider.createDatabaseHelper(this);
		mDb = mDbHelper.getWritableDatabase();

		mAdapter = new FlashcardChapterAdapter(this,
		QuizDatabaseHelper.getFlashcardChapterCursor(mDb));
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mDb != null)
			mDb.close();
		if (mDbHelper != null)
			mDbHelper.close();
		if (mAdapter != null)
			mAdapter.close();
	}

}
