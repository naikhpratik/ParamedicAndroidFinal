package com.code3apps.para;

import java.util.ArrayList;

import com.code3apps.para.beans.SkillSheetBean;
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

public class ScenariosActivity extends Activity implements OnClickListener {
    private static final String TAG = "ScenariosActivity";
    private static final boolean DEBUG_ENABLED = true;

    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private HtmlFileAdapter mAdapter;
    private ListView mListView;
    
    private ArrayList<SkillSheetBean> mSkillSheetBeans = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("onCreate");
        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.scenarios);

        findViewById(R.id.bookmarks).setOnClickListener(this);
        findViewById(R.id.scenarios_quiz).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.chapter_list);
//        prepareData();
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

        //get skill sheet beans
		mSkillSheetBeans = QuizDatabaseHelper
		.getScenriosBeans(mDb);
		mAdapter = new HtmlFileAdapter(this,mSkillSheetBeans,Const.VALUE_TYPE_SCENARIO);       
 
        /*mAdapter = new HtmlFileAdapter(this,
                QuizDatabaseHelper.getHtmlFileCursor(mDb,
                        Const.VALUE_TYPE_SCENARIO), Const.VALUE_TYPE_SCENARIO);*/
		
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
            intent.putExtra(Const.KEY_BOOKMARK_TYPE, Const.VALUE_TYPE_SCENARIO);
            startActivity(intent);
            break;
        case R.id.scenarios_quiz:
            intent = new Intent(this, ScenariosQuizActivity.class);
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