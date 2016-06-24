package com.code3apps.para;

import java.util.ArrayList;
import com.code3apps.para.beans.SkillSheetBean;
import com.code3apps.para.utils.Const;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HtmlFileAdapter extends BaseAdapter implements OnClickListener {
	private static final String TAG = "HtmlFileAdapter";
	private static final boolean DEBUG_ENABLED = true;

	private Context mContext;
	private LayoutInflater mInflater;
	private Cursor mCursor;
	private int mCount = 0;
	private String mType = "";
	private int[] mIds;

	private ArrayList<SkillSheetBean> mSkillsheetBeans = null;
	private SkillSheetBean mCurrentBean = null;

	/*
	 * public HtmlFileAdapter(Context context, Cursor cursor, String type) {
	 * mContext = context; mInflater = LayoutInflater.from(context); mCursor =
	 * cursor; mCount = mCursor.getCount(); mType = type; mCursor.moveToFirst();
	 * }
	 */

	public HtmlFileAdapter(Context context,
			ArrayList<SkillSheetBean> skillbeans, String type) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mType = type;
		log("mType" + mType);
		mSkillsheetBeans = skillbeans;
		if (mSkillsheetBeans != null) {
			mCount = mSkillsheetBeans.size();
			if (mType.equalsIgnoreCase(Const.VALUE_TYPE_SCENARIO)) {
				mIds = new int[mCount];
				for (int i = 0; i < mCount; i++) {
					mIds[i] = mSkillsheetBeans.get(i).get_id();
				}
			} else if (mType.equalsIgnoreCase(Const.VALUE_TYPE_SKILL_SHEET)) {
				mIds = new int[mCount - 2];
				int j = 0;
				for (int i = 0; i < mCount; i++) {
					if (mSkillsheetBeans.get(i).get_id() > 0) {
						mIds[j] = mSkillsheetBeans.get(i).get_id();
						j++;
					}
				}
			}
		}
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public int getCount() {
		// log("getCount is " + mCount);
		return mCount;
	}

	class ViewHolder {
		TextView name;
		TextView _id;
		int position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		// log("getView(), position is " + position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.chapter_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder._id = (TextView) convertView.findViewById(R.id._id);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String name;

		if (mSkillsheetBeans != null) {
			/*
			 * if (mCurPos != position) { mCurPos = position;
			 * mCursor.moveToPosition(mCurPos); }
			 */

			mCurrentBean = mSkillsheetBeans.get(position);
			log("the position is: " + String.valueOf(position));
			if (mCurrentBean != null) {
				log("set text" + String.valueOf(position));
				name = mCurrentBean.getSkillName();
				holder.name.setText(name);
				if (mCurrentBean.getS_order() == -1
						&& mCurrentBean.getS_order() != (position + 1)) {
					// holder.name.setGravity(Gravity.CENTER);
					// holder.name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
				}
				holder._id.setText(String.valueOf(mCurrentBean.get_id()));
			}
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
		ViewHolder holder = (ViewHolder) v.getTag();
		// int id = holder.position + 1;
		String id = holder._id.getText().toString();
		int _id = Integer.valueOf(id);
		Intent intent = new Intent(mContext, AllHtmlFileActivity.class);
		// intent.putExtra(Const.KEY_HTML_COUNT, mCount);
		intent.putExtra(Const.KEY_ID, String.valueOf(id));
		intent.putExtra(Const.KEY_IDS, mIds);
		intent.putExtra(Const.KEY_BOOKMARK_TYPE, mType);
		intent.putExtra(Const.KEY_IS_BOOKMARK, false);

		log("id is " + id);
		if (_id > 0) {
			try {
				mContext.startActivity(intent);
				((Activity) mContext).finish();
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
