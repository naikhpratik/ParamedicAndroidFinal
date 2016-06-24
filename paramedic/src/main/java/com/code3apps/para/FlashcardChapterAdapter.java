package com.code3apps.para;

import com.code3apps.para.beans.FlashcardRecorderBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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

public class FlashcardChapterAdapter extends BaseAdapter implements
OnClickListener {
	private static final String TAG = "FlashcardChapterAdapter";
	private static final boolean DEBUG_ENABLED = true;

	private static String sAllChapters = "";

	private Context mContext;
	private LayoutInflater mInflater;
	private Cursor mCursor;
	private int mCurPos = 0;
	private int mCount = 0;
	//added by Amy on 2012/02/28
	private QuizDatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	public FlashcardChapterAdapter(Context context, Cursor cursor) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mCursor = cursor;
		mCount = mCursor.getCount();
//		mCount = mCursor.getCount() + 1;
		log("mCount is " + mCount);
		mCursor.moveToFirst();

		if (sAllChapters == "")
			sAllChapters = mContext.getString(R.string.all_chapters);

		//added by Amy on 2012/02/28
		mDbHelper = QuizContentProvider.createDatabaseHelper(context);
		mDb = mDbHelper.getWritableDatabase();

	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public int getCount() {
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

//		if (position == 0) {
//			holder.name.setText(sAllChapters);
//			holder.position = position;
//			mCurPos = position;
//		} else {
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

//		}

		convertView.setOnClickListener(this);

		return convertView;
	}

	public void close() {
		if (mCursor != null)
			mCursor.close();
	}

	public void onClick(View v) {
		final ViewHolder holder = (ViewHolder) v.getTag();
		final int chanpter_id = holder.position+1;
		final FlashcardRecorderBean fr = QuizDatabaseHelper.getFlashChaperHistoryRecorder(mDb, chanpter_id);
		if(fr!=null){
			new AlertDialog.Builder(mContext)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.continue_cards)
			.setMessage(R.string.continue_test)
			.setPositiveButton(R.string.continue_btn, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(mContext, AllFlashcard.class);
					intent.putExtra(Const.KEY_CHAPTER_ID, String.valueOf(chanpter_id));
					intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
					intent.putExtra(Const.KEY_IDS, QuizDatabaseHelper.getFlashCardIdByChapter(mDb,chanpter_id));
					Bundle b = new Bundle();
					b.putSerializable("fr", fr);
					intent.putExtra("bundle", b);
					mContext.startActivity(intent);
				}
			})
			.setNegativeButton(R.string.delete_btn, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// delete history record
					QuizDatabaseHelper.deletFlashRecorder(mDb, ""+chanpter_id);

					Intent intent = new Intent(mContext, AllFlashcard.class);
					intent.putExtra(Const.KEY_CHAPTER_ID, String.valueOf(chanpter_id));
					intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
					intent.putExtra(Const.KEY_IDS, QuizDatabaseHelper.getFlashCardIdByChapter(mDb,chanpter_id));
					mContext.startActivity(intent);
				}
			})
			.create().show();
		}else{
			Intent intent = new Intent(mContext, AllFlashcard.class);
			intent.putExtra(Const.KEY_CHAPTER_ID, String.valueOf(chanpter_id));
			intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
			intent.putExtra(Const.KEY_IDS, QuizDatabaseHelper.getFlashCardIdByChapter(mDb,chanpter_id));
			mContext.startActivity(intent);
		}

		log("id is " + chanpter_id);
		try {
//			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "No activity found for this intent!");
			e.printStackTrace();
		}
	}

	private static void log(String msg) {
		if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
			Log.d(TAG, Const.TAG_PREFIX + msg);
		}
	}

}
