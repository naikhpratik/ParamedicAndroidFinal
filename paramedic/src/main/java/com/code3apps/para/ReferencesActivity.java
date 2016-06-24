package com.code3apps.para;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

public class ReferencesActivity extends Activity {
    private static final String TAG = "ReferencesActivity";
    private static final boolean DEBUG_ENABLED = true;

    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private ReferenceAdapter mAdapter;
    private AutoCompleteTextView mSearchView;
    private ListView mListView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("onCreate");

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.references);

        mSearchView = (AutoCompleteTextView) findViewById(R.id.search_src_text);
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
    }

    private void prepareData() {
        mDbHelper = QuizContentProvider.createDatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();

        mSearchView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (event.getAction() == KeyEvent.ACTION_UP) {
						//do nothing
					}
					return true;
				}
				return false;
			}
		});
        mAdapter = new ReferenceAdapter(this,QuizDatabaseHelper.getReferenceCursor(mDb));
        mSearchView.setAdapter(mAdapter);
        if (mListView != null)
            mListView.setAdapter(mAdapter);
        
    }

    private class ReferenceAdapter extends CursorAdapter implements Filterable,
            OnClickListener {
        private Context mContext;
        private LayoutInflater mInflater;

        class ViewHolder {
            TextView nameView;
            int id;
        }

        public ReferenceAdapter(Context context, Cursor cursor) {
            super(context, cursor);
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            log("bindView, view is " + view + ", cursor is " + cursor);
            ViewHolder holder;
            holder = (ViewHolder) view.getTag();
            holder.nameView.setText(cursor.getString(1));
            holder.id = cursor.getInt(0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final View view = mInflater.inflate(R.layout.chapter_item, null);
            ViewHolder holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
            view.setOnClickListener(this);
            return view;
        }

        @Override
        public String convertToString(Cursor cursor) {
            // return cursor.getString(1);
            return null;
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            log("runQueryOnBackgroundThread, constraint is " + constraint);
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }
            return QuizDatabaseHelper.getReferenceCursor(mDb, constraint);
        }

        public void onClick(View v) {
            ViewHolder holder = (ViewHolder) v.getTag();
            Intent intent = new Intent(mContext, FlashcardDetail.class);
            int id = holder.id;
            // int[] ids = { (int) id };
            // intent.putExtra(Const.KEY_CHAPTER_ID, "0");
            intent.putExtra(Const.KEY_ID, String.valueOf(id));
            // intent.putExtra(Const.KEY_IDS, ids);
            log("id is " + id);
            try {
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "No activity found for this intent!");
                e.printStackTrace();
            }
        }

    }

    private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }

}