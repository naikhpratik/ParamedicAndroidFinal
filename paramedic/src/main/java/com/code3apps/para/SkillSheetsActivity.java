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

public class SkillSheetsActivity extends Activity implements OnClickListener {
    private static final String TAG = "SkillSheetsActivity";
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
        setContentView(R.layout.skill_sheets);

        mListView = (ListView) findViewById(R.id.skill_sheet_list);
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

        //get skill sheet beans
		mSkillSheetBeans = QuizDatabaseHelper.getSkillSheetBean(mDb);
		SkillSheetBean tempBean = new SkillSheetBean();
		tempBean.setSkillName(" - NREMT Required Skill Sheets - ");
		mSkillSheetBeans.add(0, tempBean);
		tempBean = new SkillSheetBean();
		tempBean.setSkillName(" - Other Skill Sheets - ");
		mSkillSheetBeans.add(11, tempBean);
		mAdapter = new HtmlFileAdapter(this,mSkillSheetBeans,Const.VALUE_TYPE_SKILL_SHEET);
		
        /*mAdapter = new HtmlFileAdapter(this,
                QuizDatabaseHelper.getHtmlFileCursor(mDb,
                        Const.VALUE_TYPE_SKILL_SHEET),
                Const.VALUE_TYPE_SKILL_SHEET);*/
		
        if (mListView != null)
            mListView.setAdapter(mAdapter);
        else
            log("mListView is null!");
    }

    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id) {
//        case R.id.bookmarks:
//            intent = new Intent(this, BookmarksActivity.class);
//            intent.putExtra(Const.KEY_BOOKMARK_TYPE,
//                    Const.VALUE_TYPE_SKILL_SHEET);
//            startActivity(intent);
//            break;
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