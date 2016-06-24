package com.code3apps.para;

import java.util.ArrayList;

import com.code3apps.para.beans.SkillSheetBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

public class ScenariosQuizActivity extends Activity {

	private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private HtmlFileAdapter mAdapter;
    private ListView mListView;
    
    private ArrayList<SkillSheetBean> mSkillSheetBeans = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scenarios_quiz);
		mListView = (ListView) findViewById(R.id.chapter_list);
        prepareData();
	}
	private void prepareData() {
        mDbHelper = QuizContentProvider.createDatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();

        //get skill sheet beans 
		mSkillSheetBeans = QuizDatabaseHelper.getScenriosBeans(mDb);
		mAdapter = new HtmlFileAdapter(this,mSkillSheetBeans,Const.VALUE_TYPE_SCENARIO);       
 
        /*mAdapter = new HtmlFileAdapter(this,
                QuizDatabaseHelper.getHtmlFileCursor(mDb,
                        Const.VALUE_TYPE_SCENARIO), Const.VALUE_TYPE_SCENARIO);*/
		
        if (mListView != null)
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
