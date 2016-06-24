package com.code3apps.para.ff;

import com.code3apps.para.R;
import com.code3apps.para.utils.Const;

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

public class SubCheckListsAdapter extends BaseAdapter implements OnClickListener {
    private static final String TAG = "SubCheckListsAdapter";
    private static final boolean DEBUG_ENABLED = true;

    private Context mContext;
    private LayoutInflater mInflater;
    private Cursor mCursor;
    private int mCurPos = 0;
    private int mCount = 0;
    private int[] mIds = null;

    public SubCheckListsAdapter(Context context, Cursor cursor) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCursor = cursor;
        mCount = mCursor.getCount();
		if (mCount > 0) {
			mIds = new int[mCount];
		}
        log("mCount is " + mCount);
        mCursor.moveToFirst();
        setIds();
    }
    
    private void setIds(){
    	if(mCursor!=null && mIds!=null){
    		for(int i =0;i<mCount;i++){
    			mIds[i] = mCursor.getInt(0);
    			if(!mCursor.moveToNext()){
    				break;
    			}
    		}
    		mCursor.moveToFirst();
    	}
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
    	TextView _id;
        TextView name;
        int position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // log("getView(), position is " + position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.subchecklists_item, null);

            holder = new ViewHolder();
            holder._id = (TextView) convertView.findViewById(R.id._id);
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
            
            holder._id.setText(String.valueOf(mCursor.getInt(0)));
            name = mCursor.getString(1);
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
        ViewHolder holder = (ViewHolder) v.getTag();
        //TODO: can't get id by this way
        //int id = holder.position + 1;
        int id =Integer.valueOf(holder._id.getText().toString());
        Intent intent = new Intent(mContext, CheckListDetail.class);
        intent.putExtra(Const.KEY_CHAPTER_ID, String.valueOf(id));
        intent.putExtra(Const.KEY_CHAPTER_NAME, holder.name.getText());
        log("id is " + id);
        try {
            mContext.startActivity(intent);
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
