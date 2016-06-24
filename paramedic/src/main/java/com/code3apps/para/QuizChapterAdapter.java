package com.code3apps.para;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.code3apps.para.beans.QuizRecorderBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizDatabaseHelper;

public class QuizChapterAdapter extends BaseAdapter implements OnClickListener {
    private static final String TAG = "QuizChapterAdapter";
    private static final boolean DEBUG_ENABLED = true;

    private Context mContext;
    private LayoutInflater mInflater;
    private Cursor mCursor;
    private int mCurPos = 0;
    private int mCount = 0;
    private SQLiteDatabase mDb;

    public QuizChapterAdapter(Context context, Cursor cursor, SQLiteDatabase db) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCursor = cursor;
        mCount = mCursor.getCount();
        mCursor.moveToFirst();
        this.mDb = db;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public int getCount() {
        log("getCount is " + mCount);
        return mCount;
    }

    class ViewHolder {
        TextView name;
        int position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // log("getView(), position is " + position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.chapter_item, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name;

        if (mCursor != null) {
            if (mCurPos != position) {
                mCurPos = position;
                mCursor.moveToPosition(mCurPos);
            }

            name = mCursor.getString(0);
            holder.name.setText(name);
            holder.position = position;
        }

        convertView.setOnClickListener(this);

        return convertView;
    }

    public void close() {
        if (mCursor != null)
            mCursor.close();
    }

    public void onClick(View v) {
        final ViewHolder holder = (ViewHolder) v.getTag();
        final int id = holder.position + 1;
        final QuizRecorderBean recordbean = QuizDatabaseHelper.getChaperHistoryRecorder(mDb, id);
        if(recordbean!=null){
        	new AlertDialog.Builder(mContext)
        	.setIcon(android.R.drawable.ic_dialog_alert)
        	.setTitle(R.string.quit_quiz)
        	.setMessage(R.string.continue_test)
        	.setPositiveButton(R.string.continue_btn, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Intent intent = new Intent(mContext, AllQuiz.class);
			        intent.putExtra(Const.KEY_CHAPTER_ID, String.valueOf(id));
			        intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
			        Bundle b = new Bundle();
			        b.putSerializable("rb", recordbean);
			        intent.putExtra("bundle", b);
			        mContext.startActivity(intent);
				}
			})
			.setNegativeButton(R.string.delete_btn, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// delete history record
					QuizDatabaseHelper.deletRecorder(mDb, ""+id);
					// update last quizScore
					QuizDatabaseHelper.setQuizScoreDone(mDb, recordbean.getChapterName());
					
					Intent intent = new Intent(mContext, AllQuiz.class);
		        	intent.putExtra(Const.KEY_CHAPTER_ID, String.valueOf(id));
		        	intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
		        	mContext.startActivity(intent);
				}
			})
			.create().show();
        }else{
        	Intent intent = new Intent(mContext, AllQuiz.class);
        	intent.putExtra(Const.KEY_CHAPTER_ID, String.valueOf(id));
        	intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
        	mContext.startActivity(intent);
        }
        
//        intent.putExtra(name, value);
        log("id is " + id);
    }

    private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }

}
