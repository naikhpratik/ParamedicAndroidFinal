package com.code3apps.para;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.code3apps.para.beans.BookmarkBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class BookmarkList extends Activity implements OnItemClickListener {
    private static final String TAG = "BookmarkList";
    private static final boolean DEBUG_ENABLED = true;
    private static final String LIST_COLUMN_NAME = "name";
    private static final String LIST_COLUMN_ID = "id";

    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private TextView mTitle;
    private ListView mListView;
    private String mType;
    private String mChapterId = "";
    private String mChapterName = "";
    private List<BookmarkBean> mBookmarks;
    private int[] mIds;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("BookmarkList onCreate");

        Intent intent = getIntent();
        mType = getIntent().getStringExtra(Const.KEY_BOOKMARK_TYPE);
        mChapterId = intent.getStringExtra(Const.KEY_CHAPTER_ID);
        mChapterName = intent.getStringExtra(Const.KEY_CHAPTER_NAME);

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.bookmark_list);

        prepareViews();
        prepareData();
    }

    // @Override
    // public void onResume() {
    // super.onResume();
    // log("onResume");
    // }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");

        if (mDb != null)
            mDb.close();
        if (mDbHelper != null)
            mDbHelper.close();
    }

    private void prepareViews() {
        mTitle = (TextView) findViewById(R.id.title);
        mListView = (ListView) findViewById(R.id.bookmark_list);
        mListView.setOnItemClickListener(this);
    }

    private void prepareData() {
        mTitle.setText(mChapterName);

        mDbHelper = QuizContentProvider.createDatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();

        mBookmarks = QuizDatabaseHelper.getBookmarks(mDb, mType, mChapterId);

        if (mListView != null)
            mListView.setAdapter(new SimpleAdapter(this, getData(),
                    R.layout.booklist_item, new String[] { LIST_COLUMN_NAME },
                    new int[] { R.id.name }));
        else
            log("mListView is null!");
    }

    private List<? extends Map<String, ?>> getData() {
        List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> temp = new HashMap<String, String>();
        BookmarkBean bookmark = null;
        int size = mBookmarks.size();
        int id;
        mIds = new int[size];
        for (int i = 0; i < size; i++) {
            temp = new HashMap<String, String>();
            bookmark = mBookmarks.get(i);
            id = bookmark.getId();
            mIds[i] = id;
            temp.put(LIST_COLUMN_NAME, bookmark.getName());
            temp.put(LIST_COLUMN_ID, String.valueOf(id));
            myData.add(temp);
        }
        if (mType.equals(Const.VALUE_TYPE_QUIZ)) {
            log("getData(), VALUE_TYPE_QUIZ");
        } else if (mType.equals(Const.VALUE_TYPE_FLASHCARD)) {
            log("getData(), VALUE_TYPE_FLASHCARD");
        }
        return myData;
    }

    // @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        log("onItemClick(), position is " + position + ", id is " + id);
        Intent intent = null;
        @SuppressWarnings("unchecked")
        HashMap<String, String> temp = (HashMap<String, String>) arg0
                .getItemAtPosition(position);
        String recId = temp.get(LIST_COLUMN_ID);
        // String recName = temp.get(LIST_COLUMN_NAME);
        if (mType.equals(Const.VALUE_TYPE_QUIZ)) {
            intent = new Intent(this, AllQuiz.class);
        } else if (mType.equals(Const.VALUE_TYPE_FLASHCARD)) {
            intent = new Intent(this, FlashcardBookMarked.class);
        }
        // intent.putExtra(Const.KEY_BOOKMARK_TYPE, mType);
        intent.putExtra(Const.KEY_CHAPTER_ID, mChapterId);
        intent.putExtra(Const.KEY_CHAPTER_NAME, mChapterName);
        intent.putExtra(Const.KEY_ID, recId);
        intent.putExtra(Const.KEY_IDS, mIds);
        intent.putExtra(Const.KEY_POSITION, position);
        startActivity(intent);
        log("mChapterId is " + mChapterId);
        log("mChapterName is " + mChapterName);
        finish();
    }

    private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }

}