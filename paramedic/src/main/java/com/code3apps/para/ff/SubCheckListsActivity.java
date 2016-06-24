package com.code3apps.para.ff;

import com.code3apps.para.R;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class SubCheckListsActivity extends Activity implements OnClickListener {
    private static final String TAG = "SubCheckListsActivity";
    private static final boolean DEBUG_ENABLED = true;

    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private SubCheckListsAdapter mAdapter;
    private ListView mListView;
    private String mParentId = "";
    private String mChapterName = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("onCreate");

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.fflist);
        
        //get data
		Intent intent = getIntent();
		//mChapterId = intent.getStringExtra(Const.KEY_CHAPTER_ID);
		mParentId = intent.getStringExtra(Const.KEY_PARENT_ID);
		mChapterName = intent.getStringExtra(Const.KEY_CHAPTER_NAME);

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(R.string.checklists);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText(mChapterName);
        
        mListView = (ListView) findViewById(R.id.skillsheets_list);

        findViewById(R.id.clear_all).setOnClickListener(this);
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

        //TODO: refactor: don't transfer cursor
        mAdapter = new SubCheckListsAdapter(this,
                QuizDatabaseHelper.getSubCheckListsCursor(mDb,mParentId));
        if (mListView != null)
            mListView.setAdapter(mAdapter);
        else
            log("mListView is null!");
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
        case R.id.clear_all:
            QuizDatabaseHelper.setCheckListCheckedByParentId(mDb, mParentId,
                    false);
            // Need update all mImgCheckBox here
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